/*
 * framework: org.nrg.framework.orm.DatabaseHelper
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.orm;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;
import java.util.concurrent.Callable;

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

    public static long convertPGIntervalToSeconds(final String expression) {
        try {
            final PGInterval interval = new PGInterval(expression);
            return ((long) interval.getYears()) * 31536000L +
                   ((long) interval.getMonths()) * 2592000L +
                   ((long) interval.getDays()) * 86400L +
                   ((long) interval.getHours()) * 3600L +
                   ((long) interval.getMinutes()) * 60L +
                   ((long) interval.getSeconds());
        } catch (SQLException e) {
            // This is ignored because it doesn't happen: there's no database transaction in this call.
            return 0L;
        }
    }

    public static int convertPGIntervalToIntSeconds(final String interval) {
        return (int) convertPGIntervalToSeconds(interval);
    }


    /**
     * Gets the database helper's JDBC template to allow database transactions outside of the helper's standard
     * functions.
     *
     * @return The internal JDBC template.
     */
    public JdbcTemplate getJdbcTemplate() {
        return _template;
    }

    /**
     * Checks whether the indicated table exists in the database.
     *
     * @param table The table for which to test.
     *
     * @return Returns true if the table exists, false otherwise.
     *
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
     *
     * @return Returns true if the table exists, false otherwise.
     *
     * @throws SQLException If an error occurs while accessing the database.
     */
    public boolean tableExists(final String schema, final String table) throws SQLException {
        try (final Connection connection = _template.getDataSource().getConnection();
             final ResultSet results = connection.getMetaData().getTables("catalog", schema, table, new String[]{"TABLE"})) {
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
     *
     * @return Returns the column type if the column exists in the table, null otherwise.
     *
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
     * @param dataType The datatype to set for the column.
     *
     * @throws SQLWarning   When the specified table or column doesn't exist.
     * @throws SQLException When an error occurs executing the alter query.
     */
    public void setColumnDatatype(final String table, final String column, final String dataType) throws SQLException {
        if (!tableExists(table)) {
            throw new SQLWarning("The requested table " + table + " does not exist.");
        }
        final String type = columnExists(table, column);
        if (type == null) {
            throw new SQLWarning("The requested column " + column + " does not exist in the table " + table + ".");
        }
        if (!StringUtils.equals(type, dataType)) {
            executeTransaction(new SetColumnDataType(table, column, dataType), true);
        } else {
            _log.info("Not updating datatype for column {} in the table {}, the datatype is already {}.", column, table, dataType);
        }
    }

    /**
     * Executes the callable's <b>call()</b> method, wrapping it within a transaction if possible. Note that if a
     * transaction manager or template wasn't available to the database helper at instantiation, this method will NOT
     * attempt to execute the transaction! If you want your method to be executed even if no transaction support is
     * available, call the {@link #executeTransaction(Callable, boolean)} method, setting the second parameter to
     * <b>true</b>.
     *
     * @param callable A callable object that implements the desired function.
     *
     * @return A message with the results of the transaction. The value for this depends on your implementation.
     */
    public String executeTransaction(final Callable<String> callable) {
        return executeTransaction(callable, false);
    }

    /**
     * Executes the callable's <b>call()</b> method, wrapping it within a transaction if possible. Note that if a
     * transaction manager or template wasn't available to the database helper at instantiation, this method will check
     * the second parameter: if <b>true</b>, the code will be executed without transaction protection or rollback
     * capabilities.
     *
     * @param callable                         A callable object that implements the desired function.
     * @param executeWithoutTransactionManager Whether the transaction should be executed if no transaction manager is available.
     *
     * @return A message with the results of the transaction. The value for this depends on your implementation.
     */
    public String executeTransaction(final Callable<String> callable, final boolean executeWithoutTransactionManager) {
        if (_transactionTemplate != null) {
            return _transactionTemplate.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(final TransactionStatus transactionStatus) {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        return logMessage("An error occurred executing the transaction from the callable class " + callable.getClass().getName(), e);
                    }
                }
            });
        } else if (executeWithoutTransactionManager) {
            _log.warn("No transaction template found in the application context, so I'm performing the requested operation without transactional protection.");
            try {
                return callable.call();
            } catch (Exception e) {
                return logMessage("An error occurred executing the transaction from the callable class " + callable.getClass().getName(), e);
            }
        } else {
            return logMessage("No transaction template found in the application context, will not perform the requested operation without transactional protection.");
        }
    }

    private class SetColumnDataType implements Callable<String> {
        public SetColumnDataType(final String table, final String column, final String dataType) {
            _table = table;
            _column = column;
            _dataType = dataType;
        }

        @Override
        public String call() throws Exception {
            // Add the new column with the suffix "_new" and a timestamp.
            final String tempColumnName = _column + "_new_" + Long.toString(new Date().getTime());
            _template.execute("ALTER TABLE " + _table + " ADD COLUMN " + tempColumnName + " " + _dataType);

            // Copy all values from the existing column into the new column
            _template.execute("UPDATE " + _table + " SET " + tempColumnName + " = " + _column);

            // Drop the old column
            _template.execute("ALTER TABLE " + _table + " DROP COLUMN " + _column);

            // Move the new column to the same name as the old.
            _template.execute("ALTER TABLE " + _table + " RENAME " + tempColumnName + " TO " + _column);
            return null;
        }

        private final String _table;

        private final String _column;
        private final String _dataType;
    }

    private String logMessage(final String message) {
        return logMessage(message, null);
    }

    private String logMessage(final String message, final Exception e) {
        if (e != null) {
            _log.error(message, e);
            return message + "\n" + "Exception type: " + e.getClass().getName() + "\n" + e.getMessage();
        } else {
            _log.warn(message);
            return message;
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(DatabaseHelper.class);

    private final JdbcTemplate        _template;
    private final TransactionTemplate _transactionTemplate;
}
