package org.nrg.framework.orm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class DatabaseHelper {
    public DatabaseHelper(final DataSource dataSource) {
        this(dataSource, null);
    }

    public DatabaseHelper(final JdbcTemplate template) {
        this(template, null);
    }

    public DatabaseHelper(final DataSource dataSource, final TransactionTemplate transactionTemplate) {
        _template = new JdbcTemplate(dataSource);
        _transactionTemplate = transactionTemplate;
    }

    public DatabaseHelper(final JdbcTemplate template, final TransactionTemplate transactionTemplate) {
        _template = template;
        _transactionTemplate = transactionTemplate;
    }

    /**
     * Checks whether the indicated table exists in the database.
     *
     * @param table The table for which to test.
     * @return Returns true if the table exists, false otherwise.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public boolean tableExists(final String table) throws SQLException {
        return tableExists(null, table);
    }

    /**
     * Checks whether the indicated table exists in the specified schema.
     *
     * @param schema The schema to look in.
     * @param table  The table for which to test.
     * @return Returns true if the table exists, false otherwise.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public boolean tableExists(final String schema, final String table) throws SQLException {
        try (final Connection connection = _template.getDataSource().getConnection();
             final ResultSet results = connection.getMetaData().getTables("catalog", schema, table, new String[] {"TABLE"})) {
            if (results.next()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the indicated column exist in the specified table in the database. Note that this method does NOT
     * test for the existence of the table.
     *
     * @param table  The table for which to test.
     * @param column The column for which to test.
     * @return Returns the column type if the column exists in the table, null otherwise.
     * @throws SQLException If an error occurs while accessing the database.
     */
    @Nullable
    public String columnExists(final String table, final String column) throws SQLException {
        try (final Connection connection = _template.getDataSource().getConnection();
             final ResultSet results = connection.getMetaData().getColumns("catalog", null, table, column)) {
            // We should only find a single result. If we find that (i.e. next() returns true), then return the type.
            if (results.next()) {
                final String typeName = results.getString("TYPE_NAME");
                if (typeName.equals("varchar")) {
                    return "varchar(" + results.getString("COLUMN_SIZE") + ")";
                } else {
                    return typeName;
                }
            }
            // If we didn't find any results, return null. That's bad, m'kay?
            return null;
        }
    }

    /**
     * Alters the indicated column to set the datatype to the submitted definition. This method checks first for the
     * existence of the table and column and throws an SQLWarning if either of those is not found.
     * <p>
     * Note that this method will fail on columns that are part of a unique or composite key or have other referential
     * dependencies.
     *
     * @param table    The table in which the column can be found.
     * @param column   The column to be altered.
     * @param datatype The datatype to set for the column.
     * @throws SQLWarning   When the specified table or column doesn't exist.
     * @throws SQLException When an error occurs executing the alter query.
     */
    public void setColumnDatatype(final String table, final String column, final String datatype) throws SQLException {
        if (!tableExists(table)) {
            throw new SQLWarning("The requested table " + table + " does not exist.");
        }
        final String type = columnExists(table, column);
        if (type == null) {
            throw new SQLWarning("The requested column " + column + " does not exist in the table " + table + ".");
        }
        if (!StringUtils.equals(type, datatype)) {
            if (_transactionTemplate != null) {
                _transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {
                        _setColumnDataType(table, column, datatype);
                    }
                });
            } else {
                _log.warn("No transaction template found in the application context, so I'm performing the requested operation without transactional protection.");
                _setColumnDataType(table, column, datatype);
            }
        } else {
            _log.info("Not updating datatype for column {} in the table {}, the datatype is already {}.", column, table, datatype);
        }
    }

    private void _setColumnDataType(final String table, final String column, final String datatype) {
        // Add the new column with the suffix "_new" and a timestamp.
        final String tempColumnName = column + "_new_" + Long.toString(new Date().getTime());
        _template.execute("ALTER TABLE " + table + " ADD COLUMN " + tempColumnName + " " + datatype);

        // Copy all values from the existing column into the new column
        _template.execute("UPDATE " + table + " SET " + tempColumnName + " = " + column);

        // Drop the old column
        _template.execute("ALTER TABLE " + table + " DROP COLUMN " + column);

        // Move the new column to the same name as the old.
        _template.execute("ALTER TABLE " + table + " RENAME " + tempColumnName + " TO " + column);
    }

    private static final Logger _log = LoggerFactory.getLogger(DatabaseHelper.class);

    private final JdbcTemplate        _template;
    private final TransactionTemplate _transactionTemplate;
}
