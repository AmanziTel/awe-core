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

package org.amanzi.neo.services.events;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;

/**
 * <p>
 *Event for node selection
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class SelectEvent extends UpdateViewEvent {

    private final Set<Node>selectedNodes=new HashSet<Node>();
    public SelectEvent(Collection<Node>selectedNodes) {
        super(UpdateViewEventType.SELECT);
        this.selectedNodes.addAll(selectedNodes);
    }
    public Collection<Node> getSelectedNodes(){
        return Collections.unmodifiableCollection(selectedNodes);
    }

}
