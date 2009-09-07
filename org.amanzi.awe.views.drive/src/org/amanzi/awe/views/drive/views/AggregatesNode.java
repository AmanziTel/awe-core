package org.amanzi.awe.views.drive.views;

import java.util.ArrayList;

import org.amanzi.awe.views.network.proxy.NeoNode;

/**
 * <p>
 * Aggregated node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class AggregatesNode extends DriveNeoNode {

	private final ArrayList<DriveNeoNode> subnodes;

    /**
     * Constructor
     * 
     * @param subnodes - list of subnodes
     */
	public AggregatesNode(ArrayList<DriveNeoNode> subnodes) {
		//for icons sets the first node
		super(subnodes.get(0).getNode());
		this.subnodes = subnodes;
		name="and "+subnodes.size()+" more";
	}
	@Override
	public NeoNode[] getChildren() {
		//TODO adds aggregate subnodes if necessary
		return subnodes.toArray(NO_NODES);
	}
	@Override
	public boolean hasChildren() {
		return !subnodes.isEmpty();
	}
}
