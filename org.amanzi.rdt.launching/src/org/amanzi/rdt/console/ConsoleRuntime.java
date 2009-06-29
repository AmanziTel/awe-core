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

public class ConsoleRuntime {
	
	private static final String PROCESS_TYPE_ATTRIBUTE = "processType";
	private static final String CONSOLE_LINE_TRACKER_ID = "org.eclipse.debug.ui.consoleLineTrackers";

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
