package org.amanzi.awe.catalog.neo;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.api.core.Node;

/**
 * This class produces information about the CSV data stream, including
 * the bounds of the data. Currently this is done by reading the stream
 * and expanding an envelope to include all points. A better approach would
 * be to provide meta-data with the stream.
 */
public class NeoGeoResourceInfo extends IGeoResourceInfo {
    NeoGeoResource handle;
    Node gisNode;
    public NeoGeoResourceInfo( NeoGeoResource resource, Node gisNode, IProgressMonitor monitor )
            throws IOException {
        this.handle = resource;
        this.gisNode = gisNode;
        this.title = handle.getIdentifier().toString();
        GeoNeo geoNeo = handle.getGeoNeo(monitor);
        try {
            this.name = gisNode.getProperty("name").toString();
            this.description = gisNode.hasProperty("description") ? gisNode.getProperty("description").toString() : "GeoNeo Data";
            this.bounds = geoNeo.getBounds();
        } catch (Exception e) {
            System.err.println("Failed to determine GeoResourceInfo: "+e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
