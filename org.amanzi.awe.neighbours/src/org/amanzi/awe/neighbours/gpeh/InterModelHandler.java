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
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class InterModelHandler extends IntraModelHandler {

    /**
     * @param period
     * @param service
     */
    public InterModelHandler(CallTimePeriods period, GraphDatabaseService service) {
        super(period, service);
    }
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
//      Pattern pat=Pattern.compile("^(\\D)(\\d)$");
      Pattern pat=Pattern.compile("^(interMr)(\\d+)$");
      Set<Long> timestamps=defineTimestamps(interfCell.getCellSectorInfo(),pat, 2);
      if (timestamps.isEmpty()){
          return false;
      }
      // Serving cell name
      data=new LinkedList<Object>();
      data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(),service));
      // psc
      data.add((String)bestCell.getCellSector().getProperty(GpehReportUtil.PRIMARY_SCR_CODE, ""));
      // UARFCN
      data.add(bestCell.getCellSector().getProperty("uarfcnDl", "").toString());
      // Interfering cell name
      data.add(NeoUtils.getGpehCellName(interfCell.getCellSector(),service));
      // Interfering PSC
      data.add((String)interfCell.getPsc());
      // UARFCN
      data.add(interfCell.getCellSector().getProperty("uarfcnDl", "").toString());
      // Defined NBR
      data.add(interfCell.isDifinedNeighbour(bestCell.getCellSector(),service));
      // Distance
      data.add(interfCell.getDistance());
      // Tier Distance
      String value = String.valueOf("N/A");
      data.add(value);
      // # of MR for best cell
      data.add(computeValue(bestCell.getCellSectorInfo(),"numMrForBestCell%s",timestamps));
      // # of MR for Interfering cell
      data.add(computeValue(interfCell.getCellSectorInfo(),"intraMr%s",timestamps));
      // EcNo 1-5
      int[]values=new int[5];
      computeArrayValue(values,interfCell.getCellSectorInfo(),"interEcno%s",timestamps);
      for (int i = 0; i < 5; i++) {
          data.add(values[i]);
      }
      values=new int[10];
      computeArrayValue(values,interfCell.getCellSectorInfo(),"interRscp%s",timestamps);
      for (int i = 0; i < 10; i++) {
          data.add(values[i]);
      }

      return true;
  }


}
