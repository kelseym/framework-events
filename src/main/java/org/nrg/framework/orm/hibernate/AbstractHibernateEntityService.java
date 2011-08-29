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

import java.lang.reflect.ParameterizedType;
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
abstract public class AbstractHibernateEntityService<E extends BaseHibernateEntity> implements BaseHibernateService<E> {

    @SuppressWarnings("unchecked")
    public AbstractHibernateEntityService() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        _parameterizedType = (Class<E>) parameterizedType.getActualTypeArguments()[0];
        _isAuditable = HibernateUtils.isAuditable(_parameterizedType);
    }

    /**
     * @return A new empty entity object.
     * @see BaseHibernateService#newEntity()
     */
    abstract public E newEntity();

    /**
     * Adds the submitted entity to the system.
     * @param entity The entity to be added to the system.
     * @see BaseHibernateService#create(Object)
     */
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
     * @see BaseHibernateService#update(Object)
     */
    @Transactional
    public void update(E entity) {
        if (_log.isDebugEnabled()) {
            _log.debug("Updating entity for ID: " + entity.getId());
        }
        getDao().update(entity);
    }

    /**
     * @see BaseHibernateService#delete(Object)
     */
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
    @Transactional
    public void delete(long id) {
        if (_log.isDebugEnabled()) {
            _log.debug("Deleting entity for ID: " + id);
        }
        delete(getDao().retrieve(id));
    }

    @Transactional
    public List<E> getAll() {
        _log.debug("Getting all enabled entities");
        return getDao().findAllEnabled();
    }

    @Transactional
    public List<E> getAllWithDisabled() {
        _log.debug("Getting all enabled and disabled entities");
        return getDao().findAll();
    }

    
    /**
     * Gets the DAO configured for the service instance.
     * @return The DAO object.
     */
    abstract protected BaseHibernateDAO<E> getDao();


    private static final Log _log = LogFactory.getLog(AbstractHibernateEntityService.class);
    private boolean _isAuditable;
    private Class<E> _parameterizedType;
}
