package org.nrg.framework.orm.hibernate;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PacsEntityServiceTest {

    @Inject
    private TestDBUtils testDbUtils;

    @Inject
    private PacsEntityService pacsEntityService;

    @Before
    public void before() throws SQLException {
	testDbUtils.cleanDb();
    }

    @Test
    @ExpectedException(value = ConstraintViolationException.class)
    public void testNullPacs() {
        Pacs entity = pacsEntityService.newEntity();
        entity.setAeTitle("testNullPacs");
        pacsEntityService.create(entity);
    }

    @Test
    public void testAllServiceMethods() {
	assertEquals(0, pacsEntityService.getAll().size());
	Pacs pacs = buildTestPacs();
	pacsEntityService.create(pacs);
	assertEquals(1, pacsEntityService.getAll().size());
	pacs.setAeTitle("FOO");
	pacsEntityService.update(pacs);
	pacs = pacsEntityService.retrieve(pacs.getId());
	assertEquals("FOO", pacs.getAeTitle());
	pacsEntityService.delete(pacs);
	assertEquals(0, pacsEntityService.getAll().size());
    }

    private Pacs buildTestPacs() {
	Pacs pacs = new Pacs();
	pacs.setAeTitle("TIP-DEV-PACS");
	pacs.setHost("10.28.16.215");
	pacs.setStoragePort(11112);
	pacs.setQueryRetrievePort(11112);
	pacs.setOrmStrategySpringBeanId("dicomOrmStrategy");
	return pacs;
    }
}
