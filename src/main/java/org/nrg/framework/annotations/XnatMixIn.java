package org.nrg.framework.annotations;

import java.lang.annotation.*;

/**
 * Associates a mix-in class with the annotated class. Mix-in classes provide a way to customize how instances of
 * associated classes are serialized by the Jackson object mapper framework. <a
 * href="http://www.baeldung.com/jackson-annotations">This article</a> describes using annotations to control JSON
 * serialization and how to use mix-in classes to apply this to other data types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface XnatMixIn {
    /**
     * Indicates the mix-in class to be used when serializing the annotated class.
     *
     * @return The mix-in class.
     */
    Class<?> value();
}
