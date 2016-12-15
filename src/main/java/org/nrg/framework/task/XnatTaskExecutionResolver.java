package org.nrg.framework.task;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface XnatTask.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XnatTaskExecutionResolver {
	
	/** The class. */
	String CLASS = "class";
	
	/** The resolver id. */
	String RESOLVER_ID = "taskId";
	
	/** The description. */
	String DESCRIPTION = "description";
	
	/**
	 * Resolver id.
	 *
	 * @return the string
	 */
	String resolverId();
	
	/**
	 * Description.
	 *
	 * @return the string
	 */
	String description();
	
}
