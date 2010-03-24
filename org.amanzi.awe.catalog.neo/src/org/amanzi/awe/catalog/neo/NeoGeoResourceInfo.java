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

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * This class produces information about the CSV data stream, including the
 * bounds of the data. Currently this is done by reading the stream and
 * expanding an envelope to include all points. A better approach would be to
 * provide meta-data with the stream.
 */
public class NeoGeoResourceInfo extends IGeoResourceInfo {
	NeoGeoResource handle;
	Node gisNode;

    public NeoGeoResourceInfo(NeoGeoResource resource, Node gisNode, IProgressMonitor monitor) throws IOException {
        GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
        Transaction tx = service.beginTx();
        try {
            this.handle = resource;
            this.gisNode = gisNode;

            GeoNeo geoNeo = handle.getGeoNeo(monitor);
            try {
                this.name = gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                this.title = this.name;
                this.description = gisNode.hasProperty(INeoConstants.PROPERTY_DESCRIPTION_NAME) ? gisNode.getProperty(
                        INeoConstants.PROPERTY_DESCRIPTION_NAME).toString() : "GeoNeo Data";
                this.bounds = geoNeo.getBounds();
            } catch (Exception e) {
                System.err.println("Failed to determine GeoResourceInfo: " + e.getMessage());
                e.printStackTrace(System.err);
            }
            this.icon = new ImageDescriptor() {
                private ImageData imageData;

                @Override
                public ImageData getImageData() {
                    if (imageData == null) {
                        imageData = IconManager.getIconManager().getImage(IconManager.NETWORK_ICON).getImageData();
                    }
                    return imageData;
                }
            };
        } finally {
            tx.finish();
        }
	}

    @Override
    public String getTitle() {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            return gisNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        } finally {
            tx.finish();
        }
    }
}
