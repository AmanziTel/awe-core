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
package org.amanzi.awe.catalog.neo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Neo Catalog Plugin
 * </p>
 * 
 * @author Bondoronok_p
 */
public class NeoCatalogPlugin extends AbstractProviderPlugin {

    private static final String FILE_PREFIX = "file://";

    private static final String PLUGIN_ID = "org.amanzi.awe.catalog.neo";

    /**
     * Plugin variable
     */
    static private NeoCatalogPlugin plugin;

    /**
     * Constructor for SplashPlugin.
     */
    public NeoCatalogPlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static NeoCatalogPlugin getDefault() {
        return plugin;
    }

    public String getDatabaseLocation() {
        return DatabaseManagerFactory.getDatabaseManager().getLocation().replace(" ", "%20");
    }

    /**
     * Returns Map service
     * 
     * @return
     * @throws MalformedURLException
     */
    public IService getMapService() throws MalformedURLException {
        String databaseLocation = getDatabaseLocation();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = new URL(FILE_PREFIX + databaseLocation);
        ID id = new ID(url);
        IService curService = catalog.getById(IService.class, id, null);
        return curService;
    }

    public IService createService(final URL url) {
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        ID id = new ID(url);
        IService curService = catalog.getById(IService.class, id, null);
        curService = CatalogPlugin.getDefault().getServiceFactory().createService(url).get(0);
        updateMapServices();

        return curService;
    }

    public void updateMapServices() {
        String databaseLocation = getDatabaseLocation();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = null;
        try {
            url = new URL(FILE_PREFIX + databaseLocation);
        } catch (MalformedURLException e) {
            getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "Error while set url", e));
        }
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        for (IService service : services) {
            if (catalog.getById(IService.class, service.getID(), new NullProgressMonitor()) != null) {
                catalog.replace(service.getID(), service);
            } else {
                catalog.add(service);
            }
        }
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
