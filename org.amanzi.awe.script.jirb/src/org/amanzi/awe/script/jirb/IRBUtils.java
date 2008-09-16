package org.amanzi.awe.script.jirb;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IRBUtils {
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

	/** try find jruby if jruby.home property was not set. Default to "." */
	public static String ensureJRubyHome(String jRubyHome) {
		if (jRubyHome == null) {
			for (String path : new String[] { ".", "C:/Program Files/JRuby",
					"/usr/lib/jruby", "/home/craig/dev/jruby-1.1.2" }) {
				try {
					if ((new java.io.File(path)).isDirectory()) {
						jRubyHome = path;
					}
				} catch (Exception e) {
					System.err
							.println("Failed to process possible JRuby path '"
									+ path + "': " + e.getMessage());
				}
			}
		}
		if (jRubyHome == null) {
			jRubyHome = ".";
		}
		return jRubyHome;
	}

	/** try determine ruby version jruby.version property was not set. Default to "1.8" */
	public static String ensureJRubyVersion(String jRubyHome, String jRubyVersion) {
		jRubyHome = ensureJRubyHome(jRubyHome);
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

	public static List<String> makeLoadPath() {
		// The following code finds the location of jruby on the computer and
        // makes sure the right loadpath is provided to the interpreter
        // The paths can be overridden by -Djruby.home and -Djruby.version
		String jRubyHome = IRBUtils.ensureJRubyHome(System.getProperty("jruby.home"));
		String jRubyVersion = IRBUtils.ensureJRubyVersion(jRubyHome, System.getProperty("jruby.version"));
        List<String> loadPath = new ArrayList<String>();
        loadPath.add(jRubyHome+"/lib/ruby/site_ruby/"+jRubyVersion);
        loadPath.add(jRubyHome+"/lib/ruby/site_ruby");
        loadPath.add(jRubyHome+"/lib/ruby/"+jRubyVersion);
        loadPath.add(jRubyHome+"/lib/ruby/"+jRubyVersion+"/java");
        loadPath.add(jRubyHome+"/lib");
        loadPath.add("lib/ruby/"+jRubyVersion);
        loadPath.add(".");
		return loadPath;
	}

}
