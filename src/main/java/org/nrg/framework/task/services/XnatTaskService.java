/*
 * framework: org.nrg.framework.task.services.XnatTaskService
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.task.services;

import java.util.List;

import org.nrg.framework.task.XnatTaskExecutionResolverI;

/**
 * The Interface XnatTaskService.
 */
public interface XnatTaskService {
	
	/**
	 * Should this process run the task?.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	boolean shouldRunTask(Class<?> clazz);
	
	/**
	 * Record task run information.
	 *
	 * @param clazz the clazz
	 */
	void recordTaskRun(Class<?> clazz);
	
	/**
	 * Gets the resolver for task.
	 *
	 * @param clazz the clazz
	 * @return the resolver for task
	 */
	XnatTaskExecutionResolverI getResolverForTask(Class<?> clazz);
	
	/**
	 * Gets the configuration elements yaml.
	 *
	 * @param clazz the clazz
	 * @return the configuration elements yaml
	 */
	List<String> getConfigurationElementsYaml(Class<?> clazz);
	
}
