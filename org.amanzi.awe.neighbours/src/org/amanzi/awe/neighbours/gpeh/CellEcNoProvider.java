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
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class CellEcNoProvider extends CellCorrelationProvider {

    /**
     * @param dataset
     * @param network
     * @param service
     * @param period
     * @param luceneService
     */
    public CellEcNoProvider(Node dataset, Node network, GraphDatabaseService service, CallTimePeriods period, LuceneIndexService luceneService) {
        super(dataset, network, service, period, luceneService);
    }
    @Override
    protected void defineHandler() {
        modelHandler = new CellEcNoHandler(period, service, dateFormat, dateFormat2);

    }

    @Override
    public String getDataName() {
        return "Cell EcNo analysis";
    }
    @Override
    protected void createHeader() {
        headers = new LinkedList<String>();
        headers.add("Cell Name");
        headers.add("Date");
        headers.add("Time");
        headers.add("Resolution");
        for (double i=-24.5;i<=0;i+=0.5){
            headers.add(String.valueOf(i));
        }
    }

}
