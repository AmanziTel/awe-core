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

package org.amanzi.awe.statistics.database;

import java.io.IOException;
import java.util.Collection;

import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistics.engine.IDatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.utils.Pair;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public abstract class AbstractDatasetService implements IDatasetService {
    /*
     * a Hour period
     */
    public static final long HOUR = 1000 * 60 * 60;

    /*
     * a Day period
     */
    public static final long DAY = 24 * HOUR;

    protected GraphDatabaseService service;
    protected Node dataset;

    private MultiPropertyIndex<Long> timeIndex;

    /**
     * @param service
     */
    public AbstractDatasetService(GraphDatabaseService service, Node dataset) {
        this.service = service;
        this.dataset = dataset;
        initializeIndex();
    }

    @Override
    public Collection<Node> getNodes(Long startTime, Long endTime) {
        if (timeIndex != null) {
            return timeIndex.find(new Long[] {startTime}, new Long[] {endTime});
        }
        return null;
    }

    private void initializeIndex() {
        try {
            timeIndex = Utils.getTimeIndexProperty((String)dataset.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            timeIndex.initialize(service, null);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public Pair<Long, Long> getTimeBounds() {
        // Transaction tx = service.beginTx();
        // try {
        // tx.success();
        return new Pair<Long, Long>((Long)dataset.getProperty(INeoConstants.MIN_TIMESTAMP), (Long)dataset
                .getProperty(INeoConstants.MAX_TIMESTAMP));
        // } finally {
        // tx.finish();
        // }
    }

    @Override
    public CallTimePeriods getHighestPeriod() {
        Pair<Long, Long> timeBounds = getTimeBounds();
        long delta = CallTimePeriods.DAILY.getFirstTime(timeBounds.r()) - CallTimePeriods.DAILY.getFirstTime(timeBounds.l());
        if (delta >= DAY) {
            return CallTimePeriods.MONTHLY;
        }
        delta = CallTimePeriods.HOURLY.getFirstTime(timeBounds.r()) - CallTimePeriods.HOURLY.getFirstTime(timeBounds.l());
        if (delta >= HOUR) {
            return CallTimePeriods.DAILY;
        }

        return CallTimePeriods.HOURLY;
    }

    @Override
    public Node getDatasetNode() {
        return dataset;
    }
}
