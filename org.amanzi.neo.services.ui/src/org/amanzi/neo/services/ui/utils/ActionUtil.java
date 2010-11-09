/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.neo.services.ui.utils;

import org.amanzi.neo.services.utils.RunnableWithResult;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class that provides running actions from Display. Since the code that calls this also
 * runs in non-GUI unit and system tests, we support a fallback mechanism where if the workbench
 * cannot be created (headless tests), we run the runnables in the current thread instead.
 * 
 * @author Lagutko_N
 */
public class ActionUtil {
	
    /*
     * Display for running
     */
	private Display display;
	
	/*
	 * Instance of ActionUtil 
	 */
	private static ActionUtil util;
	
	/**
	 * Constructor.
	 * 
	 */
	protected ActionUtil() {
		try {
            this.display = PlatformUI.getWorkbench().getDisplay();
        } catch (RuntimeException e) {
            //We are probably running unit tests, log and error and continue
            System.err.println("Failed to get display: "+e);
        }
	}
	
	/**
	 * Returns an instance of ActionUtil 
	 *
	 * @return instance of ActionUtil
	 */

	public static ActionUtil getInstance() {
		if (util == null) {
			util = new ActionUtil();
		}
		
		return util;
	}
	
	/**
	 * Runs a Task
	 *
	 * @param task task for running
	 * @param async is this task must be ran asyncrhonically
	 */
	
	public void runTask(Runnable task, boolean async) {
	    if (display != null) {
            if (async) {
                display.asyncExec(task);
            } else {
                display.syncExec(task);
            }
        } else {
            task.run();
        }
	}
	
	/**
	 * Runs a task that must return a Result
	 *
	 * @param task task for running
	 * @return result
	 */
	public <T> T runTaskWithResult(RunnableWithResult<T> task) {
	    if (display != null) {
            display.syncExec(task);
        } else {
            task.run();
        }
	    return task.getValue();
	}
	
	/**
     * runs job with getting result
     * 
     * @param task RunnableWithResult
     * @return result;
     */
    public static <T> T runJobWithResult(final RunnableWithResult<T> task) {
        Job job = new Job(task.toString()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                task.run();
                return Status.OK_STATUS;
            }

        };
        job.schedule();
        try {
            job.join();
        } catch (InterruptedException e) {
            Logger.getLogger(ActionUtil.class).error(e.getLocalizedMessage(), e);
            return null;
        }
        return task.getValue();
    }

}
