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

/**
 * 
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
public class HibernateUtils {
    /**
     * Tests whether the entity is auditable. Auditable entities are not deleted in delete operations,
     * but instead are disabled by calling the {@link BaseHibernateEntity#setEnabled(boolean)} method
     * with the value <b>false</b>.
     * 
     * Classes are by default not auditable. You can declare an entity class to be auditable by adding
     * the {@link Auditable} annotation to the class declaration.
     * 
     * @param entity
     * @return Whether the class is auditable or not.
     */
    @SuppressWarnings("unchecked")
    public static <E> boolean isAuditable(E entity) {
        return isAuditable((Class<E>) entity.getClass());
    }
    
    public static <E> boolean isAuditable(Class<E> clazz) {
        return clazz.isAnnotationPresent(Auditable.class);
    }

}
