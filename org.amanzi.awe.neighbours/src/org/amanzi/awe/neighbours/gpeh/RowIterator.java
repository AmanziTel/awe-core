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

package org.amanzi.awe.neighbours.gpeh;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * 
 * <p>
 * RowIterator implements iterator by statistic by pair (best/interfering cell)
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RowIterator implements Iterator<CellInfo> {
	protected Iterator<Node> bestCellIterator;
	private List<Node> statList = new LinkedList<Node>();
	protected Iterator<Node> interferenceCellIterator;
	protected Node bestCellRel;
	protected Node servingCell;
	private Integer ci;
	private Integer rnc;
	private String name;

	/**
	 * Instantiates a new row iterator.
	 * 
	 * @param statRoot
	 *            the stat root
	 */
	public RowIterator(String reltype, Node statRoot) {
		statList.clear();
		boolean firstElement = true;
		// if (statRoot.hasRelationship(NetworkRelationshipTypes.CHILD,
		// Direction.OUTGOING)) {
		// statRoot = statRoot.getSingleRelationship(
		// NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
		// .getEndNode();
		// }
		// name = statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString();
		// bestCellIterator =
		// statRoot.getRelationships(Direction.OUTGOING).iterator();
		// interferenceCellIterator = getemptyIterator();

		// while (statRoot.hasRelationship(Direction.OUTGOING)) {
		// if (statRoot.hasProperty("statistic property type")
		// && statRoot.getProperty("statistic property type").equals(
		// reltype)
		// && statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString().equals(name) && !firstElement) {
		//
		// statList.add(statRoot);
		// } else if (statRoot.hasProperty("statistic property type")
		// && statRoot.getProperty("statistic property type").equals(
		// reltype)) {
		// firstElement = false;
		// name = statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString();
		// }
		//
		// statRoot = statRoot.getSingleRelationship(
		// NetworkRelationshipTypes.NEXT, Direction.OUTGOING)
		// .getEndNode();
		// }
		// bestCellIterator = statList.iterator();
		// interferenceCellIterator = getemptyIterator();
		statList.clear();
		statRoot = statRoot.getSingleRelationship(
				NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
				.getEndNode();
		// String name = statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString();
		// statList.add(statRoot);
		// while (statRoot.hasRelationship(NetworkRelationshipTypes.NEXT,
		// Direction.OUTGOING)) {
		// if (statRoot.hasProperty("statistic property type")
		// && statRoot.getProperty("statistic property type").equals(
		// reltype)
		// && statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString().indexOf("_") > -1) {
		// statList.add(statRoot);
		// }
		//
		// statRoot = statRoot.getSingleRelationship(
		// NetworkRelationshipTypes.NEXT, Direction.OUTGOING)
		// .getEndNode();
		// }
		// bestCellIterator =
		// statRoot.getRelationships(Direction.OUTGOING).iterator();
		Traverser td = Traversal
				.description()
				.depthFirst()
				.relationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
				.evaluator(new BestCellEvaluator(reltype)).traverse(statRoot);
		bestCellIterator = td.nodes().iterator();
		interferenceCellIterator = getemptyIterator();
	}

	/**
	 * Gets the empty iterator.
	 * 
	 * @return the empty iterator
	 */
	protected Iterator<Node> getemptyIterator() {
		return Collections.<Node> emptyList().iterator();

	}

	@Override
	public boolean hasNext() {
		if (interferenceCellIterator.hasNext()) {
			return true;
		} else if (!bestCellIterator.hasNext()) {
			return false;
		}
		defineIterator();
		return interferenceCellIterator.hasNext();
	}

	/**
	 * Define iterator.
	 */
	protected void defineIterator() {
		List<Node> inteftList = new LinkedList<Node>();
		Node temp;
		String type = null;
		// while (!interferenceCellIterator.hasNext() &&
		// bestCellIterator.hasNext()) {
		// bestCellRel = bestCellIterator.next();
		// interferenceCellIterator =
		// bestCellRel.getEndNode().getRelationships(Direction.OUTGOING).iterator();
		// }
		// String[] ciRnc = bestCellRel.getType().name().split("_");
		// ci = Integer.valueOf(ciRnc[0]);
		// rnc = Integer.valueOf(ciRnc[1]);
		while (!interferenceCellIterator.hasNext()
				&& bestCellIterator.hasNext()) {
			bestCellRel = bestCellIterator.next();
			name = bestCellRel.getProperty(INeoConstants.PROPERTY_NAME_NAME)
					.toString();
			type = bestCellRel.getProperty("statistic property type")
					.toString();
			temp = bestCellRel;
			while (temp.hasRelationship(Direction.OUTGOING)
					&& temp.getProperty("statistic property type").equals(type)) {
				Node n = temp.getSingleRelationship(
						NetworkRelationshipTypes.NEXT, Direction.OUTGOING)
						.getEndNode();
				if (temp.getProperty("statistic property type").equals(type)) {
					inteftList.add(n);
				}

				temp = n;

			}
			interferenceCellIterator = inteftList.iterator();
		}

		String[] ciRnc = bestCellRel
				.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()
				.split("_");
		ci = Integer.valueOf(ciRnc[0]);
		rnc = Integer.valueOf(ciRnc[1]);
	}

	@Override
	public CellInfo next() {
		// if (!interferenceCellIterator.hasNext() &&
		// bestCellIterator.hasNext()) {
		// defineIterator();
		// }
		// Relationship interfRel = interferenceCellIterator.next();
		// String psc = interfRel.getType().name();
		// return new CellInfo(ci, rnc, psc, bestCellRel.getEndNode(),
		// interfRel.getEndNode());
		// }
		if (!interferenceCellIterator.hasNext() && bestCellIterator.hasNext()) {
			defineIterator();
		}
		Node interfRel = interferenceCellIterator.next();
		String psc = interfRel.getProperty(INeoConstants.PROPERTY_NAME_NAME)
				.toString();
		return new CellInfo(ci, rnc, psc, bestCellRel, interfRel);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}