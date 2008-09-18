package org.amanzi.scripting.jruby;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This utility class has some static methods for investigating the environment
 * and finding appropriate settings for JRuby. Originally this class started
 * as shared code between the pure-swing and the swt-swing versions of the IRBConsole
 * but it has expanded to include directory searching for ideal locations for JRuby.
 * @author craig
 */
public class ScriptUtils {
	private static ScriptUtils instance = new ScriptUtils();
	private String jRubyHome = null;
	private String jRubyVersion = null;

	/**
	 * Utility to setup fonts for the swing console. This was taken directly from the
	 * relevant code in org.jruby.demo.IRBConsole.
	 * @param otherwise
	 * @param style
	 * @param size
	 * @param families
	 * @return
	 */
    public static Font findFont(String otherwise, int style, int size, String[] families) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Arrays.sort(fonts);
        Font font = null;
        for (int i = 0; i < families.length; i++) {
            if (Arrays.binarySearch(fonts, families[i]) >= 0) {
                font = new Font(families[i], style, size);
                break;
            }
        }
        if (font == null)
            font = new Font(otherwise, style, size);
        return font;
    }

    /**
     * Utility to find the best working location of JRuby. Set -Djruby.home
     * to add an explicit location to the beginning of the search path.
     * @return String path to JRuby installation (eg. "/usr/lib/jruby")
     */
    public static String getJRubyHome(){
    	return instance.ensureJRubyHome();
    }

    /**
     * Utility to find the version of ruby supported by the current JRuby location.
     * Set -Djruby.version to override this result.
     * @return String describing supported ruby version (eg. "1.8")
     */
    public static String getJRubyVersion(){
    	return instance.ensureJRubyVersion();
    }

    /**
     * Build a list of String paths to be added to the JRuby search paths
     * for finding ruby libraries. We use this when starting the interpreter
     * to ensure that any custom code we've written is found.
     * @param extras
     * @return
     */
    public static List<String> makeLoadPath(String[] extras) {
		return instance.doMakeLoadPath(extras);
	}

    /** Actually construct the list of load paths */ 
    private List<String> doMakeLoadPath(String[] extras) {
		// The following code finds the location of jruby on the computer and
        // makes sure the right loadpath is provided to the interpreter
        // The paths can be overridden by -Djruby.home and -Djruby.version
		ensureJRubyHome();
		ensureJRubyVersion();
        List<String> loadPath = new ArrayList<String>();
        if(extras!=null) for(String path:extras) loadPath.add(path);
        loadPath.add(jRubyHome+"/lib/ruby/site_ruby/"+jRubyVersion);
        loadPath.add(jRubyHome+"/lib/ruby/site_ruby");
        loadPath.add(jRubyHome+"/lib/ruby/"+jRubyVersion);
        loadPath.add(jRubyHome+"/lib/ruby/"+jRubyVersion+"/java");
        loadPath.add(jRubyHome+"/lib");
        loadPath.add("lib/ruby/"+jRubyVersion);
        loadPath.add(".");
		return loadPath;
	}

	/** return JRubyHome, searching for it if necessary */
	private String ensureJRubyHome() {
		if(jRubyHome==null){
			jRubyHome = ScriptUtils.findJRubyHome(System.getProperty("jruby.home"));
		}
		return(jRubyHome);
	}

	/** return JRubyVersion, searching for it if necessary */
	private String ensureJRubyVersion() {
		if(jRubyVersion==null){
			jRubyVersion = ScriptUtils.findJRubyVersion(ensureJRubyHome(),System.getProperty("jruby.version"));
		}
		return(jRubyVersion);
	}

	/** search for jruby home, starting with passed value, if any */
	private static String findJRubyHome(String suggested) {
		String jRubyHome = null;
		String userDir = System.getProperty("user.home");
		for (String path : new String[] { suggested,
				".",
				"C:/Program Files/JRuby",
				"/usr/lib/jruby",
				"/usr/local/lib/jruby",
				userDir+"/.jruby",
				userDir+"/dev/jruby-1.1.5",
				userDir+"/dev/jruby-1.1.4",
				userDir+"/dev/jruby-1.1.3",
				userDir+"/dev/jruby-1.1.2",
				userDir+"/dev/jruby-1.1.1",
				userDir+"/dev/jruby-1.1",
				userDir+"/dev/jruby-1.1RC1"
		}) {
			try {
				if ((new java.io.File(path+"/lib")).isDirectory() && (new java.io.File(path+"/lib/jruby.jar")).exists()) {
					jRubyHome = path;
					break;
				}
			} catch (Exception e) {
				System.err
						.println("Failed to process possible JRuby path '"
								+ path + "': " + e.getMessage());
			}
		}
		if (jRubyHome == null) {
			jRubyHome = ".";
		}
		return jRubyHome;
	}

	/** try determine ruby version jruby.version property was not set. Default to "1.8" */
	private static String findJRubyVersion(String jRubyHome, String jRubyVersion) {
		if (jRubyVersion == null) {
			for (String version : new String[] { "1.8", "1.9", "2.0", "2.1" }) {
				String path = jRubyHome + "/lib/ruby/" + version;
				try {
					if ((new java.io.File(path)).isDirectory()) {
						jRubyVersion = version;
						break;
					}
				} catch (Exception e) {
					System.err
							.println("Failed to process possible JRuby path '"
									+ path + "': " + e.getMessage());
				}
			}
		}
		if (jRubyVersion == null) {
			jRubyVersion = "1.8";
		}
		return jRubyVersion;
	}

}
