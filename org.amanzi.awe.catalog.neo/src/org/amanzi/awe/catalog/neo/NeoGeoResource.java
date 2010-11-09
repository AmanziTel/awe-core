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

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.INeoServiceProviderListener;
import org.amanzi.neo.services.ui.IconManager;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * This class represents a single collection of geographic objects in the Neo4j
 * database. In other words this class represents something that can be turned
 * into a single layer in the map.
 * 
 * @author craig
 * @since 1.0.0
 */
public class NeoGeoResource extends IGeoResource implements INeoServiceProviderListener {
    //TODO ZNN need main solution for using class NeoServiceProviderListener instead of interface INeoServiceProviderListener  
    // private URL identifierUrl;
    private final String identifier;
	private final NeoService service;
	private final Node gisNode;
	private GeoNeo geoNeo;
    private URL identifierFull;
    private GraphDatabaseService graphDatabaseService;

	public NeoGeoResource(NeoService service,
			GraphDatabaseService neo, Node gisNode) {
		this.service = service;
        this.graphDatabaseService = neo;
		this.gisNode = gisNode;
		this.geoNeo = new GeoNeo(neo, this.gisNode);

			URL serviceUrl = service.getIdentifier();
        identifier = serviceUrl + "#";// +
                                      // this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        identifierFull = this.getIdentifier();
	}
	public void updateCRS(){
	    Transaction tx = graphDatabaseService.beginTx();
	    try{
	        geoNeo = new GeoNeo(graphDatabaseService, this.gisNode);
	        info=null;
	    }finally{
	        tx.finish();
	    }
	}
	@Override
	public URL getIdentifier() {
        Transaction tx = NeoServiceProviderUi.getProvider().getService().beginTx();
        try {
            URL url = new URL(identifier + this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            if (identifierFull == null || !url.equals(identifierFull)) {
                changeIconsId(url);
                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                        new UpdateDatabaseEvent(UpdateViewEventType.GIS));
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

	@Override
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

	@Override
    public <T> boolean canResolve(Class<T> adaptee) {
		return adaptee.isAssignableFrom(GeoNeo.class)
 || adaptee.isAssignableFrom(Node.class) || super.canResolve(adaptee);
	}

	@Override
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
	@Override
    public void onNeoStop(Object source) {
        graphDatabaseService = null;
    }

    @Override
    public void onNeoStart(Object source) {
        graphDatabaseService = NeoServiceProviderUi.getProvider().getService();
    }

    @Override
    public void onNeoCommit(Object source) {
    }

    @Override
    public void onNeoRollback(Object source) {
    }
}
