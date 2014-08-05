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

import com.google.common.base.Joiner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.utilities.Reflection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

abstract public class AbstractHibernateEntityService<E extends BaseHibernateEntity, DAO extends BaseHibernateDAO<E>> extends AbstractParameterizedWorker<E> implements BaseHibernateService<E>, ApplicationContextAware, InitializingBean {

    public AbstractHibernateEntityService() {
        super();
        _isAuditable = HibernateUtils.isAuditable(getParameterizedType());
    }

    /**
     * Gets a new entity object, using the entity constructor matching the submitted parameters. If the entity class has
     * the method <b>setService()</b>, this will set the service instance on the entity. The service instance should
     * always be declared as {@link javax.persistence.Transient}.
     * @return A new entity object.
     */
    @Override
    public E newEntity(Object... parameters) {
        Class<?>[] types = null;
        try {
            if (parameters != null && parameters.length > 0) {
                List<Class<?>> buffer = new ArrayList<Class<?>>();
                for (Object parameter : parameters) {
                    buffer.add(parameter.getClass());
                }
                types = buffer.toArray(new Class<?>[buffer.size()]);
            }
            Constructor<E> constructor = Reflection.getConstructorForParameters(getParameterizedType(), types);
            if (constructor == null) {
                throw new NrgServiceRuntimeException(NrgServiceError.Instantiation, "No constructor available for the class " + getParameterizedType().getName() + " that matches the submitted signature: (" + displayTypes(types) + ")");
            }
            E instance = constructor.newInstance(parameters);
            try {
                Method method = getParameterizedType().getMethod("setService", AbstractHibernateEntityService.class);
                method.invoke(instance, this);
            } catch (NoSuchMethodException ignored) {
                // Ignore this here, it just may not have the method.
            }
            return instance;
        } catch (InvocationTargetException e) {
            throw new NrgServiceRuntimeException(NrgServiceError.Instantiation, e);
        } catch (IllegalAccessException e) {
            throw new NrgServiceRuntimeException(NrgServiceError.Instantiation, e);
        } catch (InstantiationException e) {
            throw new NrgServiceRuntimeException(NrgServiceError.Instantiation, e);
        }
    }

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
            entity.setDisabled(new Date());
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

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        _context = context;
    }

    /**
     * Wires up the appropriate repository class based on the parameterized type. When moving to Spring 4.0, this can be
     * replaced by autowired generics, see here: http://spring.io/blog/2013/12/03/spring-framework-4-0-and-java-generics.
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractHibernateDAO> daos = getContext().getBeansOfType(AbstractHibernateDAO.class);
        for (AbstractHibernateDAO dao : daos.values()) {
            if (isMatchingType(dao)) {
                _dao = (DAO) dao;
                break;
            }
        }
        if (_dao == null) {
            throw new NrgServiceRuntimeException(NrgServiceError.NoMatchingRepositoryForService, "Couldn't find a repository object for the NRG service " + getClass().getName());
        }
    }

    /**
     * Gets the DAO configured for the service instance.
     * @return The DAO object.
     */
    protected DAO getDao() {
        return _dao;
    }

    protected ApplicationContext getContext() {
        return _context;
    }

    private String displayTypes(final Class<?>[] types) {
        if (types == null) {
            return "Default constructor";
        }
        return Joiner.on(", ").join(types);
    }

    private static final Log _log = LogFactory.getLog(AbstractHibernateEntityService.class);

    private ApplicationContext _context;
    private DAO _dao;
    private boolean _isAuditable;
}
