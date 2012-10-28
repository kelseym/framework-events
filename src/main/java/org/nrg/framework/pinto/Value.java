/**
 * Value
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/26/12 by rherri01
 */
package org.nrg.framework.pinto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a method is the value for a command-line parameter, mapped by the short option to the parameter.
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RUNTIME)
public @interface Value {
    /**
     * Specifies the short option for this command-line parameter.
     * @return The value of the short option.
     */
    public String value();
}
