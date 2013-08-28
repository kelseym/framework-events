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

import java.util.List;

import org.nrg.framework.services.NrgService;

public interface BaseHibernateService<E extends BaseHibernateEntity> extends NrgService {

    /**
     * Gets a new empty entity object. There is no guarantee
     * as to the contents of the entity.
     * @return A new empty entity object.
     */
    public abstract E newEntity();

    /**
     * Adds the submitted entity object to the system. This will always create 
     * an entirely new entity, but if data validation constraints are violated
     * for the particular table or schema, an exception will be thrown.
     * @param entity The new entity to be created.
     */
    public abstract void create(E entity);

    /**
     * Retrieves the entity with the specified ID.
     * @param id The ID of the entity to be retrieved.
     */
    public abstract E retrieve(long id);

    /**
     * Updates the submitted entity.
     * @param entity The entity to update.
     */
    public abstract void update(E entity);

    /**
     * Deletes the entity with the specified ID from the system.
     * @param id The ID of the entity to be deleted.
     */
    public abstract void delete(long id);

    /**
     * Deletes the submitted entity from the system.
     * @param entity The entity to be deleted.
     */
    public abstract void delete(E entity);
    
    public abstract List<E> getAll();
    public abstract List<E> getAllWithDisabled();
    
    public abstract void refresh(E entity);
    public abstract void refresh(List<E> entities);
    public abstract void refresh(E... entities);
    public abstract void refresh(boolean initialize, E entity);
    public abstract void refresh(boolean initialize, List<E> entities);
    public abstract void refresh(boolean initialize, E... entities);
    
    /**
     * Provides a hook for programmatically validating entities before committing them
     * to the database. 
     * @param entity The entity to be validated.
     * @return A non-null string with a message if the entity has invalid state, null otherwise.
     */
    public abstract String validate(E entity);
}
