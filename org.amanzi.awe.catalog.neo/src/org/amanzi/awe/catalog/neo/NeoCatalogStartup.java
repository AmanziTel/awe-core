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
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
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
    public static final String FILE_PREFIX = "file://";

    @SuppressWarnings("unchecked")
    public NeoCatalogStartup() {
        EventManager.getInstance().addListener(EventsType.UPDATE_DATA, new UpdateDataHandling());
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
    }

}
