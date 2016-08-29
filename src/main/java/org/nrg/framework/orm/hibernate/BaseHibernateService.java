/**
 * BaseHibernateService
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import org.nrg.framework.services.NrgService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BaseHibernateService<E extends BaseHibernateEntity> extends NrgService {

    /**
     * Gets a new entity object, using the entity constructor matching the submitted parameters. Note
     * that the new entity is not yet created and added to the system!
     *
     * @param parameters The parameters passed to the entity constructor
     *
     * @return A new entity object.
     */
    E newEntity(Object... parameters);

    /**
     * Adds the submitted entity object to the system. This will always create
     * an entirely new entity, but if data validation constraints are violated
     * for the particular table or schema, an exception will be thrown.
     *
     * @param entity The new entity to be created.
     *
     * @return The newly created entity.
     */
    E create(E entity);

    /**
     * Creates a new entity similar to the {@link #newEntity(Object...)} method, then adds the submitted
     * entity object to the system. This will always create an entirely new entity, but if data validation
     * constraints are violated for the particular table or schema, an exception will be thrown.
     *
     * This method is a convenience method that combines the functions of the {@link #newEntity(Object...)} and
     * {@link #create(BaseHibernateEntity)} methods.
     *
     * @param parameters The parameters passed to the entity constructor
     *
     * @return A new entity object.
     */
    E create(Object... parameters);

    /**
     * Retrieves the entity with the specified ID.
     *
     * @param id The ID of the entity to be retrieved.
     *
     * @return The requested entity.
     */
    E retrieve(long id);

    /**
     * Updates the submitted entity.
     *
     * @param entity The entity to update.
     */
    void update(E entity);

    /**
     * Deletes the entity with the specified ID from the system.
     *
     * @param id The ID of the entity to be deleted.
     */
    void delete(long id);

    /**
     * Deletes the submitted entity from the system.
     *
     * @param entity The entity to be deleted.
     */
    void delete(E entity);

    /**
     * Gets all active enabled objects of the service's parameterized type.
     *
     * @return A list of all of the active enabled objects of the service's parameterized type.
     */
    List<E> getAll();

    /**
     * Gets all objects of the service's parameterized type, including those that are inactive or disabled.
     *
     * @return A list of all of the objects of the service's parameterized type, including those that are inactive or disabled.
     */
    List<E> getAllWithDisabled();

    /**
     * Gives a count of the total number of enabled objects of the service's parameterized type.
     *
     * @return A count of all of the active enabled objects of the service's parameterized type.
     */
    long getCount();

    /**
     * Gives a count of the total number of enabled objects of the service's parameterized type, including those that are inactive or disabled.
     *
     * @return A count of all of the objects of the service's parameterized type, including those that are inactive or disabled.
     */
    @SuppressWarnings("unused")
    long getCountWithDisabled();

    /**
     * Refreshes the submitted entity. If the entity's properties have been changed elsewhere, the submitted instance
     * is updated appropriately.
     *
     * @param entity The entity to be refreshed.
     */
    void refresh(E entity);

    /**
     * Refreshes the submitted list of entities. If any of the entities' properties have been changed elsewhere, the
     * instances in the list are updated appropriately.
     *
     * @param entities The entities to be refreshed.
     */
    void refresh(List<E> entities);

    /**
     * Refreshes the submitted list of entities. If any of the entities' properties have been changed elsewhere, the
     * instances in the list are updated appropriately.
     *
     * @param entities The entities to be refreshed.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void refresh(E... entities);

    /**
     * Refreshes the submitted entity. If the entity's properties have been changed elsewhere, the submitted instance
     * is updated appropriately. The initialize parameter indicates whether the entity should be initialized prior to
     * being returned. Initialization populates any lazily initialized properties of the entity so that those properties
     * can be referenced after the Hibernate session has gone out of scope.
     *
     * @param initialize Indicates whether the entity should be initialized prior to being returned.
     * @param entity     The entity to be refreshed.
     */
    void refresh(boolean initialize, E entity);

    /**
     * Refreshes the submitted list of entities. If any of the entities' properties have been changed elsewhere, the
     * instances in the list are updated appropriately. The initialize parameter indicates whether the entities should
     * be initialized prior to being returned. Initialization populates any lazily initialized properties of the entity
     * so that those properties can be referenced after the Hibernate session has gone out of scope.
     *
     * @param initialize Indicates whether the entity should be initialized prior to being returned.
     * @param entities   The entities to be refreshed.
     */
    void refresh(boolean initialize, List<E> entities);

    /**
     * Refreshes the submitted list of entities. If any of the entities' properties have been changed elsewhere, the
     * instances in the list are updated appropriately. The initialize parameter indicates whether the entities should
     * be initialized prior to being returned. Initialization populates any lazily initialized properties of the entity
     * so that those properties can be referenced after the Hibernate session has gone out of scope.
     *
     * @param initialize Indicates whether the entity should be initialized prior to being returned.
     * @param entities   The entities to be refreshed.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void refresh(boolean initialize, E... entities);

    void flush();

    @Transactional
    List<Number> getRevisions(long id);

    @Transactional
    E getRevision(final long id, final Number revision);

    /**
     * Provides a hook for programmatically validating entities before committing them
     * to the database.
     *
     * @param entity The entity to be validated.
     *
     * @return A non-null string with a message if the entity has invalid state, null otherwise.
     */
    String validate(E entity);

    /**
     * Indicates whether entities should be initialized before being returned from transactional service methods.
     * If <b>true</b>, {@link #initialize(BaseHibernateEntity)} is called before returning entities. This
     * deals with the problem of lazily initialized data members being unavailable in the web tier once the Hibernate
     * session is no longer accessible. For performance benefits, you should set this to <b>false</b> when working with
     * a service with the "open session in view" pattern available.
     *
     * @return Whether the service is set to initialize entities prior to returning them.
     *
     * @see org.nrg.framework.orm.hibernate.BaseHibernateService#setInitialize(boolean)
     */
    boolean getInitialize();

    /**
     * Sets whether entities should be initialized before being returned from transactional service methods.
     * If <b>true</b>, {@link #initialize(BaseHibernateEntity)} is called before returning entities. This
     * deals with the problem of lazily initialized data members being unavailable in the web tier once the Hibernate
     * session is no longer accessible. For performance benefits, you should set this to <b>false</b> when working with
     * a service with the "open session in view" pattern available.
     *
     * @param initialize Indicates whether the service should initialize entities prior to returning them.
     *
     * @see BaseHibernateService#getInitialize()
     */
    void setInitialize(final boolean initialize);

    /**
     * Method to initialize entity. By default, calls {@link org.hibernate.Hibernate#initialize(Object)}, but this
     * can be overridden.
     *
     * @param entity Entity object to initialize.
     *
     * @see BaseHibernateService#getInitialize()
     */
    void initialize(final E entity);
}
