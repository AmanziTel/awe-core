package org.amanzi.awe.catalog.json;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.sf.json.JSONObject;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class produces information about the CSV data stream, including
 * the bounds of the data. Currently this is done by reading the stream
 * and expanding an envelope to include all points. A better approach would
 * be to provide meta-data with the stream.
 */
public class JSONGeoResourceInfo extends IGeoResourceInfo {
    JSONGeoResource handle;
    public JSONGeoResourceInfo( JSONGeoResource resource, IProgressMonitor monitor )
            throws IOException {
        this.handle = resource;
        this.title = handle.getIdentifier().toString();
        JSONReader jsonReader = handle.getJSONReader(monitor);
        JSONObject json = jsonReader.jsonObject();
        try {
            this.name = json.has("name") ? json.getString("name") : "GeoJSON Data";
            this.description = json.has("description") ? json.getString("description") : "GeoJSON Data";
            this.bounds = jsonReader.getBounds();   // This also provides the CRS
        } catch (Exception e) {
            System.err.println("Failed to determine GeoResourceInfo: "+e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
