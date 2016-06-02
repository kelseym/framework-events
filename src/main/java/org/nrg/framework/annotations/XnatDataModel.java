package org.nrg.framework.annotations;

import java.lang.annotation.*;

/**
 * Informs XFT of data types to be loaded.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface XnatDataModel {
    /**
     * Indicates the name of the schema. If your schema follows the standard XNAT naming pattern of
     * <b>schemas/<i>name</i>/<i>name</i>.xsd</b>, then this value only needs to specify the value for <i>name</i>. If
     * the name of the XSD file differs from the name of the containing folder, then you must specify the full path,
     * e.g. schemas/one/two.xsd.
     *
     * @return The name of the schema file that defines the data model.
     */
    String value();

    /**
     * Lists the elements provided by the data model.
     *
     * @return Any data elements provided by the data model that should be provisioned.
     */
    XnatDataModelElement[] elements() default {};
}
