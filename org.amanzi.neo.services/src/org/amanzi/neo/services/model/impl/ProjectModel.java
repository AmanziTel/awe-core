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

package org.amanzi.neo.services.model.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.ProjectService.ProjectNodeType;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.ICountersModel;
import org.amanzi.neo.services.model.ICountersType;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * The model for managing projects. Currently all the errors are only logged.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class ProjectModel extends AbstractModel implements IProjectModel {

    /**
     * Class that describes Distribution Item It consist of DistributionalModel and Type of Node to
     * Analyze
     * 
     * @author gerzog
     * @since 1.0.0
     */
    public static class DistributionItem {

        /*
         * Model for Distribution
         */
        private IDistributionalModel model;

        /*
         * Type of Node to Analyze
         */
        private INodeType nodeType;

        /**
         * Constructor for multi-type models
         * 
         * @param model
         * @param nodeType
         */
        public DistributionItem(IDistributionalModel model, INodeType nodeType) {
            this.model = model;
            this.nodeType = nodeType;
        }

        /**
         * Constructor for single-type models
         * 
         * @param model
         */
        public DistributionItem(IDistributionalModel model) {
            this.model = model;
        }

        /**
         * Returns model
         * 
         * @return
         */
        public IDistributionalModel getModel() {
            return model;
        }

        /**
         * Returns NodeType
         * 
         * @return
         */
        public INodeType getNodeType() {
            if (nodeType == null)
                return model.getType();
            return nodeType;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder(model.getName());

            if (nodeType != null) {
                result.append(" - ").append(nodeType.getId());
            }

            return result.toString();
        }

    }

    private static Logger LOGGER = Logger.getLogger(ProjectModel.class);

    ProjectService prServ = NeoServiceFactory.getInstance().getProjectService();
    DatasetService dsServ = NeoServiceFactory.getInstance().getDatasetService();

    /**
     * Constructor. Tries to find or create a project node with the defined <code>name</code>, and
     * uses it as the root node for the new project model.
     * 
     * @param name the name of the project.
     */
    public ProjectModel(String name) {
        super(ProjectNodeType.PROJECT);

        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalArgumentException("Name is null or empty.");
        }
        try {
            this.rootNode = prServ.getProject(name);
            this.name = name;
        } catch (AWEException e) {
            LOGGER.error("Could not create project model.", e);
        }
    }

    /**
     * Constructor for internal static methods
     * 
     * @param projectNode node for a Project
     */
    public ProjectModel(Node projectNode) {
        super(ProjectNodeType.PROJECT);

        if (projectNode == null) {
            throw new IllegalArgumentException("Project node is null");
        }

        this.rootNode = projectNode;
        this.name = (String)projectNode.getProperty(AbstractService.NAME, StringUtils.EMPTY);
    }

    /**
     * Creates a new drive dataset with the defined <code>name</code> and <code>driveType</code>,
     * attaches it to the current project, and creates a new <code>DriveModel</code>, based on the
     * new dataset.
     * 
     * @param name name of the dataset to create
     * @param driveType drive type of the new dataset
     * @return a <code>DriveModel</code>, based on a new dataset node.
     */
    @Override
    public IDriveModel createDriveModel(String name, IDriveType driveType) {

        try {
            return createDriveModel(name, driveType, DriveNodeTypes.M);
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return null;
    }

    @Override
    public ICountersModel createCountersModel(String name, ICountersType driveType, INodeType primaryType) throws AWEException {

        Node countersRoot = null;
        try {
            countersRoot = dsServ.createDataset(rootNode, name, DatasetTypes.COUNTERS, driveType, primaryType);
        } catch (AWEException e) {
            LOGGER.error("Could not create network model.", e);
        }
        return countersRoot == null ? null : new CountersModel(countersRoot);
    }

    /**
     * Creates a <code>DriveModel</code> based on a new dataset, attached to the current project,
     * with the defined <code>name</code> and <code>driveType</code>, and additionally sets drive
     * model's primary node type.
     * 
     * @param name name of the dataset to create
     * @param driveType drive type of the new dataset
     * @param primaryType <code>DriveModel</code> primary type
     * @return a <code>DriveModel</code> object with the defined primary type
     * @throws AWEException
     */
    @Override
    public IDriveModel createDriveModel(String name, IDriveType driveType, INodeType primaryType) throws AWEException {

        Node driveRoot = null;
        try {
            driveRoot = dsServ.createDataset(rootNode, name, DatasetTypes.DRIVE, driveType, primaryType);
        } catch (AWEException e) {
            LOGGER.error("Could not create network model.", e);
        }
        return driveRoot == null ? null : new DriveModel(driveRoot);
    }

    /**
     * Tries to find a dataset node within the current project, with the defined <code>name</code>
     * and <code>driveType</code>. If found, creates a new <code>DriveModel</code>, based on it.
     * 
     * @param name
     * @param driveType
     * @return a <code>DriveModel</code>, based on the found node, or <code>null</code>
     */
    @Override
    public IDriveModel findDriveModel(String name, IDriveType driveType) {
        IDriveModel dataset = null;
        try {
            Node datasetNode = dsServ.findDataset(rootNode, name, DatasetTypes.DRIVE, driveType);
            if (datasetNode != null) {
                dataset = new DriveModel(datasetNode);
            }
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return dataset;
    }

    @Override
    public ICountersModel findCountersModel(String name, ICountersType driveType) {
        ICountersModel dataset = null;
        try {
            Node datasetNode = dsServ.findDataset(rootNode, name, DatasetTypes.COUNTERS, driveType);
            if (datasetNode != null) {
                dataset = new CountersModel(datasetNode);
            }
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return dataset;
    }

    /**
     * Tries to find a dataset node within the current project, with the defined <code>name</code>
     * and <code>driveType</code>. If found, creates a new <code>DriveModel</code>, based on it, and
     * sets its primary type to <code>primaryType</code>.
     * 
     * @param name
     * @param driveType
     * @return a <code>DriveModel</code>, based on the found node, or <code>null</code>
     */
    protected IDriveModel findDataset(String name, IDriveType driveType, INodeType primaryType) {
        Node dataset = null;
        try {
            dataset = dsServ.findDataset(rootNode, name, DatasetTypes.DRIVE, driveType);
            return dataset == null ? null : new DriveModel(null, dataset, name, driveType, primaryType);
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return null;
    }

    /**
     * Tries to find a dataset node within the current project, with the defined <code>name</code>
     * and <code>driveType</code>; creates a new dataset node, if nothing found. Creates a new
     * <code>DriveModel</code> object, based on the resulting node.
     * 
     * @param name
     * @param driveType
     * @return a <code>DriveModel</code> object, based on the found or created node with the defined
     *         parameters.
     */
    @Override
    public IDriveModel getDriveModel(String name, IDriveType driveType) {
        IDriveModel result = findDriveModel(name, driveType);
        if (result == null) {
            result = createDriveModel(name, driveType);
        }
        return result;
    }

    /**
     * Tries to find a dataset node within the current project, with the defined <code>name</code>
     * and <code>driveType</code>; creates a new dataset node, if nothing found. Creates a new
     * <code>DriveModel</code> object, based on the resulting node, and sets its primary node type.
     * 
     * @param name
     * @param driveType
     * @param primaryType
     * @throws AWEException
     * @returna <code>DriveModel</code> object with the defined <code>primaryType</code>, based on
     *          the found or created node with the defined parameters.
     */
    @Override
    public IDriveModel getDrive(String name, IDriveType driveType, INodeType primaryType) throws AWEException {
        IDriveModel result = findDataset(name, driveType, primaryType);
        if (result == null) {
            result = createDriveModel(name, driveType, primaryType);
        }
        return result;
    }

    /**
     * Creates a new network node with the defined <code>name</code>, attaches it to the current
     * project node, and creates a new <code>NetworkModel</code> object, based on the new network
     * node.
     * 
     * @param name
     * @return a <code>NetworkModel</code>, based on the new network node
     */
    @Override
    public INetworkModel createNetwork(String name) throws AWEException {
        Node networkRoot = null;
        try {
            networkRoot = dsServ.createDataset(rootNode, name, DatasetTypes.NETWORK);
        } catch (AWEException e) {
            LOGGER.error("Could not create network model.", e);
        }
        return networkRoot == null ? null : new NetworkModel(networkRoot);
    }

    /**
     * Tries to find a network node with the defined <code>name</code> within the current project.
     * If found, creates a new <code>NetworkModel</code>, based on it.
     * 
     * @param name
     * @return a <code>NetworkModel</code> object, based on the found node, or <code>null</code>
     */
    @Override
    public INetworkModel findNetwork(String name) throws AWEException {
        Node networkRoot = null;
        try {
            networkRoot = dsServ.findDataset(rootNode, name, DatasetTypes.NETWORK);
        } catch (AWEException e) {
            LOGGER.error("Could not create network model.", e);
        }
        return networkRoot == null ? null : new NetworkModel(networkRoot);
    }

    /**
     * Tries to find a network node with the defined <code>name</code> within the current project;
     * if not found, creates a new network node. Creates a new <code>NetworkModel</code>, based on
     * the resulting node.
     * 
     * @param name
     * @return a <code>NetworkModel</code>, based on the found or created node.
     */
    @Override
    public INetworkModel getNetwork(String name) throws AWEException {
        INetworkModel result = findNetwork(name);
        if (result == null) {
            result = createNetwork(name);
        }
        return result;
    }

    /**
     * Returns a DB Model of currently Active Project from uDIG
     * 
     * @return Model of active Project
     * @throws AWEException
     */
    public static IProjectModel getCurrentProjectModel() throws AWEException {
        ProjectService projectService = NeoServiceFactory.getInstance().getProjectService();

        Node projectNode = projectService.getProject(ApplicationGIS.getActiveProject().getName());
        return new ProjectModel(projectNode);
    }

    /**
     * Reset active project
     * 
     * @return Model of active Project
     * @throws AWEException
     */
    public static IProjectModel setActiveProject(String projectName) throws AWEException {
        ProjectService projectService = NeoServiceFactory.getInstance().getProjectService();

        Node projectNode = projectService.getProject(projectName);
        return new ProjectModel(projectNode);
    }

    /**
     * find all existed project models.
     * 
     * @return Iterable of projectsModels
     * @throws AWEException
     */
    public static Iterable<IProjectModel> findAllProjectModels() throws AWEException {
        ProjectService projectService = NeoServiceFactory.getInstance().getProjectService();
        List<IProjectModel> projectList = new LinkedList<IProjectModel>();

        for (Node projectNode : projectService.findAllProjects()) {
            projectList.add(new ProjectModel(projectNode));
        }

        return projectList;
    }

    @Override
    public Iterable<IRenderableModel> getAllRenderableModels() throws AWEException {
        List<IRenderableModel> result = new ArrayList<IRenderableModel>();
        for (DatasetTypes type : DatasetTypes.getRenderableDatasets()) {
            for (Node node : dsServ.findAllDatasetsByType(rootNode, type)) {
                switch (type) {
                case NETWORK:
                    result.add(new NetworkModel(node));
                    break;
                case DRIVE:
                    result.add(new DriveModel(rootNode, node, null, null));
                    break;
                case COUNTERS:
                    result.add(new CountersModel(rootNode, node, null, null));
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Iterable<INetworkModel> findAllNetworkModels() throws AWEException {
        List<INetworkModel> networkModels = new ArrayList<INetworkModel>();

        List<Node> allNetworkNodes = findAllMeasurmentModels(DatasetTypes.NETWORK);
        for (Node networkRoot : allNetworkNodes) {
            networkModels.add(new NetworkModel(networkRoot));
        }

        return networkModels;
    }

    private List<Node> findAllMeasurmentModels(DatasetTypes datasetType) throws AWEException {

        List<Node> allNetworkNodes = null;
        allNetworkNodes = dsServ.findAllDatasetsByType(getRootNode(), datasetType);

        return allNetworkNodes;
    }

    @Override
    public Iterable<IDriveModel> findAllDriveModels() throws AWEException {
        List<IDriveModel> datasets = new ArrayList<IDriveModel>();

        List<Node> allNetworkNodes = findAllMeasurmentModels(DatasetTypes.DRIVE);
        for (Node networkRoot : allNetworkNodes) {
            datasets.add(new DriveModel(networkRoot));
        }

        return datasets;
    }

    @Override
    public Iterable<IPropertyStatisticalModel> findAllModels() throws AWEException {
        List<IPropertyStatisticalModel> datasets = new ArrayList<IPropertyStatisticalModel>();
        Iterable<INetworkModel> networks = findAllNetworkModels();
        for (IPropertyStatisticalModel model : networks) {
            datasets.add(model);
        }
        Iterable<IDriveModel> drives = findAllDriveModels();
        for (IPropertyStatisticalModel model : drives) {
            datasets.add(model);
        }
        // TODO add counters
        return datasets;
    }

    @Override
    public List<DistributionItem> getAllDistributionalModels() throws AWEException {
        List<DistributionItem> result = new ArrayList<DistributionItem>();
        // first add all NetworkModels and it's n2nrelationship models
        for (INetworkModel network : findAllNetworkModels()) {
            // create Distribution Items for all possible network Types
            for (INodeType nodeType : network.getNetworkStructure()) {
                result.add(new DistributionItem(network, nodeType));
            }
            // create Distribution Items for n2n relationships
            for (INodeToNodeRelationsModel n2nModel : network.getNodeToNodeModels()) {
                result.add(new DistributionItem(n2nModel));
            }
        }
        // add all drive models
        for (IDriveModel drive : findAllDriveModels()) {
            result.add(new DistributionItem(drive, drive.getPrimaryType()));
            // add virtual model for current drive
            for (IDriveModel virtualDriveModel : drive.getVirtualDatasets()) {
                result.add(new DistributionItem(virtualDriveModel, virtualDriveModel.getPrimaryType()));
            }
        }
        // add all counters models
        for (ICountersModel counter : findAllCountersModel()) {
            result.add(new DistributionItem(counter, counter.getPrimaryType()));
        }
        return result;
    }

    @Override
    public Iterable<ICountersModel> findAllCountersModel() throws AWEException {
        List<ICountersModel> datasets = new ArrayList<ICountersModel>();

        List<Node> allNetworkNodes = findAllMeasurmentModels(DatasetTypes.COUNTERS);
        for (Node counterRoot : allNetworkNodes) {
            ICountersModel model = new CountersModel(counterRoot);
            datasets.add(model);
        }

        return datasets;
    }

    @Override
    public ICountersModel getCountersModel(String name, ICountersType countersType) throws AWEException {
        ICountersModel result = findCountersModel(name, countersType);
        if (result == null) {
            result = createCountersModel(name, countersType);
        }
        return result;
    }

    @Override
    public ICountersModel createCountersModel(String name, ICountersType type) throws AWEException {
        try {
            return createCountersModel(name, type, DriveNodeTypes.M);
        } catch (AWEException e) {
            LOGGER.error("Could not create counters model.", e);
        }
        return null;
    }
}
