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

package org.amanzi.neo.services.model.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiTimeIndexConverter;
import org.amanzi.neo.services.utils.Utils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractIndexedModel extends PropertyStatisticalModel {

    private static Logger LOGGER = Logger.getLogger(AbstractIndexedModel.class);

    private long min_timestamp = Long.MAX_VALUE;
    private long max_timestamp = 0;
    private double min_latitude = Double.MAX_VALUE;
    private double max_latitude = -Double.MAX_VALUE;
    private double min_longitude = Double.MAX_VALUE;
    private double max_longitude = -Double.MAX_VALUE;

    private Map<String, List<MultiPropertyIndex< ? >>> indexes;
    private Transaction tx = null;
    private GraphDatabaseService graphDb = NeoServiceProvider.getProvider().getService();

    protected void addLocationIndex(INodeType nodeType) throws IOException {
        LOGGER.debug("addLocationIndex(" + nodeType + ")");

        // validate parameters
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        String typeName = nodeType.getId();
        List<MultiPropertyIndex< ? >> indList = indexes.get(typeName);
        if (indList == null) {
            indList = new ArrayList<MultiPropertyIndex< ? >>();
            indexes.put(typeName, indList);
        }

        MultiPropertyIndex<Double> index = createLocationIndex(nodeType);
        if (!indList.contains(index)) {
            indList.add(index);
        }
    }

    protected void addTimestampIndex(INodeType nodeType) throws IOException {
        LOGGER.debug("addTimestampIndex(" + nodeType + ")");

        // validate parameters
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        String typeName = nodeType.getId();
        List<MultiPropertyIndex< ? >> indList = indexes.get(typeName);
        if (indList == null) {
            indList = new ArrayList<MultiPropertyIndex< ? >>();
            indexes.put(typeName, indList);
        }

        MultiPropertyIndex<Long> index = createTimestampIndex(nodeType);
        if (!indList.contains(index)) {
            indList.add(index);
        }
    }

    protected void indexNode(Node node) {
        LOGGER.debug("indexNode(" + node + ")");

        // validate parameters
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }

        String typeName = node.getProperty(NewAbstractService.TYPE, "").toString();
        List<MultiPropertyIndex< ? >> indList = indexes.get(typeName);
        if (indList != null) {
            for (MultiPropertyIndex< ? > index : indList) {
                try {
                    index.add(node);
                } catch (IOException e) {
                    LOGGER.error("Could not index node " + node.getId(), e);
                }
            }
        }
    }

    protected void flushIndexes() {
        LOGGER.debug("flushIndexes()");

        for (List<MultiPropertyIndex< ? >> list : indexes.values()) {
            for (MultiPropertyIndex< ? > index : list) {
                index.finishUp();
            }
        }
    }

    private MultiPropertyIndex<Long> createTimestampIndex(INodeType nodeType) throws IOException {
        String indexName = NewAbstractService.getIndexKey(getRootNode(), nodeType);
        MultiPropertyIndex<Long> result = new MultiPropertyIndex<Long>(indexName, new String[] {DriveModel.TIMESTAMP},
                new MultiTimeIndexConverter(), 10);
        result.initialize(NeoServiceProvider.getProvider().getService(), getRootNode());
        return result;
    }

    private MultiPropertyIndex<Double> createLocationIndex(INodeType nodeType) throws IOException {
        String indexName = NewAbstractService.getIndexKey(getRootNode(), nodeType);
        MultiPropertyIndex<Double> result = new MultiPropertyIndex<Double>(indexName, new String[] {DriveModel.LATITUDE,
                DriveModel.LONGITUDE}, new MultiDoubleConverter(0.001), 10);
        result.initialize(NeoServiceProvider.getProvider().getService(), getRootNode());
        return result;
    }

    protected void updateTimestamp(long timestamp) throws DatabaseException {
        if (timestamp < min_timestamp) {
            min_timestamp = timestamp;
        }
        if (timestamp > max_timestamp) {
            max_timestamp = timestamp;
        }
    }

    protected void updateLocationBounds(double latitude, double longitude) throws DatabaseException {

        // update latitude
        if (latitude > max_latitude) {
            max_latitude = latitude;
        }
        if (latitude < min_latitude) {
            min_latitude = latitude;
        }

        // update longitude
        if (longitude > max_longitude) {
            max_longitude = longitude;
        }
        if (longitude < min_longitude) {
            min_longitude = longitude;
        }

    }

    protected double getMinLatitude() {
        return min_latitude;
    }

    protected double getMaxLatitude() {
        return max_latitude;
    }

    protected double getMinLongitude() {
        return min_longitude;
    }

    protected double getMaxLongitude() {
        return max_longitude;
    }

    protected long getMinTimestamp() {
        return min_timestamp;
    }

    protected long getMaxTimestamp() {
        return max_timestamp;
    }

    @Override
    public void finishUp() {
        super.finishUp();

        Node rootNode = getRootNode();
        rootNode.setProperty(DriveModel.MIN_TIMESTAMP, min_timestamp);
        rootNode.setProperty(DriveModel.MAX_TIMESTAMP, max_timestamp);

        // TODO: approve code
        Node gis = Utils.getGisNodeByDataset(rootNode);
        gis.setProperty(DriveModel.MIN_LATITUDE, min_latitude);
        gis.setProperty(DriveModel.MIN_LONGITUDE, min_longitude);
        gis.setProperty(DriveModel.MAX_LATITUDE, max_latitude);
        gis.setProperty(DriveModel.MAX_LONGITUDE, max_longitude);
    }
}
