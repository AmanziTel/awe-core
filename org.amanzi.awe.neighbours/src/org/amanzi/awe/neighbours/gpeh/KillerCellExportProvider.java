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
 * 
 * <p>
 *Provider of  Killer cells analysis
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class KillerCellExportProvider extends CellCorrelationProvider {


    /**
     * Instantiates a new killer cell export provider.
     *
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param luceneService the lucene service
     */
    public KillerCellExportProvider(Node dataset, Node network, GraphDatabaseService service,  LuceneIndexService luceneService) {
        super(dataset, network, service, CallTimePeriods.ALL, luceneService);
    }
    @Override
    protected void createHeader() {
        headers = new LinkedList<String>();
        headers.add("Cell name");
        headers.add("Killer Cell Rank");
        headers.add("Sum of Impacts");
        headers.add("# of Impacts");
    }
    @Override
    protected void defineHandler() {
        modelHandler = new KillerCellExportHandler(period,service);
    }
    @Override
    public String getDataName() {
        return "Killer cell analysis";
    }
}
