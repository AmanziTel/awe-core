package org.amanzi.splash.neo4j.database.nodes;

import java.util.Iterator;

import org.neo4j.api.core.Node;

/**
 * Abstract iterator for Node's children
 * 
 * @author Lagutko_N
 * @param <T> type of Children
 */

public abstract class AbstractIterator<T extends AbstractNode> implements Iterator<T> {
    
    /*
     * Iterator of Nodes 
     */
    protected Iterator<Node> iterator;
    
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        Node nextNode = iterator.next();
        return wrapNode(nextNode);
    }
    
    /**
     * Abstract method that wraps Node to type that we need
     *
     * @param node node
     * @return wrapped node
     */
    
    protected abstract T wrapNode(Node node);

    public void remove() {
        iterator.remove();
    }
    
}
