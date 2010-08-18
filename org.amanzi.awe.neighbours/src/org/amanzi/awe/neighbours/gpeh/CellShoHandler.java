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

import java.util.LinkedList;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Handler for cell SHO report
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CellShoHandler extends IntraModelHandler {

    /**
     * Instantiates a new cell sho handler.
     * 
     * @param service the service
     */
    public CellShoHandler(GraphDatabaseService service) {
        super(CallTimePeriods.ALL, service);
    }

    @Override
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
        data = new LinkedList<Object>();
        data.add(bestCell.getCellSectorInfo().getProperty("oneWay", 0));
        data.add(bestCell.getCellSectorInfo().getProperty("twoWay", 0));
        data.add(bestCell.getCellSectorInfo().getProperty("threeWay", 0));
        return true;

    }

}
