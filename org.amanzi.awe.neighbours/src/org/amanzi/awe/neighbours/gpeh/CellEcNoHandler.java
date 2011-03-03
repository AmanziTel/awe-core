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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Handler for RRC EcNo reports
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CellEcNoHandler extends IntraModelHandler {
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;
    private Calendar calendar;

    /**
     * Instantiates a new cell ec no handler.
     * 
     * @param period the period
     * @param service the service
     * @param dateFormat the date format
     * @param timeFormat the time format
     */
    public CellEcNoHandler(CallTimePeriods period, GraphDatabaseService service, SimpleDateFormat dateFormat, SimpleDateFormat timeFormat) {
        super(period, service);
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        calendar = Calendar.getInstance();
    }

    @Override
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
        // Pattern pat=Pattern.compile("^(\\D)(\\d)$");
        Pattern pat = Pattern.compile("^(\\d+)(rscp0)$");
        Set<Long> timestamps = defineTimestamps(bestCell.getCellSectorInfo(), pat, 1);
        if (timestamps.isEmpty()) {
            return false;
        }
        // Serving cell name
        data = new LinkedList<Object>();
        data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(), service));
        calendar.setTimeInMillis(computeTime);
        data.add(dateFormat.format(calendar.getTime()));
        data.add(timeFormat.format(calendar.getTime()));
        data.add(period.getId());
        int[][] rscpEcNo = getEcnoRscpArray(bestCell.getCellSectorInfo(), timestamps);

        for (int j = 0; j < 50; j++) {
            int value = 0;
            for (int i = 0; i < 92; i++) {
                value += rscpEcNo[i][j];
            }
            data.add(value);
        }
        return true;
    }

}
