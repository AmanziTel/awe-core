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

package org.amanzi.neo.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Loader for GSM data
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class GSMLoader extends DriveLoader {
    private Float currentLatitude = null;
    private Float currentLongitude = null;
    private String time = null;
    
    private final ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    
    private Node mNode;
    private Node gisNode;
    // private Node dataSetNode;
    private Node fileNode;
    private Node lastChild;

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public GSMLoader(Calendar workTime, String filename, Display display, String dataset) {
        _workDate = workTime;
        driveType = DriveTypes.GSM;
        initialize("Romes", null, filename, display, dataset);
        // initData();
        initializeLuceneIndex();
        initializeKnownHeaders();
        addDriveIndexes();
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        addKnownHeader(1, "latitude", ".*latitude.*");
        addKnownHeader(1, "longitude", ".*longitude.*");
        addKnownHeader(1, "timestamp", ".*timestamp.*");
        dropHeaderStats(1, new String[] {"timestamp", "latitude", "longitude"});
    }

    private void addDriveIndexes() {
        try {
            addIndex(NodeTypes.M.getId(), NeoUtils.getTimeIndexProperty(dataset));
            addIndex(NodeTypes.MP.getId(), NeoUtils.getLocationIndexProperty(dataset));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    protected Node getStoringNode(Integer key) {
//        GisProperties gisProperties = gisNodes.get(dataset);
//        return gisProperties == null ? null : gisProperties.getGis();
        return gisNode;
    }

    @Override
    protected void parseLine(String line) {
        if (fileNode == null) {

            Transaction tx = neo.beginTx();
            try {
                gisNode = NeoUtils.findRootNode(NodeTypes.GIS, "GSM", neo);
                if (gisNode == null) {
                    gisNode = neo.createNode();
                    gisNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.GIS.getId());
                    gisNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "gis_name");
                    neo.getReferenceNode().createRelationshipTo(gisNode, GeoNeoRelationshipTypes.CHILD);
                }
                tx.success();
            } finally {
                tx.finish();
            }
            gisNode.createRelationshipTo(datasetNode, GeoNeoRelationshipTypes.CHILD);

            Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateFileNode(neo, datasetNode, new File(basename).getName(), new File(basename).getName());
            fileNode = fileNodePair.getRight();
            lastChild = null;

        }
        List<String> fields = splitLine(line);
        if (fields.size() < 2)
            return;
        if (this.isOverLimit())
            return;
        Map<String, Object> lineData = makeDataMap(fields);

        this.time = (String)lineData.get("timestamp");
        // Date nodeDate = (Date)lineData.get("timestamp");
        // this.timestamp = getTimeStamp(nodeDate);
        Float latitude = (Float)lineData.get("latitude");
        Float longitude = (Float)lineData.get("longitude");
        if (time == null || latitude == null || longitude == null) {
            new Exception("Wrong time, latitude or longitude.").printStackTrace();
            return;
        }
        if ((latitude != null)
                && (longitude != null)
                && (((currentLatitude == null) && (currentLongitude == null)) || ((Math.abs(currentLatitude - latitude) > 10E-10) || (Math.abs(currentLongitude
                        - longitude) > 10E-10)))) {
            currentLatitude = latitude;
            currentLongitude = longitude;
            saveData(); // persist the current data to database
        }
        this.incValidLocation(); // we have not filtered the message out on lack of location
        if (lineData.size() > 0) {
            data.add(lineData);
        }
        
        
        saveStatistic(lineData);
    }
    
    /**
     * This method is called to dump the current cache of signals as one located point linked to a
     * number of signal strength measurements.
     */
    private void saveData() {
        if (data.size() > 0) {
            Transaction transaction = neo.beginTx();
            try {
                Node mp = neo.createNode();
                mp.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.MP.getId());
                mp.setProperty(INeoConstants.PROPERTY_TIME_NAME, this.time);
                mp.setProperty(INeoConstants.PROPERTY_LAT_NAME, currentLatitude.doubleValue());
                mp.setProperty(INeoConstants.PROPERTY_LON_NAME, currentLongitude.doubleValue());
                index(mp);

                // debug("Added measurement point: " + propertiesString(mp));

                boolean haveEvents = false;
                for (Map<String, Object> dataLine : data) {
                    Node m = neo.createNode();
                    file.createRelationshipTo(m, GeoNeoRelationshipTypes.CHILD);
                    m.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.M.getId());
                    for (Map.Entry<String, Object> entry : dataLine.entrySet()) {
                        if (entry.getKey().equals(INeoConstants.SECTOR_ID_PROPERTIES)) {
                            mp.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, entry.getValue());
                            // ms.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, entry.getValue());
                        } else if ("timestamp".equals(entry.getKey())) {
                            long timeStamp = getTimeStamp(1, ((Date)entry.getValue()));
                            if (timeStamp != 0) {
                                m.setProperty(entry.getKey(), timeStamp);
                                mp.setProperty(entry.getKey(), timeStamp);
                            }
                        } else {
                            m.setProperty(entry.getKey(), entry.getValue());
                            haveEvents = haveEvents || INeoConstants.PROPERTY_TYPE_EVENT.equals(entry.getKey());
                        }
                    }
                    m.createRelationshipTo(mp, GeoNeoRelationshipTypes.LOCATION);
                    if (mNode != null) {
                        mNode.createRelationshipTo(m, GeoNeoRelationshipTypes.NEXT);
                    }
                    m.setProperty(INeoConstants.PROPERTY_NAME_NAME, getMNodeName(dataLine));
                    mNode = m;
                    index(m);
                }
                if (haveEvents) {
                    index.index(mp, INeoConstants.EVENTS_LUCENE_INDEX_NAME, dataset);
                }
                GisProperties gisProperties = getGisProperties(dataset);
                gisProperties.updateBBox(currentLatitude, currentLongitude);
                gisProperties.checkCRS(currentLatitude, currentLongitude, null);
                gisProperties.incSaved();
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        data.clear();
    }

    /**
     * @param lineData
     */
    private void saveStatistic(Map<String, Object> lineData) {
        Transaction transaction = neo.beginTx();
        try {
            Node node = neo.createNode();
            NodeTypes.M.setNodeType(node, neo);
            for (Map.Entry<String, Object> entry : lineData.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }
            node.setProperty(INeoConstants.PROPERTY_NAME_NAME, "GSM counter");
            index(node);
            NeoUtils.addChild(fileNode, node, lastChild, neo);
            lastChild = node;
        } finally {
            transaction.finish();
        }
    }
    /**
     * get name of m node
     * 
     * @param dataLine - node data
     * @return node name
     */
    private String getMNodeName(Map<String, Object> dataLine) {
        Object timeNode = dataLine.get("time");
        return timeNode == null ? "ms node" : timeNode.toString();
    }

}
