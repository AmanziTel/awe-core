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

import java.util.List;

import org.amanzi.awe.neighbours.gpeh.Calculator3GPPdBm.ValueType;
import org.amanzi.awe.statistics.CallTimePeriods;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * Prowider for RTWP report
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class RtwpProvider extends ExportProvider3GPP {

    /**
     * Instantiates a new rtwp provider.
     * 
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param period the period
     * @param dataname the dataname
     * @param luceneService the lucene service
     */
    public RtwpProvider(Node dataset, Node network, GraphDatabaseService service, CallTimePeriods period, String dataname, LuceneIndexService luceneService) {
        super(dataset, network, service, ValueType.UL_INTERFERENCE, GpehRelationshipType.Ul_RTWP, period, dataname, luceneService);
    }

    @Override
    protected void createArrayHeader() {
        for (double i = -112; i <= -90; i += 0.5) {
            headers.add(String.valueOf(String.valueOf(i)));
        }
        headers.add(String.valueOf("-50"));
    }

    @Override
    protected void processArray(List<Object> result, int[] array) {
        int j = 5;
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (i <= 220) {
                count += array[i];
                j--;
                if (j == 0) {
                    result.add(count);
                    count = 0;
                    j = 5;
                }
            } else {
                if (i==221){
                    result.add(count);
                    count=0;
                }
                count += array[i];
            }
        }
        result.add(count);
    }
}
