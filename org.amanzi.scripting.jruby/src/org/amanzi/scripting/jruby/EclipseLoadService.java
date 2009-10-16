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
package org.amanzi.scripting.jruby;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.runtime.load.LoadService;

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
     * This code works within equinox to change URL's that point into bundles into full-path URL's.
     * This means ingoing URL's can have protocol 'bundleentry', and outgoing URL's should have
     * protocols like 'file', 'http' or 'jar.
     * 
     * @param loc URL pointing to equinox bundles
     * @return URL modified to use normal protocols
     */
    protected URL resolveClassPathURL(URL loc) {
        if (loc != null) {
            try {
                loc = FileLocator.resolve(loc);
            } catch (IOException e) {
            }
        }
        return loc;
    }
}
