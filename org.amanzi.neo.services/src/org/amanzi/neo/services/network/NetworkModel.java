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
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.neo4j.graphdb.GraphDatabaseService;
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
	
	protected NodeToNodeRelationModel getNodeToNodeRelationModel(INodeToNodeType type, String name) {
		return new NodeToNodeRelationModel(rootNode, type, name);
	}
	
	public NodeToNodeRelationModel getInterferenceMatrix(String name) {
		return getNodeToNodeRelationModel(NodeToNodeTypes.INTERFERENCE_MATRIX, name);
	}
	
	public NodeToNodeRelationModel getShadowing(String name) {
		return getNodeToNodeRelationModel(NodeToNodeTypes.SHADOWING, name);
	}
	
	public NodeToNodeRelationModel getNeighbours(String name) {
		return getNodeToNodeRelationModel(NodeToNodeTypes.NEIGHBOURS, name);
	}
	
	public NodeToNodeRelationModel getTriangulation(String name) {
		return getNodeToNodeRelationModel(NodeToNodeTypes.TRIANGULATION, name);
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
    
    public Node createCarrier(Node sectork, String subCell, Integer trxId, String band, Integer hoppingType, Boolean bcch) {
        return null;
    }
    
    public ArrayList<Node> getCarriers(Node sector) {
        return null;
    }
    
    public Node createPlan(Node carrierNode, String hsn, String maio, Integer[] arfcnArray, final GraphDatabaseService service) {
        //create a node
        Node plan = service.createNode();
        
        //set main properties
        plan.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.FREQUENCY_PLAN.getId());
        plan.setProperty(INeoConstants.PROPERTY_NAME_NAME, "original");
        
        if (hsn != null) {
            //set hsn-property
            plan.setProperty("hsn", hsn);
        }
        if (maio != null) {
            //set maio-property
            plan.setProperty("maio", maio);
        }
        //set arfcn array
        plan.setProperty("arfcn", arfcnArray);
        
        //add to carrier
        carrierNode.createRelationshipTo(plan, DatasetRelationshipTypes.PLAN_ENTRY);
        
        return plan;
    }
    
    public Node createCarrier(Node sectorNode, Integer trxId, String band, String extended, Integer hoppingType, boolean bcch, final GraphDatabaseService service) {
        //create a node
        Node carrier = service.createNode();
        
        //set main properties
        carrier.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.TRX.getId());
        carrier.setProperty(INeoConstants.PROPERTY_NAME_NAME, trxId.toString());
        
        //set additional properties
        carrier.setProperty("trx_id", trxId);
        carrier.setProperty("band", band);
        if (extended != null) {
            carrier.setProperty("extended", extended);
        }
        carrier.setProperty("hopping_type", hoppingType);
        carrier.setProperty("bcch", bcch);
        
        //add to sector's children
        
        sectorNode.createRelationshipTo(carrier, GeoNeoRelationshipTypes.CHILD);
        
        return carrier;
    }

}
