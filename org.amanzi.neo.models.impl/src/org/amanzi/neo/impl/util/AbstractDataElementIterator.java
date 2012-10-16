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

package org.amanzi.neo.impl.util;

import java.util.Iterator;

import org.amanzi.neo.dto.IDataElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDataElementIterator<T extends IDataElement> implements IDataElementIterator<T> {

    private Iterator<Node> nodeIterator;

    private T next;

    private boolean computeNext = true;

    protected AbstractDataElementIterator(final Iterator<Node> nodeIterator) {
        this.nodeIterator = nodeIterator;
    }

    protected void updateIterator(final Iterator<Node> nodeIterator) {
        this.nodeIterator = nodeIterator;
    }

    @Override
    public boolean hasNext() {
        if (((next == null) || computeNext) && nodeIterator.hasNext()) {
            do {
                next = createDataElement(nodeIterator.next());
            } while ((next != null) && nodeIterator.hasNext());

            computeNext = false;
        }

        return nodeIterator.hasNext();
    }

    @Override
    public T next() {
        T result = next;

        next = null;
        computeNext = true;

        return result;
    }

    protected abstract T createDataElement(Node node);

    @Override
    public void remove() {
        nodeIterator.remove();
    }

    @Override
    public Iterable<T> toIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return AbstractDataElementIterator.this;
            }
        };
    }
}
