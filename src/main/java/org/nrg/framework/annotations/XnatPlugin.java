/*
 * framework: org.nrg.framework.annotations.XnatPlugin
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.annotations;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Indicates that the annotated class is a plugin configuration class. When referenced within XNAT, this class will be
 * loaded as a Spring configuration class. Any packages referenced in the {@link #entityPackages()} attribute will be
 * passed onto the XNAT session factory to be scanned for persistent entity classes.
 */
@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface XnatPlugin {
    String PLUGIN_CLASS            = "class";
    String PLUGIN_ID               = "id";
    String PLUGIN_NAMESPACE        = "namespace";
    String PLUGIN_NAME             = "name";
    String PLUGIN_DESCRIPTION      = "description";
    String PLUGIN_BEAN_NAME        = "beanName";
    String PLUGIN_ENTITY_PACKAGES  = "entityPackages";

    /**
     * In combination with the {@link #namespace()} value, indicates the unique ID for this plugin.
     *
     * @return The ID for the plugin.
     */
    String value();

    /**
     * In combination with the {@link #value()} value, indicates the unique ID for this plugin.
     *
     * @return The namespace for the plugin.
     */
    String namespace() default "";

    /**
     * The readable name for this plugin.
     *
     * @return The name of the plugin.
     */
    String name();

    /**
     * A description for this plugin.
     *
     * @return The plugin description.
     */
    String description() default "";

    /**
     * The name to use as a bean name for this plugin when instantiated.
     *
     * @return The desired bean name.
     */
    String beanName() default "";

    /**
     * Any packages that should be scanned for persistent entity classes, e.g. Hibernate or JPA entities.
     *
     * @return A list of packages to be scanned.
     */
    String[] entityPackages() default {};

    /**
     * Defines all data models provided by the plugin.
     *
     * @return An array of data models provided by the plugin.
     */
    XnatDataModel[] dataModels() default {};
}
