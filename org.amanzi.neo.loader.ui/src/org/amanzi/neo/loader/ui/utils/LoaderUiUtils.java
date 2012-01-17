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
package org.amanzi.neo.loader.ui.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.ZoomToLayer;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.preferences.PreferenceStore;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * common actions in loaders ui
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class LoaderUiUtils extends LoaderUtils {
    private static final Logger LOGGER = Logger.getLogger(LoaderUiUtils.class);
    /*
     * constants
     */
    public static final String FILE_PREFIX = "file://";
    public static final String COMMA_SEPARATOR = ",";

    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    public static String[] getPossibleHeaders(String key) {

        String text = PreferenceStore.getPreferenceStore().getValue(key);
        if (text == null) {
            return new String[0];
        }
        String[] array = text.split(COMMA_SEPARATOR);
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Confirm load network on map
     * 
     * @param map map
     * @param fileName name of loaded file
     * @return true or false
     */
    public static boolean confirmAddToMap(final IMap map, final String fileName) {

        final IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
        return (Integer)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Integer>() {
            int result;

            @Override
            public void run() {
                boolean boolean1 = preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER);
                String message = String.format(NeoLoaderPluginMessages.ADD_LAYER_MESSAGE, fileName, map.getName());
                if (map == ApplicationGIS.NO_MAP) {
                    message = String.format(NeoLoaderPluginMessages.ADD_NEW_MAP_MESSAGE, fileName);
                }
                MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), NeoLoaderPluginMessages.ADD_LAYER_TITLE, message,
                        NeoLoaderPluginMessages.TOGLE_MESSAGE, boolean1, preferenceStore, DataLoadPreferences.ZOOM_TO_LAYER);
                result = dialog.getReturnCode();
                if (result == IDialogConstants.YES_ID) {
                    preferenceStore.putValue(DataLoadPreferences.ZOOM_TO_LAYER, String.valueOf(dialog.getToggleState()));
                }
            }

            @Override
            public Integer getValue() {
                return result;
            }
        }) == IDialogConstants.YES_ID;
    }

    /**
     * Get map service.
     * 
     * @return IService
     * @throws MalformedURLException
     */
    private static IService getMapService() throws MalformedURLException {
        String databaseLocation = DatabaseManagerFactory.getDatabaseManager().getLocation();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        URL url = new URL(FILE_PREFIX + databaseLocation);
        ID id = new ID(url);
        IService curService = catalog.getById(IService.class, id, null);
        return curService;
    }

    /**
     * Add model to map.
     * 
     * @param firstDataset String
     * @param modelsList Node...
     */
    public static void addGisDataToMap(String dataName, List<IDataModel> modelsList) {
        try {
            IService curService = getMapService();
            IMap map = ApplicationGIS.getActiveMap();
            if (confirmAddToMap(map, dataName)) {
                List<ILayer> layerList = new ArrayList<ILayer>();
                List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                for (IDataModel gis : modelsList) {
                    if (!checkForExistCoordinateElement(gis)) {
                        LOGGER.info("Cann't add layer to map because model: " + gis.getName() + " doesn't contain locations");
                        AweConsolePlugin.error("Cann't add layer to map because model" + gis.getName()
                                + "  doesn't contain locations");
                        continue;
                    }
                    IGeoResource iGeoResource = getResourceForGis(curService, map, gis);
                    if (iGeoResource != null) {
                        listGeoRes.add(iGeoResource);
                    } else {
                        ILayer layer = findLayerByGisModel(map, gis);
                        if (layer != null) {
                            layer.refresh(null);
                            layerList.add(layer);
                        }
                    }
                }
                layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, -1));
                IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
                if (preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER)) {
                    zoomToLayer(layerList);
                }
            }
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * just check for location contain
     * 
     * @param gis
     * @return
     */
    private static boolean checkForExistCoordinateElement(IDataModel gis) {
        IRenderableModel model = (IRenderableModel)gis;
        if (model.getMaxLongitude() >= 0d && model.getMinLongitude() >= 0d && model.getMaxLatitude() >= 0d
                && model.getMinLatitude() >= 0d) {
            return true;
        }
        return false;
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
    private static IGeoResource getResourceForGis(IService service, IMap map, IDataModel gis) throws IOException {
        if (service != null && findLayerByGisModel(map, gis) == null) {
            for (IGeoResource iGeoResource : service.resources(null)) {
                if (iGeoResource.canResolve(IDataModel.class)) {
                    IDataModel resolvedElement = iGeoResource.resolve(IDataModel.class, null);
                    if (resolvedElement.getName().equals(gis.getName()) && resolvedElement.getType() == gis.getType()) {
                        return iGeoResource;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns layer, that contains necessary model
     * 
     * @param map map
     * @param gis model
     * @return layer or null
     */
    public static ILayer findLayerByGisModel(IMap map, IDataModel gis) {
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(IDataModel.class);
                if (resource == null) {
                    continue;
                }
                IDataModel resolvedElement = resource.resolve(IDataModel.class, null);
                if (resolvedElement.getName().equals(gis.getName()) && resolvedElement.getType() == gis.getType()) {
                    return layer;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Zoom To 1st layers in list
     * 
     * @param layers list of layers
     */
    public static void zoomToLayer(final List< ? extends ILayer> layers) {
        if (layers.isEmpty()) {
            return;
        }
        ActionUtil.getInstance().runTask(new Runnable() {
            @Override
            public void run() {
                ZoomToLayer zoomCommand = new ZoomToLayer();
                zoomCommand.selectionChanged(null, new StructuredSelection(layers));
                zoomCommand.runWithEvent(null, null);
            }
        }, true);
    }

    /**
     * Returns Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @return default directory
     */

    public static String getDefaultDirectory() {
        String result = PreferenceStore.getPreferenceStore().getValue(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER);
        if (result == null) {
            result = System.getProperty("user.home");
        }

        return result;
    }

    /**
     * Sets Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @param newDirectory new default directory
     */

    public static void setDefaultDirectory(String newDirectory) {
        PreferenceStore.getPreferenceStore().setDefault(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER, newDirectory);
    }

}
