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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

abstract public class AbstractHibernateDAO<E extends BaseHibernateEntity> extends AbstractParameterizedWorker<E> implements BaseHibernateDAO<E> {
    protected AbstractHibernateDAO()
    {
        super();
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
    }

    protected AbstractHibernateDAO(Class<E> clazz)
    {
        super(clazz);
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
    }

    protected AbstractHibernateDAO(SessionFactory factory)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Adding session factory in constructor: " + factory.hashCode());
        }
        _factory = factory;
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
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
        // TODO: Setting all of these things would be best done in an EntityListener class, but that doesn't work for some reason.
        Date now = new Date();
        entity.setCreated(now);
        entity.setTimestamp(now);
        entity.setEnabled(true);
        entity.setDisabled(new Date(0));
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
        // TODO: When JPA persistence lifecycle support is working, remove explicit timestamp update.
        entity.setTimestamp(new Date());
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
        Criteria criteria = getCriteriaForType();
        criteria.add(Restrictions.eq("enabled", true));
        return criteria.list();
    }

   /**
     * @see BaseHibernateDAO#findByExample(BaseHibernateEntity, String[])
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<E> findByExample(E exampleInstance, String[] excludeProperty) {
        Criteria criteria = getCriteriaForType();
        Example example =  Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        criteria.add(example);
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<E> findByProperty(final String property, final Object value) {
        Criteria criteria = getCriteriaForType();
        criteria.add(Restrictions.eq(property, value));
        if (criteria.list().size() == 0) {
            return null;
        } else {
            return (List<E>) criteria.list();
        }
    }

    @SuppressWarnings("unchecked")
    public E findByUniqueProperty(final String property, final Object value) {
        List<E> matches = findByProperty(property, value);
        if (matches != null && matches.size() > 1) {
            throw new RuntimeException("The specified property " + property + " is not a unique constraint!");
        }
        return matches != null ? matches.get(0) : null;
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
        Criteria criteria = getCriteriaForType();
        for (Criterion c : criterion) {
            criteria.add(c);
        }
        return criteria.list();
    }

    /**
     * Gets a {@link Criteria Criteria object} for the parameterized type of the concrete definition. Default standard
     * values are set for the criteria object, including {@link Criteria#setCacheable(boolean)} set to <b>true</b>.
     * @return An initialized {@link Criteria Criteria object}.
     */
    protected Criteria getCriteriaForType() {
        Criteria criteria = getSession().createCriteria(getParameterizedType());
        criteria.setCacheable(true);
        criteria.setCacheRegion(getCacheRegion());
        if (_addDistinctRootEntity) {
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }
        return criteria;
    }

    /**
     * Add a {@link Restrictions} {@link Criterion criterion} to the {@link Criteria} object for the name/value pair. If
     * the value is null, the criterion is set to {@link Restrictions#isNull(String)} for the indicated name, otherwise
     * it's set to the given value.
     * @param c        The {@link Criteria} object to which the restriction should be added.
     * @param name     The name of the property.
     * @param value    The value of the property. May be null.
     */
    protected void addNullableCriteria(Criteria c, String name, Object value) {
        if(value == null){
            c.add(Restrictions.isNull(name));
        } else {
            c.add(Restrictions.eq(name, value));
        }
    }

    private static final Log _log = LogFactory.getLog(AbstractHibernateDAO.class);

    @Inject
    private SessionFactory _factory;

    private final boolean _isAuditable;
    private final boolean _addDistinctRootEntity;
}
