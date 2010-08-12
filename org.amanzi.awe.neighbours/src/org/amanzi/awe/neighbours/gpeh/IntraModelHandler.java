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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IntraModelHandler extends RrcModelHandler {
    private List<Object> data;
    private final CallTimePeriods period;
    private final GraphDatabaseService service;

    public IntraModelHandler(CallTimePeriods period,GraphDatabaseService service){
        this.period = period;
        this.service = service;
        
    }
    @Override
    public boolean haveData() {
        return data!=null;
    }

    @Override
    public List<Object> formLine() {
        return data;
    }

    @Override
    public void clearData() {
        data=null;
    }

    @Override
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
//        Pattern pat=Pattern.compile("^(\\D)(\\d)$");
        Pattern pat=Pattern.compile("^(numMrForBestCell)(\\d)$");
        Set<Long> timestamps=defineTimestamps(bestCell.getCellSectorInfo(),pat);
        if (timestamps.isEmpty()){
            return false;
        }
        data=new LinkedList<Object>();
        data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(),service));
        data.add((String)bestCell.getCellSector().getProperty(GpehReportUtil.PRIMARY_SCR_CODE, ""));
        data.add(NeoUtils.getGpehCellName(interfCell.getCellSector(),service));
        data.add((String)interfCell.getPsc());
        data.add(interfCell.isDifinedNeighbour(bestCell.getCellSector(),service));
        // Distance
        data.add(interfCell.getDistance());
        // Tier Distance
        String value = String.valueOf("N/A");
        data.add(value);
        // # of MR for best cell
        
        data.add(computeValue(bestCell.getCellSectorInfo(),"numMrForBestCell%s",timestamps));
        // # of MR for Interfering cell
        data.add(0);
        // Delta EcNo 1-5
        for (int i = 1; i <= 5; i++) {
            data.add(0);
        }
        for (int i = 1; i <= 5; i++) {
            data.add(0);
        }
        for (int i = 1; i <= 5; i++) {
            data.add(0);
        }
        return true;
    }
    /**
     *
     * @param cellSectorInfo
     * @param string
     * @param timestamps
     * @return
     */
    private int computeValue(Node cellSectorInfo, String string, Set<Long> timestamps) {
        return 0;
    }
    /**
     *
     * @param node
     * @param pat 
     * @return
     */
    private Set<Long> defineTimestamps(Node node, Pattern pat) {
        Set<Long> result=new HashSet<Long>();
        for (String propertyName:node.getPropertyKeys()){
             Matcher mat = pat.matcher(propertyName);
             if (mat.matches()){
                 result.add(Long.valueOf(mat.group(2)));
             }
        }
        return result;
    }

}
