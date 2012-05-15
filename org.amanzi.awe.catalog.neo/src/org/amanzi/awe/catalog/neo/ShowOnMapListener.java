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
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.internal.command.navigation.AbstractNavCommand;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomCommand;
import net.refractions.udig.project.internal.command.navigation.ZoomExtentCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;

/**
 * <p>
 * SHOW_ON_MAP listener
 * </p>
 * 
 * @author Bondoronok_p
 */
public class ShowOnMapListener implements IEventsListener<ShowOnMapEvent> {

	private static final Logger LOGGER = Logger
			.getLogger(ShowOnMapListener.class);

	@Override
	public void handleEvent(ShowOnMapEvent data) {
		try {
			IService curService = NeoCatalogPlugin.getDefault().getMapService();
			IMap map = ApplicationGIS.getActiveMap();
			List<ILayer> layerList = new ArrayList<ILayer>();
			List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
			IRenderableModel selectedModel = null;
			for (IRenderableModel gis : data.getRenderableModelList()) {
				if (!checkForExistCoordinateElement(gis)) {
					LOGGER.info("Cann't add layer to map because model: "
							+ gis.getName() + " doesn't contain locations");
					continue;
				}
				IGeoResource iGeoResource = getResourceForGis(curService, map,
						gis);
				if (iGeoResource != null) {
					listGeoRes.add(iGeoResource);
				} else {
					Pair<ILayer, IRenderableModel> pair = getLayerModelPair(
							map, gis);
					ILayer layer = pair.getLeft();

					if (layer != null) {
						layer.refresh(null);
						layerList.add(layer);
					}

					IRenderableModel renderableModel = pair.getRight();
					if (renderableModel != null) {
						renderableModel.setSelectedDataElements(data
								.getSelectedElements());
						renderableModel
								.setDrawNeighbors(data.isDrawNeighbors());
						selectedModel = renderableModel;
					}
				}
			}
			layerList
					.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, -1));

			executeCommands(layerList, selectedModel, data);
		} catch (Exception e) {
			throw (RuntimeException) new RuntimeException().initCause(e);
		}
	}

	@Override
	public Object getSource() {
		return null;
	}

	/**
	 * Get geo resource for model
	 * 
	 * @param service
	 *            IService
	 * @param map
	 *            IMap
	 * @param gis
	 *            Node
	 * @return IGeoResource
	 * @throws IOException
	 */
	private IGeoResource getResourceForGis(IService service, IMap map,
			IRenderableModel gis) throws IOException {
		if (service != null && getLayerModelPair(map, gis).getLeft() == null) {
			for (IGeoResource iGeoResource : service.resources(null)) {
				if (iGeoResource.canResolve(IDataModel.class)) {

					IRenderableModel resolvedElement = iGeoResource.resolve(
							IRenderableModel.class, null);

					if (resolvedElement.getName().equals(gis.getName())
							&& resolvedElement.getType() == gis.getType()) {
						return iGeoResource;
					}
				}
			}
		}
		return null;
	}

	/**
	 * check for location contain
	 * 
	 * @param gis
	 * @return
	 */
	private boolean checkForExistCoordinateElement(IRenderableModel gis) {
		return (gis.getMaxLongitude() != 0d && gis.getMinLongitude() != 0d
				&& gis.getMaxLatitude() != 0d && gis.getMinLatitude() != 0d);
	}

	/**
	 * Returns Pair, that contains necessary layer and model
	 * 
	 * @param map
	 *            map
	 * @param gis
	 *            model
	 * @return layer or null
	 */
	private Pair<ILayer, IRenderableModel> getLayerModelPair(IMap map,
			IRenderableModel gis) {
		Pair<ILayer, IRenderableModel> resultPair = new Pair<ILayer, IRenderableModel>(
				null, null);
		try {
			for (ILayer layer : map.getMapLayers()) {
				IGeoResource resource = layer.findGeoResource(IDataModel.class);
				if (resource == null) {
					continue;
				}
				IRenderableModel resolvedElement = resource.resolve(
						IRenderableModel.class, null);
				if (resolvedElement.getName().equals(gis.getName())
						&& resolvedElement.getType() == gis.getType()) {
					// clear previous selected elements
					resolvedElement.clearSelectedElements();
					return resultPair.create(layer, resolvedElement);
				}
			}
			return resultPair;
		} catch (IOException e) {
			e.printStackTrace();
			return resultPair;
		}
	}

	/**
	 * Create commands and synchronously execute them
	 * 
	 * @param layerList
	 *            layers list
	 * @param selectedModel
	 *            selected model
	 * @param data
	 *            showOnMapEvent
	 */
	private void executeCommands(List<ILayer> layerList,
			IRenderableModel selectedModel, ShowOnMapEvent data) {
		boolean haveSelectedElements = !data.getSelectedElements().isEmpty();
		List<AbstractNavCommand> commands = new ArrayList<AbstractNavCommand>();
		if (haveSelectedElements && data.isDrawNeighbors()) {
			if (data.isWithZoomCommands()) {
				commands.add(new ZoomExtentCommand());
				commands.add(new ZoomCommand(data.getZoom()));
			}
			commands.add(new SetViewportCenterCommand(selectedModel
					.getCoordinate(data.getSelectedElements().get(0))));
		}
		sendCommandsToLayer(layerList, commands);
	}

	/**
	 * Executes a commands synchronously
	 * 
	 * @param layers
	 *            layers list
	 * @param commands
	 *            commands list
	 */
	private void sendCommandsToLayer(final List<ILayer> layers,
			List<AbstractNavCommand> commands) {
		if (layers.isEmpty()) {
			return;
		}
		CompositeCommand compositeCommand = new CompositeCommand(commands);
		for (ILayer layer : layers) {
			layer.getMap().executeSyncWithoutUndo(compositeCommand);
		}

	}
}
