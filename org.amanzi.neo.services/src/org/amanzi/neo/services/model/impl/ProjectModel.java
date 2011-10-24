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
import java.util.List;

import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.IRenderableModel;
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
     * Class that describes Distribution Item
     * 
     * It consist of DistributionalModel and Type of Node to Analyze
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

    ProjectService prServ = NeoServiceFactory.getInstance().getNewProjectService();
    NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();

    /**
     * Constructor. Tries to find or create a project node with the defined <code>name</code>, and
     * uses it as the root node for the new project model.
     * 
     * @param name the name of the project.
     */
    public ProjectModel(String name) {
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
        if (projectNode == null) {
            throw new IllegalArgumentException("Project node is null");
        }

        this.rootNode = projectNode;
        this.name = (String)projectNode.getProperty(NewAbstractService.NAME, StringUtils.EMPTY);
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
    public IDriveModel createDataset(String name, IDriveType driveType) {

        try {
            return new DriveModel(rootNode, null, name, driveType);
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return null;
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
     */
    @Override
    public IDriveModel createDataset(String name, IDriveType driveType, INodeType primaryType) {

        try {
            return new DriveModel(rootNode, null, name, driveType, primaryType);
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return null;
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
    public IDriveModel findDataset(String name, IDriveType driveType) {
        Node dataset = null;
        try {
            dataset = dsServ.findDataset(rootNode, name, DatasetTypes.DRIVE, driveType);
            return dataset == null ? null : new DriveModel(null, dataset, name, driveType);
        } catch (AWEException e) {
            LOGGER.error("Could not create drive model.", e);
        }
        return null;
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
    public IDriveModel getDataset(String name, IDriveType driveType) {
        IDriveModel result = findDataset(name, driveType);
        if (result == null) {
            result = createDataset(name, driveType);
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
     * @returna <code>DriveModel</code> object with the defined <code>primaryType</code>, based on
     *          the found or created node with the defined parameters.
     */
    @Override
    public IDriveModel getDataset(String name, IDriveType driveType, INodeType primaryType) {
        IDriveModel result = findDataset(name, driveType, primaryType);
        if (result == null) {
            result = createDataset(name, driveType, primaryType);
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
        ProjectService projectService = NeoServiceFactory.getInstance().getNewProjectService();

        // TODO: LN: since for now we can't use
        // ApplicationGIS.getActiveProject().getName()
        // name of active project will be hard-coded
        Node projectNode = projectService.getProject("project");

        return new ProjectModel(projectNode);
    }

    @Override
    public Iterable<IRenderableModel> getAllRenderableModels() throws AWEException {
        List<IRenderableModel> result = new ArrayList<IRenderableModel>();
        for (Node node : dsServ.findAllDatasets(rootNode)) {
            INodeType type = NodeTypeManager.getType(node.getProperty(NewAbstractService.TYPE, StringUtils.EMPTY).toString());
            if (type != null) {
                if (type.equals(DatasetTypes.DRIVE)) {
                    result.add(new DriveModel(rootNode, node, null, null));
                }
                if (type.equals(DatasetTypes.NETWORK)) {
                    result.add(new NetworkModel(node));
                }
            }
        }
        return result;
    }

    @Override
    public Iterable<INetworkModel> findAllNetworkModels() throws AWEException {
        List<INetworkModel> networkModels = new ArrayList<INetworkModel>();

        List<Node> allNetworkNodes = null;
        allNetworkNodes = dsServ.findAllDatasetsByType(getRootNode(), DatasetTypes.NETWORK);
        for (Node networkRoot : allNetworkNodes) {
            networkModels.add(new NetworkModel(networkRoot));
        }

        return networkModels;
    }

    @Override
    public List<DistributionItem> getAllDistributionalModels() throws AWEException {
        List<DistributionItem> result = new ArrayList<DistributionItem>();
        //first add all NetworkModels and it's n2nrelationship models
        for (INetworkModel network : findAllNetworkModels()) {
            //create Distribution Items for all possible network Types
            for (INodeType nodeType : network.getNetworkStructure()) {
                if (!nodeType.equals(NetworkElementNodeType.NETWORK)) {
                    result.add(new DistributionItem(network, nodeType));
                }
            }
            
            //create Distribution Items for n2n relationships
        }
        
        return result;
    }
}
