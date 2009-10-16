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
package org.amanzi.rdt.internal.launching.launcher;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.rdt.internal.launching.IAweLaunchConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.launcher.RubyApplicationShortcut;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;

/**
 * Class that creates launch configuration for this given configuration type
 * 
 * @author Lagutko_N
 */

public class RubyLaunchShortcut extends RubyApplicationShortcut {
	
	/**
	 * Returns type of LaunchConfiguration
	 * 
	 */
	
	protected ILaunchConfigurationType getRubyLaunchConfigType()
	{
		//override method from RubyApplicationShortcut to use LaunchConfiguration that we need
		return getLaunchManager().getLaunchConfigurationType(IAweLaunchConstants.ID_RUBY_SCRIPT);
	}
	
	public void launch(String fileName) {
		try {
			ILaunchConfiguration config = findOrCreateLaunchConfiguration(fileName, IAweLaunchConstants.RUN_MODE);
			if (config != null)
			{
				DebugUITools.launch(config, IAweLaunchConstants.RUN_MODE);
			}
		}
		catch (CoreException e) {
			log(e);
			IStatus status = e.getStatus();
			String title = RdtDebugUiMessages.Dialog_launchErrorTitle;
			String message = RdtDebugUiMessages.Dialog_launchErrorMessage;
			if (status != null)
			{
				ErrorDialog.openError(RdtDebugUiPlugin.getActiveWorkbenchWindow().getShell(), title, message, status);
			}
		}
	}
	
	/**
	 * Search Launch Configuration for given file and mode
	 * 
	 * @param fileName name of file
	 * @param mode mode
	 * @return founded or created LaunchConfiguration
	 * @throws CoreException
	 */
	
	protected ILaunchConfiguration findOrCreateLaunchConfiguration(String fileName, String mode) throws CoreException {
		IFile rubyFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
		ILaunchConfigurationType configType = getRubyLaunchConfigType();
		List<ILaunchConfiguration> candidateConfigs = null;

		ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
		candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
		for (int i = 0; i < configs.length; i++)
		{
			ILaunchConfiguration config = configs[i];
			boolean projectsEqual = config.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, "")
					.equals(rubyFile.getProject().getName());
			if (projectsEqual)
			{
				boolean projectRelativeFileNamesEqual = config.getAttribute(
						IRubyLaunchConfigurationConstants.ATTR_FILE_NAME, "").equals(
						rubyFile.getProjectRelativePath().toString());
				if (projectRelativeFileNamesEqual)
				{
					candidateConfigs.add(config);
				}
			}
		}

		switch (candidateConfigs.size())
		{
			case 0:
				return createConfiguration(rubyFile);
			case 1:
				return (ILaunchConfiguration) candidateConfigs.get(0);
			default:
				Status status = new Status(Status.WARNING, RdtDebugUiPlugin.PLUGIN_ID, 0,
						RdtDebugUiMessages.LaunchConfigurationShortcut_Ruby_multipleConfigurationsError, null);
				throw new CoreException(status);
		}
	}

}
