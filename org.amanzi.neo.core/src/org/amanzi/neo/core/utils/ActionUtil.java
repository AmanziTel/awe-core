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
package org.amanzi.neo.core.utils;

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
	public Object runTaskWithResult(RunnableWithResult task) {
	    if (display != null) {
            display.syncExec(task);
        } else {
            task.run();
        }
	    return task.getValue();
	}
	
	/**
	 * Interface for Task that must return a Result
	 * 
	 * @author Lagutko_N
	 */
	public interface RunnableWithResult extends Runnable {
	    
	    /**
	     * Computed result
	     *
	     * @return result of this task
	     */
	    public Object getValue();
	    
	}
}
