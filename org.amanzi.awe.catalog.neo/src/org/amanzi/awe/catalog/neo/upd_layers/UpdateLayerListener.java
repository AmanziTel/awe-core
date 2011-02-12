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

package org.amanzi.awe.catalog.neo.upd_layers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.command.factory.NavigationCommandFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.awe.catalog.neo.upd_layers.events.AddSelectionEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeModelEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeSelectionEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.RefreshPropertiesEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesAndMapEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesEvent;
import org.amanzi.awe.ui.IGraphModel;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Pair;
import org.amanzi.neo.services.utils.Utils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Listener for layer events.
 * <p>
 * </p>
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdateLayerListener {

    private final ILayer layer;

    /**
     * Constructor.
     * 
     * @param aLayer ILayer
     */
    public UpdateLayerListener(ILayer aLayer) {
        layer = aLayer;
    }

    /**
     * Execute layer update.
     * 
     * @param event UpdateLayerEvent
     */
    public void updateLayerOnEvent(UpdateLayerEvent event) {
        try {
            switch (event.getType()) {
            case REFRESH:
                if (isEventForThisLayer(event.getGisNode())) {
                    layer.refresh(null);
                }
                break;
            case CHANGE_SELECTION:
                changeSelection((ChangeSelectionEvent)event);
                break;
            case ADD_SELECTION:
                addSelection((AddSelectionEvent)event);
                break;
            case PROPERTY_UPDATE:
                // 9.11.2010, Lagutko: layer should be updated after property change
                updateProperties((UpdatePropertiesEvent)event, true);
                break;
            case PROPERTY_AND_MAP_UPDATE:
                // 9.11.2010, Lagutko: layer should not be updated after property change, since it
                // will be updated in showOnMap
                updateProperties((UpdatePropertiesEvent)event, false);
                showOnMap((UpdatePropertiesAndMapEvent)event);
                break;
            case PROPERTY_REFRESH:
                refreshProperties((RefreshPropertiesEvent)event);
                break;
            case CHANGE_GRAPH_MODEL:
                changeModel((ChangeModelEvent)event);
                break;
            case ZOOM:
                zoomToData((ChangeSelectionEvent)event);
                break;
            default:

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param event
     * @throws IOException
     */
    private void zoomToData(ChangeSelectionEvent event) throws IOException {
        Node gis = event.getGisNode();
        if (!isEventForThisLayer(gis)) {
            return;
        }
        Iterator<Node> it = event.getSelected().iterator();
        if (!it.hasNext()) {
            return;
        }
        Node site = it.next();
        Pair<Double, Double> loc = Utils.getLocationPair(site);
        if (loc.l() == null || loc.r() == null) {
            return;
        }
        Coordinate cr = new Coordinate(loc.l(), loc.r());

        try {
            NavigationCommandFactory factory = NavigationCommandFactory.getInstance();
            IMap m = layer.getMap();
            CoordinateReferenceSystem worldCrs =m.getViewportModel().getCRS();
            CoordinateReferenceSystem dataCRS = layer.getGeoResource().getInfo(null).getCRS();
            MathTransform transform_d2w = CRS.findMathTransform(dataCRS, worldCrs, true);
            Coordinate cr2=new Coordinate();
            JTS.transform(cr,cr2, transform_d2w);
            NavCommand[] commands = new NavCommand[] {
            factory.createSetViewportCenterCommand(cr2), factory.createZoomCommand(4d)};

            ((Map)m).sendCommandASync(factory.createCompositeCommand(commands));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change model event handler.
     * 
     * @param event the event
     */
    private void changeModel(ChangeModelEvent event) throws IOException {
        Node gis = event.getGisNode();
        GeoNeo geo = getGeoNeo();
        if (gis != null && isEventForThisLayer(gis)) {
            IGraphModel prevSel = geo.getGraphModel();
            geo.setGraphModel(event.getModel());
            layer.refresh(null);
        }
    }

    /**
     * Change selection in layer.
     * 
     * @param event SelectOnMapEvent
     * @throws IOException problem in getting resource
     */
    private void changeSelection(ChangeSelectionEvent event) throws IOException {
        Node gis = event.getGisNode();
        GeoNeo geo = getGeoNeo();
        if (gis == null || isEventForThisLayer(gis)) {
            Collection<Node> nodes = event.getSelected();
            Set<Node> prevSel = geo.getSelectedNodes();
            if (!prevSel.equals(nodes)) {
                geo.setSelectedNodes(new HashSet<Node>(nodes));
                layer.refresh(null);
            }
        } else {
            Set<Node> prevSel = geo.getSelectedNodes();
            if (prevSel != null && !prevSel.isEmpty()) {
                geo.setSelectedNodes(new HashSet<Node>());
                layer.refresh(null);
            }
        }
    }

    /**
     * Add selection in layer.
     * 
     * @param event SelectOnMapEvent
     * @throws IOException problem in getting resource
     */
    private void addSelection(AddSelectionEvent event) throws IOException {
        Node gis = event.getGisNode();
        GeoNeo geo = getGeoNeo();
        if (gis == null || isEventForThisLayer(gis)) {
            for (Node node : event.getSelected()) {
                geo.addNodeToSelect(node);
            }
            layer.refresh(null);
        }
    }

    /**
     * Update layer properties by event.
     * 
     * @param event UpdatePropertiesEvent
     * @param autoRefresh should layer be refreshed after changes
     */
    private void updateProperties(UpdatePropertiesEvent event, boolean autoRefresh) throws IOException {
        HashMap<String, Object> values = event.getValues();
        if (values == null) {
            return;
        }
        // TODO: Lagutko: check is this cases can be united
        if (isEventForThisLayer(event.getGisNode())) {
            GeoNeo geo = getGeoNeo();
            geo.setProperties(values);

            if (autoRefresh) {
                layer.refresh(null);
            }
        } else if (event.isNeedClearOther()) {
            GeoNeo geo = getGeoNeo();
            for (String key : values.keySet()) {
                geo.setProperty(key, null);
            }

            if (autoRefresh) {
                layer.refresh(null);
            }
        }
    }

    private GeoNeo getGeoNeo() throws IOException {
        IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
        GeoNeo geo = resourse.resolve(GeoNeo.class, null);
        return geo;
    }

    private void showOnMap(UpdatePropertiesAndMapEvent event) throws IOException {
        Node gis = event.getGisNode();
        if (!isEventForThisLayer(gis)) {
            return;
        }
        try {
            GeoNeo geo = getGeoNeo();
            Collection<Node> selection = event.getSelection();
            if (selection != null) {
                geo.setSelectedNodes(new HashSet<Node>(selection));
            }
            IMap map = layer.getMap();
            CoordinateReferenceSystem crs = null;
            if (event.isNeedCentered()) {
                crs = NeoUtils.getCRS(gis, null);
                // if (gis.hasProperty(INeoConstants.PROPERTY_CRS_NAME)) {
                // crs = CRS.decode(gis.getProperty(INeoConstants.PROPERTY_CRS_NAME).toString());
                // } else if (gis.hasProperty(INeoConstants.PROPERTY_CRS_HREF_NAME)) {
                // URL crsURL = new
                // URL(gis.getProperty(INeoConstants.PROPERTY_CRS_HREF_NAME).toString());
                // crs = CRS.decode(crsURL.getContent().toString());
                // }
                double[] c = event.getCoords();
                if (c == null) {
                    return;
                }
                if (event.isAutoZoom()) {
                    // TODO: Check that this works with all CRS
                    map.sendCommandSync(new net.refractions.udig.project.internal.command.navigation.SetViewportWidth(30000));
                }
                map.sendCommandSync(new SetViewportCenterCommand(new Coordinate(c[0], c[1]), crs));
            } else {
                layer.refresh(null);
            }
        } catch (MalformedURLException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    private void refreshProperties(RefreshPropertiesEvent event) throws IOException {
        if (isEventForThisLayer(event.getGisNode())) {
            GeoNeo geo = getGeoNeo();
            geo.setPropertyToRefresh(event.getAggrNode(), event.getPropertyNode(), event.getMinSelNode(), event.getMaxSelNode(), event.getValues());
            layer.refresh(null);
        }
    }

    private boolean isEventForThisLayer(Node gis) throws IOException {
        IGeoResource resource = layer.findGeoResource(NeoGeoResource.class);
        return gis != null && resource.resolve(Node.class, null).equals(gis);
    }
}
