/**
 * HibernateUtils
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import org.nrg.framework.orm.hibernate.annotations.Auditable;

import java.util.Date;

public class HibernateUtils {

    /**
     * This is the default date that basically maps to null for the purpose of identifying
     * {@link Auditable auditable} entities that have <i>not</i> been deleted (or, really,
     * disabled: auditable entities should never actually be deleted from the database).
     * Entities that have been "deleted" will have a {@link BaseHibernateEntity#getDisabled()
     * disabled timestamp} that indicates the date and time the entity was actually disabled.
     */
    public static Date DEFAULT_DATE = new Date(0L);

    /**
     * Tests whether the entity is auditable. Auditable entities are not deleted in delete operations,
     * but instead are disabled by calling the {@link BaseHibernateEntity#setEnabled(boolean)} method
     * with the value <b>false</b>.
     * 
     * Classes are by default not auditable. You can declare an entity class to be auditable by adding
     * the {@link Auditable} annotation to the class declaration.
     * 
     * @param entity    The entity to check for auditability.
     * @return Whether the class is auditable or not.
     */
    @SuppressWarnings("unchecked")
    public static <E> boolean isAuditable(E entity) {
        return isAuditable(entity.getClass());
    }
    
    public static <E> boolean isAuditable(Class<E> clazz) {
        return clazz.isAnnotationPresent(Auditable.class);
    }

}
