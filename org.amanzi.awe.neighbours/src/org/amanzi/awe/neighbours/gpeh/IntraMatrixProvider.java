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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IntraMatrixProvider implements IExportProvider{
    protected Node dataset;
    protected Node network;
    protected GraphDatabaseService service;
    protected CallTimePeriods period;
    protected LuceneIndexService luceneService;
    protected Pair<Long, Long> minMax;
    protected Long startTime;
    protected Long computeTime;
    protected Node statRoot;
    private List<String> headers=null;

    public IntraMatrixProvider(Node dataset, Node network, GraphDatabaseService service,  CallTimePeriods period, LuceneIndexService luceneService) {
        this.dataset = dataset;
        this.network = network;
        this.service = service;
        this.period = period;
        this.luceneService = luceneService;
        minMax=NeoUtils.getMinMaxTimeOfDataset(dataset, service);
        startTime=period.getFirstTime(minMax.getLeft());
        computeTime=startTime;
        statRoot=defineStatRoot();
    }

    protected Node defineStatRoot() {
        Transaction tx = NeoUtils.beginTx(service);
        try{
           Node statMain= dataset.getSingleRelationship(GpehRelationshipType.GPEH_STATISTICS, Direction.OUTGOING).getOtherNode(dataset);
           return statMain.getSingleRelationship(GpehRelationshipType.RRC, Direction.OUTGOING).getOtherNode(statMain);
        }finally{
            tx.finish();
        }
    }
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasNextLine() {
        return false;
    }

    @Override
    public String getDataName() {
        return null;
    }

    @Override
    public List<Object> getNextLine() {
        return null;
    }

    /**
     * Gets the headers.
     *
     * @return the headers
     */
    @Override
    public List<String> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<String>();
            createHeader();
        }
        return headers;
    }

    /**
     *
     */
    protected void createHeader() {
    }

}
