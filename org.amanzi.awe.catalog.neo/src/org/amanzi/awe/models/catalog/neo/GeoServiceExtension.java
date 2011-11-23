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
package org.amanzi.awe.models.catalog.neo;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.URLUtils;

import org.amanzi.awe.catalog.neo.NeoService;
import org.amanzi.awe.startup.splash.Activator;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <p>
 * Currently copied from the previous implementation. MAybe need improvement.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class GeoServiceExtension implements ServiceExtension {
    private static final String NEOSTORE_DIR = "neostore";
    private static final String FILE_PROTOCOL = "file";
    /* Neo4J service key, URL to the Neo4J database and gis node */
    public static final String URL_KEY = "org.amanzi.awe.catalog.neo.url";
    public static final String CLASS_KEY = "org.amanzi.awe.catalog.neo.class";

    public Map<String, Serializable> createParams(URL url) {
        try {
            if (url.getProtocol().equals(FILE_PROTOCOL)) {
                // the URL represent a normal file or directory on disk
                File path = URLUtils.urlToFile(url);
                if (path.exists() && path.isDirectory()) {
                    // check the directory, does it contain a neo4j database
                    File neostore = new File(path, NEOSTORE_DIR);
                    if (neostore.exists()) {
                        Map<String, Serializable> params = new HashMap<String, Serializable>();
                        params.put(URL_KEY, url);
                        params.put(CLASS_KEY, URL.class);
                        return params;
                    }
                }
            }
        } catch (Throwable t) {
            // something went wrong, URL must be for another service
        }

        // unable to create the parameters, URL must be for another service
        return null;
    }

    public IService createService(URL id, Map<String, Serializable> params) {

        // sets job name for splash monitor
        try {
            IProgressMonitor monitor = Activator.getDefault().getSplashMonitor();
            if (monitor != null) {
                monitor.subTask("Initializing Amanzi Network Engine");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // good defensive programming
        if (params == null) {
            return null;
        }

        // check for the property service key
        if (params.containsKey(URL_KEY)) {
            // found it, create the service handle
            return new GeoService(params);
        }

        // key not found
        return null;
    }

}
