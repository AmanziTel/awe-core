package org.amanzi.splash.neo4j.utilities;

import java.util.HashMap;

import org.eclipse.swt.widgets.Display;

public class ActionUtil {
	
	private Display display;
	
	private static HashMap<Display, ActionUtil> actionUtils = new HashMap<Display, ActionUtil>();
	
	protected ActionUtil(Display display) {
		this.display = display;
	}

	public static ActionUtil getInstance(Display display) {
		ActionUtil util = actionUtils.get(display);
		if (util == null) {
			util = new ActionUtil(display);
			actionUtils.put(display, util);
		}
		
		return util;
	}
	
	public void runTask(Runnable task) {
		display.asyncExec(task);
	}
	
}
