import org.amanzi.awe.neighbours.gpeh.Calculator3GPPdBm.ValueType;
import org.amanzi.awe.neighbours.gpeh.ExportProvider3GPP;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.index.lucene.LuceneIndexService;

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

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NBAPWattExportProvider extends ExportProvider3GPP {

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
    public NBAPWattExportProvider(Node dataset, Node network, GraphDatabaseService service, ValueType value3gpp, RelationshipType statRelation, CallTimePeriods period,
            String dataname, LuceneIndexService luceneService) {
        super(dataset, network, service, value3gpp, statRelation, period, dataname, luceneService);
    }
    @Override
    protected void createHeader() {
        super.createHeader();
        headers.add(1, "maximumTransmissionPower(W)");
    }

    @Override
    protected void createArrayHeader() {
        for (double i = 0.1; i <1; i += 0.1) {
            headers.add(String.format("%1.1f",i));
        }
        for (int i = 1; i <= 100; i ++) {
            headers.add(String.valueOf(i));
        }
    }

}
