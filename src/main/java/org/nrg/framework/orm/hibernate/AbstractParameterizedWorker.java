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
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        _parameterizedType = (Class<E>) parameterizedType.getActualTypeArguments()[0];
    }

    protected Class<E> getParameterizedType() {
        return _parameterizedType;
    }

    private final Class<E> _parameterizedType;
}
