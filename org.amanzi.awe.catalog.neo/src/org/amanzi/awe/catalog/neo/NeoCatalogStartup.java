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
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IStartup;

/**
 * TODO Purpose of
 * <p>
 * 
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NeoCatalogStartup implements IStartup {
	private static Logger LOGGER = Logger.getLogger(NeoCatalogStartup.class);

	@Override
	public void earlyStartup() {
		try {
			String databaseLocation = DatabaseManagerFactory.getDatabaseManager().getLocation();
			// TODO: old event

			ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
			URL url = new URL("file://" + databaseLocation);
			List<IService> services = CatalogPlugin.getDefault()
					.getServiceFactory().createService(url);
			for (IService service : services) {
				if (catalog.getById(IService.class, service.getID(),
						new NullProgressMonitor()) != null) {
					catalog.replace(service.getID(), service);
				} else {
					catalog.add(service);
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Could not create database location URL.", e);
		}
	}

}
