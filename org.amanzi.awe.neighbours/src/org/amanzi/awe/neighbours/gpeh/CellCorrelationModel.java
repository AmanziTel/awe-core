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

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * Model for CellCorrelation reports
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CellCorrelationModel extends RrcModel<RrcModelHandler> {

    /**
     * Instantiates a new cell correlation model.
     * 
     * @param modelHandler the model handler
     */
    public CellCorrelationModel(RrcModelHandler modelHandler) {
        super(modelHandler,true);
    }

    @Override
    public void load(Node network, Iterator<CellInfo> rowIter, GraphDatabaseService service, LuceneIndexService luceneService) {
        scrCodeIndName = NeoUtils.getLuceneIndexKeyByProperty(network, GpehReportUtil.PRIMARY_SCR_CODE, NodeTypes.SECTOR);
        Transaction tx = NeoUtils.beginTx(service);
        try {
            while (rowIter.hasNext()) {
                CellInfo cell = rowIter.next();
                Node bestCell = NeoUtils.findSector(network, cell.getCi(), String.valueOf(cell.getRnc()), luceneService, service);
                if (bestCell == null) {
                    CellCorrelationProvider.LOGGER.warn(String.format("Data not included in statistics! Not found sector with ci=%s, rnc=%s", cell.getCi(), cell.getRnc()));
                    continue;
                }
                CellNodeInfo bci = findInCache(bestCell, cache.keySet());
                if (bci == null) {
                    bci = new CellNodeInfo(bestCell, cell.getBestCellInfo());
                    cache.put(bci, null);
                }
            }
        } finally {
            tx.finish();
        }
    }

    @Override
    public boolean defineNextData() {
        modelHandler.clearData();
        while (bestCellIterator.hasNext()) {
            bestCellInfo = bestCellIterator.next();
            if (modelHandler.setData(bestCellInfo, null)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearIter() {
        modelHandler.clearData();
        bestCellIterator = cache.keySet().iterator();

    }

}