package org.amanzi.awe.neighbours;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;

public enum GpehNodeTypes implements INodeType {
	GPEH_CELL_ROOT("cell_root"),
    GPEH_CELL("gpeh_cell"),
	GPEH_EVENT("gpeh_event");

	private GpehNodeTypes(String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	/**
	 * Check node by type
	 * 
	 * @param currentNode
	 *            - node
	 * @return true if node type
	 */
	public boolean checkNode(Node currentNode) {
		return getId().equals(Utils.getNodeType(currentNode, ""));
	}

}
