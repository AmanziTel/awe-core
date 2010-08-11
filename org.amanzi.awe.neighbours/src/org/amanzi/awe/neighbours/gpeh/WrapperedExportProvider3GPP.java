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
import org.amanzi.awe.statistic.CallTimePeriods;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class WrapperedExportProvider3GPP extends ExportProvider3GPP {

    private final int cellLen;
    private final boolean downLevel;

    /**
     * @param dataset
     * @param network
     * @param service
     * @param value3gpp
     * @param statRelation
     * @param period
     * @param dataname
     * @param luceneService
     */
    public WrapperedExportProvider3GPP(Node dataset, Node network, GraphDatabaseService service, ValueType value3gpp, RelationshipType statRelation, CallTimePeriods period,
            String dataname, LuceneIndexService luceneService, int cellLen, boolean downLevel) {
        super(dataset, network, service, value3gpp, statRelation, period, dataname, luceneService);
        this.cellLen = cellLen;
        this.downLevel = downLevel;
    }

    @Override
    protected void createArrayHeader() {
        for (int i = value3gpp.getMin3GPP(); i <= value3gpp.getMax3GPP(); i += cellLen) {
            headers.add(String.valueOf(downLevel ? value3gpp.getLeftBound(i) : value3gpp.getRightBound(i)));
        }
    }

    @Override
    protected void processArray(List<Object> result, int[] array) {
        for (int i = value3gpp.getMin3GPP(); i <= value3gpp.getMax3GPP(); i += cellLen) {
            int to = i + cellLen;
            if (to > value3gpp.getMax3GPP()) {
                to = value3gpp.getMax3GPP() + 1;
            }
            int count = 0;
            for (int j = i; j <= to; j++) {
                Integer value = array[j];
                if (value != null) {
                    count += value;
                }
            }
            result.add(count);
        }
    }
}
