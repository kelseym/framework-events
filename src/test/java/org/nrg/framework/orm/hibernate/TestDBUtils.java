package org.nrg.framework.orm.hibernate;

import org.h2.constant.ErrorCode;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public final class TestDBUtils {

    public void cleanDb() throws SQLException {
	Connection connection = _dataSource.getConnection();
	Statement statement = connection.createStatement();
        try {
	statement.execute("DELETE FROM XHBM_PACS;");
        } catch (SQLException exception) {
            // If we didn't find the table, that's OK, because it just may not have been created yet.
            if (exception.getErrorCode() != ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1) {
                throw exception;
            }
        }
	statement.close();
    }

    @Inject
    @Named("dataSource")
    private DataSource _dataSource;
}
