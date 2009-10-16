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
package org.amanzi.awe.views.tree.drive.views;

import java.util.ArrayList;
import java.util.Collections;

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
        Collections.sort(this.subnodes, new NeoNodeComparator());
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
