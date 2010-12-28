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

package org.amanzi.neo.services.network;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.node2node.INodeToNodeRelationType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationTypes;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * 
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NetworkModel {
	
	private final Node rootNode;
	private final DatasetService ds;
	
	private final NetworkService networkService;

	public NetworkModel(Node rootNode) {
		this.rootNode = rootNode;
		ds = NeoServiceFactory.getInstance().getDatasetService();
		networkService = NeoServiceFactory.getInstance().getNetworkService();
	}
	
	protected NodeToNodeRelationModel getNodeToNodeRelationModel(INodeToNodeRelationType type, String name) {
		return new NodeToNodeRelationModel(rootNode, type, name);
	}
	
	public NodeToNodeRelationModel getInterferenceMatrix(String name) {
		return getNodeToNodeRelationModel(NodeToNodeRelationTypes.INTERFERENCE_MATRIX, name);
	}
	
	public NodeToNodeRelationModel getShadowing(String name) {
		return getNodeToNodeRelationModel(NodeToNodeRelationTypes.SHADOWING, name);
	}
	
	public NodeToNodeRelationModel getNeighbours(String name) {
		return getNodeToNodeRelationModel(NodeToNodeRelationTypes.NEIGHBOURS, name);
	}
	
	public NodeToNodeRelationModel getTriangulation(String name) {
		return getNodeToNodeRelationModel(NodeToNodeRelationTypes.TRIANGULATION, name);
	}

    public List<Node> findSectorByBsicAndArfcn(String bsic, String arfcn) {
        // IndexHits<Node> nodes = ds.findSectorByBsicAndArfcn(bsic, arfcn);
        return null;
    }
    
    public Node findSector(String bsic, String arfcn) {
        return null;
    }
    
    public Node findSector(String name) {
        return networkService.findSector(rootNode, name, true);
    }

//	public List<NetworkSelectionModel> getSelectionModels() {
//		List<NetworkSelectionModel> models = ds.getSelectionModels(rootNode);
//		return models;
//	}
//
//	public NetworkSelectionModel getSelectionModel(String selectionName) {
//		return null;
//	}
    
    public Node getCarrier(Node sector, Integer trxId) {
        return null;
    }
    
    public ArrayList<Node> getCarriers(Node sector) {
        return null;
    }

}
