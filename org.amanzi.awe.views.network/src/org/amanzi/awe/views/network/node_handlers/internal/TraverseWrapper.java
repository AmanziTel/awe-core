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

package org.amanzi.awe.views.network.node_handlers.internal;

import java.util.Iterator;

import org.amanzi.awe.views.network.node_handlers.INeoNode;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Traverser wrapper
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class TraverseWrapper implements Iterable<INeoNode> {

    private final Iterable<Node> source;

    /**
     * @param nodes
     */
    public TraverseWrapper(Iterable<Node> nodes) {
        this.source = nodes;
    }

    @Override
    public Iterator<INeoNode> iterator() {
        return new IteratorWrapper(source.iterator());
    }

}
