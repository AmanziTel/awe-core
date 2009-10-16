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
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * This class represents a single collection of geographic objects in the Neo4j
 * database. In other words this class represents something that can be turned
 * into a single layer in the map.
 * 
 * @author craig
 * @since 1.0.0
 */
public class NeoGeoResource extends IGeoResource {
    // private URL identifierUrl;
    private String identifier;
	private NeoService service;
	private Node gisNode;
	private GeoNeo geoNeo;
    private URL identifierFull;

	public NeoGeoResource(NeoService service,
			org.neo4j.api.core.NeoService neo, Node gisNode) {
		this.service = service;
		this.gisNode = gisNode;
		this.geoNeo = new GeoNeo(neo, this.gisNode);

			URL serviceUrl = service.getIdentifier();
        identifier = serviceUrl + "#";// +
                                      // this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        identifierFull = this.getIdentifier();
	}

	@Override
	public URL getIdentifier() {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            URL url = new URL(identifier + this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            if (identifierFull == null || !url.equals(identifierFull)) {
                changeIconsId(url);
                NeoCatalogPlugin.getDefault().updateCatalog();
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            tx.finish();
        }
	}

    /**
     * @param url
     */
    private void changeIconsId(URL url) {
        try {
            ImageRegistry imageRegistry = CatalogUIPlugin.getDefault().getImageRegistry();
            if (identifierFull != null) {
                // imageRegistry.remove(identifierFull.toString());
            }
            identifierFull = url;
            imageRegistry.put(identifierFull.toString(), IconManager.getIconManager().getImage(IconManager.NETWORK_ICON));
        } catch (IllegalArgumentException e) {
        }
    }

    private NeoGeoResourceInfo info;

	public NeoGeoResourceInfo getInfo(IProgressMonitor monitor)
			throws IOException {
		if (info == null) {
			synchronized (this) {
				if (info == null) {
					info = new NeoGeoResourceInfo(this, gisNode, monitor);
				}
			}
		}
		return info;
	}

	@Override
	public NeoService service(IProgressMonitor monitor) throws IOException {
		return service;
	}

	/**
	 * Return a utility object used to read the GIS data. This is used in two
	 * places:
	 * <ul>
	 * <li>the NeoGeoResourceInfo class uses this to determine the envelope for
	 * the map</li>
	 * <li>the resolve method returns this to the map for use in drawing the
	 * points</li>
	 * </ul>
	 * 
	 * @param monitor
	 * @return
	 * @throws IOException
	 */
	public GeoNeo getGeoNeo(IProgressMonitor monitor) throws IOException {
		return geoNeo;
	}

	public <T> boolean canResolve(Class<T> adaptee) {
		return adaptee.isAssignableFrom(GeoNeo.class)
 || adaptee.isAssignableFrom(Node.class) || super.canResolve(adaptee);
	}

	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
			throws IOException {
		if (adaptee.isAssignableFrom(GeoNeo.class)) {
			return adaptee.cast(getGeoNeo(monitor));
        } else if (adaptee.isAssignableFrom(Node.class)) {
            return adaptee.cast(geoNeo.getMainGisNode());
		}
		return super.resolve(adaptee, monitor);
	}

	public Throwable getMessage() {
		return service.getMessage();
	}

	public Status getStatus() {
		return service.getStatus();
	}
}
