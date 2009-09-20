package org.amanzi.awe.catalog.neo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.icons.IconManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Node;

/**
 * This class represents a single collection of geographic objects in the Neo4j
 * database. In other words this class represents something that can be turned
 * into a single layer in the map.
 * 
 * @author craig
 * @since 1.0.0
 */
public class NeoGeoResource extends IGeoResource {
	private URL identifierUrl;
	private NeoService service;
	private Node gisNode;
	private GeoNeo geoNeo;

	public NeoGeoResource(NeoService service,
			org.neo4j.api.core.NeoService neo, Node gisNode) {
		this.service = service;
		this.gisNode = gisNode;
		this.geoNeo = new GeoNeo(neo, this.gisNode);
		try {
			URL serviceUrl = service.getIdentifier();
            identifierUrl = new URL(serviceUrl + "#" + this.gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            try {
                CatalogUIPlugin.getDefault().getImageRegistry().put(this.getIdentifier().toString(),
                        IconManager.getIconManager().getImage(IconManager.NETWORK_ICON));
            } catch (IllegalArgumentException e) {
            }
		} catch (MalformedURLException e) {
		}
	}

	@Override
	public URL getIdentifier() {
		return identifierUrl;
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
