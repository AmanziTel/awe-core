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

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * NeighbourLoader - imports Neighbour data from file into database
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeighbourLoader {

    private static final String PROXY_NAME_SEPARATOR = "/";

    // private static String directory = null;
    private static final int COMMIT_MAX = 1000;
    private final Node network;
    private final String fileName;
    private Header header;
    private Node neighbour;
    private final String baseName;
    private final LuceneIndexService index;
    private final String gisName;
    
    private boolean isTest = false;
    private boolean isInterference = false;

    /**
     * Constructor
     * 
     * @param networkNode network Node
     * @param fileName Neighbour file Name
     */
    public NeighbourLoader(Node networkNode, String fileName, boolean isInterference) {
        network = NeoUtils.findRoot(networkNode);
        this.fileName = fileName;
        this.baseName = new File(fileName).getName();
        this.isInterference = isInterference;
        gisName = NeoUtils.getNodeName(networkNode);
        index = NeoServiceProviderUi.getProvider().getIndexService();
    }
    
    /**
     * Constructor
     * 
     * @param networkNode network Node
     * @param fileName Neighbour file Name
     */
    public NeighbourLoader(Node networkNode, String fileName, LuceneIndexService indexService, boolean isTesting) {
        network = NeoUtils.findRoot(networkNode);
        this.fileName = fileName;
        this.baseName = new File(fileName).getName();
        gisName = NeoUtils.getNodeName(networkNode);
        index = indexService;
        isTest = isTesting;
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
        Transaction tx = network.getGraphDatabase().beginTx();
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
            header = new Header(line, network.getGraphDatabase(), index, gisName);
            neighbour = getNeighbour(network, baseName);
//            lastSector = neighbour;
            int commit = 0;
            while ((line = reader.readLine()) != null) {
                header.parseLine(line, network, baseName, neighbour, isInterference);
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
                    tx = network.getGraphDatabase().beginTx();
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
            if (!isTest) {
                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                        new UpdateDatabaseEvent(UpdateViewEventType.NEIGHBOUR));
            }
            tx.finish();
            // 8.02.2010 Shcharbatsevich A. - check header for null.
            if (header != null) {
                header.finish();
            }
        }

    }

    /**
     * get neighbour
     * 
     * @param network network node
     * @param fileName neighbour file name
     * @return neighbour node
     */
    private Node getNeighbour(Node network, String fileName) {
        GraphDatabaseService neo = network.getGraphDatabase();
        Node result = NeoUtils.findNeighbour(network, fileName, neo);
        if (result != null) {
            return result;
        }
        Transaction tx = neo.beginTx();
        try {
            result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, fileName);
            if(this.isInterference) {
            	network.createRelationshipTo(result, NetworkRelationshipTypes.INTERFERENCE_DATA);
            } else {
            	network.createRelationshipTo(result, NetworkRelationshipTypes.NEIGHBOUR_DATA);
            }
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
    public  class Header {
        /** String STRING field */
        private static final String STRING = "STRING";
        /** String DOUBLE field */
        private static final String DOUBLE = "DOUBLE";
        /** String INTEGER field */
        private static final String INTEGER = "INTEGER";
        private final Map<Integer, Pair<String, String>> indexMap = new LinkedHashMap<Integer, Pair<String, String>>();
        private final NodeName serverNodeName;
        private final NodeName neighbourNodeName;
        private final String[] headers;
        private final LuceneIndexService index;
        private final GraphDatabaseService neo;
        private final String gisName;
        private Node lastSector;
        private String proxySectorName;
        private String proxyNeighbourName;
        private HashMap<String,Integer> missingServers;
        private HashMap<String,Integer> missingNeighbours;
        private int lineNumber;

        /**
         * Constructor
         * 
         * @param line - header line
         */
        public Header(String line, GraphDatabaseService neo, LuceneIndexService index, String gisName) {
            this.index = index;
            this.gisName = gisName;
            this.neo = neo;
            headers = line.split("\\t");
            IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
            String ciString = preferenceStore.getString(DataLoadPreferences.NE_SRV_CI);
            String lacString = preferenceStore.getString(DataLoadPreferences.NE_SRV_LAC);
            String btsString = preferenceStore.getString(DataLoadPreferences.NE_SRV_NAME);
            serverNodeName = new NodeName(ciString, lacString, btsString);
            ciString = preferenceStore.getString(DataLoadPreferences.NE_NBR_CI);
            lacString = preferenceStore.getString(DataLoadPreferences.NE_NBR_LAC);
            btsString = preferenceStore.getString(DataLoadPreferences.NE_NBR_NAME);
            neighbourNodeName = new NodeName(ciString, lacString, btsString);
            missingServers = new HashMap<String,Integer>();
            missingNeighbours = new HashMap<String,Integer>();
            lineNumber = 0;
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
            logMissing();
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
        public void parseLine(String line, Node network, String fileName, Node neighbour, boolean isInterference) {
            String fields[] = line.split("\\t");
            lineNumber++;
            RelationshipType relType2Proxy = NetworkRelationshipTypes.NEIGHBOURS;
            RelationshipType relTypeProxy2Proxy = NetworkRelationshipTypes.NEIGHBOUR;
            if(isInterference) {
                relType2Proxy = NetworkRelationshipTypes.INTERFERENCE;
                relTypeProxy2Proxy = NetworkRelationshipTypes.INTERFERS;
            }
            Transaction tx = neo.beginTx();
        	try {
                String servCounName = NeoUtils.getNeighbourPropertyName(fileName);
                serverNodeName.setFieldValues(fields);
                neighbourNodeName.setFieldValues(fields);
                Node serverNode = getSectorNodeById(serverNodeName);
                if (serverNode == null) {
                    if (addMissing(missingServers, serverNodeName) < 2)
                        NeoLoaderPlugin.error("Could not find server from line: " + line);
                    return;
                }

                Node neighbourNode = null;
                Node proxyServer = null;
                Node proxyNeighbour = null;

                if (serverNode != null) {
                	proxySectorName = fileName + PROXY_NAME_SEPARATOR + serverNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                    neighbourNode = getSectorNodeById(neighbourNodeName);
                    for (Node node: serverNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
                    	@Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            Node node = currentPos.currentNode();
                            return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString().equals(proxySectorName);
                        }
                    }, relType2Proxy, Direction.OUTGOING)){
                    		proxyServer = node;
                    		break;
                    }
                    if (proxyServer == null) {
                    	proxyServer = NeoUtils.createProxySector(serverNode, fileName, neighbour, lastSector, relType2Proxy, neo);
                    	index.index(proxyServer, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
                    	lastSector = proxyServer;
                    }
                }
                
                if (neighbourNode != null){
                	proxyNeighbourName = fileName + PROXY_NAME_SEPARATOR + neighbourNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
                	for (Node node: neighbourNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
                		@Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            Node node = currentPos.currentNode();
                            return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString().equals(proxyNeighbourName);
                        }
                	}, relType2Proxy, Direction.OUTGOING)){
                    		proxyNeighbour = node;
                    		break;
                    }
                    if (proxyNeighbour == null) {
                    	proxyNeighbour = NeoUtils.createProxySector(neighbourNode, fileName, neighbour, lastSector, relType2Proxy, neo);
                    	index.index(proxyNeighbour, NeoUtils.getLuceneIndexKeyByProperty(neighbour, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxyNeighbourName);
                    	lastSector = proxyNeighbour;
                    }
                } else {
                    if (addMissing(missingNeighbours, neighbourNodeName) < 2)
                        NeoLoaderPlugin.error("Could not find neighbour from line: " + line);
                    return;
                }

                Relationship relation = proxyServer.createRelationshipTo(proxyNeighbour, relTypeProxy2Proxy);
//                relation.setProperty(INeoConstants.NEIGHBOUR_NAME, fileName);
                for (Integer index : indexMap.keySet()) {
                    String value = fields[index];
                    if (value.length() > 0) {
                        saveValue(relation, index, value);
                    }
                }
                // count++;
                // pairServ.setRight(count);
                updateCount(serverNode, servCounName);
                tx.success();
            } catch (Exception e) {
                NeoLoaderPlugin.error(line + "\n" + e.getLocalizedMessage());
            } finally {
                tx.finish();
            }

        }

        private int addMissing(HashMap<String, Integer> missing, NodeName nodeName) {
            String name = nodeName.getName();
            Integer count = missing.get(name);
            if (count == null)
                count = 1;
            else
                count++;
            missing.put(name, count);
            return count;
        }
        
        private void logMissing() {
            logMissing("server", missingServers);
            logMissing("neighbour", missingNeighbours);
        }

        private void logMissing(String type, HashMap<String, Integer> missing) {
            if (missing.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String name : missing.keySet()) {
                    if (sb.length() < 1) {
                        sb.append("Could not find " + missing.size() + " " + type);
                        if (missing.size() == 1)
                            sb.append(": ");
                        else
                            sb.append("s: ");
                    } else {
                        sb.append(", ");
                    }
                    sb.append(name);
                }
                NeoLoaderPlugin.notify(sb.toString());
            }
        }

        /**
         * find node by id
         * 
         * @param nodeName NodeName
         * @return sector node or null
         */
        private Node getSectorNodeById(NodeName nodeName) {
            Node result = null;
            String idByName = nodeName.getName();
            if (idByName != null) {
                result = index.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), idByName);
            }
            if (result == null) {
                Pair<String, String> idByCiLac = nodeName.getCiLac();
                if (idByCiLac != null) {
                    Iterable<Node> serverNodeIter = index.getNodes(NeoUtils.getLuceneIndexKeyByProperty(network, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR),
                            idByCiLac.getLeft());
                    for (Node sector : serverNodeIter) {
                        if (sector.getProperty(INeoConstants.PROPERTY_SECTOR_LAC, "").toString().equals(idByCiLac.getRight())) {
                            result = sector;
                            // TODO add to log found by CI+LAC!
                            break;
                        }
                    }
                }
            }
            return result;
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
     * Class that contains information about sectors name and optional LAC/CI identities. Two
     * instances of this class are used, one for each of the server and the neighbour (interferer)
     * in the relationship.
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private static class NodeName {
        private static final String NAME = "NAME";
        private static final String LAC = "LAC";
        private static final String CI = "CI";
        Map<String, String> nameMap = new HashMap<String, String>();
        Map<String, Integer> indexMap = new HashMap<String, Integer>();
        Map<String, String> valuesMap = new HashMap<String, String>();

        /**
         * Construct with the user configured or default regular expressions to match to find the
         * appropriate headers.
         * 
         * @param Regex for "CI" properties
         * @param Regex for "LAC" properties
         * @param Regex for "NAME" properties (server, interferer, neighbour)
         */
        public NodeName(String ciString, String lacString, String nameString) {
            for (String ci : getPossibleHeaders(ciString)) {
                nameMap.put(ci, CI);
            }
            for (String lac : getPossibleHeaders(lacString)) {
                nameMap.put(lac, LAC);
            }
            for (String name : getPossibleHeaders(nameString)) {
                nameMap.put(name, NAME);
            }
        }

        /**
         * @param key -key of value from preference store
         * @return array of possible headers
         */
        protected List<String> getPossibleHeaders(String text) {
            String[] array = text.split(",");
            List<String> result = new ArrayList<String>();
            for (String string : array) {
                String value = string.trim();
                if (!value.isEmpty()) {
                    result.add(value);
                }
            }
            return result;
        }

        /**
         * Get the current value of the CI and LAC fields as a pair.
         * 
         * @return Pair of strings for CI and LAC
         */
        public Pair<String, String> getCiLac() {
            String ci = valuesMap.get(CI);
            if (ci == null || ci.isEmpty()) {
                return null;
            }
            String lac = valuesMap.get(LAC);
            if (lac == null || lac.isEmpty()) {
                return null;
            }
            return new Pair<String, String>(ci, lac);
        }

        /**
         * Get the current value of the Name field (BTS_NAME, Server, Neighbour)
         * 
         * @return String name
         */
        public String getName() {
            String name = valuesMap.get(NAME);
            if (name == null || name.isEmpty()) {
                return null;
            }
            return name;
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
            if (key == null) {
                return false;
            }
            indexMap.put(key, i);
            return true;
        }

    }

}
