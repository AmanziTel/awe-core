package org.amanzi.scripting.jruby;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.runtime.load.LoadService;
import org.jruby.runtime.load.LoadServiceResource;

/**
 * This class represents a modification of the org.jruby.runtime.load.LoadService class.
 * That class follows a strict rule for finding resources to load based on the resource name,
 * the load path and the classpath. For working in an equinox environment, we need to add
 * classpath entries to the load path that include URL's of 'bundleentry' protocol. This
 * is not known in a non-eclipse (non-equinox or OSGi) environment, so these load paths
 * are never accessed by the normal LoadService process.
 * 
 * Here we override the critical method in the process for finding a file in the classpath,
 * and enable support for 'bundleentry' protocole URL's in the loadpath.
 * 
 * To use this, you need to add code like the following when you start Ruby:
 * <pre>
 *      RubyInstanceConfig config = new RubyInstanceConfig() {{
 *          setLoadServiceCreator(new LoadServiceCreator() {
 *              public LoadService create(Ruby runtime) {
 *                  return new EclipseLoadService(runtime);
 *              }
 *          });
 *      }};
 *      Ruby runtime = Ruby.newInstance(config);
 * </pre>
 * 
 * Then the EclipseLoadService will be used instead of the normal LoadService class.
 * 
 * @author craig
 * @since AWE 1.0
 */
public class EclipseLoadService extends LoadService {
	public EclipseLoadService(Ruby runtime) {
        super(runtime);
    }
	
    /**
     * This method is a direct copy of the same method in LoadService from JRuby 1.2.0.
     * It is important to keep this method up to date with enhancements to JRuby. Our edits
     * are commented with as 'AWE:'
     *
     * @param name the file to find, this is a path name
     * @return the correct file
     */
    private LoadServiceResource findFileInClasspath(String name) {
        // Look in classpath next (we do not use File as a test since UNC names will match)
        // Note: Jar resources must NEVER begin with an '/'. (previous code said "always begin with a /")
        ClassLoader classLoader = runtime.getJRubyClassLoader();

        // handle security-sensitive case
        if (Ruby.isSecurityRestricted() && classLoader == null) {
            classLoader = runtime.getInstanceConfig().getLoader();
        }

        for (Iterator pathIter = loadPath.getList().iterator(); pathIter.hasNext();) {
            String entry = pathIter.next().toString();

            // if entry starts with a slash, skip it since classloader resources never start with a /
            if (entry.charAt(0) == '/' || (entry.length() > 1 && entry.charAt(1) == ':')) continue;
            
            // otherwise, try to load from classpath (Note: Jar resources always uses '/')
            URL loc = classLoader.getResource(entry + "/" + name);
            
            // AWE: Lagutko, 29.06.2009, compute full path from bundle-dependent path
			if (loc != null) {
				try {
					loc = FileLocator.resolve(loc);
				} catch (IOException e) {
				}
			}

            // Make sure this is not a directory or unavailable in some way
            if (isRequireable(loc)) {
                return new LoadServiceResource(loc, loc.getPath());
            }
        }

        // if name starts with a / we're done (classloader resources won't load with an initial /)
        if (name.charAt(0) == '/' || (name.length() > 1 && name.charAt(1) == ':')) return null;
        
        // Try to load from classpath without prefix. "A/b.rb" will not load as 
        // "./A/b.rb" in a jar file.
        URL loc = classLoader.getResource(name);
        
        // AWE: Lagutko, 29.06.2009, compute full path from bundle-dependent path
		if (loc != null) {
			try {
				loc = FileLocator.resolve(loc);
			} catch (IOException e) {
			}
		}

        return isRequireable(loc) ? new LoadServiceResource(loc, loc.getPath()) : null;
    }

    /**
     * Directories and unavailable resources are not able to open a stream.
     * We needed to copy this from LoadService also, because it was private.
     */
    private boolean isRequireable(URL loc) {
        if (loc != null) {
        	if (loc.getProtocol().equals("file") && new java.io.File(loc.getFile()).isDirectory()) {
        		return false;
        	}
        	
        	try {
                loc.openConnection();
                return true;
            } catch (Exception e) {}
        }
        return false;
    }

}
