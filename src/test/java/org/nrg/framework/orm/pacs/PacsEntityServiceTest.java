/*
 * framework: org.nrg.framework.orm.pacs.PacsEntityServiceTest
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.orm.pacs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.framework.exceptions.NrgServiceException;
import org.nrg.framework.orm.utils.TestDBUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PacsEntityServiceTestConfiguration.class)
public class PacsEntityServiceTest {

    @Inject
    private TestDBUtils testDbUtils;

    @Inject
    private PacsEntityService pacsEntityService;

    @Before
    public void before() throws SQLException {
        testDbUtils.cleanDb("XHBM_PACS");
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullPacs() throws NrgServiceException {
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
        pacs = pacsEntityService.findByAeTitle("TIP-DEV-PACS");
        assertNotNull(pacs);
        assertEquals("TIP-DEV-PACS", pacs.getAeTitle());
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
