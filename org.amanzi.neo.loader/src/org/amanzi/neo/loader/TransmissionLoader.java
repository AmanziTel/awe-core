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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.CSVParser;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * TODO extend from NeighbourLoader
 * <p>
 * Transmission data loader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class TransmissionLoader {
    
    /** String STRING field */
    // private static String directory = null;
    private static final int COMMIT_MAX = 1000;
    private final Node network;
    private final String fileName;
    private Header header;
    private Node neighbour;
    private final String baseName;
    private final GraphDatabaseService neo;
    private final String gisName;
    private final Node gis;
    private Node lastSector;
    private static final String PROXY_NAME_SEPARATOR = "/";

    /**
     * Constructor
     * 
     * @param networkNode network Node
     * @param fileName Neighbour file Name
     */
    public TransmissionLoader(String gisName, String fileName, GraphDatabaseService neo) {
        this.gisName=gisName;
        this.neo = neo;
        this.fileName = fileName;
        this.baseName = new File(fileName).getName();
         gis = findOrCreateGISNode(gisName, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
        network = findOrCreateNetworkNode(gis);
    }

    /**
     * gets directory
     * 
     * @return
     */
    public static String getDirectory() {
        return NeoLoaderPlugin.getDefault().getPluginPreferences().getString(AbstractLoader.DEFAULT_DIRRECTORY_LOADER);
    }

    /**
     * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
     * 
     * @param newDirectory new default directory
     * @author Lagutko_N
     */

    public static void setDirectory(String newDirectory) {
        NeoLoaderPlugin.getDefault().getPluginPreferences().setValue(AbstractLoader.DEFAULT_DIRRECTORY_LOADER, newDirectory);
    }

    /**
     * Runs NeighbourLoader
     * 
     * @param monitor monitor
     * @throws IOException
     */
    public void run(IProgressMonitor monitor) throws IOException {
        CountingFileInputStream stream = null;
        BufferedReader reader = null;
        Transaction tx = neo.beginTx();
        try {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }
            monitor.beginTask("Importing " + baseName, 100);
            stream = new CountingFileInputStream(new File(fileName));
            String charSet = NeoLoaderPlugin.getDefault().getCharacterSet();
            reader = new BufferedReader(new InputStreamReader(stream, charSet));
            int perc = stream.percentage();
            int prevPerc = 0;
            String line = reader.readLine();
            if (line == null) {
                monitor.setCanceled(true);
                return;
            }
            header = new Header(line, neo);
            neighbour = getTransmission(network, baseName);
            int commit = 0;
            while ((line = reader.readLine()) != null) {
                header.parseLine(line, network, baseName);
                if (monitor.isCanceled())
                    break;
                perc = stream.percentage();
                if (perc > prevPerc) {
                    monitor.worked(perc - prevPerc);
                    prevPerc = perc;
                }
                if (++commit > COMMIT_MAX) {
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                    commit = 0;
                }
            }
            header.saveStatistic(neighbour);
            monitor.done();
            tx.success();
        } finally {
            if (reader != null) {
                reader.close();
            }
            tx.finish();
            header.finish();
            NeoServiceProvider.getProvider().commit();
            NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                    new UpdateDatabaseEvent(UpdateViewEventType.TRANSMISSION));
            NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                    new UpdateDatabaseEvent(UpdateViewEventType.GIS));
        }

    }

    /**
     * get transmission
     * 
     * @param network network node
     * @param fileName neighbour file name
     * @return neighbour node
     */
    private Node getTransmission(Node network, String fileName) {
        Node result = NeoUtils.findTransmission(network, fileName, neo);
        if (result != null) {
            return result;
        }
        Transaction tx = neo.beginTx();
        try {
            result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.TRANSMISSION.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, fileName);
            network.createRelationshipTo(result, NetworkRelationshipTypes.TRANSMISSION_DATA);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * <p>
     * Header of Neighbour file
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class Header {

        private static final String STRING = "STRING";
        /** String DOUBLE field */
        private static final String DOUBLE = "DOUBLE";
        /** String INTEGER field */
        private static final String INTEGER = "INTEGER";
        private final Map<Integer, Pair<String, String>> indexMap = new LinkedHashMap<Integer, Pair<String, String>>();
        private final NodeName serverNodeName;
        private final NodeName neighbourNodeName;
        private final String[] headers;
        // private Map<String, Pair<Node, Integer>> cach = new HashMap<String, Pair<Node,
        // Integer>>();
        private final LuceneIndexService index;
        private final GraphDatabaseService neo;
        private char fieldSepRegex;
        private final char[] possibleFieldSepRegexes = new char[] {'\t', ',', ';'};
        private CSVParser parser;

        /**
         * Constructor
         * 
         * @param line - header line
         */
        public Header(String line, GraphDatabaseService neo) {
            this.neo = neo;
            index=NeoServiceProvider.getProvider().getIndexService();
            determineFieldSepRegex(line);
            headers = splitLine(line);
            serverNodeName = new NodeName(getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_SERV), 
                                          getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_SERV),
                                          getPossibleHeaders(DataLoadPreferences.TR_ITEM_NAME_SERV));
            neighbourNodeName = new NodeName(getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_NEIB), 
                                             getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_NEIB),
                                             getPossibleHeaders(DataLoadPreferences.TR_ITEM_NAME_NEIB));
            for (int i = 0; i < headers.length; i++) {
                String fieldHeader = headers[i];
                if (serverNodeName.setFieldIndex(fieldHeader, i)) {
                    continue;
                } else if (neighbourNodeName.setFieldIndex(fieldHeader, i)) {
                    continue;
                } else {
                    indexMap.put(i, new Pair<String, String>(fieldHeader, null));
                }
            }
        }
        
        /**
         * @param key -key of value from preference store
         * @return array of possible headers
         */
        protected String[] getPossibleHeaders(String key) {
            String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
            String[] array = text.split(",");
            List<String> result = new ArrayList<String>();
            for (String string : array) {
                String value = string.trim();
                if (!value.isEmpty()) {
                    result.add(value);
                }
            }
            return result.toArray(new String[0]);
        }

        protected String[] splitLine(String line) {
            return parser.parse(line).toArray(new String[0]);
        }

        private void determineFieldSepRegex(String line) {
            int maxMatch = 0;
            for (char regex : possibleFieldSepRegexes) {
                String[] fields = line.split(String.valueOf(regex));
                if (fields.length > maxMatch) {
                    maxMatch = fields.length;
                    fieldSepRegex = regex;
                }
            }
            parser = new CSVParser(fieldSepRegex);
        }

        /**
         * save statistic information in node
         * 
         * @param neighbour
         */
        public void saveStatistic(Node neighbour) {
            saveNumericList(neighbour);
            saveAllFields(neighbour);
        }

        /**
         * finish work with header
         */
        public void finish() {

        }


        /**
         * Save list of Numeric properties in database
         * 
         * @param neighbour neighbour node
         */
        public void saveNumericList(Node neighbour) {
            Transaction tx = neo.beginTx();
            try {
                Set<String> propertyes = new HashSet<String>();
                for (Pair<String, String> pair : indexMap.values()) {
                    String clas = pair.getRight();
                    if (INTEGER.equals(clas) || DOUBLE.equals(clas)) {
                        propertyes.add(pair.getLeft());

                    }
                }
                neighbour.setProperty(INeoConstants.LIST_NUMERIC_PROPERTIES, propertyes.toArray(new String[0]));
                tx.success();
            } finally {
                tx.finish();
            }
        }

        /**
         * Save list of All properties in database
         * 
         * @param neighbour neighbour node
         */
        public void saveAllFields(Node neighbour) {
            Transaction tx = neo.beginTx();

            try {
                Set<String> integer = new HashSet<String>();
                Set<String> doubl = new HashSet<String>();
                Set<String> propertyes = new HashSet<String>();
                for (Pair<String, String> pair : indexMap.values()) {
                    String clas = pair.getRight();
                    if (INTEGER.equals(clas)) {
                        integer.add(pair.getLeft());

                    } else if (DOUBLE.equals(clas)) {
                        doubl.add(pair.getLeft());
                    }
                    propertyes.add(pair.getLeft());

                }
                neighbour.setProperty(INeoConstants.LIST_ALL_PROPERTIES, propertyes.toArray(new String[0]));
                neighbour.setProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, doubl.toArray(new String[0]));
                neighbour.setProperty(INeoConstants.LIST_INTEGER_PROPERTIES, integer.toArray(new String[0]));
                tx.success();
            } finally {
                tx.finish();
            }
        }

        /**
         * Parse single line
         * 
         * @param line - string line
         * @param fileName - neighbour name
         * @param network - network node
         */
        public void parseLine(String line, Node network, String fileName) {
            String fields[] = splitLine(line);// line.split("\\t");
            Transaction tx = neo.beginTx();
            try {
                String servCounName = NeoUtils.getTransmissionPropertyName(fileName);
                serverNodeName.setFieldValues(fields);
                neighbourNodeName.setFieldValues(fields);
                Node proxyServer = null;
                Node proxyNeighbour = null;
                Node serverNode = getSiteNodeById(serverNodeName);
                Node neighbourNode = getSiteNodeById(neighbourNodeName);
                String proxySectorName = null;
                String proxyNeighbourName = null;
                if (serverNode == null) {
                    serverNode = createTransmissionSite(serverNodeName, fields);
                    proxySectorName = fileName + PROXY_NAME_SEPARATOR + serverNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    proxyServer = NeoUtils.createProxySite(serverNode, fileName, neighbour, lastSector, NetworkRelationshipTypes.TRANSMISSIONS, neo);
                	index.index(proxyServer, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
                	lastSector = proxyServer;
                    NeoLoaderPlugin.error("Not found site: " + serverNodeName.getId1());
                }
                else {
                	proxySectorName = fileName + PROXY_NAME_SEPARATOR + serverNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                	proxyServer = NeoUtils.getProxySector(serverNode, fileName);
                	if (proxyServer == null) {
                    	proxyServer = NeoUtils.createProxySite(serverNode, fileName, neighbour, lastSector, NetworkRelationshipTypes.TRANSMISSIONS, neo);
                    	index.index(proxyServer, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
                    	lastSector = proxyServer;
                    }
                }
                
                if (neighbourNode == null) {
                    neighbourNode = createTransmissionSite(neighbourNodeName, fields);
                    proxyNeighbourName = fileName + PROXY_NAME_SEPARATOR + neighbourNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                    proxyNeighbour = NeoUtils.createProxySector(neighbourNode, fileName, neighbour, lastSector, NetworkRelationshipTypes.TRANSMISSIONS, neo);
                	index.index(proxyNeighbour, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxyNeighbourName);
                	lastSector = proxyNeighbour;
                    NeoLoaderPlugin.error("Not found site: " + neighbourNodeName.getId1());
                }
                else{
                	proxyNeighbourName = fileName + PROXY_NAME_SEPARATOR + neighbourNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                	proxyNeighbour = NeoUtils.getProxySector(neighbourNode, fileName);
                	if (proxyNeighbour == null) {
                    	proxyNeighbour = NeoUtils.createProxySector(neighbourNode, fileName, neighbour, lastSector, NetworkRelationshipTypes.TRANSMISSIONS, neo);
                    	index.index(proxyNeighbour, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxyNeighbourName);
                    	lastSector = proxyNeighbour;
                    }
                }

                Relationship relation = proxyServer.createRelationshipTo(proxyNeighbour, NetworkRelationshipTypes.TRANSMISSION);
//                relation.setProperty(INeoConstants.NEIGHBOUR_NAME, fileName);
                for (Integer index : indexMap.keySet()) {
                    String value = fields[index];
                    if (value.length() > 0) {
                        saveValue(relation, index, value);
                    }
                }
                updateCount(serverNode, servCounName);
                tx.success();
            } catch (Exception e) {
                NeoLoaderPlugin.error(line + "\n" + e.getLocalizedMessage());
            } finally {
                tx.finish();
            }

        }        /**
         * find node by id
         * 
         * @param nodeName NodeName
         * @return sector node or null
         */
        private Node getSiteNodeById(NodeName nodeName) {
            Node result = null;
            String idByName = nodeName.getId1();
            if (idByName != null) {
                result = index.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), idByName);
            }
            if (result == null) {
                String siteNo = nodeName.getId2();
                if (siteNo != null) {
                    result= index.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_SITE_NO, NodeTypes.SITE),siteNo);
                }
            }
            return result;
        }

        /**
         * Creates Transmission Sites if
         * 
         * @param nodeName - Id of node
         * @param fields - line fields
         * @return Node
         */
        private Node createTransmissionSite(NodeName nodeName, String[] fields) {
            if (nodeName==null||(nodeName.getId1()==null&&nodeName.getId2()==null)){
                return null;
            }
            //TODO create NetworkIndexes  if necessary.
            Transaction tx = neo.beginTx();
            try {
                Node result = neo.createNode();
                result.setProperty(INeoConstants.PROPERTY_TYPE_NAME,NodeTypes.SITE.getId());

                String id1 = nodeName.getId1();
                if (id1 != null) {
                    result.setProperty(INeoConstants.PROPERTY_NAME_NAME, id1);
                    index.index(result, NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), id1);
                }
                String id2 = nodeName.getId2();
                if (id2 != null) {
                    result.setProperty(INeoConstants.PROPERTY_SITE_NO, id2);
                    index.index(result, NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_SITE_NO, NodeTypes.SITE), id2);
                }
                tx.success();
                network.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
                return result;
            } finally {
                tx.finish();
            }
        }

        /**
         * Updates count of properties
         * 
         * @param serverNode node
         * @param name name of properties
         */
        private void updateCount(Node serverNode, String name) {
            serverNode.setProperty(name, (Integer)serverNode.getProperty(name, 0) + 1);
        }

        /**
         * Save value in property container
         * 
         * @param container property container
         * @param index index of property
         * @param value value of property
         * @return true if save is successful
         */
        private boolean saveValue(PropertyContainer container, Integer index, String value) {
            Pair<String, String> pair = indexMap.get(index);

            if (pair == null || pair.left() == null) {
                return false;
            }
            String key = pair.left();
            Object valueToSave;
            String clas = pair.right();
            try {
                if (clas == null) {
                    try {
                        valueToSave = Integer.parseInt(value);
                        clas = INTEGER;
                    } catch (NumberFormatException e) {
                        valueToSave = Double.parseDouble(value);
                        clas = DOUBLE;
                    }
                } else if (INTEGER.equals(clas)) {
                    try {
                        valueToSave = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        valueToSave = Double.parseDouble(value);
                        clas = DOUBLE;
                    }
                } else if (DOUBLE.equals(clas)) {
                    valueToSave = Double.parseDouble(value);
                } else {
                    valueToSave = value;
                    clas = STRING;
                }
            } catch (NumberFormatException e) {
                clas = STRING;
                valueToSave = value;
            }
            if (!valueToSave.toString().equals(value)) {
                valueToSave = value;
                clas = STRING;
            }
            pair.setRight(clas);
            // indexMap.put(index, pair.create(key, clas));
            container.setProperty(key, valueToSave);
            return true;
        }
    }

    /**
     * <p>
     * Class that contains information about sectors name
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class NodeName {
        /** String BTS_NAME field "Site No", "Site ID", "Item Name" */
        private static final String SITE_NO = "site_no";
        /** String LAC field */
        private static final String SITE_ID = "site_id";
        /** String CI field */
        private static final String ITEM_NAME = "item_Name";
        Map<String, String> nameMap = new HashMap<String, String>();
        Map<String, Integer> indexMap = new HashMap<String, Integer>();
        Map<String, String> valuesMap = new HashMap<String, String>();
        
        /**
         * Constructor
         * 
         * @param ci name of "SITE_NO" properties
         * @param lac name of "SITE_ID" properties
         * @param btsName name of "ITEM_NAME" properties
         */
        public NodeName(String[] siteId, String[] siteN, String[] ItemName) {
            for (String id : siteId) {
                nameMap.put(id, SITE_ID);
            }
            for (String id : siteN) {
                nameMap.put(id, SITE_NO);
            }
            for (String id : ItemName) {
                nameMap.put(id, ITEM_NAME);
            }
        }

        /**
         * get Id1 (SITE_ID)
         * 
         * @return id
         */
        public String getId1() {
            String ci = valuesMap.get(SITE_ID);
            if (ci == null || ci.isEmpty()) {
                return null;
            }
            return ci;
        }

        /**
         * get Id1 (SITE_ID)
         * 
         * @return id
         */
        public String getId2() {
            String ci = valuesMap.get(SITE_NO);
            if (ci == null || ci.isEmpty()) {
                return null;
            }
            return ci;
        }

        /**
         * get Id1 (CI+LAC) of node
         * 
         * @param node node
         * @return id
         */
        public static String getId1(Node node) {
            return (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
        }

        /**
         * get Id1 (CI+LAC) of node
         * 
         * @param node node
         * @return id
         */
        public static String getId2(Node node) {
            Object property = node.getProperty(INeoConstants.PROPERTY_SITE_NO, null);
            return property == null ? null : property.toString();
        }

        /**
         * Sets the properties of sector
         * 
         * @param fields array of values
         */
        public void setFieldValues(String[] fields) {
            valuesMap.clear();
            for (String index : indexMap.keySet()) {
                valuesMap.put(index, fields[indexMap.get(index)]);
            }
        }

        /**
         * Sets field index
         * 
         * @param fieldHeader field name
         * @param i - index
         * @return true if NodeName contains field
         */
        public boolean setFieldIndex(String fieldHeader, int i) {
            String key = nameMap.get(fieldHeader);
            // TODO identify column
            if (key == null && fieldHeader.length() > 1) {
                String findSubstring = fieldHeader.toLowerCase();
                for (String nameKey : nameMap.keySet()) {
                    if (nameKey.toLowerCase().equals(findSubstring)) {
                        key = nameMap.get(nameKey);
                        break;
                    }
                }
            }
            if (key == null) {
                return false;
            }
            // because site and child site header have equal name
            if (indexMap.get(key) != null) {
                return false;
            }
            indexMap.put(key, i);
            return true;
        }
    }
    /**
     * Search the database for the 'gis' node for this dataset. If none found it created an
     * appropriate node. The search is done for 'gis' nodes that reference the specified main node.
     * If a node needs to be created it is linked to the main node so future searches will return
     * it.
     * 
     * @param mainNode main network or drive data node
     * @return gis node for mainNode
     */
    protected final Node findOrCreateGISNode(String gisName, String gisType, NetworkTypes fileType) {
            Transaction transaction = neo.beginTx();
            Node gis;
            try {
                Node reference = neo.getReferenceNode();
                gis = NeoUtils.findGisNode(gisName, neo);
                if (gis == null) {
                    gis = neo.createNode();
                    gis.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.GIS.getId());
                    gis.setProperty(INeoConstants.PROPERTY_NAME_NAME, gisName);
                    gis.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, gisType);
                    if (fileType != null) {
                        gis.setProperty(NetworkTypes.PROPERTY_NAME, fileType.getId());
                    }
                    reference.createRelationshipTo(gis, NetworkRelationshipTypes.CHILD);
                }
                transaction.success();
            } finally {
                transaction.finish();
            }
        return gis;
    }
    /**
     * This code finds the specified network node in the database, creating its own transaction for
     * that.
     * 
     * @param gis gis node
     */
    protected Node findOrCreateNetworkNode(Node gisNode) {
        Node network;
        Transaction tx = neo.beginTx();
        try {
            Relationship relation = gisNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            if (relation != null) {
                return relation.getOtherNode(gisNode);
            }
            network = neo.createNode();
            network.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NETWORK.getId());
            network.setProperty(INeoConstants.PROPERTY_NAME_NAME, baseName);
            network.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, fileName);
            gisNode.createRelationshipTo(network, GeoNeoRelationshipTypes.NEXT);
            tx.success();
        } finally {
            tx.finish();
        }
        return network;
    }
}
