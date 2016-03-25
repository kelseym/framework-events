package org.nrg.framework.orm.datasource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataSourceConfig.class)
public class DataSourceConfigTest {
    @Test
    public void verifyDataSource() {
        assertNotNull(_dataSource);
    }

    @Inject
    private DataSource _dataSource;
}
