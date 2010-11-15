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
 *Iterator wrapper
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IteratorWrapper implements Iterator<INeoNode> {

    private final Iterator<Node> iterator;

    /**
     * @param iterator
     */
    public IteratorWrapper(Iterator<Node> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public INeoNode next() {
        Node nextNode = iterator.next();
        return new NeoNodeImpl(nextNode);
    }

    @Override
    public void remove() {
        iterator.remove();
    }

}
