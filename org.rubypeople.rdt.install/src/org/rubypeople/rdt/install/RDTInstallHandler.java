package org.rubypeople.rdt.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.BaseInstallHandler;
import org.eclipse.update.core.ContentReference;
import org.eclipse.update.core.IPluginEntry;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.Site;
import org.eclipse.update.core.SiteManager;

public class RDTInstallHandler extends BaseInstallHandler {
	
//	private File logFile;

	public void installCompleted(boolean success) throws CoreException {
		warn("Install Completed. Feature is: " + feature.getVersionedIdentifier().toString());
		if (!success) return;
		if (Platform.getOS().equals(Platform.OS_WIN32)) return; // don't need to set an executable flag on windows		
		
		IPluginEntry pluginEntry = findEntry("org.jruby");
		if (pluginEntry == null) {
			warn("Was unable to find the plugin entry for org.jruby. Can't force executable on bin scripts.");
			return;
		}
		log("Found org.jruby plugin entry: " + pluginEntry.getVersionedIdentifier().toString());
		String pluginPath = getPluginPath(pluginEntry);
		if (pluginPath == null) {
			log("Unable to grab the path for the org.jruby plugin properly. We're screwed!");
			return;
		}
		ContentReference[] refs = feature.getFeatureContentProvider().getPluginEntryContentReferences(pluginEntry, monitor);
		for (int j = 0; j < refs.length; j++) {
			ContentReference contentReference = refs[j];
			if (contentReference.getPermission() != 0) {
				warn("found content reference that needs executable bit set: " + contentReference.getIdentifier());
				String pluginEntryPath = getPluginEntryPath(pluginPath, contentReference);	
				setExecutableBit(contentReference, pluginEntryPath);
			}
		}	
	}

	/**
	 * Iterates through the possible local sites, and returns the path to the jruby plugin on the filesystem if it exists within one of the sites.
	 * @param jrubyPluginEntry
	 * @return
	 */
	private String getPluginPath(IPluginEntry jrubyPluginEntry) {
		try {
			ILocalSite local = SiteManager.getLocalSite();
			log("Grabbed local site: " + local);
			IConfiguredSite[] sites = local.getCurrentConfiguration().getConfiguredSites();
			log("Grabbed " + sites.length + " configured sites.");
			for (int i = 0; i < sites.length; i++) {
				ISite site = sites[i].getSite();
				log(site.getURL().toString());
				try {
					URL newURL = new URL(site.getURL(), Site.DEFAULT_PLUGIN_PATH + jrubyPluginEntry.getVersionedIdentifier().toString());
					String pluginPath = newURL.getFile();
					File file = new File(pluginPath);
					if (file.exists()) {
						log("Got plugin path: " + pluginPath);
						return pluginPath;
					}
				} catch (MalformedURLException e) {
					log("MalformedURLException");
					log(e);
				}				
			}			
		} catch (CoreException e) {
			log("CoreException!");
			log(e);			
		}
		return null;
	}

	private IPluginEntry findEntry(String string) {
		IPluginEntry[] entries = this.feature.getPluginEntries();
		for (int i = 0; i < entries.length; i++) {
			String id = entries[i].getVersionedIdentifier().getIdentifier();
			if (id.equals(string)) return entries[i];
		}
		return null;
	}

	private String getPluginEntryPath(String pluginPath, ContentReference contentReference) {
		String contentKey = contentReference.getIdentifier();
		String pluginEntryPath = pluginPath;
		pluginEntryPath += pluginPath.endsWith(File.separator) ? contentKey : File.separator + contentKey;
		return pluginEntryPath;
	}

	private void log(String string) {
		warn(string);		
	}

	private void setExecutableBit(ContentReference reference, String filePath) {
		warn("Setting executable bit for: " + filePath);
		
		if (filePath == null) return;
		try {
			Process pr = Runtime.getRuntime().exec(new String[] { "chmod", "a+x", filePath }); //$NON-NLS-1$ //$NON-NLS-2$
			Thread chmodOutput = new StreamConsumer(pr.getInputStream());
			chmodOutput.setName("chmod output reader"); //$NON-NLS-1$
			chmodOutput.start();
			Thread chmodError = new StreamConsumer(pr.getErrorStream());
			chmodError.setName("chmod error reader"); //$NON-NLS-1$
			chmodError.start();
		} catch (IOException ioe) {
			log(ioe);
		}		
	}
	
	private void warn(String string) {
		File log = getLogFile();
		if (log == null || !log.exists()) return;
		FileWriter writer = null;
		try {
			writer = new FileWriter(log, true);
			writer.write(string + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// ignore
				e.printStackTrace();
			}
		}
	}	

	private File getLogFile() {
		return null;
		// HACK Uncomment this to set up logging to /tmp/rdt_install_handler.log in case we ever need to change this code and figure out what's going on.
//		if (logFile == null) {
//			logFile = new File("/tmp/rdt_install_handler.log");
//			try {
//				logFile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return logFile;
	}

	private void log(Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter pWriter = new PrintWriter(writer);
		e.printStackTrace(pWriter);
		warn(e.getMessage());
		warn(writer.toString());
		try {
			writer.close();
			pWriter.close();
		} catch (IOException e1) {
			// ignore
		}
	}

	public static class StreamConsumer extends Thread {
		InputStream is;
		byte[] buf;
		public StreamConsumer(InputStream inputStream) {
			super();
			this.setDaemon(true);
			this.is = inputStream;
			buf = new byte[512];
		}
		public void run() {
			try {
				int n = 0;
				while (n >= 0)
					n = is.read(buf);
			} catch (IOException ioe) {
			}
		}
	}

}
