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

package org.amanzi.awe.nem.managers.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.nem.exceptions.NemManagerOperationException;
import org.amanzi.awe.nem.internal.NemPlugin;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.ui.events.impl.DataUpdatedEvent;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.manager.EventChain;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

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

    private final INetworkModelProvider networkModelProvider;

    private final IProjectModelProvider projectModelPovider;

    protected NetworkElementManager(INetworkModelProvider provider, IProjectModelProvider projectModelProvider) {
        this.networkModelProvider = provider;
        this.projectModelPovider = projectModelProvider;
    }

    private NetworkElementManager() {
        this(NemPlugin.getDefault().getNetworkModelProvider(), NemPlugin.getDefault().getProjectModelProvider());
    }

    public void removeModel(final INetworkModel model) throws ModelException {
        LOGGER.info("Start removing model " + model + " at " + new Date(System.currentTimeMillis()));

        Job job = new Job("removing model " + model.getName()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    networkModelProvider.deleteModel(model);
                    LOGGER.info("Finished removing model " + model + " at " + new Date(System.currentTimeMillis()));
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            AWEEventManager.getManager().fireDataUpdatedEvent(null);
                        }
                    }, true);

                } catch (Exception e) {
                    LOGGER.error("Can't remove model " + model, e);
                    return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "Error on deleting element", e);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    public void removeElement(final INetworkModel model, final IDataElement element) throws ModelException {
        LOGGER.info("Start  removing element " + element + " from model " + model + " at " + new Date(System.currentTimeMillis()));

        Job job = new Job("Removing element" + element.getName() + " from model " + model.getName()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    model.deleteElement(element);
                    model.finishUp();
                    LOGGER.info("Finished  removing element " + element + " from model " + model + " at "
                            + new Date(System.currentTimeMillis()));
                    ActionUtil.getInstance().runTask(new Runnable() {
                        @Override
                        public void run() {
                            AWEEventManager.getManager().fireDataUpdatedEvent(null);
                        }
                    }, true);

                } catch (Exception e) {
                    LOGGER.error("Can't remove element " + element + " from model " + model, e);
                    return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "Error on deleting element", e);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    public List<INodeType> updateNodeTypes(INodeType[] types) {
        return NodeTypeManager.getInstance().addDynamicNodeTypes(types);
    }

    /**
     * @param name
     * @param structure
     * @param typeProperties
     * @throws NemManagerOperationException
     */
    public void createModel(String name, List<INodeType> structure, Map<INodeType, List<PropertyContainer>> typeProperties)
            throws NemManagerOperationException {
        EventChain eventChain = new EventChain(true);
        try {
            INetworkModel model = networkModelProvider.createModel(projectModelPovider.getActiveProjectModel(), name, structure);
            IPropertyStatisticsModel propertiesModel = model.getPropertyStatistics();
            updateProperties(propertiesModel, typeProperties);
            model.finishUp();

        } catch (ModelException e) {
            LOGGER.error("can't create model", e);
            throw new NemManagerOperationException("can't create model", e);
        }
        eventChain.addEvent(new DataUpdatedEvent(this));
        AWEEventManager.getManager().fireEventChain(eventChain);
    }

    /**
     * @param propertiesModel
     * @param typeProperties
     */
    private void updateProperties(IPropertyStatisticsModel propertiesModel, Map<INodeType, List<PropertyContainer>> typeProperties) {
        for (Entry<INodeType, List<PropertyContainer>> properties : typeProperties.entrySet()) {
            Map<String, Object> preparedProeprties = preparePropertiesMapFromContainer(properties.getValue());
            propertiesModel.updateDefaultProperties(properties.getKey(), preparedProeprties);
        }
        try {
            propertiesModel.finishUp();
        } catch (ModelException e) {
            LOGGER.error("Can't update property statisticsModel ", e);
        }
    }

    /**
     * @param value
     * @return
     */
    private Map<String, Object> preparePropertiesMapFromContainer(Collection<PropertyContainer> containers) {
        Map<String, Object> properties = new HashMap<String, Object>();
        for (PropertyContainer container : containers) {
            properties.put(container.getName(), container.getValue());
        }
        return properties;
    }

    /**
     * @param model
     * @param parent
     * @param type
     * @param map
     */
    public void createElement(final INetworkModel model, IDataElement parent, final INodeType type,
            Collection<PropertyContainer> properties) {
        LOGGER.info("Start create new element  from model " + model.getName() + " at " + new Date(System.currentTimeMillis()));

        final IDataElement parentElement = parent == null ? model.asDataElement() : parent;
        final Map<String, Object> prop = preparePropertiesMapFromContainer(properties);
        final String name = (String)prop.get("name");

        Job job = new Job("Create new element  from model " + model.getName()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    model.createElement((INetworkElementType)type, parentElement, name, prop);
                    model.finishUp();
                    LOGGER.info("Finished creating new element  from model " + model.getName()
                            + new Date(System.currentTimeMillis()));
                    ActionUtil.getInstance().runTask(new Runnable() {

                        @Override
                        public void run() {
                            AWEEventManager.getManager().fireDataUpdatedEvent(null);
                        }
                    }, true);

                } catch (Exception e) {
                    LOGGER.error("Can't create new element from model " + model, e);
                    return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "Error on deleting element", e);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();

    }

    public void copyElement(final INetworkModel model, IDataElement element, final INodeType type,
            Collection<PropertyContainer> properties) {
        LOGGER.info("Start copying " + element.getName() + "element  from model " + model.getName() + " at "
                + new Date(System.currentTimeMillis()));
        IDataElement parent;
        try {
            if (model.asDataElement().equals(element)) {
                copyNetworkModel(model, properties);
                return;
            }
            parent = model.getParentElement(element);
        } catch (ModelException e) {
            LOGGER.error("Can't copy element" + element, e);
            return;
        }
        createElement(model, parent, type, properties);
    }

    /**
     * @param model
     * @param properties
     */
    private void copyNetworkModel(INetworkModel model, Collection<PropertyContainer> properties) {
        final Map<String, Object> prop = preparePropertiesMapFromContainer(properties);
        final String name = (String)prop.get("name");
        try {
            INetworkModel newModel = networkModelProvider.createModel(projectModelPovider.getActiveProjectModel(), name,
                    Arrays.asList(model.getNetworkStructure()));
            CopyNetworkJob job = new CopyNetworkJob("Copying elements from " + model.getName() + " to " + newModel.getName(),
                    model, newModel);
            job.schedule();
        } catch (ModelException e) {
            LOGGER.error("can't create model", e);
        }

    }

}
