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

import org.amanzi.awe.neighbours.gpeh.IntraMatrixProvider.CellInfo;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class BestCellIterator implements Iterator<CellInfo> {

    private Iterator<Relationship> bestCellIterator;

    /**
     * @param statRoot
     */
    public BestCellIterator(Node statRoot) {
        bestCellIterator = statRoot.getRelationships(Direction.OUTGOING).iterator();
    }

    @Override
    public boolean hasNext() {
        return bestCellIterator.hasNext();
    }

    @Override
    public CellInfo next() {
        Relationship bestCellRel = bestCellIterator.next();
        String[] ciRnc = bestCellRel.getType().name().split("_");
        Integer ci = Integer.valueOf(ciRnc[0]);
        Integer rnc = Integer.valueOf(ciRnc[1]);
        return new CellInfo(ci, rnc, null, bestCellRel.getEndNode(), null);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
