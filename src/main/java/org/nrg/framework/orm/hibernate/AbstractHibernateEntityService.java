/**
 * AbstractHibernateEntityService
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements a generic service functionality to provide default entity management
 * functionality. In the generic terminology, E is the entity class and D is the
 * DAO class.
 * 
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
// TODO: Integrate calls to the validate() method in the methods that commit objects to the database. Currently that returns a String,
// but this should probably be modified to either throw an exception or return a more complex object that has a severity and message.
// For example, in the exclusion service, you can save a System-scoped object with a target ID. This is worth a warning, but not worth
// an error. However, you can NOT save a Project- or DataType-scoped object WITHOUT a target ID, since you need something to relate with
// the scope. That should be an error or fatal or something.
//
// See javadoc comments on the validate method regarding Hibernate Validator.
abstract public class AbstractHibernateEntityService<E extends BaseHibernateEntity> extends AbstractParameterizedWorker<E> implements BaseHibernateService<E> {

    public AbstractHibernateEntityService() {
        super();
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
    }

    /**
     * @return A new empty entity object.
     * @see BaseHibernateService#newEntity()
     */
    abstract public E newEntity();

    /**
     * Adds the submitted entity to the system.
     * @param entity The entity to be added to the system.
     * @see BaseHibernateService#create(BaseHibernateEntity)
     */
    @Override
    @Transactional
    public void create(E entity) {
        if (_log.isDebugEnabled()) {
            _log.debug("Creating a new entity: " + entity.toString());
        }
        getDao().create(entity);
    }

    /**
     * 
     * @see BaseHibernateService#retrieve(long)
     */
    @Override
    @Transactional
    public E retrieve(long id) {
        if (_log.isDebugEnabled()) {
            _log.debug("Retrieving entity for ID: " + id);
        }
        if (_isAuditable) {
            return getDao().findEnabledById(id);
        } else {
            return getDao().retrieve(id);
        }
    }

    /**
     * @see BaseHibernateService#update(BaseHibernateEntity)
     */
    @Override
    @Transactional
    public void update(E entity) {
        if (_log.isDebugEnabled()) {
            _log.debug("Updating entity for ID: " + entity.getId());
        }
        getDao().update(entity);
    }

    /**
     * @see BaseHibernateService#delete(BaseHibernateEntity)
     */
    @Override
    @Transactional
    public void delete(E entity) {
        if (_log.isDebugEnabled()) {
            _log.debug("Deleting entity for ID: " + entity.getId());
        }
        if (_isAuditable) {
            entity.setEnabled(false);
            getDao().update(entity);
        } else {
            getDao().delete(entity);
        }
    }

    /**
     * @see BaseHibernateService#delete(long)
     */
    @Override
    @Transactional
    public void delete(long id) {
        if (_log.isDebugEnabled()) {
            _log.debug("Deleting entity for ID: " + id);
        }
        delete(getDao().retrieve(id));
    }

    @Override
    @Transactional
    public List<E> getAll() {
        _log.debug("Getting all enabled entities");
        return getDao().findAllEnabled();
    }

    @Override
    @Transactional
    public List<E> getAllWithDisabled() {
        _log.debug("Getting all enabled and disabled entities");
        return getDao().findAll();
    }

    @Override
    @Transactional
    public void refresh(E entity) {
        refresh(true, entity);
    }
    
    @Override
    @Transactional
    public void refresh(List<E> entities) {
        refresh(true, entities);
    }
    
    @Override
    @Transactional
    public void refresh(E... entities) {
        refresh(true, entities);
    }
    
    @Override
    @Transactional
    public void refresh(boolean initialize, E entity) {
        getDao().refresh(initialize, entity);
    }
    
    @Override
    @Transactional
    public void refresh(boolean initialize, List<E> entities) {
        for (E entity : entities) {
            getDao().refresh(initialize, entity);
        }
    }
    
    @Override
    @Transactional
    public void refresh(boolean initialize, E... entities) {
        for (E entity : entities) {
            getDao().refresh(initialize, entity);
        }
    }

    /**
     * Provides a default validation method that can be overridden in specific implementations. This implementation
     * always returns <b>null</b>, i.e. entities are always considered to be in a valid state. Overriding
     * implementations should return a non-null string message for entities that are in an invalid state, but otherwise
     * return null.
     * 
     * Note, though, that Hibernate will automatically validate entities that are annotated with validation criteria if
     * it finds a validation provider on the classpath (it is in XNAT builder, so all XNAT entities will be validated on
     * save). Given that, there may not be much need for this method, but we'll leave it here for now. Someday there may
     * be need for validation in the business layer (here) as well.
     */
    @Override
    public String validate(E entity) {
        return null;
    }

    /**
     * Gets the DAO configured for the service instance.
     * @return The DAO object.
     */
    abstract protected BaseHibernateDAO<E> getDao();

    private static final Log _log = LogFactory.getLog(AbstractHibernateEntityService.class);
    private boolean _isAuditable;
}
