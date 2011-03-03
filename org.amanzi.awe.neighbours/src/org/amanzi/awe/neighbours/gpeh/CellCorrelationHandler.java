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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.neighbours.gpeh.CellCorrelationProvider.IntRange;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Handler for Cell Correlation analysis
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CellCorrelationHandler extends IntraModelHandler {

    private final ArrayList<IntRange> ecnoRangeNames;
    private final ArrayList<IntRange> rscpRangeNames;
    private Calendar calendar;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat dateFormat2;


    /**
     * Instantiates a new cell correlation handler.
     *
     * @param period the period
     * @param service the service
     * @param ecnoRangeNames the ecno range names
     * @param rscpRangeNames the rscp range names
     * @param dateFormat the date format
     * @param dateFormat2 the date format2
     */
    public CellCorrelationHandler(CallTimePeriods period, GraphDatabaseService service, ArrayList<IntRange> ecnoRangeNames, ArrayList<IntRange> rscpRangeNames,
            SimpleDateFormat dateFormat, SimpleDateFormat dateFormat2) {
        super(period, service);
        this.ecnoRangeNames = ecnoRangeNames;
        this.rscpRangeNames = rscpRangeNames;
        this.dateFormat = dateFormat;
        this.dateFormat2 = dateFormat2;
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
        data.add(dateFormat2.format(calendar.getTime()));
        data.add(period.getId());
        int[][] rscpEcNo = getEcnoRscpArray(bestCell.getCellSectorInfo(), timestamps);
        for (IntRange rscpName : rscpRangeNames) {
            for (IntRange ecnoName : ecnoRangeNames) {
                int count = 0;
                for (int rscp = rscpName.getMin(); rscp <= rscpName.getMax(); rscp++) {
                    for (int ecno = ecnoName.getMin(); ecno <= ecnoName.getMax(); ecno++) {
                        count += rscpEcNo[rscp][ecno];
                    }
                }
                data.add(count);
            }
        }
        return true;
    }




}
