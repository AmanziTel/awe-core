package org.amanzi.awe.afp;

import org.amanzi.awe.console.AweConsolePlugin;

public class AfpEngine {
	
	private static AfpEngine instance;

	public static AfpEngine getAfpEngine() {
		// load the os specific fragment
		if(instance == null) {
			try {
				ClassLoader loader = AfpEngine.class.getClassLoader();
				Class<?> cls = null;
				try {
					cls = loader.loadClass("org.amanzi.awe.afp.engine.AfpEngineImpl");
				} catch (ClassNotFoundException e) {
				}
				instance = (AfpEngine) cls.newInstance();
			} catch (Exception e) {
				AweConsolePlugin.error("Unable to load Afp Engine.");
			}
		}
		return instance;
	}
	// return the complete path + filename to the engine
	public String getAfpEngineExecutablePath() {
		AweConsolePlugin.error("No OS specific Engine loaded.");
		return null;
	}
}
