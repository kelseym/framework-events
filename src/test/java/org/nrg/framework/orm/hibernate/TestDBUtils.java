package org.nrg.framework.orm.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public final class TestDBUtils {

    public void cleanDb() throws SQLException {
	Connection connection = _dataSource.getConnection();
	Statement statement = connection.createStatement();
	statement.execute("DELETE FROM XHBM_PACS;");
	statement.close();
    }

    @Inject
    private DataSource _dataSource;
}
