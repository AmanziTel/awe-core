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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.preferences.NeoCorePreferencesConstants;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.geotools.geometry.jts.JTS;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexHits;
import org.neo4j.index.lucene.LuceneIndexService;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * <p>
 * Rrc model for handling information about RRC events
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 * @param <M>
 */
public class RrcModel<M extends RrcModelHandler> {
    protected Integer maxRange;
    protected final M modelHandler;

    protected String scrCodeIndName;

    protected Map<CellNodeInfo, Set<InterfCellInfo>> cache = new LinkedHashMap<CellNodeInfo, Set<InterfCellInfo>>();

    protected Iterator<CellNodeInfo> bestCellIterator;

    protected Iterator<InterfCellInfo> interfCellIter;

    protected CellNodeInfo bestCellInfo;
    private CoordinateReferenceSystem crs;
    private final boolean checkRule;

    /**
     * Instantiates a new rrc model.
     *
     * @param modelHandler the model handler
     * @param checkRule 
     */
    public RrcModel(M modelHandler, boolean checkRule) {
        this.modelHandler = modelHandler;
        this.checkRule = checkRule;
    }

    /**
     * Load model
     * 
     * @param network the network
     * @param rowIter the row iter
     * @param service the service
     * @param luceneService the lucene service
     */
    public void load(Node network, Iterator<CellInfo> rowIter, GraphDatabaseService service, LuceneIndexService luceneService) {
        scrCodeIndName = NeoUtils.getLuceneIndexKeyByProperty(network, GpehReportUtil.PRIMARY_SCR_CODE, NodeTypes.SECTOR);
        Transaction tx = NeoUtils.beginTx(service);
        try {
            while (rowIter.hasNext()) {
                CellInfo cell = rowIter.next();
                Node bestCell = NeoUtils.findSector(network, cell.getCi(), String.valueOf(cell.getRnc()), luceneService, service);
                if (bestCell == null) {
                    IntraMatrixProvider.LOGGER.warn(String.format("Data not included in statistics! Not found sector with ci=%s, rnc=%s", cell.getCi(), cell.getRnc()));
                    continue;
                }
                CellNodeInfo bci = findInCache(bestCell, cache.keySet());
                if (bci == null) {
                    bci = new CellNodeInfo(bestCell, cell.getBestCellInfo());
                    if (!bci.setupLocation()) {
                        IntraMatrixProvider.LOGGER.warn(String.format("Data not included in statistics! Not found location for best cell %s", bestCell));
                        continue;
                    }
                    cache.put(bci, new LinkedHashSet<InterfCellInfo>());
                }
                Set<InterfCellInfo> cacheInt = cache.get(bci);
                InterfCellInfo sector = findInCache(bestCell, cacheInt);
                if (sector == null) {
                    sector = findClosestSector(bci, cell, network, service, luceneService);
                    if (sector == null) {
                        continue;
                    }
                    cacheInt.add(sector);
                }
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * Find in cache.
     * 
     * @param <E> the element type
     * @param bestCell the best cell
     * @param cache the cache
     * @return the e
     */
    protected <E extends CellNodeInfo> E findInCache(Node bestCell, Collection<E> cache) {
        for (E info : cache) {
            if (info.getCellSector().equals(bestCell)) {
                return info;
            }
        }
        return null;
    }

    /**
     * Find closest sector.
     * 
     * @param bestCell the best cell
     * @param cell the cell
     * @param network the network
     * @param service the service
     * @param luceneService the lucene service
     * @return the interf cell info
     */
    private InterfCellInfo findClosestSector(CellNodeInfo bestCell, CellInfo cell, Node network, GraphDatabaseService service, LuceneIndexService luceneService) {
        if (bestCell.getLat() == null || bestCell.getLon() == null) {
            IntraMatrixProvider.LOGGER.debug("bestCell " + bestCell.getCellSector() + " do not have location");
            return null;
        }
        if (crs == null) {
            Node gis = NeoUtils.findGisNodeByChild(network, service);
            if (gis != null) {
                crs = NeoUtils.getCRS(gis, null);
            } else {
                IntraMatrixProvider.LOGGER.debug("Not fount CRS for network");
                return null;
            }
        }
        Integer uarFcnDl = bestCell.defineUarfcnDl();
        if (uarFcnDl==null){
            IntraMatrixProvider.LOGGER.error("Not fount uarFcnDl properety in sector "+bestCell.getCellSector());
            return null;           
        }
        InterfCellInfo result = null;
        IndexHits<Node> nodes = luceneService.getNodes(scrCodeIndName, String.valueOf(cell.getPsc()));
        for (Node sector : nodes) {
            Integer uarfcnDlCandidate = (Integer)sector.getProperty("uarfcnDl", null);
            if (!checkNode(uarFcnDl,uarfcnDlCandidate)){
                continue;
            }
            if (result == null) {
                result = new InterfCellInfo(sector, cell.getInterfCellInfo(), cell.getPsc());
                if (result.setupLocation()) {
                    // TODO check correct distance! maybe use CRS for this

                    result.setDistance(calculateDistance(bestCell, result));
                    if (result.getDistance() > getMaxRange()) {
                        IntraMatrixProvider.LOGGER.debug("sector " + result + " have too big distance: " + result.getDistance());
                        result = null;
                    }
                } else {
                    IntraMatrixProvider.LOGGER.debug("sector " + result + " do not have location");
                    result = null;
                }
            } else {
                InterfCellInfo candidate = new InterfCellInfo(sector, cell.getInterfCellInfo(), cell.getPsc());
                if (candidate.setupLocation()) {
                    candidate.setDistance(calculateDistance(bestCell, candidate));
                    if (candidate.getDistance() < result.getDistance()) {
                        result = candidate;
                    }
                }
            }
        }
        return result;
    }



    /**
     * Check node.
     *
     * @param uarFcnDl the uar fcn dl
     * @param uarfcnDlCandidate the uarfcn dl candidate
     * @return true, if successful
     */
    protected boolean checkNode(Integer uarFcnDl, Integer uarfcnDlCandidate) {
        return checkRule?uarFcnDl.equals(uarfcnDlCandidate):!uarFcnDl.equals(uarfcnDlCandidate);
    }

    /**
     * Gets the max range.
     *
     * @return the max range
     */
    public double getMaxRange() {
        if (maxRange == null) {
            maxRange = NeoCorePlugin.getDefault().getPreferenceStore().getInt(NeoCorePreferencesConstants.MAX_SECTOR_DISTANSE);
        }
        return maxRange;
    }

    /**
     * Define next data.
     *
     * @return true, if successful
     */
    public boolean defineNextData() {
        modelHandler.clearData();
        while (interfCellIter.hasNext() || bestCellIterator.hasNext()) {
            while (!interfCellIter.hasNext() && bestCellIterator.hasNext()) {
                bestCellInfo = bestCellIterator.next();
                interfCellIter = cache.get(bestCellInfo).iterator();
            }
            if (!interfCellIter.hasNext()) {
                return false;
            }
            if (modelHandler.setData(bestCellInfo, interfCellIter.next())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Clear iterator
     */
    public void clearIter() {
        modelHandler.clearData();
        bestCellIterator = cache.keySet().iterator();
        interfCellIter = Collections.<InterfCellInfo> emptySet().iterator();

    }

    /**
     * Calculate distance.
     * 
     * @param bestCell the best cell
     * @param result the candidate
     * @return the distance between sectors
     */
    private Double calculateDistance(CellNodeInfo bestCell, CellNodeInfo result) {
        Coordinate p1 = new Coordinate(bestCell.getLat(), bestCell.getLon());
        Coordinate p2 = new Coordinate(result.getLat(), result.getLon());
        try {
            return JTS.orthodromicDistance(p1, p2, crs);
        } catch (TransformException e) {
            e.printStackTrace();
            return Math.sqrt(Math.pow(bestCell.getLat() - result.getLat(), 2) + Math.pow(bestCell.getLon() - result.getLon(), 2));
        }
    }
}