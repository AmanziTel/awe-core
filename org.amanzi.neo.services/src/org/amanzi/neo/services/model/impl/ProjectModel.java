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

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.ProjectService;
import org.amanzi.neo.services.enums.IDriveType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.INetworkModel;
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
public class ProjectModel extends AbstractModel {

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
     * Creates a new drive dataset with the defined <code>name</code> and <code>driveType</code>,
     * attaches it to the current project, and creates a new <code>DriveModel</code>, based on the
     * new dataset.
     * 
     * @param name name of the dataset to create
     * @param driveType drive type of the new dataset
     * @return a <code>DriveModel</code>, based on a new dataset node.
     */
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
    public INetworkModel createNetwork(String name) {
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
    public INetworkModel findNetwork(String name) {
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
    public INetworkModel getNetwork(String name) {
        INetworkModel result = findNetwork(name);
        if (result == null) {
            result = createNetwork(name);
        }
        return result;
    }
}
