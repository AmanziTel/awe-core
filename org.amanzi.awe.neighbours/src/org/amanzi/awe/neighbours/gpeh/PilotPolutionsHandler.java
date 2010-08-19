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
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Handler for pilot polutions reports
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class PilotPolutionsHandler extends IntraModelHandler {

    /**
     * Instantiates a new pilot polutions handler.
     * 
     * @param period the period
     * @param service the service
     */
    public PilotPolutionsHandler(CallTimePeriods period, GraphDatabaseService service) {
        super(period, service);
    }

    @Override
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
        // Pattern pat=Pattern.compile("^(\\D)(\\d)$");
        Pattern pat = Pattern.compile("^(intraMr)(\\d+)$");
        Set<Long> timestamps = defineTimestamps(interfCell.getCellSectorInfo(), pat, 2);
        if (timestamps.isEmpty()) {
            return false;
        }
        data = new LinkedList<Object>();
        data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(), service));
        data.add((String)bestCell.getCellSector().getProperty(GpehReportUtil.PRIMARY_SCR_CODE, ""));
        data.add(NeoUtils.getGpehCellName(interfCell.getCellSector(), service));
        data.add((String)interfCell.getPsc());
        data.add(interfCell.isDifinedNeighbour(bestCell.getCellSector(), service));
        // Distance
        data.add(interfCell.getDistance());
        // Tier Distance
        String value = String.valueOf("N/A");
        data.add(value);
        // # of MR for best cell

        data.add(computeValue(bestCell.getCellSectorInfo(), "numMrForBestCellIntra%s", timestamps));
        // # of MR for Interfering cell
        data.add(computeValue(interfCell.getCellSectorInfo(), "intraMr%s", timestamps));
        int[] values = new int[5];
        computeArrayValue(values, interfCell.getCellSectorInfo(), "intraEcnoD%s", timestamps);
        // Delta EcNo 1-5
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }
        values = new int[5];
        computeArrayValue(values, interfCell.getCellSectorInfo(), "intraRscpD%s", timestamps);
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }
        values = new int[5];
        computeArrayValue(values, interfCell.getCellSectorInfo(), "positions%s", timestamps);
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }

        return true;
    }
}
