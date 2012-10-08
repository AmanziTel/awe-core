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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;
import net.refractions.udig.project.internal.command.navigation.SetViewportBBoxCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.selection.ISelection;
import org.amanzi.awe.catalog.neo.selection.Selection;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ShowElementsOnMap;
import org.amanzi.awe.ui.events.impl.ShowGISOnMap;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

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
        case PROJECT_CHANGED:
            updateCatalog();
            break;
        case SHOW_GIS:
            final ShowGISOnMap showEvent = (ShowGISOnMap)event;
            showOnMap(showEvent.getModel());
            break;
        case SHOW_ELEMENTS:
            final ShowElementsOnMap showElementsEvent = (ShowElementsOnMap)event;
            showOnMap(showElementsEvent.getModel(), showElementsEvent.getElements(), showElementsEvent.getBounds());
            break;
        case REFRESH_MAP:
            refreshMap();
            break;
        default:
            break;
        }
    }

    protected void updateCatalog() {
        NeoCatalogPlugin.getDefault().updateMapServices();
    }

    protected void showOnMap(final IRenderableModel model, final Set<IDataElement> elements, ReferencedEnvelope bounds) {
        final boolean computeBounds = bounds == null;

        final Iterable<ILocationElement> selectedElements = model.getElementsLocations(elements);
        final Set<ILocationElement> selectedLocations = new HashSet<ILocationElement>();

        if (computeBounds) {
            double minLat = Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE;
            double maxLat = -Double.MAX_VALUE;
            double maxLon = -Double.MAX_VALUE;

            for (final ILocationElement element : selectedElements) {
                minLat = Math.min(minLat, element.getLatitude());
                maxLat = Math.max(maxLat, element.getLatitude());

                minLon = Math.min(minLon, element.getLongitude());
                maxLon = Math.max(maxLon, element.getLongitude());

                selectedLocations.add(element);
            }

            bounds = new ReferencedEnvelope(minLon, maxLon, minLat, maxLat, model.getMainGIS().getCRS());
        }

        try {
            final IMap map = ApplicationGIS.getActiveMap();

            final List<ILayer> layerList = new ArrayList<ILayer>();

            for (final IGISModel gis : model.getAllGIS()) {
                final Pair<IGISModel, ILayer> pair = getLayerModelPair(map, gis);

                final ILayer layer = pair.getRight();

                if (layer != null) {
                    layer.refresh(null);
                    layerList.add(layer);
                }
            }

            final Selection selection = new Selection(model, elements, selectedLocations);
            map.getBlackboard().put(ISelection.SELECTION_BLACKBOARD_PROPERTY, selection);

            if (!selectedLocations.isEmpty()) {
                executeCommands(layerList, bounds);
            }
        } catch (final Exception e) {
            LOGGER.error("Error on putting model <" + model + "> on a Map", e);
        }

    }

    protected void showOnMap(final IGISModel model) {
        try {
            final IService curService = NeoCatalogPlugin.getDefault().getMapService();
            final IMap map = ApplicationGIS.getActiveMap();
            final List<ILayer> layerList = new ArrayList<ILayer>();
            final List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();

            if (!model.canRender()) {
                LOGGER.info("Can't add layer to map because model: " + model.getName() + " doesn't contain locations");
                return;
            }

            final IGeoResource iGeoResource = getResourceForGis(curService, map, model);
            if (iGeoResource != null) {
                listGeoRes.add(iGeoResource);
            } else {
                final Pair<IGISModel, ILayer> pair = getLayerModelPair(map, model);
                final ILayer layer = pair.getRight();

                if (layer != null) {
                    layer.refresh(null);
                    layerList.add(layer);
                }
            }

            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, -1));

            executeCommands(layerList, model);
        } catch (final Exception e) {
            LOGGER.error("Error on putting model <" + model + "> on a Map", e);
        }
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
            for (final IGeoResource iGeoResource : service.resources(new NullProgressMonitor())) {
                if (iGeoResource.canResolve(IGISModel.class)) {

                    final IGISModel resolvedElement = iGeoResource.resolve(IGISModel.class, null);

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
        final Pair<IGISModel, ILayer> resultPair = new MutablePair<IGISModel, ILayer>(gis, null);
        try {
            for (final ILayer layer : map.getMapLayers()) {
                final IGeoResource resource = layer.findGeoResource(IGISModel.class);
                if (resource == null) {
                    continue;
                }
                final IGISModel resolvedElement = resource.resolve(IGISModel.class, null);
                if (resolvedElement.getName().equals(gis.getName())) {
                    // clear previous selected elements
                    resultPair.setValue(layer);
                    return resultPair;
                }
            }
            return resultPair;
        } catch (final IOException e) {
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
    private void executeCommands(final List<ILayer> layerList, final IGISModel selectedModel) {
        final List<AbstractNavCommand> commands = new ArrayList<AbstractNavCommand>();

        commands.add(new SetViewportBBoxCommand(selectedModel.getBounds()));
        commands.add(new ZoomCommand(selectedModel.getBounds()));

        sendCommandsToLayer(layerList, commands);
    }

    private void executeCommands(final List<ILayer> layerList, final ReferencedEnvelope bounds) {
        final List<AbstractNavCommand> commands = new ArrayList<AbstractNavCommand>();

        commands.add(new SetViewportBBoxCommand(bounds));
        commands.add(new ZoomCommand(bounds));
        commands.add(new ZoomCommand(0.80));

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
        final CompositeCommand compositeCommand = new CompositeCommand(commands);
        for (final ILayer layer : layers) {
            layer.getMap().executeSyncWithoutUndo(compositeCommand);
        }

    }

    private void refreshMap() {
        ApplicationGIS.getActiveMap().getRenderManager().refresh(null);
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }
}
