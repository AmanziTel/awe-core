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

package org.amanzi.awe.wizards.geoptima;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.ZoomToLayer;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 *Visualise handler
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class VisualiseHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String id = event.getParameter("nodeId");
        if (id==null){
            return null;
        }
        GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
        Transaction tx = service.beginTx();
        
        try{
            Node node=service.getNodeById(Long.parseLong(id));
            Node gis = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).getOtherNode(node);
            ILayer layer = launchLayer(gis);
            ZoomToLayer zoomCommand = new ZoomToLayer();
            zoomCommand.selectionChanged(null, new StructuredSelection(layer));
            zoomCommand.runWithEvent(null, null);
        }finally{
            tx.finish();
        }
        return null;
    }
    //TODO move to utility class
    private ILayer launchLayer(Node gisNode) {
        try {
            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
            URL url = new URL("file://" + databaseLocation);
            List<ILayer> layers = ApplicationGIS.getActiveMap().getMapLayers();
            for (ILayer iLayer : layers) {
                if (iLayer.getGeoResource().canResolve(Node.class)) {
                    if (iLayer.getGeoResource().resolve(Node.class, new NullProgressMonitor()).equals(gisNode)) {
                        return iLayer;
                    }
                }
            }
            IService curService = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, url, null);
            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
            for (IGeoResource iGeoResource : curService.resources(null)) {
                if (iGeoResource.canResolve(Node.class)) {
                    if (iGeoResource.resolve(Node.class, null).equals(gisNode)) {
                        listGeoRes.add(iGeoResource);
                        break;
                    }
                }
            };
            List< ? extends ILayer> result = ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), listGeoRes, 0);
            return result.iterator().next();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
