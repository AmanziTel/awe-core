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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.IndexService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * This class contains methods that handle node indexing with MultiPropertyIndex'es, and some
 * implementations of methods, used in descendants.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractIndexedModel extends PropertyStatisticalModel {

    private static Logger LOGGER = Logger.getLogger(AbstractIndexedModel.class);

    protected long min_timestamp = Long.MAX_VALUE;
    protected long max_timestamp = 0;
    protected double min_latitude = Double.MAX_VALUE;
    protected double max_latitude = -Double.MAX_VALUE;
    protected double min_longitude = Double.MAX_VALUE;
    protected double max_longitude = -Double.MAX_VALUE;

    private Map<INodeType, List<MultiPropertyIndex< ? >>> indexes = new HashMap<INodeType, List<MultiPropertyIndex< ? >>>();

    private IndexService indexService = NeoServiceFactory.getInstance().getIndexService();

    static DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();

    protected AbstractIndexedModel(Node rootNode, INodeType nodeType) throws AWEException {
        super(nodeType);
        this.rootNode = rootNode;
        
        if (rootNode != null) {
            max_timestamp = (Long)rootNode.getProperty(DriveModel.MAX_TIMESTAMP, max_timestamp);
            min_timestamp = (Long)rootNode.getProperty(DriveModel.MIN_TIMESTAMP, min_timestamp);
        }

        Node gis = datasetService.getGisNodeByDataset(rootNode);
        if (gis != null) {
            min_latitude = (Double)gis.getProperty(DriveModel.MIN_LATITUDE, min_latitude);
            min_longitude = (Double)gis.getProperty(DriveModel.MIN_LONGITUDE, min_longitude);
            max_latitude = (Double)gis.getProperty(DriveModel.MAX_LATITUDE, max_latitude);
            max_longitude = (Double)gis.getProperty(DriveModel.MAX_LONGITUDE, max_longitude);
        }
    }

    /**
     * Creates and stores a location index for the defined node type.
     * 
     * @param nodeType
     * @throws IOException if was unable to create an index in the database.
     */
    protected void addLocationIndex(INodeType nodeType) throws AWEException {
        LOGGER.debug("addLocationIndex(" + nodeType + ")");

        // since location index exist it should also be a GIS node
        datasetService.getGisNodeByDataset(rootNode);

        // validate parameters
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        List<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList == null) {
            indList = new ArrayList<MultiPropertyIndex< ? >>();
            indexes.put(nodeType, indList);
        }

        MultiPropertyIndex<Double> index = indexService.createLocationIndex(rootNode, nodeType);
        if (!indList.contains(index)) {
            indList.add(index);
        }
    }
    
    protected Iterable<Node> getNodesInBounds(INodeType nodeType, double minLat, double minLon, double maxLat, double maxLon) throws AWEException{
        //validate
        if(nodeType == null){
            throw new IllegalArgumentException("Node type is null.");
        }
        
        List<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if(indList == null){
            return datasetService.emptyTraverser(rootNode);
        }
        MultiPropertyIndex<Double> locationIndex = indexService.createLocationIndex(rootNode, nodeType);
        if (!indList.contains(locationIndex)) {
            return datasetService.emptyTraverser(rootNode);
        }
        return locationIndex.searchTraverser(new Double[]{minLat, minLon}, new Double[]{maxLat, maxLon});
    }
    
    protected Iterable<Node> getNodesByTimestampPeriod(INodeType nodeType, long min_timestamp, long max_timestamp) throws AWEException{
        
        //validate
        if(nodeType == null){
            throw new IllegalArgumentException("Node type is null.");
        }
        
        List<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if(indList == null){
            return datasetService.emptyTraverser(rootNode);
        }
        MultiPropertyIndex<Long> timestampIndex = indexService.createTimestampIndex(rootNode, nodeType);
        if (!indList.contains(timestampIndex)) {
            return datasetService.emptyTraverser(rootNode);
        }
        return timestampIndex.searchTraverser(new Long[] {min_timestamp}, new Long[] {max_timestamp});
    }

    /**
     * Creates and stores a timestamp index for the defined node type.
     * 
     * @param nodeType
     * @throws IOException if was unable to create an index in the database.
     */
    protected void addTimestampIndex(INodeType nodeType) throws AWEException {
        LOGGER.debug("addTimestampIndex(" + nodeType + ")");

        // validate parameters
        if (nodeType == null) {
            throw new IllegalArgumentException("Node type is null.");
        }

        List<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList == null) {
            indList = new ArrayList<MultiPropertyIndex< ? >>();
            indexes.put(nodeType, indList);
        }

        MultiPropertyIndex<Long> index = indexService.createTimestampIndex(rootNode, nodeType);
        if (!indList.contains(index)) {
            indList.add(index);
        }
    }

    /**
     * Adds node to all the indexes that exist for its type.
     * 
     * @param node the node to index
     */
    protected void indexNode(Node node) {
        LOGGER.debug("indexNode(" + node + ")");

        // validate parameters
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }

        INodeType type = NodeTypeManager.getType(node.getProperty(AbstractService.TYPE, StringUtils.EMPTY).toString());
        List<MultiPropertyIndex< ? >> indList = indexes.get(type);
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

    /**
     * Runs over all the indexes to create the index structure in the database.
     */
    protected void flushIndexes() {
        LOGGER.debug("flushIndexes()");

        for (List<MultiPropertyIndex< ? >> list : indexes.values()) {
            for (MultiPropertyIndex< ? > index : list) {
                index.finishUp();
            }
        }
    }

    /**
     * Updates the stored values of minimum and maximum timestamp.
     * 
     * @param timestamp the new value of timestamp
     */
    public void updateTimestamp(long timestamp) {
        if (timestamp < min_timestamp) {
            min_timestamp = timestamp;
        }
        if (timestamp > max_timestamp) {
            max_timestamp = timestamp;
        }
    }

    /**
     * Updates the stored values of minimum and maximum latitude and longitude.
     * 
     * @param latitude
     * @param longitude
     */
    public void updateLocationBounds(double latitude, double longitude) {

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

    public double getMinLatitude() {
        return min_latitude;
    }

    public double getMaxLatitude() {
        return max_latitude;
    }

    public double getMinLongitude() {
        return min_longitude;
    }

    public double getMaxLongitude() {
        return max_longitude;
    }

    public long getMinTimestamp() {
        return min_timestamp;
    }

    public long getMaxTimestamp() {
        return max_timestamp;
    }

    /**
     * Writes the stored values of timestamp, latitude and longitude to database.
     */
    @Override
    public void finishUp() throws AWEException {
        flushIndexes();

        Node rootNode = getRootNode();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(DriveModel.MIN_TIMESTAMP, min_timestamp);
        params.put(DriveModel.MAX_TIMESTAMP, max_timestamp);
        datasetService.setProperties(rootNode, params);

        Node gis = datasetService.getGisNodeByDataset(rootNode);
        if (gis != null) {
            params = new HashMap<String, Object>();
            params.put(DriveModel.MIN_LATITUDE, min_latitude);
            params.put(DriveModel.MIN_LONGITUDE, min_longitude);
            params.put(DriveModel.MAX_LATITUDE, max_latitude);
            params.put(DriveModel.MAX_LONGITUDE, max_longitude);
            datasetService.setProperties(gis, params);
        }
        super.finishUp();
    }
}
