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

import org.amanzi.awe.statistics.CallTimePeriods;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * Provider for Inter ICDM reports
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class InterMatrixProvider extends IntraMatrixProvider {

    /**
     * Instantiates a new inter matrix provider.
     * 
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param period the period
     * @param luceneService the lucene service
     */
    public InterMatrixProvider(Node dataset, Node network, GraphDatabaseService service, CallTimePeriods period, LuceneIndexService luceneService) {
        super(dataset, network, service, period, luceneService);
    }

    @Override
    protected void defineHandler() {
        modelHandler = new InterModelHandler(period, service);
    }

    @Override
    protected void defineModel() {
        model = new RrcModel<RrcModelHandler>(modelHandler, false);
    }

    @Override
    public String getDataName() {
        return "INTER-FREQUENCY ICDM";
    }

    @Override
    protected void createHeader() {
        headers = new LinkedList<String>();
        headers.add("Serving cell name");
        headers.add("Serving PSC");
        headers.add("Serving cell UARFCN");
        headers.add("Overlapping cell name");
        headers.add("Interfering PSC");
        headers.add("Overlapping cell UARCFN");
        headers.add("Defined NBR");
        headers.add("Distance");
        headers.add("Tier Distance");
        headers.add("# of MR for best cell");
        headers.add("# of MR for Interfering cell");
        headers.add("EcNo 1");
        headers.add("EcNo 2");
        headers.add("EcNo 3");
        headers.add("EcNo 4");
        headers.add("EcNo 5");
        headers.add("RSCP1_14");
        headers.add("RSCP2_14");
        headers.add("RSCP3_14");
        headers.add("RSCP4_14");
        headers.add("RSCP5_14");
        headers.add("RSCP1_10");
        headers.add("RSCP2_10");
        headers.add("RSCP3_10");
        headers.add("RSCP4_10");
        headers.add("RSCP5_10");
    }
}
