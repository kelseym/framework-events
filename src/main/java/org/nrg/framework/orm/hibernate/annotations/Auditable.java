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

import org.hibernate.envers.Audited;
import org.nrg.framework.orm.hibernate.BaseHibernateEntity;

/**
 * @deprecated Use the Hibernate Envers {@link Audited} annotation instead.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Deprecated
public @interface Auditable {

}
