package org.nrg.framework.annotations;

import java.lang.annotation.*;

/**
 * Informs XFT of data types to be loaded.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface XnatDataModelElement {
    /**
     * Provides the fully qualified name of the data model element. This includes the namespace, e.g. foo:barData.
     *
     * @return The name of the element.
     */
    String value();

    /**
     * Indicates whether the data model element is secured by default. This defaults to true.
     *
     * @return Whether the data model element is secured by default.
     */
    boolean secured() default true;

    /**
     * Defines the default singular name for the data model element.
     *
     * @return The default singular name.
     */
    String singularName() default "";

    /**
     * Defines the default plural name for the data model element.
     *
     * @return The default plural name.
     */
    String pluralName() default "";
}
