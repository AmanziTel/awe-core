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
package org.amanzi.rdt.launching;

import java.io.File;

import org.amanzi.rdt.console.RubyConsole;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.launching.AbstractRubyLaunchConfigurationDelegate;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;

/**
 * Launching Delegate for AWE run configuration
 * 
 * @author Lagutko_N
 *
 */

public class AweLaunchDelegate extends AbstractRubyLaunchConfigurationDelegate {
	
	/**
	 * Method that launch script with given configuration
	 * 
	 */

	public void launch(ILaunchConfiguration configuration, String mode, 
						ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		launch.setAttribute(IRubyLaunchConfigurationConstants.ATTR_USE_TERMINAL, IRubyLaunchConfigurationConstants.ID_RUBY_PROCESS_TYPE);
		RubyPlugin.getDefault().getWorkbench().saveAllEditors(true);
		
		RubyConsole console = new RubyConsole(configuration, launch, monitor);
		
		console.init(verifyVMInstall(configuration));
		
		IPath workingDir = getWorkingDirectoryPath(configuration);
		String scriptPath = null;
		if (workingDir != null) {			
			scriptPath = workingDir.toOSString() + File.separator + getFileToLaunch(configuration);			
		}
		else {
			scriptPath = verifyFileToLaunch(configuration); 
		}
		
		console.run(scriptPath);		
	}

}
