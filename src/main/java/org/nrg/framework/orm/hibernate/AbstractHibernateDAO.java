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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.nrg.framework.generics.AbstractParameterizedWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
abstract public class AbstractHibernateDAO<E extends BaseHibernateEntity> extends AbstractParameterizedWorker<E> implements BaseHibernateDAO<E> {

    public static final String DEFAULT_CACHE_REGION = "nrg";

    protected AbstractHibernateDAO() {
        super();
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _cacheRegion = extractCacheRegion(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
    }

    protected AbstractHibernateDAO(Class<E> clazz) {
        super(clazz);
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _cacheRegion = extractCacheRegion(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
    }

    protected AbstractHibernateDAO(SessionFactory factory) {
        if (_log.isDebugEnabled()) {
            _log.debug("Adding session factory in constructor: " + factory.hashCode());
        }
        _factory = factory;
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
        _cacheRegion = extractCacheRegion(getParameterizedType());
        _addDistinctRootEntity = HibernateUtils.hasEagerlyFetchedCollection(getParameterizedType());
    }

    /**
     * @see BaseHibernateDAO#setSessionFactory(org.hibernate.SessionFactory)
     */
    @Override
    public void setSessionFactory(SessionFactory factory) {
        if (_log.isDebugEnabled()) {
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
    public List<E> findAllEnabled() {
        Criteria criteria = getCriteriaForType();
        criteria.add(Restrictions.eq("enabled", true));
        return criteria.list();
    }

    @Override
    public long countAll() {
        Criteria criteria = getCriteriaForType();
        criteria.setProjection(Projections.rowCount());
        return (long) criteria.uniqueResult();
    }

    @Override
    public long countAllEnabled() {
        Criteria criteria = getCriteriaForType();
        criteria.setProjection(Projections.rowCount());
        criteria.add(Restrictions.eq("enabled", true));
        return (long) criteria.uniqueResult();
    }

    /**
     * @see BaseHibernateDAO#findByExample(BaseHibernateEntity, String[])
     */
    @Override
    public List<E> findByExample(E exampleInstance, String[] excludeProperty) {
        Criteria criteria = getCriteriaForType();
        if (_isAuditable) {
            exampleInstance.setEnabled(true);
        }
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            if (!_isAuditable || !exclude.equals("enabled")) {
                example.excludeProperty(exclude);
            }
        }
        criteria.add(example);
        return criteria.list();
    }

    /**
     * @see BaseHibernateDAO#findByExample(BaseHibernateEntity, String[])
     */
    @Override
    public List<E> findAllByExample(E exampleInstance, String[] excludeProperty) {
        Criteria criteria = getCriteriaForType();
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        criteria.add(example);
        return criteria.list();
    }

    @Override
    public List<E> findByProperty(final String property, final Object value) {
        Criteria criteria = getCriteriaForType();
        criteria.add(Restrictions.eq(property, value));
        if (_isAuditable) {
            criteria.add(Restrictions.eq("enabled", true));
        }
        final List list = criteria.list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return (List<E>) list;
        }
    }

    @Override
    public List<E> findByProperties(final Map<String, Object> properties) {
        Criteria criteria = getCriteriaForType();
        for (final String property : properties.keySet()) {
            final Object value = properties.get(property);
            criteria.add(Restrictions.eq(property, value));
        }
        if (_isAuditable) {
            criteria.add(Restrictions.eq("enabled", true));
        }
        final List list = criteria.list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return (List<E>) list;
        }
    }

    @Override
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

    /**
     * @see BaseHibernateDAO#refresh(boolean, BaseHibernateEntity)
     */
    @Override
    public void refresh(boolean initialize, E entity) {
        getSession().refresh(entity);
        if (initialize) {
            Hibernate.initialize(entity);
        }
    }

    @Override
    public List<Number> getRevisions(final long id) {
        return getAuditReader().getRevisions(getParameterizedType(), id);
    }

    @Override
    public E getRevision(final long id, final Number revision) {
        return getAuditReader().find(getParameterizedType(), id, revision);
    }

    /**
     * Returns the current Hibernate session object.
     *
     * @return The current Hibernate session object if available.
     */
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
     * @param criterion    The criteria on which you want to search.
     * @return All entities matching the submitted criteria.
     */
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
     *
     * @return An initialized {@link Criteria Criteria object}.
     */
    protected Criteria getCriteriaForType() {
        Criteria criteria = getSession().createCriteria(getParameterizedType(), StringUtils.uncapitalize(getParameterizedType().getSimpleName()));
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
     *
     * @param criteria    The {@link Criteria} object to which the restriction should be added.
     * @param name        The name of the property.
     * @param value       The value of the property. May be null.
     */
    protected void addNullableCriteria(Criteria criteria, String name, Object value) {
        if (value == null) {
            criteria.add(Restrictions.isNull(name));
        } else {
            criteria.add(Restrictions.eq(name, value));
        }
    }

    protected String getCacheRegion() {
        return _cacheRegion;
    }

    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(getSession());
    }

    private String extractCacheRegion(Class<E> type) {
        return type.isAnnotationPresent(org.hibernate.annotations.Cache.class)
               ? type.getAnnotation(org.hibernate.annotations.Cache.class).region()
               : DEFAULT_CACHE_REGION;
    }

    private static final Logger _log = LoggerFactory.getLogger(AbstractHibernateDAO.class);

    @Inject
    private SessionFactory _factory;

    private final boolean _isAuditable;
    private final String _cacheRegion;
    private final boolean _addDistinctRootEntity;
}
