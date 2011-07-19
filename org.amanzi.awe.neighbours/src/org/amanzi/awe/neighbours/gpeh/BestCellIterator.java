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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Best cell iterator provide iterator by best cell
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class BestCellIterator implements Iterator<CellInfo> {

	private Iterator<Node> bestCellIterator;
	List<Node> statList = new LinkedList<Node>();

	/**
	 * Instantiates a new best cell iterator.
	 * 
	 * @param statRoot
	 *            the stat root
	 */
	public BestCellIterator(String relString, Node statRoot) {
		statList.clear();

		statRoot = statRoot.getSingleRelationship(
				NetworkRelationshipTypes.CHILD, Direction.OUTGOING)
				.getEndNode();
//		String name = statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
//				.toString();
//		statList.add(statRoot);
		// while (statRoot.hasRelationship(NetworkRelationshipTypes.NEXT,
		// Direction.OUTGOING)) {
		// if (statRoot.hasProperty("statistic property type")
		// && statRoot.getProperty("statistic property type").equals(
		// relString)&&statRoot.getProperty(INeoConstants.PROPERTY_NAME_NAME)
		// .toString().indexOf("_") > -1) {
		// statList.add(statRoot);
		//
		// }
		//
		// statRoot = statRoot.getSingleRelationship(
		// NetworkRelationshipTypes.NEXT, Direction.OUTGOING)
		// .getEndNode();
		// }
		Traverser td = Traversal
				.description()
				.depthFirst()				
				.relationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING)
				.evaluator(new BestCellEvaluator(relString)).traverse(statRoot);
		// bestCellIterator =
		// statRoot.getRelationships(Direction.OUTGOING).iterator();
		bestCellIterator = td.nodes().iterator();
	}

	@Override
	public boolean hasNext() {
		return bestCellIterator.hasNext();
	}

	@Override
	public CellInfo next() {
		// Relationship bestCellRel = bestCellIterator.next();
		// String[] ciRnc = bestCellRel.getType().name().split("_");
		// Integer ci = Integer.valueOf(ciRnc[0]);
		// Integer rnc = Integer.valueOf(ciRnc[1]);
		// return new CellInfo(ci, rnc, null, bestCellRel.getEndNode(), null);

		Node bestCellRel = bestCellIterator.next();
		String[] ciRnc = bestCellRel
				.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString()
				.split("_");
		Integer ci = Integer.valueOf(ciRnc[0]);
		Integer rnc = Integer.valueOf(ciRnc[1]);
		return new CellInfo(ci, rnc, null, bestCellRel, null);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
