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
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.index.lucene.LuceneIndexService;


/**
 * <p>
 * Handler of Intra ICDM report
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IntraMatrixProvider extends AbstractGpehExportProvider {
    static final Logger LOGGER = Logger.getLogger(IntraMatrixProvider.class);
    protected Long computeTime;
    protected Iterator<CellInfo> rowIter;
    @SuppressWarnings("rawtypes")
    protected RrcModel model;

    protected RrcModelHandler modelHandler;

    /**
     * Instantiates a new intra matrix provider.
     *
     * @param dataset the dataset
     * @param network the network
     * @param service the service
     * @param period the period
     * @param luceneService the lucene service
     */
    public IntraMatrixProvider(Node dataset, Node network, GraphDatabaseService service, CallTimePeriods period, LuceneIndexService luceneService) {
        super(dataset, network, GpehRelationshipType.RRC, period, service, luceneService);
        computeTime = startTime;

    }
    @Override
    protected void init() {
        super.init();
        defineHandler();
        defineModel();
        defineRowIterator();
        loadModel();
    }


    /**
     * Define row iterator.
     */
    protected void defineRowIterator() {
        rowIter = new RowIterator(statRoot);
    }


    /**
     * Load model.
     */
    @SuppressWarnings("unchecked")
    protected void loadModel() {
        model.load(network, rowIter, service, luceneService);
    }


    /**
     * Define model.
     */
    protected void defineModel() {
        model = new RrcModel<RrcModelHandler>(modelHandler,true);
    }


    /**
     * Define handler.
     */
    protected void defineHandler() {
        modelHandler = new IntraModelHandler(period,service);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasNextLine() {
        while (computeTime < minMax.getRight() || computeTime == startTime) {
            if (modelHandler.getComputeTime() != computeTime) {
                modelHandler.setTime(computeTime);
                model.clearIter();
            }
            while (!modelHandler.haveData()) {
                if (!model.defineNextData()) {
                    break;
                }
            }
            if (modelHandler.haveData()) {
                return true;
            }
            computeTime = period.addPeriod(computeTime);
        }
        return false;
    }

    @Override
    public String getDataName() {
        return "INTRA-FREQUENCY ICDM";
    }

    @Override
    public List<Object> getNextLine() {
        if (!modelHandler.haveData()) {
            hasNextLine();
        }
        List<Object> result = modelHandler.formLine();
        modelHandler.clearData();
        return result;
    }

    @Override
    protected void createHeader() {
        headers = new LinkedList<String>();
        headers.add("Serving cell name");
        headers.add("Serving PSC");
        headers.add("Interfering cell name");
        headers.add("Interfering PSC");
        headers.add("Defined NBR");
        headers.add("Distance");
        headers.add("Tier Distance");
        headers.add("# of MR for best cell");
        headers.add("# of MR for Interfering cell");
        headers.add("EcNo Delta1");
        headers.add("EcNo Delta2");
        headers.add("EcNo Delta3");
        headers.add("EcNo Delta4");
        headers.add("EcNo Delta5");
        headers.add("RSCP Delta1");
        headers.add("RSCP Delta2");
        headers.add("RSCP Delta3");
        headers.add("RSCP Delta4");
        headers.add("RSCP Delta5");
        headers.add("Position1");
        headers.add("Position2");
        headers.add("Position3");
        headers.add("Position4");
        headers.add("Position5");
    }


}
