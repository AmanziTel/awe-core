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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.sax_parsers.AbstractTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTagFactory;
import org.amanzi.neo.loader.sax_parsers.ReadContentHandler;
import org.amanzi.neo.loader.sax_parsers.SkipTag;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.GisTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Utils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.index.lucene.LuceneIndexService;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * Loader for NokiaGSM files
 * </p>
 * 
 * @author Cinkel_A
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaTopologyLoader extends AbstractLoader {
    /** String FORMAT_STR field */
    private static final String FORMAT_STR = "Node %s. Property %s. Old valus %s. New value %s not saved";
    private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private static final String TAG_ATTR_NAME = "name";
    private static final String LIST_NAME = "frequency";
    private static final String PROXY_NAME_SEPARATOR = "/";
    
    private final ReadContentHandler handler;
    private CountingFileInputStream inputStream;
    private int currentJobPr;
    private IProgressMonitor monitor;
    private final LinkedHashMap<String, Header> headers;
    
    private int counter;
    private int counterAll;
    
    private Node gisNode;
    private Node networkNode;
    private Node neighborNode;
    private Node lastSite;
    private Node lastUmtsSite;
    private final HashMap<String, Node> bscMap = new HashMap<String, Node>();
    private final HashMap<String, Node> sectorMap = new HashMap<String, Node>();
    private final Set<String> sectorMissBalList = new HashSet<String>();
    private final Set<String> sectorMissUsedMalList = new HashSet<String>();
    private final Set<String> sectorMissUnderlayMalList = new HashSet<String>();
    private final HashMap<String, Node> siteMap = new HashMap<String, Node>();
    private final HashMap<String, Node> umtsSectorMap = new HashMap<String, Node>();
    private final HashMap<String, Node> umtsSiteMap = new HashMap<String, Node>();
    private final HashMap<String, Set<Integer>> balMap = new HashMap<String, Set<Integer>>();
    private final HashMap<String, Set<Integer>> malMap = new HashMap<String, Set<Integer>>();
    private final LuceneIndexService luceneInd;
    
    private String neighbourName;
    private String proxySectorName;
    private Node lastSector;
    private String proxyNeighbourName;
    private final Set<String> allNeibProperties = new HashSet<String>();
    private final Set<String> intNeibProperties = new HashSet<String>();
    private final Set<String> doubleNeibProperties = new HashSet<String>();
    
    /**
     * Constructor.
     * @param fileName file name
     * @param network network(gis) name
     * @param display Display
     */
    public NokiaTopologyLoader(String fileName, String network, Display display, LuceneIndexService index, GraphDatabaseService servise) {
        initialize("Network", servise, fileName, display);
        basename = network;
        headers = getHeaderMap(1).headers;
        handler = new ReadContentHandler(new LoadFactory());
        if(index == null){
            luceneInd = NeoServiceProviderUi.getProvider().getIndexService();
        }else{
            luceneInd = index;
        }
        addNetworkIndexes();
    }
    
    /**
     * Constructor.
     * @param fileName file name
     * @param network network(gis) name
     * @param display Display
     */
    public NokiaTopologyLoader(String fileName, String network, Display display) {
        initialize("Network", null, fileName, display);
        basename = network;
        headers = getHeaderMap(1).headers;
        handler = new ReadContentHandler(new LoadFactory());
        luceneInd = NeoServiceProviderUi.getProvider().getIndexService();
        addNetworkIndexes();
    }
    
    /**
    * Add indexes to network.
    */
   private void addNetworkIndexes() {
       try {
           addIndex(NodeTypes.SITE.getId(), NeoUtils.getLocationIndexProperty(basename));
       } catch (IOException e) {
           throw (RuntimeException)new RuntimeException().initCause(e);
       }
   }
   
   public Node getNeighbourNode() {
       if(neighborNode==null){
           neighborNode = initNeighbour();
       }
       return neighborNode;
   }
   
   /**
    * Find neighbor by network or create it.
    * 
    * @return Node
    */
   private Node initNeighbour() {
       neighbourName = basename;
       Node result = NeoUtils.findNeighbour(networkNode, neighbourName, neo);
       if (result != null) {
           return result;
       }
       Transaction tx = neo.beginTx();
       try {
           result = neo.createNode();
           result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
           result.setProperty(INeoConstants.PROPERTY_NAME_NAME, neighbourName);
           networkNode.createRelationshipTo(result, NetworkRelationshipTypes.NEIGHBOUR_DATA);
           tx.success();
           return result;
       } finally {
           tx.finish();
       }
   }

    @Override
    protected Node getStoringNode(Integer key) {
        return networkNode;
    }
    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.SECTOR.getId();
    }
    @Override
    public Node[] getRootNodes() {
        return new Node[]{networkNode};
    }
    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }

    @Override
    public void run(IProgressMonitor aMonitor) throws IOException {
        if (aMonitor == null){
            monitor = new NullProgressMonitor();
        }else{
            monitor = aMonitor;
        }
        monitor.subTask(basename);
        mainTx = neo.beginTx();
        currentJobPr = 0;
        counter = 0;
        counterAll = 0;
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try{
            initializeIndexes();
            gisNode = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
            networkNode = findOrCreateNetworkNode(gisNode);
            int allJob = 100;
            monitor.beginTask("Load Nokia topology", allJob);
            File file = new File(filename);            
            try {
                monitor.subTask(filename);
                XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                rdr.setContentHandler(handler);
                rdr.setFeature(EXTERNAL_DTD_LOADING_FEATURE, false);
                inputStream = new CountingFileInputStream(file);
                rdr.parse(new InputSource(new BufferedInputStream(inputStream, 64 * 1024)));
            } catch (SAXException e) {
                e.printStackTrace();
                NeoLoaderPlugin.error("Wrong parse file: " + file.getName());
                NeoLoaderPlugin.exception(e);
            }
            inputStream.close();
            saveProperties();
            saveNeighbourFields();
            finishUpIndexes();
            finishUp();
        }finally{
            commit(true);
        }
        
    }
    
    @Override
    protected void finishUp() {
        super.finishUp();
        GisProperties gisProperties = getGisProperties(basename);
        gisProperties.saveBBox();
        gisProperties.saveCRS();
        if(!isTest()){           
            try {
                finishUpGis();
            } catch (MalformedURLException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            if (neighborNode!=null&&!isTest()) {
                sendUpdateEvent(UpdateViewEventType.NEIGHBOUR);
            }
        }
    }
    
    /**
     * Save list of Neighbor properties in database
     */
    private void saveNeighbourFields() {
        if(neighborNode==null){
            return;
        }
        Transaction tx = neo.beginTx();
        try {
            neighborNode.setProperty(INeoConstants.LIST_ALL_PROPERTIES, allNeibProperties.toArray(new String[0]));
            neighborNode.setProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, doubleNeibProperties.toArray(new String[0]));
            neighborNode.setProperty(INeoConstants.LIST_INTEGER_PROPERTIES, intNeibProperties.toArray(new String[0]));
            tx.success();
        } finally {
            tx.finish();
        }
    }
    
    /**
     * This code expects you to create a transaction around it, so don't forget to do that.
     * 
     * @param parent
     * @param type
     * @param name
     * @return Node
     */
    private Node addChild(Node parent, NodeTypes type, String name) {
        Node child = null;
        child = neo.createNode();
        child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type.getId());
        child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        luceneInd.index(child, NeoUtils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, type), name);
        if (parent != null) {
            parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            debug("Added '" + name + "' as child of '" + parent.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        }
        return child;
    }
    
    public void updateMonitor() {
        int pr = inputStream.percentage();
        if (pr > currentJobPr) {
            info(String.format("parsed %s bytes\tcreated nodes %s", inputStream.tell(), counterAll));
            monitor.worked(pr - currentJobPr);
            currentJobPr = pr;
        }
    }
    
    protected void updateTx() {
        counter++;
        counterAll=bscMap.size()+sectorMap.size()+siteMap.size()+umtsSectorMap.size()+umtsSiteMap.size();
        if (counter > getCommitSize()) {
            commit(true);
            counter = 0;
        }
    }
    
    private void setSiteLocation(Node site, Float latitude, Float longitude) {
        Double oldLat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, 0.0);
        Double oldLon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, 0.0);
        if(oldLat.equals(0.0) || oldLon.equals(0.0)){
            GisProperties gisProperties = getGisProperties(basename);
            gisProperties.updateBBox(latitude, longitude);
            gisProperties.incSaved();
            if (gisProperties.getCrs() == null) {
                gisProperties.checkCRS(latitude, longitude, "");
                if (!isTest()&&gisProperties.getCrs() != null) {
                    CoordinateReferenceSystem crs = askCRSChoise(gisProperties);
                    if (crs != null) {
                        gisProperties.setCrs(crs);
                        gisProperties.saveCRS();
                    }
                }
            }
            site.setProperty(INeoConstants.PROPERTY_LAT_NAME, latitude.doubleValue());
            site.setProperty(INeoConstants.PROPERTY_LON_NAME, longitude.doubleValue());
            index(site);
        }
    }

    private void addNeighborLink(HashMap<String, Object> properties, Node server, Node neighbour) {
    	Node proxyServer = null;
        Node proxyNeighbour = null;
        
        getNeighbourNode(); //initialize neighbors
        proxySectorName = neighbourName + PROXY_NAME_SEPARATOR + server.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        proxyNeighbourName = neighbourName + PROXY_NAME_SEPARATOR + neighbour.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        
        for (Node node: server.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
        	@Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString().equals(proxySectorName);
            }
        }, NetworkRelationshipTypes.NEIGHBOURS, Direction.OUTGOING)){
        		proxyServer = node;
        		break;
        }
        if (proxyServer == null) {
        	proxyServer = NeoUtils.createProxySector(server, neighbourName, neighborNode, lastSector, NetworkRelationshipTypes.NEIGHBOURS, neo);
        	luceneInd.index(proxyServer, NeoUtils.getLuceneIndexKeyByProperty(neighborNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
        	lastSector = proxyServer;
        }
        
        
        for (Node node: neighbour.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
    		@Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString().equals(proxyNeighbourName);
            }
    	}, NetworkRelationshipTypes.NEIGHBOURS, Direction.OUTGOING)){
        		proxyNeighbour = node;
        		break;
        }
        if (proxyNeighbour == null) {
        	proxyNeighbour = NeoUtils.createProxySector(neighbour, neighbourName, neighborNode, lastSector, NetworkRelationshipTypes.NEIGHBOURS, neo);
        	luceneInd.index(proxyNeighbour, NeoUtils.getLuceneIndexKeyByProperty(neighborNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxyNeighbourName);
        	lastSector = proxyNeighbour;
        }
        
        Relationship relation = Utils.getNeighbourRelation(proxyServer, proxyNeighbour, neighbourName);
        if (relation==null){
            relation = proxyServer.createRelationshipTo(proxyNeighbour, NetworkRelationshipTypes.NEIGHBOUR);
//            relation.setProperty(INeoConstants.NEIGHBOUR_NAME, neighbourName);
        }
        for(String key : properties.keySet()){
            if(key.equals("name")||key.equals("targetCellDN")){
                continue;
            }
            Object value = properties.get(key);
            if(!(value instanceof StringBuilder)){
                continue;
            }
            value = value.toString();
            if(!key.equals("address")){
                value = Integer.parseInt((String)value);
                intNeibProperties.add(key);
            }
            relation.setProperty(key, value);
            allNeibProperties.add(key);
        }
        String servCounName = NeoUtils.getNeighbourPropertyName(neighbourName);
        updateCount(server, servCounName);
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
     * Pars object to integer value
     *
     * @param chars Object
     * @return Integer
     */
    private Integer getIntValue(Object chars){
        if(chars==null){
            return 0;
        }
        return Integer.parseInt(chars.toString());
    }
    
    private Integer setUsedMalFrequency(String bscKey,Node sector){
        Set<Integer> freqSet = malMap.get(bscKey);
        if(freqSet==null || freqSet.isEmpty()){
            return null;
        }        
        Integer num = (Integer)sector.getProperty("usedMobileAllocIdUsed", null);
        if(num==null || num>=freqSet.size()){
            return null;
        }
        List<Integer> freqList = getSortedListBySet(freqSet);
        Integer real = freqList.get(num);
        setIndexPropertyNotParcedValue(headers, sector, "usedMobileAllocIdUsed", real.toString());
        return real;
    }
    
    private Integer setUnderlayMalFrequency(String bscKey,Node sector){
        Set<Integer> freqSet = malMap.get(bscKey);
        if(freqSet==null || freqSet.isEmpty()){
            return null;
        }        
        Integer num = (Integer)sector.getProperty("underlayMaIdUsed", null);
        if(num==null || num>=freqSet.size()){
            return null;
        }
        List<Integer> freqList = getSortedListBySet(freqSet);
        Integer real = freqList.get(num);
        setIndexPropertyNotParcedValue(headers, sector, "underlayMaIdUsed", real.toString());
        return real;
    }
    
    private Integer setBalFrequency(String bscKey,Node sector){
        Set<Integer> freqSet = balMap.get(bscKey);
        if(freqSet==null || freqSet.isEmpty()){
            return null;
        }        
        Integer num = (Integer)sector.getProperty("idleStateBcchAllocListId", null);
        if(num==null || num>=freqSet.size()){
            return null;
        }
        List<Integer> freqList = getSortedListBySet(freqSet);
        Integer real = freqList.get(num);
        setIndexPropertyNotParcedValue(headers, sector, "idleStateBcchAllocListId", real.toString());
        return real;
    }
    
    private List<Integer> getSortedListBySet(Set<Integer> set){
        List<Integer> result = new ArrayList<Integer>(set);
        Collections.sort(result);
        return result;
    }

    /**
     * <p>
     * Factory for parse file.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class LoadFactory implements IXmlTagFactory{

        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (tagName.equals(CmDataTag.TAG_NAME_CM)) {
                return new CmDataTag();
            }
            return null;
        }
        
    }
    
    /**
     * <p>
     * Handler "CmData" tag
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class CmDataTag extends AbstractTag{
        
        private static final String BSC_CLASS = "BSC";
        private static final String BCF_CLASS = "BCF";
        private static final String BTS_CLASS = "BTS";
        private static final String TRX_CLASS = "TRX";
        private static final String LCSE_CLASS = "LCSE";
        private static final String ADCE_CLASS = "ADCE";
        private static final String EXCC_CLASS = "EXCC";
        private static final String EWCE_CLASS = "EWCE";
        private static final String ADJW_CLASS = "ADJW";
        private static final String BAL_CLASS = "BAL";
        private static final String MAL_CLASS = "MAL";
        
        public static final String TAG_NAME_CM = "cmData";
        public static final String TAG_NAME_HEADER = "header";
        
        public static final String TAG_ATTR_CLASS = "class";        

        /**
         * @param tagName
         * @param parent
         */
        public CmDataTag() {
            super(TAG_NAME_CM, null);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(TAG_NAME_HEADER)){
                return new SkipTag(this);
            }
            if(localName.equals(ManagedObjectTag.TAG_NAME)){
                String objClass = attributes.getValue(TAG_ATTR_CLASS);
                return getTagByName(objClass,attributes);
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }
     
        /**
         * Returns tag for managed object by class name.
         *
         * @param name class name
         * @return IXmlTag
         */
        private IXmlTag getTagByName(String name, Attributes attributes){
            if(name.equals(BSC_CLASS)){
                return new BSCTag(this,attributes);
            }
            if(name.equals(BCF_CLASS)){
                return new SiteTag(this,attributes);
            }
            if(name.equals(BTS_CLASS)){
                return new SectorTag(this,attributes);
            }
            if(name.equals(TRX_CLASS)){
                return new SkipTag(this);
                //return new RadioTag(this,attributes); TODO Uncomment after implement.
            }
            if(name.equals(LCSE_CLASS)){
                return new LCSETag(this,attributes);
            }
            if(name.equals(ADCE_CLASS)){
                return new ADCETag(this,attributes);
            }
            if(name.equals(EXCC_CLASS)){
                return new EXCCTag(this,attributes);
            }
            if(name.equals(EWCE_CLASS)){
                return new EWCETag(this,attributes);
            }
            if(name.equals(ADJW_CLASS)){
                return new ADJWTag(this,attributes);
            }
            if(name.equals(BAL_CLASS)){
                return new BALTag(this,attributes);
            }
            if(name.equals(MAL_CLASS)){
                return new MALTag(this,attributes);
            }
            return new SkipTag(this);
        }
    }  
    
    private abstract class PropertyContainerTag extends AbstractTag{

        private final HashMap<String, Object> properties;
        
        /**
         * @param tagName
         * @param parent
         */
        protected PropertyContainerTag(String tagName, IXmlTag parent) {
            super(tagName, parent);
            properties = new HashMap<String, Object>();
        }
        
        /**
         * Add parsed property.
         *
         * @param key
         * @param value
         */
        public void addProperty(String key,Object value){
            properties.put(key, value);
        }
        
        /**
         * @return Returns the properties.
         */
        public HashMap<String, Object> getProperties() {
            return properties;
        }
    }
    
    /**
     * <p>
     * Common class for all parsed managed objects.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private abstract class ManagedObjectTag extends PropertyContainerTag{
        
        protected static final String EXTERNAL_BSC = "BSC-0";
        
        public static final String TAG_NAME = "managedObject";
        public static final String TAG_NAME_DEFAULTS = "defaults";
        private static final String TAG_ATTR_DIST_NAME = "distName";

        private String distName;

        /**
         * Constructor.
         * @param parent
         */
        protected ManagedObjectTag(IXmlTag parent, Attributes attributes) {
            super(TAG_NAME, parent);
            distName = attributes.getValue(TAG_ATTR_DIST_NAME);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(ListTag.TAG_NAME)){
                return new SkipTag(this);
            }
            if(localName.equals(PropertyTag.TAG_NAME)){
                return new PropertyTag(this, attributes);
            }
            if(localName.equals(TAG_NAME_DEFAULTS)){
                return new SkipTag(this); 
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }
        
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (localName.equals(getName())) {
                saveData();
                updateTx();
                updateMonitor();
                return parent;
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
        }
        
        /**
         * Save parsed data.
         */
        protected abstract void saveData();
        
        /**
         * @param distName The distName to set.
         */
        public void setDistName(String distName) {
            this.distName = distName;
        }
        
        /**
         * Gets key from distName by number.
         *
         * @param keyNum
         * @return
         */
        public String getKeyFromDistName(int keyNum){
            String[] splitted = distName.split("/");
            if(splitted.length>keyNum){
                return splitted[keyNum];
            }
            return "";
        }
        
        /**
         * Find or create BSC Node.
         *
         * @return Node.
         */
        public Node getBSCNode(String bscName){
            String bscKey = getKeyFromDistName(1);
            Node bsc = bscMap.get(bscKey);
            if(bsc == null){                
                bsc = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
                if(bsc==null){
                    bsc = addChild(networkNode, NodeTypes.BSC, bscName);
                }
                bscMap.put(bscKey, bsc);
            }
            return bsc;
        }
        
        /**
         * Find or create Site Node.
         *
         * @return Node.
         */
        public Node getSiteNode(String siteName, boolean isGsm){
            String bcfKey = getKeyFromDistName(1)+(isGsm?("_"+getKeyFromDistName(2)):"");
            Node site = isGsm?siteMap.get(bcfKey):umtsSiteMap.get(bcfKey);            
            if(site == null){
                site = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(networkNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if(site == null){
                    Node parent = isGsm?getBSCNode(getKeyFromDistName(1)):(lastUmtsSite==null?networkNode:lastUmtsSite);
                    site = addChild(parent, NodeTypes.SITE, siteName);
                    site.setProperty("site_type", isGsm?"site_2g":"site_3g");
                    if (isGsm) {
                        (lastSite == null ? networkNode : lastSite).createRelationshipTo(site, GeoNeoRelationshipTypes.NEXT);
                        lastSite = site;
                    }else{
                        lastUmtsSite = site;
                    }
                }
                if (isGsm) {
                    siteMap.put(bcfKey, site);
                }else{
                    umtsSiteMap.put(bcfKey, site);
                }
            }
            return site;
        }
        
        /**
         * Find or create Sector Node.
         *
         * @return Node.
         */
        public Node getSectorNode(String sectorName,Integer ci,Integer lac, boolean isGsm){
            String btsKey = getKeyFromDistName(1)+"_"+getKeyFromDistName(2)+(isGsm?("_"+getKeyFromDistName(3)):"");
            Node sector = isGsm?sectorMap.get(btsKey):umtsSectorMap.get(btsKey);
            if(sector == null){
                sector=NeoUtils.findSector(networkNode, ci, lac, sectorName, false, luceneInd, neo);
//                sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
                if(sector == null){
                    Node parent = getSiteNode(getKeyFromDistName(2),isGsm);
                    sector = addChild(parent, NodeTypes.SECTOR, sectorName);
                    storingProperties.values().iterator().next().incSaved();
                }
                if (isGsm) {
                    sectorMap.put(btsKey, sector);
                }else{
                    umtsSectorMap.put(btsKey, sector);
                    index(sector);
                }
            }
            return sector;
        }
        
        protected boolean isExternal() {
            return getKeyFromDistName(1).equals(EXTERNAL_BSC);
        }
        
    }
    
    /**
     * <p>
     * Tag for BSC Node.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    protected class BSCTag extends ManagedObjectTag{

        /**
         * Constructor.
         * @param parent
         */
        protected BSCTag(IXmlTag parent, Attributes attributes) {
            super(parent,attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties();
            String postfix = "";
            if(isExternal()){
                postfix = " (external)";
            }
            String bscName = properties.get("name").toString()+postfix;
            Node bsc = getBSCNode(bscName);            
            for(String key : properties.keySet()){
                Object old = bsc.getProperty(key, null);
                Object newValue = properties.get(key);
                if(!(newValue instanceof StringBuilder)){
                    continue;
                }
                newValue = newValue.toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("neSwRelease"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("name")){
                    newValue = bscName;
                }
                if(old == null || !old.equals(newValue)){
                    bsc.setProperty(key, newValue);
//                    if(key.equals("name")){
//                        luceneInd.index(bsc, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
//                    }
                }
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for Site Node.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class SiteTag extends ManagedObjectTag{

        /**
         * Constructor.
         * @param parent
         */
        protected SiteTag(IXmlTag parent, Attributes attributes) {
            super(parent,attributes);
        }

        @Override
        protected void saveData() {
            String bcfKey = getKeyFromDistName(2);
            String postfix = "";
            if(isExternal()){
                postfix = " (external)";
            }
            HashMap<String, Object> properties = getProperties();
            StringBuilder nameValue = (StringBuilder)properties.get("name");            
            String siteName = nameValue==null?(bcfKey+postfix):nameValue.toString();
            Node site = getSiteNode(siteName,true);
            Float latitude = Float.parseFloat(properties.get("latitude").toString());
            Float longitude = Float.parseFloat(properties.get("longitude").toString());
            setSiteLocation(site, latitude, longitude);
            for(String key : properties.keySet()){
                if(key.equals("latitude")||key.equals("longitude")){
                    continue;
                }
                Object old = site.getProperty(key, null);
                Object newValue = properties.get(key);
                if(!(newValue instanceof StringBuilder)){
                    continue;
                }
                newValue = newValue.toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("lapdLinkName"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("name")){
                    newValue = siteName;
                }                
                if(old == null || !old.equals(newValue)){
                    site.setProperty(key, newValue);
//                    if(key.equals("name")){
//                        luceneInd.index(site, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
//                    }
                }
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for Sector Node. TODO one Sector for several BTS.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class SectorTag extends ManagedObjectTag{


        /**
         * Constructor.
         * @param parent
         */
        protected SectorTag(IXmlTag parent, Attributes attributes) {
            super(parent,attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties(); 
            String sectorName = properties.get("name").toString();
            Integer ci=null;
            Integer lac=null;
            Object ciObj = properties.get("cellId");
            if (ciObj!=null){
                ci=Integer.valueOf(ciObj.toString());
            }
            Object lacObj = properties.get("locationAreaIdLAC");
            if (lacObj!=null){
                lac=Integer.valueOf(lacObj.toString());
            }
            
            Node sector = getSectorNode(sectorName,ci,lac, true);
            for(String key : properties.keySet()){
                Object newValue = properties.get(key);
                if (key.equals("cellId")){
                    key=INeoConstants.PROPERTY_SECTOR_CI;
                }else if (key.equals("locationAreaIdLAC")){
                    key=INeoConstants.PROPERTY_SECTOR_LAC;
                }else if (key.equals("name")){
                    continue;
                }
                Object old = sector.getProperty(key, null);
                if(!(newValue instanceof StringBuilder)){
                    continue;
                }
                if(old == null){
                    if (INeoConstants.PROPERTY_SECTOR_CI.equals(key)){
                        setIndexProperty(headers, sector, key, ci);
                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(networkNode, key, NodeTypes.SECTOR), ci);
                        continue;
                    }else if (INeoConstants.PROPERTY_SECTOR_LAC.equals(key)){
                        setIndexProperty(headers, sector, key, lac);
                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(networkNode, key, NodeTypes.SECTOR), lac);
                        continue;
                    }
                    setIndexPropertyNotParcedValue(headers, sector, key, newValue.toString());
//                    if(key.equals("name")){
//                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
//                    }
                }else if (!old.toString().equals(newValue.toString())){
                    info(String.format(FORMAT_STR,sectorName,key,old,newValue));
                }
            }
            String bscKey = getKeyFromDistName(1);
            String btsKey = bscKey+"_"+getKeyFromDistName(2)+"_"+getKeyFromDistName(3);
            Integer real = setBalFrequency(bscKey, sector);
            if(real == null){
                sectorMissBalList.add(btsKey);
            }
            real = setUsedMalFrequency(bscKey, sector);
            if(real == null){
                sectorMissUsedMalList.add(btsKey);
            }
            real = setUnderlayMalFrequency(bscKey, sector);
            if(real == null){
                sectorMissUnderlayMalList.add(btsKey);
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for getting LCSE data (locations)
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class LCSETag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected LCSETag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties();
            StringBuilder propValue = (StringBuilder)properties.get("linkedCellDN");
            if(propValue==null){
                return;
            }
            setDistName(propValue.toString());
            Node site = getSiteNode(getKeyFromDistName(2),true);
            int degr = getIntValue(properties.get("latDegrees"));
            int min = getIntValue(properties.get("latMinutes"));
            int sec = getIntValue(properties.get("latSeconds"));
            Float latitude = buildCoord(degr, min, sec);
            degr = getIntValue(properties.get("lonDegrees"));
            min = getIntValue(properties.get("lonMinutes"));
            sec = getIntValue(properties.get("lonSeconds"));
            Float longitude = buildCoord(degr, min, sec);
            setSiteLocation(site, latitude, longitude);
            Integer ci=null;
            Integer lac=null;
            Object ciObj = properties.get("lcsGlobalIdCi");
            if (ciObj!=null){
                ci=Integer.valueOf(ciObj.toString());
            }
            Object lacObj = properties.get("lcsGlobalIdLac");
            if (lacObj!=null){
                lac=Integer.valueOf(lacObj.toString());
            }
            Node sector = getSectorNode(getKeyFromDistName(3),ci,lac,true);
            String key = "beamwidth";
            Integer value = getIntValue(properties.get("antHorHalfPwrBeam"));
            setIndexProperty(headers, sector, key, value);
            value = getIntValue(properties.get("antBearing"));
            key = "azimuth";
            setIndexProperty(headers, sector, key, value);
            index(sector);
        }
        
        private Float buildCoord(int degrees, int min, int sec){
            float mins = min+(sec)/60.0f;
            return (degrees)+mins/60.0f;
        }
    }
    
    /**
     * <p>
     * Tag for getting ADCE data (GSM to GSM neighbors)
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class ADCETag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected ADCETag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties();
            //sector already saved in database
            Node server = getSectorNode(getKeyFromDistName(3),null,null,true);
            StringBuilder propValue = (StringBuilder)properties.get("targetCellDN");
            if(propValue==null){
                return;
            }
            setDistName(propValue.toString());
            StringBuilder nameValue = (StringBuilder)properties.get("name");
            String name = nameValue==null?getKeyFromDistName(3):nameValue.toString();
            Integer ci=null;
            Integer lac=null;
            Object ciObj = properties.get("adjacentCellIdCI");
            if (ciObj!=null){
                ci=Integer.valueOf(ciObj.toString());
            }
            Object lacObj = properties.get("adjacentCellIdLac");
            if (lacObj!=null){
                lac=Integer.valueOf(lacObj.toString());
            }            
            Node neighbour = getSectorNode(name,ci,lac,true);
            addNeighborLink(properties, server, neighbour);
        }
        
    }
    
    /**
     * <p>
     * Tag for getting EXCC data (UMTS site)
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class EXCCTag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected EXCCTag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
            initSite(attributes);
        }
        
        /**
         * Find or create empty UMTS site.
         *
         * @param attributes
         */
        private void initSite(Attributes attributes){
            String name = getKeyFromDistName(1)+" (external UMTS cells)";
            Node site = getSiteNode(name, false);
            setSiteLocation(site, 0.0f, 0.0f);
        }

        @Override
        protected void saveData() {
        }
        
    }
    
    /**
     * <p>
     * Tag for getting EWCE data (UMTS sector)
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class EWCETag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected EWCETag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties();    
            String postfix = "";//" (external)";
            String sectorName = properties.get("name").toString()+postfix;
            Integer ci=null;
            Integer lac=null;
            Object ciObj = properties.get("CId");
            if (ciObj!=null){
                ci=Integer.valueOf(ciObj.toString());
            }
            Object lacObj = properties.get("LAC");
            if (lacObj!=null){
                lac=Integer.valueOf(lacObj.toString());
            }
            Node sector = getSectorNode(sectorName,ci,lac, false);
            for(String key : properties.keySet()){
                Object newValue = properties.get(key);
                if (key.equals("CId")){
                    key=INeoConstants.PROPERTY_SECTOR_CI;
                }else if (key.equals("LAC")){
                    key=INeoConstants.PROPERTY_SECTOR_LAC;
                }else if (key.equals("name")){
                    continue;
                }
                Object old = sector.getProperty(key, null);
                if(!(newValue instanceof StringBuilder)){
                    continue;
                }
                newValue = newValue.toString();
                    newValue = Integer.parseInt((String)newValue);
                if(old == null ){
                    if (INeoConstants.PROPERTY_SECTOR_CI.equals(key)){
                        setIndexProperty(headers, sector, key, ci);
                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(networkNode, key, NodeTypes.SECTOR), ci);
                        continue;
                    }else if (INeoConstants.PROPERTY_SECTOR_LAC.equals(key)){
                        setIndexProperty(headers, sector, key, lac);
                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(networkNode, key, NodeTypes.SECTOR), lac);
                        continue;
                    }
                    sector.setProperty(key, newValue);
                }else if (!old.toString().equals(newValue.toString())){
                    info(String.format(FORMAT_STR,sectorName,key,old,newValue));
                } 
            }
        }
        
    }
    /**
     * <p>
     * Tag for getting ADCE data (GSM to UMTS neighbors)
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class ADJWTag extends ManagedObjectTag{

       /**
         * @param parent
         * @param attributes
         */
        protected ADJWTag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }

        @Override
        protected void saveData() {
            HashMap<String, Object> properties = getProperties();
            //always stored in database
            Node server = getSectorNode(getKeyFromDistName(3),null,null,true);
            StringBuilder propValue = (StringBuilder)properties.get("targetCellDN");
            if(propValue==null){
                return;
            }
            setDistName(propValue.toString());
            StringBuilder nameValue = (StringBuilder)properties.get("name");
            String name = nameValue==null?getKeyFromDistName(2):nameValue.toString();
            Integer ci=null;
            Integer lac=null;
            Object ciObj = properties.get("AdjwCId");
            if (ciObj!=null){
                ci=Integer.valueOf(ciObj.toString());
            }
            Object lacObj = properties.get("lac");
            if (lacObj!=null){
                lac=Integer.valueOf(lacObj.toString());
            } 
            Node neighbour = getSectorNode(name,ci,lac,false);            
            addNeighborLink(properties, server, neighbour);
        }
        
    }
    
    /**
     * <p>
     * Tag for getting BAL (BCCH Allocation List) data.
     * </p>
     * @author Shcharbatsevich_a
     * @since 1.0.0
     */
    private class BALTag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected BALTag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }
        
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(ListTag.TAG_NAME)){
                return new ListTag(this,attributes);
            }
            if(localName.equals(PropertyTag.TAG_NAME)){
                return new SkipTag(this);
            }
            if(localName.equals(TAG_NAME_DEFAULTS)){
                return new SkipTag(this); 
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void saveData() {
            HashMap<String, Object> allProperties = getProperties();
            Object listValue = allProperties.get(LIST_NAME);
            if(listValue==null || !(listValue instanceof HashMap<?, ?>)){
                return;
            }
            HashMap<String, Object> listMap = (HashMap<String, Object>)listValue;
            String bscKey = getKeyFromDistName(1);
            Set<Integer> freqSet = balMap.get(bscKey);
            if(freqSet == null){
                freqSet = new HashSet<Integer>();
                balMap.put(bscKey, freqSet);
            }
            for(String key : listMap.keySet()){
                Integer freq = getIntValue(listMap.get(key));
                if(freq!=null){
                    freqSet.add(freq);
                }
            }
            HashMap<String, Node> sectors = getAllLoadedSectorsByBsc(bscKey);
            if(sectors.isEmpty()){
                return;
            }
            for(String key : sectors.keySet()){
                Node sector = sectors.get(key);
                Integer freqNum = setBalFrequency(bscKey, sector);
                if(freqNum!=null){
                    sectorMissBalList.remove(key);
                }
            }
        }
        
        private HashMap<String, Node> getAllLoadedSectorsByBsc(String bscKey){
            HashMap<String, Node> result = new HashMap<String, Node>();
            for(String key : sectorMissBalList){
                if(key.startsWith(bscKey)){
                    result.put(key,sectorMap.get(key));
                }
            }
            return result;
        }
        
    }
    
    /**
     * <p>
     * Tag for getting MAL (Mobile Allocation List) data.
     * </p>
     * @author Shcharbatsevich_a
     * @since 1.0.0
     */
    private class MALTag extends ManagedObjectTag{

        /**
         * @param parent
         * @param attributes
         */
        protected MALTag(IXmlTag parent, Attributes attributes) {
            super(parent, attributes);
        }
        
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(ListTag.TAG_NAME)){
                return new ListTag(this,attributes);
            }
            if(localName.equals(PropertyTag.TAG_NAME)){
                return new SkipTag(this);
            }
            if(localName.equals(TAG_NAME_DEFAULTS)){
                return new SkipTag(this); 
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void saveData() {
            HashMap<String, Object> allProperties = getProperties();
            Object listValue = allProperties.get(LIST_NAME);
            if(listValue==null || !(listValue instanceof HashMap<?, ?>)){
                return;
            }
            HashMap<String, Object> listMap = (HashMap<String, Object>)listValue;
            String bscKey = getKeyFromDistName(1);
            Set<Integer> freqSet = malMap.get(bscKey);
            if(freqSet == null){
                freqSet = new HashSet<Integer>();
                malMap.put(bscKey, freqSet);
            }
            for(String key : listMap.keySet()){
                Integer freq = getIntValue(listMap.get(key));
                if(freq!=null){
                    freqSet.add(freq);
                }
            }
            HashMap<String, Node> sectors = getAllLoadedSectorsByBscUsed(bscKey);
            if(!sectors.isEmpty()){
                for(String key : sectors.keySet()){
                    Node sector = sectors.get(key);
                    Integer freqNum = setUsedMalFrequency(bscKey, sector);
                    if(freqNum!=null){
                        sectorMissUsedMalList.remove(key);
                    }
                }
            }
            sectors = getAllLoadedSectorsByBscUnderlay(bscKey);
            if(!sectors.isEmpty()){
                for(String key : sectors.keySet()){
                    Node sector = sectors.get(key);
                    Integer freqNum = setUnderlayMalFrequency(bscKey, sector);
                    if(freqNum!=null){
                        sectorMissUnderlayMalList.remove(key);
                    }
                }
            }
        }
        
        /**
         * Get all loaded sectors linked to current BSC, that miss used MAL data
         *
         * @param bscKey String
         * @return HashMap<String, Node>
         */
        private HashMap<String, Node> getAllLoadedSectorsByBscUsed(String bscKey){
            HashMap<String, Node> result = new HashMap<String, Node>();
            for(String key : sectorMissUsedMalList){
                if(key.startsWith(bscKey)){
                    result.put(key,sectorMap.get(key));
                }
            }
            return result;
        }
        
        /**
         * Get all loaded sectors linked to current BSC, that miss underlay MAL data
         *
         * @param bscKey String
         * @return HashMap<String, Node>
         */
        private HashMap<String, Node> getAllLoadedSectorsByBscUnderlay(String bscKey){
            HashMap<String, Node> result = new HashMap<String, Node>();
            for(String key : sectorMissUnderlayMalList){
                if(key.startsWith(bscKey)){
                    result.put(key,sectorMap.get(key));
                }
            }
            return result;
        }
        
    }
    
    /**
     * <p>
     * Tag for list of properties.
     * </p>
     * @author Shcharbatsevich_a
     * @since 1.0.0
     */
    private class ListTag extends PropertyContainerTag{
        
        public static final String TAG_NAME = "list";
        private final String listName;
        private int propCount;
        
        /**
         * @param tagName
         * @param parent
         */
        protected ListTag(IXmlTag parent, Attributes attributes) {
            super(TAG_NAME, parent);
            listName = attributes.getValue(TAG_ATTR_NAME);
            propCount = 0;
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(PropertyTag.TAG_NAME)){
                return new PropertyTag(this,"val"+(propCount++));
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (localName.equals(getName())) {
                ((PropertyContainerTag)parent).addProperty(listName, getProperties());
                return parent;
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
        }
    }
   
    /**
     * <p>
     * Tag for property.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class PropertyTag extends AbstractTag{
        
        public static final String TAG_NAME = "p";
        
        private final String pName;

        /**
         * Constructor.
         * @param parent parent tag
         * @param attributes tag attributes
         */
        protected PropertyTag(IXmlTag parent, Attributes attributes) {
            super(TAG_NAME, parent);
            pName = attributes.getValue(TAG_ATTR_NAME);
        }
        
        /**
         * Constructor.
         * @param parent
         * @param aName
         */
        protected PropertyTag(IXmlTag parent, String aName) {
            super(TAG_NAME, parent);
            pName = aName;
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            return this;
        }
        
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (localName.equals(getName())) {
                ((PropertyContainerTag)parent).addProperty(pName, chars);
                return parent;
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
        }
        
    }
}
