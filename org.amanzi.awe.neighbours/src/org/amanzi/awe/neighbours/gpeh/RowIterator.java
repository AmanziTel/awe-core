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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
/**
 * 
 * <p>
 * RowIterator implements iterator by statistic by pair (best/interfering cell)
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RowIterator implements Iterator<CellInfo> {
    protected Iterator<Relationship> bestCellIterator;
    protected Iterator<Relationship> interferenceCellIterator;
    protected Relationship bestCellRel;
    protected Node servingCell;
    private Integer ci;
    private Integer rnc;

    /**
     * Instantiates a new row iterator.
     *
     * @param statRoot the stat root
     */
    public RowIterator(Node statRoot) {
        bestCellIterator = statRoot.getRelationships(Direction.OUTGOING).iterator();
        interferenceCellIterator = getemptyIterator();
    }


    /**
     * Gets the empty iterator.
     *
     * @return the empty iterator
     */
    protected Iterator<Relationship> getemptyIterator() {
        return Collections.<Relationship> emptyList().iterator();

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
        while (!interferenceCellIterator.hasNext() && bestCellIterator.hasNext()) {
            bestCellRel = bestCellIterator.next();
            interferenceCellIterator = bestCellRel.getEndNode().getRelationships(Direction.OUTGOING).iterator();
        }
        String[] ciRnc = bestCellRel.getType().name().split("_");
        ci = Integer.valueOf(ciRnc[0]);
        rnc = Integer.valueOf(ciRnc[1]);
    }

    @Override
    public CellInfo next() {
        if (!interferenceCellIterator.hasNext() && bestCellIterator.hasNext()) {
            defineIterator();
        }
        Relationship interfRel = interferenceCellIterator.next();
        String psc = interfRel.getType().name();
        return new CellInfo(ci, rnc, psc, bestCellRel.getEndNode(), interfRel.getEndNode());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}