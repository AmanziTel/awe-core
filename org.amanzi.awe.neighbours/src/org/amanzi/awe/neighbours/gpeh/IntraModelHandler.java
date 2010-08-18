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
 * <p>
 *Hondler for Intra ICDM reports
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IntraModelHandler extends RrcModelHandler {
    protected List<Object> data;
    protected final CallTimePeriods period;
    protected final GraphDatabaseService service;

    /**
     * Instantiates a new intra model handler.
     *
     * @param period the period
     * @param service the service
     */
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
        Pattern pat=Pattern.compile("^(intraMr)(\\d+)$");
        Set<Long> timestamps=defineTimestamps(interfCell.getCellSectorInfo(),pat, 2);
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
        
        data.add(computeValue(bestCell.getCellSectorInfo(),"numMrForBestCellIntra%s",timestamps));
        // # of MR for Interfering cell
        data.add(computeValue(interfCell.getCellSectorInfo(),"intraMr%s",timestamps));
        int[]values=new int[5];
        computeArrayValue(values,interfCell.getCellSectorInfo(),"intraEcnoD%s",timestamps);
        // Delta EcNo 1-5
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }
        values=new int[5];
        computeArrayValue(values,interfCell.getCellSectorInfo(),"intraRscpD%s",timestamps);
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }
        values=new int[5];
        computeArrayValue(values,interfCell.getCellSectorInfo(),"positions%s",timestamps);
        for (int i = 0; i < 5; i++) {
            data.add(values[i]);
        }

        return true;
    }

    /**
     * Compute array value.
     *
     * @param result the result array
     * @param cellSectorInfo the cell sector info
     * @param string the formatted property name
     * @param timestamps the timestamps
     */
    protected void computeArrayValue(int result[],Node cellSectorInfo, String string, Set<Long> timestamps) {
        for (Long time:timestamps){
            int[] others = (int[])cellSectorInfo.getProperty(String.format(string, time), null);
            if (others!=null){
                for (int i = 0; i < result.length; i++) {
                    result[i]+=others[i];
                }
            }
        }
        return;
    }

    /**
     * Compute value.
     *
     * @param cellSectorInfo the cell sector info
     * @param string the formatted property name
     * @param timestamps the timestamps
     * @return the int
     */
    protected int computeValue(Node cellSectorInfo, String string, Set<Long> timestamps) {
        int result=0;
        for (Long time:timestamps){
            result+=(Integer)cellSectorInfo.getProperty(String.format(string, time), 0);
        }
        return result;
    }

    /**
     * Define timestamps.
     *
     * @param node the node
     * @param pat the pat
     * @param groupNum the group num
     * @return the set
     */
    protected Set<Long> defineTimestamps(Node node, Pattern pat, int groupNum) {
        Set<Long> result=new HashSet<Long>();
        for (String propertyName:node.getPropertyKeys()){
             Matcher mat = pat.matcher(propertyName);
             if (mat.matches()){
                 Long time = Long.valueOf(mat.group(groupNum));
                 if (period.compareByPeriods(time, computeTime)==0){
                     result.add(time);
                 }
             }
        }
        return result;
    }
    
    /**
     * Gets the ecno rscp array.
     *
     * @param node the node
     * @param timestamps the timestamps
     * @return the ecno rscp array
     */
    protected int[][] getEcnoRscpArray(Node node, Set<Long> timestamps) {
        int[][] result = new int[92][50];
        for (int i = 0; i < 92; i++) {
            computeArrayValue(result[i], node, new StringBuilder("%srscp").append(i).toString(), timestamps);
        }
        return result;
    }
}
