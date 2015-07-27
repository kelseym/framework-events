/**
 * BaseHibernateDAO
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import org.hibernate.SessionFactory;
import org.nrg.framework.orm.hibernate.annotations.Auditable;

import java.io.Serializable;
import java.util.List;

public interface BaseHibernateDAO<E extends BaseHibernateEntity> {

    void setSessionFactory(SessionFactory factory);

    Serializable create(E entity);

    E retrieve(long id);

    void update(E entity);

    void delete(E entity);

    List<E> findAll();

    List<E> findAllEnabled();

    long countAll();

    long countAllEnabled();

    E findById(long id);

    E findById(long id, boolean lock);

    E findEnabledById(long id);

    E findEnabledById(long id, boolean lock);

    /**
     * Finds entities that match the set properties. This excludes disabled instances of {@link Auditable auditable}
     * entity definitions.
     *
     * @param exampleInstance The example to use.
     * @param excludeProperty Properties of the example that should be ignored.
     * @return All entities that match the set properties.
     */
    List<E> findByExample(E exampleInstance, String[] excludeProperty);

    /**
     * Works just like {@link #findByExample(BaseHibernateEntity, String[])}, except that it includes disabled instances
     * of {@link Auditable auditable} entity definitions.
     *
     * @param exampleInstance The example to use.
     * @param excludeProperty Properties of the example that should be ignored.
     * @return All entities that match the set properties.
     */
    @SuppressWarnings("unused")
    List<E> findAllByExample(E exampleInstance, String[] excludeProperty);

    void refresh(boolean initialize, E entity);

}