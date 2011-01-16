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

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.node2node.INodeToNodeType;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
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
		//TODO define - should we always create new instance?
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
    
    public Node findSector(String name) {
        return networkService.findSector(rootNode, name, true);
    }
    public NodeResult getPlan(Node carrierNode,String fileName) {
        return networkService.getPlanNode(carrierNode, fileName);
    }
    
    public NodeResult getCarrier(Node sector, String trxId, Integer channelGr) {
        return networkService.getTRXNode(sector, trxId, channelGr);
    }

    


}
