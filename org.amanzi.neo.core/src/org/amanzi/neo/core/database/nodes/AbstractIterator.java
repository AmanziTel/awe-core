package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Abstract iterator for Node's children
 * 
 * @author Lagutko_N
 * @param <T>
 *            type of Children
 */

public abstract class AbstractIterator<T extends AbstractNode> implements
		Iterator<T> {

	/*
	 * Iterator of Nodes
	 */
	protected Iterator<Node> iterator;

	/**
	 * Constructor - create iterator with StopEvaluator.DEPTH_ONE
	 * 
	 * @param node
	 *            root node
	 * @param type
	 *            relationship type
	 */
	public AbstractIterator(Node node, SplashRelationshipTypes type) {
		this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE, type,
				Direction.OUTGOING).iterator();
	}

	/**
	 * Default constructor - do nothing
	 */
	public AbstractIterator() {

	}

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
	 * @param node
	 *            node
	 * @return wrapped node
	 */

	protected abstract T wrapNode(Node node);

	public void remove() {
		iterator.remove();
	}

}
