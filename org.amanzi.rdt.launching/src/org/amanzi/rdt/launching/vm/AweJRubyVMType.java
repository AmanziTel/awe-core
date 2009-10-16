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
package org.amanzi.rdt.launching.vm;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.amanzi.rdt.internal.launching.AweLaunchingPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.internal.launching.JRubyVMType;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

/**
 * Ruby Virtual Machine that works from org.jruby plugin
 * 
 * @author Lagutko_N
 *
 */

public class AweJRubyVMType extends JRubyVMType {

	private static final String ORG_JRUBY_PLUGIN = "org.jruby";

    public File detectInstallLocation() {
		try {
			Bundle bundle = Platform.getBundle(ORG_JRUBY_PLUGIN);
			URL url = FileLocator.find(bundle, new Path(""), null);
			url = FileLocator.toFileURL(url);
			
			return new File(url.getPath()); 
		} catch (IOException e) {
			AweLaunchingPlugin.log(null, e);
		}
		return null;
	}

	/**
	 * Starting in the specified VM install location, attempt to find the 'jruby' executable
	 * file.  If found, return the corresponding <code>File</code> object, otherwise return
	 * <code>null</code>.
	 */
	public static File findRubyExecutable(File vmInstallLocation) {
		// Try each candidate in order.  The first one found wins.  Thus, the order
		// of fgCandidateRubyLocations and fgCandidateRubyFiles is significant.		
		return vmInstallLocation;							
	}
	
	public IStatus validateInstallLocation(File rubyHome) {		
		return new Status(IStatus.OK, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_ok_2, null); 
	}
}
