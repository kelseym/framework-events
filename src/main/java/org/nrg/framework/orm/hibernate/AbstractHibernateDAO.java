/**
 * AbstractHibernateDAO
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sets the base class for notification service DAOs. A basic DAO for any entity type can
 * be easily created just by extending this class, setting the entity class as the parameterized
 * type.
 * 
 * <b>Note:</b> This uses some handy convenience functions that are described in details on the
 * <a href=http://community.jboss.org/wiki/GenericDataAccessObjects">JBoss wiki</a>.
 *
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
abstract public class AbstractHibernateDAO<E extends BaseHibernateEntity> extends AbstractParameterizedWorker<E> implements BaseHibernateDAO<E> {
    protected AbstractHibernateDAO()
    {
        super();
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
    }

    protected AbstractHibernateDAO(Class<E> clazz)
    {
        super(clazz);
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
    }
    
    protected AbstractHibernateDAO(SessionFactory factory)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Adding session factory in constructor: " + factory.hashCode());
        }
        _factory = factory;
    }

    /**
     * @see BaseHibernateDAO#setSessionFactory(org.hibernate.SessionFactory)
     */
    @Override
    public void setSessionFactory(SessionFactory factory)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Setting session factory in setter: " + factory.hashCode());
        }
        _factory = factory;
    }

    /**
     * @see BaseHibernateDAO#create(BaseHibernateEntity)
     */
    @Override
    public Serializable create(E entity) {
        // TODO: When JPA persistence lifecycle support is working, remove these.
        Date created = new Date();
        entity.setCreated(created);
        entity.setTimestamp(created);
        return getSession().save(entity);
    }

    /**
     * @see BaseHibernateDAO#retrieve(long)
     */
    @Override
    @SuppressWarnings("unchecked")
    public E retrieve(long id) {
        if (_isAuditable) {
            return findEnabledById(id);
        }
        return (E) getSession().get(getParameterizedType(), id);
    }

    /**
     * @see BaseHibernateDAO#update(BaseHibernateEntity)
     */
    @Override
    public void update(E entity) {
        // TODO: When JPA persistence lifecycle support is working, remove these.
        Date updated = new Date();
        entity.setTimestamp(updated);
        getSession().update(entity);
    }

    /**
     * @see BaseHibernateDAO#delete(BaseHibernateEntity)
     */
    @Override
    public void delete(E entity) {
        if (_isAuditable) {
            entity.setEnabled(false);
            entity.setDisabled(new Date());
            getSession().update(entity);
        } else {
            getSession().delete(entity);
        }
    }

    /**
     * @see BaseHibernateDAO#findAll()
     */
    @Override
    public List<E> findAll() {
        return findByCriteria();
    }
 
    /**
     * @see BaseHibernateDAO#findAllEnabled()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<E> findAllEnabled() {
        Criteria criteria = getSession().createCriteria(getParameterizedType());
        criteria.add(Restrictions.eq("enabled", true));
        return criteria.list();
    }

   /**
     * @see BaseHibernateDAO#findByExample(BaseHibernateEntity, String[])
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<E> findByExample(E exampleInstance, String[] excludeProperty) {
        Criteria crit = getSession().createCriteria(getParameterizedType());
        Example example =  Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }
 
    /**
     * @see BaseHibernateDAO#findById(long)
     */
    @Override
    public E findById(long id) {
        return findById(id, false);
    }

    /**
     * @see BaseHibernateDAO#findById(long, boolean)
     */
    @Override
    @SuppressWarnings("unchecked")
    public E findById(long id, boolean lock) {
        E entity;
        if (lock)
            entity = (E) getSession().load(getParameterizedType(), id, LockOptions.UPGRADE);
        else
            entity = (E) getSession().load(getParameterizedType(), id);
 
        return entity;
    }
 
    /**
     * @see BaseHibernateDAO#findEnabledById(long)
     */
    @Override
    public E findEnabledById(long id) {
        return findEnabledById(id, false);
    }
    
    /**
     * @see BaseHibernateDAO#findEnabledById(long, boolean)
     */
    @Override
    public E findEnabledById(long id, boolean lock) {
        E entity = findById(id, lock);
        return entity != null && entity.isEnabled() ? entity : null;
    }

    @Override
    public void refresh(boolean initialize, E entity) {
        getSession().refresh(entity);
        if (initialize) {
            Hibernate.initialize(entity);
        }
    }

    protected Session getSession() {
        try {
            return _factory.getCurrentSession();
        } catch (HibernateException exception) {
            _log.error("Trying to get session for parameterized type: " + getParameterizedType(), exception);
            throw exception;
        }
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    protected List<E> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getParameterizedType());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    private static final Log _log = LogFactory.getLog(AbstractHibernateDAO.class);

    @Autowired
    private SessionFactory _factory;
    
    private boolean _isAuditable;
}
