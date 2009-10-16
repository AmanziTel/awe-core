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
package org.amanzi.rdt.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

/**
 * Utility class for working with Ruby Console
 * 
 * @author Lagutko_N
 *
 */

public class ConsoleRuntime {
	
	/**
	 * Name of Process Type attribute
	 */
	private static final String PROCESS_TYPE_ATTRIBUTE = "processType";
	
	/**
	 * ID of ConsoleLineTracker Extension Point
	 * 
	 */
	private static final String CONSOLE_LINE_TRACKER_ID = "org.eclipse.debug.ui.consoleLineTrackers";
	
	/**
	 * Returns all ConsoleLineTrackes by given process name
	 * 
	 * @param processType name of process
	 * @return array of ConsoleLineTrackers
	 */

	public static IConsoleLineTracker[] getConsoleLineTrackers(String processType) {
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(CONSOLE_LINE_TRACKER_ID); //$NON-NLS-1$
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
		MultiStatus status= new MultiStatus(LaunchingPlugin.getUniqueIdentifier(), IStatus.OK, LaunchingMessages.RubyRuntime_exceptionOccurred, null); 
		IConsoleLineTracker[] trackers = new IConsoleLineTracker[configs.length];
		
		for (int i= 0; i < configs.length; i++) {
			try {
				String processTypeValue = configs[i].getAttribute(PROCESS_TYPE_ATTRIBUTE);
				if (processTypeValue.equals(processType)) {
					IConsoleLineTracker lineTracker = (IConsoleLineTracker)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
					trackers[i] = lineTracker;
				}
			} catch (CoreException e) {
				status.add(e.getStatus());
			}
		}
		if (!status.isOK()) {
			//only happens on a CoreException
			LaunchingPlugin.log(status);
			//cleanup null entries in fgVMTypes
			List<IConsoleLineTracker> temp= new ArrayList<IConsoleLineTracker>(trackers.length);
			for (int i = 0; i < trackers.length; i++) {
				if(trackers[i] != null) {
					temp.add(trackers[i]);
				}
				trackers = new IConsoleLineTracker[temp.size()];
				trackers = temp.toArray(trackers);
			}
		}
		
		return trackers;
	}
	
}
