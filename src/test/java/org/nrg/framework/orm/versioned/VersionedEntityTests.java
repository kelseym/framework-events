/**
 * VersionedEntityTests
 * (C) 2014 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 8/5/2014 by Rick Herrick
 */
package org.nrg.framework.orm.versioned;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests handling of versioned Hibernate classes, checking for persistence of "deleted" classes, handling of unique
 * constraints, etc.
 *
 * Note that this test can NOT be marked with the @Transactional annotation. Doing so will cause database operations to
 * be held as transactions within the test methods and result in calls to retrieve committed objects failing.
 *
 * @author Rick Herrick
 */

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = VersionedEntityTestsConfiguration.class)
@Rollback
public class VersionedEntityTests {

    @Test
    public void testBasicOperations() {
        final Date date = getRandomDate();
        final VersionedEntity created = _service.newEntity();
        created.setField1("BasicOperationsField1");
        created.setField2(1);
        created.setField3(date);
        _service.create(created);

        final long createdId = created.getId();
        final VersionedEntity retrieved = _service.retrieve(createdId);
        assertEquals("BasicOperationsField1", retrieved.getField1());
        assertEquals(1, retrieved.getField2());
        assertEquals(date, retrieved.getField3());

        _service.delete(retrieved);
        final VersionedEntity deleted = _service.retrieve(createdId);
        assertNull(deleted);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUniqueConstraints() {
        final Date date = getRandomDate();
        VersionedEntity created1 = _service.newEntity();
        created1.setField1("UniqueConstraintsField1");
        created1.setField2(2);
        created1.setField3(date);
        _service.create(created1);
        VersionedEntity created2 = _service.newEntity();
        created2.setField1("UniqueConstraintsField1");
        created2.setField2(2);
        created2.setField3(date);
        _service.create(created2);
    }

    @Test
    public void testFindEntityByProperties() {
        Calendar instance = Calendar.getInstance();
        instance.set(2015, Calendar.DECEMBER, 25, 8, 15);
        final Date date1 = getRandomDate();
        final Date date2 = getRandomDate();
        final Date date3 = getRandomDate();
        final Date date4 = getRandomDate();

        final VersionedEntity entity1 = _service.create("FindEntityByPropertiesField1Take1", 3, date1);
        entity1.setField1("Hello there!");
        _service.update(entity1);
        entity1.setField2(8723);
        _service.update(entity1);
        entity1.setField3(date2);
        _service.update(entity1);

        List<Number> revisions = _service.getRevisions(entity1.getId());
        assertNotNull(revisions);
        assertEquals(4, revisions.size());

        VersionedEntity version1 = _service.getRevision(entity1.getId(), revisions.get(0));
        assertNotNull(version1);
        assertEquals("FindEntityByPropertiesField1Take1", version1.getField1());

        VersionedEntity entity2 = _service.create("FindEntityByPropertiesField1Take2", 4, date3);
        entity2.setField1("Classy!");
        _service.update(entity2);
        entity2.setField3(date4);
        _service.update(entity2);

        revisions = _service.getRevisions(entity2.getId());
        assertNotNull(revisions);
        assertEquals(3, revisions.size());
        VersionedEntity version2 = _service.getRevision(entity2.getId(), revisions.get(1));
        assertEquals("Classy!", version2.getField1());
        assertEquals(date3, version2.getField3());
    }

    private Date getRandomDate() {
        long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2013-01-01 00:00:00").getTime();
        long diff = end - offset + 1;
        return new Timestamp(offset + (long)(Math.random() * diff));
    }

    private void displayResults(final List results) {
        int index = 1;
        for (final Object result : results) {
            System.out.println("Item " + index + ":");
            //noinspection unchecked
            displayResults((Map) result, "    ");
        }
    }

    private void displayResults(final VersionedEntity entity) {
        displayResults(entity, "");
    }

    private void displayResults(final Map<String, Object> entity, final String offset) {
        System.out.println(offset + "ID:       " + entity.get("ID"));
        System.out.println(offset + "FIELD1:   " + entity.get("FIELD1"));
        System.out.println(offset + "FIELD2:   " + entity.get("FIELD2"));
        System.out.println(offset + "FIELD3:   " + entity.get("FIELD3"));
        System.out.println(offset + "ENABLED:  " + entity.get("ENABLED"));
        System.out.println(offset + "DISABLED: " + entity.get("DISABLED"));
    }

    private void displayResults(final VersionedEntity entity, final String offset) {
        System.out.println(offset + "ID:       " + entity.getId());
        System.out.println(offset + "FIELD1:   " + entity.getField1());
        System.out.println(offset + "FIELD2:   " + entity.getField2());
        System.out.println(offset + "FIELD3:   " + entity.getField3());
        System.out.println(offset + "ENABLED:  " + entity.isEnabled());
        System.out.println(offset + "DISABLED: " + entity.getDisabled());
    }

    @Inject
    private JdbcTemplate           _template;
    @Inject
    private VersionedEntityService _service;
}
