package org.nrg.framework.annotations;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface XnatModule {
    String value();
    String namespace() default "";
    String name() default "";
    String description() default "";
    String beanName() default "";
    Class<?>[] config() default {};
    Class<?>[] targetType() default {};
}
