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

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.core.internal.CorePlugin;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.events.UpdateLayerEvent;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IStartup;

/**
 * <p>
 * catalog actions.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NeoCatalogStartup implements IStartup {
    private static Logger LOGGER = Logger.getLogger(NeoCatalogStartup.class);
    private static String OSM_WMS_ID = "wmt://localhost/wmt/net.refractions.udig.catalog.internal.wmt.wmtsource.OSMCloudMadeSource/1155";
    private static String URL_TO_CHECK_CONNETCTION = "www.openstreetmap.org";
    public static final String FILE_PREFIX = "file://";

    @SuppressWarnings("unchecked")
    public NeoCatalogStartup() {
        EventManager.getInstance().addListener(EventsType.UPDATE_DATA, new UpdateDataHandling());
        EventManager.getInstance().addListener(EventsType.UPDATE_LAYER, new UpdateLayerHandling());
    }

    /**
     * <p>
     * describe handling of update layer event. and response for update catalog layer
     * </p>
     * TODO should implement
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private class UpdateLayerHandling implements IEventsListener<UpdateLayerEvent> {

        @Override
        public void handleEvent(UpdateLayerEvent data) {
            try {
                String databaseLocation = DatabaseManagerFactory.getDatabaseManager().getLocation();
                ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
                URL url = new URL("file://" + databaseLocation);
                List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
                for (IService service : services) {
                    if (catalog.getById(IService.class, service.getID(), new NullProgressMonitor()) != null) {
                    } else {
                        catalog.add(service);
                    }
                }
            } catch (MalformedURLException e) {
                LOGGER.error("Could not create database location URL.", e);
            }
        }

        @Override
        public Object getSource() {
            return null;
        }

    }

    /**
     * <p>
     * describe handling of update data event. and response for update catalog info
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private class UpdateDataHandling implements IEventsListener<UpdateDataEvent> {

        @Override
        public void handleEvent(UpdateDataEvent data) {
            try {
                String databaseLocation = DatabaseManagerFactory.getDatabaseManager().getLocation();
                ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
                URL url = new URL(FILE_PREFIX + databaseLocation);
                List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
                for (IService service : services) {
                    if (catalog.getById(IService.class, service.getID(), new NullProgressMonitor()) != null) {
                        catalog.replace(service.getID(), service);
                    } else {
                        catalog.add(service);
                    }
                }
            } catch (MalformedURLException e) {
                LOGGER.error("Could not create database location URL.", e);
            }
        }

        @Override
        public Object getSource() {
            return null;
        }

    }

    @Override
    public void earlyStartup() {
        EventManager.getInstance().fireEvent(new UpdateDataEvent());
        addDefaultLayer();

    }

    /**
     * added default OpenStreetMaps layer
     */
    private void addDefaultLayer() {
        try {
            if (!isConnected()) {
                return;
            }
            IService curService;
            curService = getMapService();
            List<IGeoResource> resMap = new LinkedList<IGeoResource>();
            IMap map = ApplicationGIS.getActiveMap();

            if (map.getMapLayers().isEmpty() && !map.getLayerFactory().getLayers(curService).isEmpty()) {
                for (IGeoResource iGeoResource : curService.resources(null)) {
                    resMap.add(iGeoResource);
                }
                ApplicationGIS.addLayersToMap(map, resMap, 0);
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Could not create database location URL.", e);
        } catch (IOException e) {
            LOGGER.error("Error while try to add default layer on map", e);
        }
    }

    /**
     * try to check connection to Internet
     * 
     * @return
     */
    private Boolean isConnected() {
        try {
            InetAddress.getByName(URL_TO_CHECK_CONNETCTION);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }

    }

    /**
     * Get OSM service
     * 
     * @return IService
     * @throws MalformedURLException
     */
    private IService getMapService() throws MalformedURLException {
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = new URL(null, OSM_WMS_ID, CorePlugin.RELAXED_HANDLER);
        ID id = new ID(url);
        IService curService = catalog.getById(IService.class, id, null);
        if (curService == null) {
            curService = CatalogPlugin.getDefault().getServiceFactory().createService(url).get(0);
        }
        return curService;
    }
}
