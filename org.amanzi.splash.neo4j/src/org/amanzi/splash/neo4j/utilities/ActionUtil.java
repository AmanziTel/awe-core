package org.amanzi.splash.neo4j.utilities;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class that provides running actions from Display
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
		this.display = PlatformUI.getWorkbench().getDisplay();
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
	    if (async) {	        
	        display.asyncExec(task);
	    }
	    else {
	        display.syncExec(task);
	    }
	}
	
	/**
	 * Runs a task that must return a Result
	 *
	 * @param task task for running
	 * @return result
	 */
	public Object runTaskWithResult(RunnableWithResult task) {
	    display.syncExec(task);
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
