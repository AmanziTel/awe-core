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
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService.NodeToNodeRelationshipTypes;
import org.amanzi.neo.services.node2node.NodeToNodeRelationTypes;
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

	public NetworkModel(Node rootNode) {
		this.rootNode = rootNode;
		ds = NeoServiceFactory.getInstance().getDatasetService();
	}
	
//	public NodeToNodeRelationModel getInterferenceMatrix() {
//		return new NodeToNodeRelationModel(rootNode, NodeToNodeRelationTypes.INTERFERENCE_MATRIX);
//	}

//	public List<NetworkSelectionModel> getSelectionModels() {
//		List<NetworkSelectionModel> models = ds.getSelectionModels(rootNode);
//		return models;
//	}
//
//	public NetworkSelectionModel getSelectionModel(String selectionName) {
//		return null;
//	}

}
