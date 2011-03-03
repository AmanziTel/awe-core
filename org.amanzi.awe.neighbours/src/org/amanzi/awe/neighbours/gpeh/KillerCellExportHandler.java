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
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.neo.services.ui.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
/**
 * <p>
 * Handler of  Killer cells analysis
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class KillerCellExportHandler extends IntraModelHandler {
    double sumOfImpacts;
    int countOfImpacts;

    /**
     * Instantiates a new killer cell export handler.
     *
     * @param period the period
     * @param service the service
     */
    public KillerCellExportHandler(CallTimePeriods period, GraphDatabaseService service) {
        super(period, service);
    }

    @Override
    public boolean setData(CellNodeInfo bestCell, InterfCellInfo interfCell) {
        sumOfImpacts=0;
        countOfImpacts=0;
        Iterator<Relationship> iterator = bestCell.getCellSectorInfo().getRelationships(Direction.OUTGOING).iterator();
    //      Pattern pat=Pattern.compile("^(\\D)(\\d)$");
    Pattern pat=Pattern.compile("^(numMrForBestCellIntra)(\\d+)$");
        Set<Long> timestamps=defineTimestamps(bestCell.getCellSectorInfo(),pat, 2);
        if (timestamps.isEmpty()){
            return false;
        }
        int numMr = computeValue(bestCell.getCellSectorInfo(),"numMrForBestCellIntra%s",timestamps);
        while (iterator.hasNext()){
            Node interNode = iterator.next().getEndNode();
            int[]values=new int[5];
            computeArrayValue(values,interNode,"intraEcnoD%s",timestamps);       
            double impact=(double)(values[2]-values[0])/numMr;
            countOfImpacts++;
            sumOfImpacts+=impact;
        }
        if (countOfImpacts==0){
            return false;
        }
        data=new LinkedList<Object>();
        data.add(NeoUtils.getGpehCellName(bestCell.getCellSector(),service));
        data.add(sumOfImpacts/countOfImpacts);
        data.add(sumOfImpacts);
        data.add(countOfImpacts);
        return true;
    }

}
