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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.ISelectionModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 * Selection model
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class SelectionModel extends NetworkModel implements ISelectionModel {
    private static Logger LOGGER = Logger.getLogger(SelectionModel.class);
    private static NewDatasetService dsServ = NeoServiceFactory.getInstance().getNewDatasetService();
    private Node networkRootNode;
    private Node selectionRootNode;
    private GraphDatabaseService graphDb;

    /**
     * should to get DataElement with network Name also should get ProjectNode
     * 
     * @param project node
     * @param rootElement
     * @throws DuplicateNodeNameException
     * @throws DatasetTypeParameterException
     * @throws InvalidDatasetParameterException
     */
    public SelectionModel(Node project, IDataElement rootElement) throws InvalidDatasetParameterException,
            DatasetTypeParameterException, DuplicateNodeNameException {
        super(dsServ.findDataset(project, rootElement.get(INeoConstants.PROPERTY_NAME_NAME).toString(), DatasetTypes.NETWORK));
        networkRootNode = super.getRootNode();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(INeoConstants.PROPERTY_NAME_NAME, networkRootNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "")
                + " Slection Model");
        params.put(INeoConstants.PROPERTY_TYPE_NAME, NetworkElementNodeType.NETWORK.getId());
        graphDb = NeoServiceProvider.getProvider().getService();
        Transaction tx = graphDb.beginTx();
        try {
            selectionRootNode = dsServ.createNode(params);
            networkRootNode.createRelationshipTo(selectionRootNode, NetworkRelationshipTypes.SELECTION);
            tx.success();
        } catch (DatabaseException e) {
            tx.failure();
            // TODO Handle DatabaseException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * try to find sector by name. if find- return iterator of nodes else null
     */
    private Iterator<Node> findElementByName(String name) {
        LOGGER.info("Start finding sector with name " + name);
        INodeType type = NodeTypeManager.getType(NetworkElementNodeType.SECTOR.getId());
        if (StringUtils.isEmpty(name) || name == null) {
            throw new IllegalArgumentException("Sector name is null.");
        }
        Index<Node> index = graphDb.index().forNodes(super.getIndexName(type));
        IndexHits<Node> cis = index.get(INeoConstants.PROPERTY_NAME_NAME, name);
        return cis.iterator();
    }

    @Override
    public void linkToSector(String name) {
        Iterator<Node> findedNodes = findElementByName(name);
        if (findedNodes == null) {
            LOGGER.error("There is no sector with name " + name);
            return;
        }
        if (!findedNodes.hasNext()) {
            LOGGER.info("There is no sectors founded with name " + name);
        }
        Transaction tx = graphDb.beginTx();
        try {
            while (findedNodes.hasNext()) {
                selectionRootNode.createRelationshipTo(findedNodes.next(), NetworkRelationshipTypes.SELECTED);
            }
            LOGGER.info("Linking compleate for sector " + name);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Cann't make relation between selection model and sectors", e);
            tx.failure();
        } finally {
            tx.finish();
        }

    }

    /**
     * @return Returns the selectionRootNode.
     */
    public Node getRoot() {
        return selectionRootNode;
    }

}
