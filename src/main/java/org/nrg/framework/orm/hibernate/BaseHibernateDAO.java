/*
 * framework: org.nrg.framework.orm.hibernate.BaseHibernateDAO
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.orm.hibernate;

import org.hibernate.SessionFactory;
import org.nrg.framework.orm.hibernate.annotations.Auditable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface BaseHibernateDAO<E extends BaseHibernateEntity> {

    void setSessionFactory(final SessionFactory factory);

    Serializable create(final E entity);

    E retrieve(final long id);

    void update(final E entity);

    void delete(final E entity);

    void saveOrUpdate(final E entity);

    List<E> findAll();

    List<E> findAllEnabled();

    long countAll();

    long countAllEnabled();

    @SuppressWarnings("unchecked")
    List<E> findByProperty(String property, Object value);

    @SuppressWarnings("unchecked")
    List<E> findByProperties(Map<String, Object> properties);

    @SuppressWarnings("unchecked")
    E findByUniqueProperty(String property, Object value);

    E findById(long id);

    E findById(long id, boolean lock);

    E findEnabledById(long id);

    E findEnabledById(long id, boolean lock);

    /**
     * Finds entities that match the set properties.
     *
     * @param exampleInstance The example to use.
     * @param excludeProperty Properties of the example that should be ignored.
     *
     * @return All entities that match the set properties.
     */
    List<E> findByExample(E exampleInstance, String[] excludeProperty);

    /**
     * Works just like {@link #findByExample(BaseHibernateEntity, String[])}, except that it includes disabled instances
     * of {@link Auditable auditable} entity definitions.
     *
     * @param exampleInstance The example to use.
     * @param excludeProperty Properties of the example that should be ignored.
     *
     * @return All entities that match the set properties.
     *
     * @deprecated Auditable entities should be transitioned to use the Hibernate Envers @Audited annotation.
     */
    @Deprecated
    @SuppressWarnings({"unused", "deprecation"})
    List<E> findAllByExample(E exampleInstance, String[] excludeProperty);

    void refresh(boolean initialize, E entity);

    void flush();

    void initialize(E entity);

    /**
     * Gets a list of the available revisions for the entity with the specified ID. These
     * revision numbers can be used when calling {@link #getRevision(long, Number)}.
     *
     * @param id       The ID of the entity to retrieve.
     *
     * @return The available revision numbers for the specified entity.
     */
    List<Number> getRevisions(final long id);

    /**
     * Gets the requested revision of the entity with the specified ID. You can get a
     * list of the available revision numbers by calling {@link #getRevisions(long)}.
     *
     * @param id       The ID of the entity to retrieve.
     * @param revision The revision of the entity to retrieve.
     *
     * @return The requested revision of the specified entity.
     */
    E getRevision(final long id, final Number revision);
}
