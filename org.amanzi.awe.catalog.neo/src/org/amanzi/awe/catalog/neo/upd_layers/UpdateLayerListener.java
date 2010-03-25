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
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.NeoGeoResource;
import org.amanzi.awe.catalog.neo.upd_layers.events.AddSelectionEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeSelectionEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.RefreshPropertiesEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdateLayerEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesAndMapEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesEvent;
import org.amanzi.neo.core.INeoConstants;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Listener for layer events.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdateLayerListener {
    
    private ILayer layer;
    
    /**
     * Constructor.
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
    public void updateLayerOnEvent(UpdateLayerEvent event){
        try {
            switch(event.getType()){
                case REFRESH:
                    if(isEventForThisLayer(event.getGisNode())){
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
                    updateProperties((UpdatePropertiesEvent)event);                    
                    break;
                case PROPERTY_AND_MAP_UPDATE:
                    updateProperties((UpdatePropertiesEvent)event);
                    showOnMap((UpdatePropertiesAndMapEvent)event);
                    break;
                case PROPERTY_REFRESH:
                    refreshProperties((RefreshPropertiesEvent)event);
                    break;
                default:
                    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Change selection in layer.
     *
     * @param event SelectOnMapEvent
     * @throws IOException problem in getting resource
     */
    private void changeSelection(ChangeSelectionEvent event)throws IOException{
        Node gis = event.getGisNode();
        GeoNeo geo = getGeoNeo();
        if (gis==null||isEventForThisLayer(gis)) {
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
    private void addSelection(AddSelectionEvent event)throws IOException{
        Node gis = event.getGisNode();
        GeoNeo geo = getGeoNeo();
        if (gis==null||isEventForThisLayer(gis)) {
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
     */
    private void updateProperties(UpdatePropertiesEvent event)throws IOException{
        HashMap<String, Object> values = event.getValues();
        if(values == null){
            return;
        }
        if(isEventForThisLayer(event.getGisNode())){
            GeoNeo geo = getGeoNeo();
            geo.setProperties(values);
            layer.refresh(null);
        } else if (event.isNeedClearOther()){
            GeoNeo geo = getGeoNeo();
            for(String key : values.keySet()){
                geo.setProperty(key, null);
            }
            layer.refresh(null);
        }
    }

    private GeoNeo getGeoNeo() throws IOException {
        IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
        GeoNeo geo = resourse.resolve(GeoNeo.class, null);
        return geo;
    }
    
    private void showOnMap(UpdatePropertiesAndMapEvent event)throws IOException{        
        Node gis = event.getGisNode();
        if(!isEventForThisLayer(gis)){
            return;
        }
        try{
            GeoNeo geo = getGeoNeo();
            Collection<Node> selection = event.getSelection();
            if (selection!=null) {
                geo.setSelectedNodes(new HashSet<Node>(selection));
            }
            IMap map = layer.getMap();
            CoordinateReferenceSystem crs = null;
            if (event.isNeedCentered()) {
                if (gis.hasProperty(INeoConstants.PROPERTY_CRS_NAME)) {
                    crs = CRS.decode(gis.getProperty(INeoConstants.PROPERTY_CRS_NAME).toString());
                } else if (gis.hasProperty(INeoConstants.PROPERTY_CRS_HREF_NAME)) {
                    URL crsURL = new URL(gis.getProperty(INeoConstants.PROPERTY_CRS_HREF_NAME).toString());
                    crs = CRS.decode(crsURL.getContent().toString());
                }
                double[] c = event.getCoords();
                if (c == null) {
                    return;
                }
                if (event.isAutoZoom()) {
                    // TODO: Check that this works with all CRS
                    map.sendCommandASync(new net.refractions.udig.project.internal.command.navigation.SetViewportWidth(30000));                
                }
                map.sendCommandASync(new SetViewportCenterCommand(new Coordinate(c[0], c[1]), crs));
            } else {
                layer.refresh(null);
            }
        } catch (NoSuchAuthorityCodeException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (MalformedURLException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    
    private void refreshProperties(RefreshPropertiesEvent event)throws IOException{
        if (isEventForThisLayer(event.getGisNode())) {
            GeoNeo geo = getGeoNeo();
            geo.setPropertyToRefresh(event.getAggrNode(), event.getPropertyNode(),event.getMinSelNode(),
                    event.getMaxSelNode(), event.getValues());
            layer.refresh(null);
        }
    }
    
    private boolean isEventForThisLayer(Node gis)throws IOException{
        IGeoResource resource = layer.findGeoResource(NeoGeoResource.class);
        return gis!=null && resource.resolve(Node.class, null).equals(gis);
    }

}
