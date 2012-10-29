/**
 * PintoApplication
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/29/12 by rherri01
 */
package org.nrg.framework.pinto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies an application as one that uses pinto beans for parameter processing.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RUNTIME)
public @interface PintoApplication {
    /**
     * Provides the application name.
     * @return The application name.
     */
    public String value();

    /**
     * Provides any required application copyright information.
     * @return Application copyright information.
     */
    public String copyright() default "";

    /**
     * Provides a introductory help paragraph.
     * @return Introductory help paragraph.
     */
    public String introduction() default "";
}
