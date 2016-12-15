package org.nrg.framework.task;

/**
 * The Interface XnatTaskI.
 */
public interface XnatTaskI {
	
	/**
	 * Should run task.
	 *
	 * @return true, if successful
	 */
	public boolean shouldRunTask();
	
	/**
	 * Record task run.
	 */
	public void recordTaskRun();

}
