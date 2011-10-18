/**
 * AbstractParameterizedType
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Oct 11, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.nrg.framework.orm.hibernate.exceptions.InvalidDirectParameterizedClassUsageException;

/**
 * 
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
abstract public class AbstractParameterizedWorker<E extends BaseHibernateEntity> {

    @SuppressWarnings("unchecked")
    protected AbstractParameterizedWorker() {
        ParameterizedType parameterizedType = null;
        Class<?> clazz = getClass();
        while(parameterizedType == null) {
            Type superclass = clazz.getGenericSuperclass();
            if (superclass == null) {
                throw new RuntimeException("Can't find superclass as parameterized type!");
            }
            if (superclass instanceof ParameterizedType) {
                parameterizedType = (ParameterizedType) superclass;
                if (parameterizedType.getActualTypeArguments()[0] instanceof TypeVariable) {
                    throw new InvalidDirectParameterizedClassUsageException("When using a parameterized worker directly (i.e. with a generic subclass), you must call the AbstractParameterizedWorker constructor that takes the parameterized type directly.");
                }
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        _parameterizedType = (Class<E>) parameterizedType.getActualTypeArguments()[0];
    }

    protected AbstractParameterizedWorker(Class<E> clazz) {
        _parameterizedType = clazz;
    }

        protected Class<E> getParameterizedType() {
        return _parameterizedType;
    }

    private final Class<E> _parameterizedType;
}