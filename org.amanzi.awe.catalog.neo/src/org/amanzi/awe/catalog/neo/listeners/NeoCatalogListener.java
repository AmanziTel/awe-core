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

package org.amanzi.awe.catalog.neo.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomExtentCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowGISOnMap;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.models.render.IGISModel;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NeoCatalogListener implements IAWEEventListenter {

    private final static Logger LOGGER = Logger.getLogger(NeoCatalogListener.class);

    @Override
    public void onEvent(final IEvent event) {
        switch (event.getStatus()) {
        case AWE_STARTED:
        case DATA_UPDATED:
            updateCatalog();
            break;
        case SHOW_GIS_ON_MAP:
            ShowGISOnMap showEvent = (ShowGISOnMap)event;
            showOnMap(showEvent.getModel(), showEvent.getZoom());
            break;
        default:
            break;
        }
    }

    protected void updateCatalog() {
        NeoCatalogPlugin.getDefault().updateMapServices();
    }

    protected void showOnMap(final IGISModel model, final int zoom) {
        try {
            IService curService = NeoCatalogPlugin.getDefault().getMapService();
            IMap map = ApplicationGIS.getActiveMap();
            List<ILayer> layerList = new ArrayList<ILayer>();
            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();

            if (!checkForExistCoordinateElement(model)) {
                LOGGER.info("Can't add layer to map because model: " + model.getName() + " doesn't contain locations");
                return;
            }

            IGeoResource iGeoResource = getResourceForGis(curService, map, model);
            if (iGeoResource != null) {
                listGeoRes.add(iGeoResource);
            } else {
                Pair<IGISModel, ILayer> pair = getLayerModelPair(map, model);
                ILayer layer = pair.getRight();

                if (layer != null) {
                    layer.refresh(null);
                    layerList.add(layer);
                }
            }

            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, -1));

            executeCommands(layerList, model, zoom);
        } catch (Exception e) {

        }
    }

    private boolean checkForExistCoordinateElement(final IGISModel gis) {
        return ((gis.getMaxLongitude() != 0d) && (gis.getMinLongitude() != 0d) && (gis.getMaxLatitude() != 0d) && (gis
                .getMinLatitude() != 0d));
    }

    /**
     * Get geo resource for model
     * 
     * @param service IService
     * @param map IMap
     * @param gis Node
     * @return IGeoResource
     * @throws IOException
     */
    private IGeoResource getResourceForGis(final IService service, final IMap map, final IGISModel gis) throws IOException {
        if ((service != null) && (getLayerModelPair(map, gis).getRight() == null)) {
            for (IGeoResource iGeoResource : service.resources(null)) {
                if (iGeoResource.canResolve(IGISModel.class)) {

                    IGISModel resolvedElement = iGeoResource.resolve(IGISModel.class, null);

                    if (resolvedElement.getName().equals(gis.getName())) {
                        return iGeoResource;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns Pair, that contains necessary layer and model
     * 
     * @param map map
     * @param gis model
     * @return layer or null
     */
    private Pair<IGISModel, ILayer> getLayerModelPair(final IMap map, final IGISModel gis) {
        Pair<IGISModel, ILayer> resultPair = new MutablePair<IGISModel, ILayer>(gis, null);
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(IGISModel.class);
                if (resource == null) {
                    continue;
                }
                IGISModel resolvedElement = resource.resolve(IGISModel.class, null);
                if (resolvedElement.getName().equals(gis.getName())) {
                    // clear previous selected elements
                    resultPair.setValue(layer);
                    return resultPair;
                }
            }
            return resultPair;
        } catch (IOException e) {
            LOGGER.error("Error on computing Model->Layer pair", e);
            return resultPair;
        }
    }

    /**
     * Create commands and synchronously execute them
     * 
     * @param layerList layers list
     * @param selectedModel selected model
     * @param data showOnMapEvent
     */
    private void executeCommands(final List<ILayer> layerList, final IGISModel selectedModel, final int zoom) {
        List<AbstractNavCommand> commands = new ArrayList<AbstractNavCommand>();

        commands.add(new ZoomExtentCommand());
        commands.add(new ZoomCommand(zoom));

        sendCommandsToLayer(layerList, commands);
    }

    /**
     * Executes a commands synchronously
     * 
     * @param layers layers list
     * @param commands commands list
     */
    private void sendCommandsToLayer(final List<ILayer> layers, final List<AbstractNavCommand> commands) {
        if (layers.isEmpty()) {
            return;
        }
        CompositeCommand compositeCommand = new CompositeCommand(commands);
        for (ILayer layer : layers) {
            layer.getMap().executeSyncWithoutUndo(compositeCommand);
        }

    }
}
