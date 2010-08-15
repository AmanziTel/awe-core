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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 *Abstract class for GPEH export providers
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class AbstractGpehExportProvider implements IExportProvider{
    protected final Node dataset;
    protected final Node network;
    protected final RelationshipType statRelation;
    protected final CallTimePeriods period;
    protected final GraphDatabaseService service;
    protected  Node statRoot;
    protected final LuceneIndexService luceneService;
    protected Pair<Long, Long> minMax;
    protected Long startTime;
    protected List<String> headers = null;
    private Transaction tx;

    public AbstractGpehExportProvider(Node dataset, Node network, RelationshipType statRelation, CallTimePeriods period,GraphDatabaseService service, LuceneIndexService luceneService) {
        tx=NeoUtils.beginTx(service);
        try{
        this.dataset = dataset;
        this.network = network;
        this.statRelation = statRelation;
        this.period = period;
        this.service = service;
        this.luceneService = luceneService;
        minMax=NeoUtils.getMinMaxTimeOfDataset(dataset, service);
        init();
        }finally{
            NeoUtils.finishTx(tx);
        }
    }

    protected void init() {
        startTime=period.getFirstTime(minMax.getLeft());  
        statRoot=defineStatRoot();
        
    }
    /**
     * Define statistic node.
     *
     * @return the node
     */
    protected Node defineStatRoot() {//TODO check on existing statistics
        Transaction tx = NeoUtils.beginTx(service);
        try{
           Node statMain= dataset.getSingleRelationship(GpehRelationshipType.GPEH_STATISTICS, Direction.OUTGOING).getOtherNode(dataset);
           Relationship rel = statMain.getSingleRelationship(statRelation, Direction.OUTGOING);
        return rel==null?null:rel.getOtherNode(statMain);
        }finally{
            tx.finish();
        }
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


    protected abstract void createHeader();
}
