package org.amanzi.rdt.launching.vm;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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

	public File detectInstallLocation() {
		try {
			Bundle bundle = Platform.getBundle("org.jruby");
			URL url = FileLocator.find(bundle, new Path(""), null);
			url = FileLocator.toFileURL(url);
			
			return new File(url.getPath()); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
