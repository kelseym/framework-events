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

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;

public interface BaseHibernateDAO<E extends BaseHibernateEntity> {

    public abstract void setSessionFactory(SessionFactory factory);

    public abstract Serializable create(E entity);

    public abstract E retrieve(long id);

    public abstract void update(E entity);

    public abstract void delete(E entity);

    public abstract List<E> findAll();

    public abstract List<E> findAllEnabled();

    public abstract E findById(long id);

    public abstract E findById(long id, boolean lock);

    public abstract E findEnabledById(long id);
    
    public abstract E findEnabledById(long id, boolean lock);
    
    public abstract List<E> findByExample(E exampleInstance, String[] excludeProperty);
    
    public abstract void refresh(boolean initialize, E entity);

}