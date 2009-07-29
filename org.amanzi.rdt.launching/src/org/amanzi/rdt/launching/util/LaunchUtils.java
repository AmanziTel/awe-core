package org.amanzi.rdt.launching.util;

import org.amanzi.rdt.internal.launching.IAweLaunchConstants;
import org.amanzi.rdt.internal.launching.launcher.RubyLaunchShortcut;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rubypeople.rdt.core.IRubyScript;

/**
 * Utility class that provides methods for launching
 * 
 * @author Lagutko_N
 *
 */

public class LaunchUtils {
	
	/**
	 * Launch RubyScript from class
	 * 
	 * @param script RubyScript class
	 */
	
	public static void launchRubyScript(IRubyScript script) {
		StructuredSelection selection = new StructuredSelection(script);
			
		RubyLaunchShortcut shortcut = new RubyLaunchShortcut();
		shortcut.launch(selection, IAweLaunchConstants.RUN_MODE);		
	}
	
	/**
	 * Launch RubyScript from file
	 * 
	 * @param path path to script
	 */
	
	public static void launchRubyScript(String path) {
		RubyLaunchShortcut shortcut = new RubyLaunchShortcut();
		shortcut.launch(path);
	}

}
