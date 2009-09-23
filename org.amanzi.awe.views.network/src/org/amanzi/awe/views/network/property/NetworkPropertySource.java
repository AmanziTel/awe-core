package org.amanzi.awe.views.network.property;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyTransform;
import org.neo4j.neoclipse.property.PropertyTransform.PropertyHandler;

/**
 * Class that creates a properties of given Node
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NetworkPropertySource extends NodePropertySource implements IPropertySource {
    public NetworkPropertySource(NeoNode node) {
        super(node.getNode(), null);
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        try {
            if (container.hasProperty((String)id)) {
                // try to keep the same type as the previous value
                Class< ? > c = container.getProperty((String)id).getClass();
                PropertyHandler propertyHandler = PropertyTransform.getHandler(c);
                if (propertyHandler == null) {
                    MessageDialog.openError(null, "Error", "No property handler was found for type " + c.getSimpleName() + ".");
                    return;
                }
                Object o = null;
                try {
                    o = propertyHandler.parse(value);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Could not parse the input as type " + c.getSimpleName() + ".");
                    return;
                }
                if (o == null) {
                    MessageDialog.openError(null, "Error", "Input parsing resulted in null value.");
                    return;
                }
                try {
                    container.setProperty((String)id, o);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
                }
            } else {
                // simply set the value
                try {
                    container.setProperty((String)id, value);
                } catch (Exception e) {
                    MessageDialog.openError(null, "Error", "Error in Neo service: " + e.getMessage());
                }
        }
            tx.success();
            updateLayer();
        } finally {
            tx.finish();
            NeoServiceProvider.getProvider().commit();
        }
    }

    /**
     *updates layer
     */
    private void updateLayer() {
        final IMap map = ApplicationGIS.getActiveMap();
        if (map == ApplicationGIS.NO_MAP) {
            return;
        }
        Job job = new Job("update layers") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
                try {
                    Node gisNode = NeoUtils.findGisNodeByChild((Node)container);
                    if (gisNode != null) {
                        ILayer layer = findLayerByNode(map, gisNode);
                        if (layer != null) {
                            layer.refresh(null);
                        }
                    }
                    return Status.OK_STATUS;
                } finally {
                    tx.finish();
                }
            }

        };
        job.schedule();
    }

    // TODO move to utility class
    /**
     *Returns layer, that contains necessary gis node
     * 
     * @param map map
     * @param gisNode gis node
     * @return layer or null
     */
    public static ILayer findLayerByNode(IMap map, Node gisNode) {
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(Node.class);
                if (resource != null && resource.resolve(Node.class, null).equals(gisNode)) {
                    return layer;
                }
            }
            return null;
        } catch (IOException e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
