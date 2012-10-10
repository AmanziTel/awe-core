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

package org.amanzi.awe.nem;

import java.util.List;
import java.util.Map;

import org.amanzi.awe.nem.exceptions.NemManagerOperationException;
import org.amanzi.awe.nem.properties.manager.PropertyContainer;
import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkElementManager {
    private static final Logger LOGGER = Logger.getLogger(NetworkElementManager.class);

    private static class NEMInstanceHolder {
        private static final NetworkElementManager NEM_MANAGER = new NetworkElementManager();
    }

    public static NetworkElementManager getInstance() {
        return NEMInstanceHolder.NEM_MANAGER;
    }

    private INetworkModelProvider networkModelProvider;
    private IProjectModelProvider projectModelPovider;

    protected NetworkElementManager(INetworkModelProvider provider, IProjectModelProvider projectModelProvider) {
        this.networkModelProvider = provider;
        this.projectModelPovider = projectModelProvider;
    }

    private NetworkElementManager() {
        this(AWEUIPlugin.getDefault().getNetworkModelProvider(), AWEUIPlugin.getDefault().getProjectModelProvider());
    }

    public void removeModel(final INetworkModel model) throws ModelException {
        LOGGER.info("Start removing model " + model);
        try {
            model.delete();
        } catch (ModelException e) {
            LOGGER.error("Can't model " + model, e);
            throw e;
        }
    }

    public void removeElement(final INetworkModel model, final IDataElement element) throws ModelException {
        LOGGER.info("Start  removing element " + element + " from model " + model);
        try {
            model.deleteElement(element);
        } catch (ModelException e) {
            LOGGER.error("Can't remove element " + element + " from model " + model, e);
            throw e;
        }
    }

    public void updateNodeTypes(String[] types) {
        NodeTypeManager.getInstance().addDynamicNodeTypes(types);
    }

    /**
     * @param name
     * @param structure
     * @param typeProperties
     * @throws NemManagerOperationException
     */
    public void createModel(String name, List<String> structure, Map<String, List<PropertyContainer>> typeProperties)
            throws NemManagerOperationException {
        try {
            networkModelProvider.createModel(projectModelPovider.getActiveProjectModel(), name, structure);
        } catch (ModelException e) {
            LOGGER.error("can't create model", e);
            throw new NemManagerOperationException("can't create model", e);
        }
        AWEEventManager.getManager().fireDataUpdatedEvent(null);
    }
}
