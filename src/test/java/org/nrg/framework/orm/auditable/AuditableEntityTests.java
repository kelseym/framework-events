/**
 * AuditableEntityTests
 * (C) 2014 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 8/5/2014 by Rick Herrick
 */
package org.nrg.framework.orm.auditable;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.framework.orm.utils.TestDBUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests handling of auditable classes, checking for persistence of "deleted" classes, handling
 * of unique constraints, etc.
 *
 * @author Rick Herrick
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AuditableEntityTests {

    public static final String FIELD_1 = "Field 1";
    public static final int FIELD_2 = 321;
    public static final Date FIELD_3 = new Date();

    @Before
    public void clearEntities() throws SQLException {
        _dbUtils.cleanDb("XHBM_AUDITABLE_ENTITY");
    }

    @Test
    public void testBasicOperations() {
        AuditableEntity created = _service.newEntity();
        created.setField1(FIELD_1);
        created.setField2(FIELD_2);
        created.setField3(FIELD_3);
        _service.create(created);

        List<AuditableEntity> items = _service.getAll();
        assertNotNull(items);
        assertEquals(1, items.size());
        AuditableEntity retrieved = items.get(0);
        assertEquals(FIELD_1, retrieved.getField1());
        assertEquals(FIELD_2, retrieved.getField2());
        assertEquals(FIELD_3, retrieved.getField3());

        _service.delete(retrieved);
        items = _service.getAll();
        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUniqueConstraints() {
        AuditableEntity created1 = _service.newEntity();
        created1.setField1(FIELD_1);
        created1.setField2(FIELD_2);
        created1.setField3(FIELD_3);
        _service.create(created1);
        AuditableEntity created2 = _service.newEntity();
        created2.setField1(FIELD_1);
        created2.setField2(FIELD_2);
        created2.setField3(FIELD_3);
        _service.create(created2);
    }

    @Test
    public void testAuditableUniqueConstraints() {
        AuditableEntity created1 = _service.newEntity();
        created1.setField1(FIELD_1);
        created1.setField2(FIELD_2);
        created1.setField3(FIELD_3);
        _service.create(created1);
        _service.delete(created1);
        AuditableEntity created2 = _service.newEntity();
        created2.setField1(FIELD_1);
        created2.setField2(FIELD_2);
        created2.setField3(FIELD_3);
        _service.create(created2);
    }

    @Inject
    private TestDBUtils _dbUtils;
    @Inject
    private AuditableEntityService _service;
}
