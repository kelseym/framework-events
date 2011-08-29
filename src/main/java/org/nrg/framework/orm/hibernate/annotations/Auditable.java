/**
 * Auditable
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on Aug 29, 2011 by Rick Herrick <rick.herrick@wustl.edu>
 */
package org.nrg.framework.orm.hibernate.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.nrg.framework.orm.hibernate.BaseHibernateEntity;


/**
 * This annotation indicates that the associated {@link BaseHibernateEntity entity} is auditable.
 * This means that the type will not actually be deleted upon a delete operation, but instead will
 * be disabled via calling the {@link BaseHibernateEntity#setEnabled(boolean)} with a value of
 * <b>false</b>.
 * @author Rick Herrick <rick.herrick@wustl.edu>
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Auditable {

}
