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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.sax_parsers.AbstractTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTagFactory;
import org.amanzi.neo.loader.sax_parsers.ReadContentHandler;
import org.amanzi.neo.loader.sax_parsers.SkipTag;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
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
private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    
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
    private HashMap<String, Node> bscMap = new HashMap<String, Node>();
    private HashMap<String, Node> sectorMap = new HashMap<String, Node>();
    private HashMap<String, Node> siteMap = new HashMap<String, Node>();
    private final LuceneIndexService luceneInd;
    
    private Set<String> allNeibProperties = new HashSet<String>();
    private Set<String> intNeibProperties = new HashSet<String>();
    private Set<String> doubleNeibProperties = new HashSet<String>();
    
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
        luceneInd = NeoServiceProvider.getProvider().getIndexService();
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
       Node result = NeoUtils.findNeighbour(gisNode, filename, neo);
       if (result != null) {
           return result;
       }
       Transaction tx = neo.beginTx();
       try {
           result = neo.createNode();
           result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
           result.setProperty(INeoConstants.PROPERTY_NAME_NAME, filename);
           gisNode.createRelationshipTo(result, NetworkRelationshipTypes.NEIGHBOUR_DATA);
           tx.success();
           return result;
       } finally {
           tx.finish();
       }
   }

    @Override
    protected Node getStoringNode(Integer key) {
        return null;
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
        mainTx = neo.beginTx();
        currentJobPr = 0;
        counter = 0;
        counterAll = 0;
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try{
            initializeIndexes();
            gisNode = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
            networkNode = findOrCreateNetworkNode(gisNode);
            File file = new File(filename);
            monitor.subTask(basename);
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
        try {
            finishUpGis(getGisProperties(basename).getGis());
        } catch (MalformedURLException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    
    /**
     * Save list of Neighbor properties in database
     */
    private void saveNeighbourFields() {
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
        luceneInd.index(child, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, type), name);
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
        counterAll++;
        if (counter > getCommitSize()) {
            commit(true);
            counter = 0;
        }
    }
    
    private void setSiteLocation(Node site, Float latitude, Float longitude) {
        Double oldLat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, 0.0);
        Double oldLon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, 0.0);
        if(oldLat == 0 || oldLon==0){
            GisProperties gisProperties = getGisProperties(basename);
            gisProperties.updateBBox(latitude, longitude);
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
        }
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
            return new SkipTag(this);
        }
    }    
    
    /**
     * <p>
     * Common class for all parsed managed objects.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private abstract class ManagedObjectTag extends AbstractTag{
        
        protected static final String EXTERNAL_BSC = "BSC-0";
        
        public static final String TAG_NAME = "managedObject";
        public static final String TAG_NAME_LIST = "list";        
        public static final String TAG_NAME_DEFAULTS = "defaults";
        private static final String TAG_ATTR_DIST_NAME = "distName";

        private String distName;
        private HashMap<String, StringBuilder> properties;
        
        /**
         * Constructor.
         * @param parent
         */
        protected ManagedObjectTag(IXmlTag parent, Attributes attributes) {
            super(TAG_NAME, parent);
            distName = attributes.getValue(TAG_ATTR_DIST_NAME);
            properties = new HashMap<String, StringBuilder>();
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if(localName.equals(TAG_NAME_LIST)){
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
                //getGisProperties(basename).incSaved();
                updateTx();
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
         * Add parsed property.
         *
         * @param key
         * @param value
         */
        public void addProperty(String key,StringBuilder value){
            properties.put(key, value);
        }
        
        /**
         * @param distName The distName to set.
         */
        public void setDistName(String distName) {
            this.distName = distName;
        }
        
        /**
         * Get BSC from distName.
         *
         * @return
         */
        public String getBSCKey(){
            String[] splitted = distName.split("/");
            if(splitted.length>1){
                return splitted[1];
            }
            return "";
        }
        
        /**
         * Find or create BSC Node.
         *
         * @return Node.
         */
        public Node getBSCNode(String bscName){
            String bscKey = getBSCKey();
            Node bsc = bscMap.get(bscKey);
            if(bsc == null){                
                bsc = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
                if(bsc==null){
                    bsc = addChild(networkNode, NodeTypes.BSC, bscName);
                }
                bscMap.put(bscKey, bsc);
            }
            return bsc;
        }
        
        /**
         * Get BCF from distName.
         *
         * @return
         */
        public String getBCFKey(){
            String[] splitted = distName.split("/");
            if(splitted.length>2){
                return splitted[2];
            }
            return "";
        }
        
        /**
         * Find or create Site Node.
         *
         * @return Node.
         */
        public Node getSiteNode(String siteName){
            String bcfKey = getBSCKey()+"_"+getBCFKey();
            Node site = siteMap.get(bcfKey);
            if(site == null){
                site = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if(site == null){
                    Node parent = getBSCNode(getBSCKey());
                    site = addChild(parent, NodeTypes.SITE, siteName);
                    site.setProperty("site_type", "site_2g");
                    (lastSite == null ? networkNode : lastSite).createRelationshipTo(site, GeoNeoRelationshipTypes.NEXT);
                    lastSite = site;
                    index(site);
                }
                siteMap.put(bcfKey, site);
            }
            return site;
        }
        
        /**
         * Get BTS from distName.
         *
         * @return String
         */
        public String getBTSKey(){
            String[] splitted = distName.split("/");
            if(splitted.length>3){
                return splitted[3];
            }
            return "";
        }
        
        /**
         * Find or create Sector Node.
         *
         * @return Node.
         */
        public Node getSectorNode(String sectorName){
            String btsKey = getBSCKey()+"_"+getBCFKey()+"_"+getBTSKey();
            Node sector = sectorMap.get(btsKey);
            if(sector == null){
                sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), sectorName);
                if(sector == null){
                    Node parent = getSiteNode(getBCFKey());
                    sector = addChild(parent, NodeTypes.SECTOR, sectorName);
                    index(sector);
                }
                sectorMap.put(btsKey, sector);
            }
            return sector;
        }
        
        protected boolean isExternal() {
            return getBSCKey().equals(EXTERNAL_BSC);
        }
        
        /**
         * @return Returns the properties.
         */
        public HashMap<String, StringBuilder> getProperties() {
            return properties;
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
            HashMap<String, StringBuilder> properties = getProperties();
            String postfix = "";
            if(isExternal()){
                postfix = " (external)";
            }
            String bscName = properties.get("name").toString()+postfix;
            Node bsc = getBSCNode(bscName);            
            for(String key : properties.keySet()){
                Object old = bsc.getProperty(key, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("neSwRelease"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("name")){
                    newValue = bscName;
                }
                if(old == null || !old.equals(newValue)){
                    bsc.setProperty(key, newValue);
                    if(key.equals("name")){
                        luceneInd.index(bsc, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
                    }
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
            String bcfKey = getBCFKey();
            String postfix = "";
            if(isExternal()){
                postfix = " (external)";
            }
            HashMap<String, StringBuilder> properties = getProperties();
            StringBuilder nameValue = properties.get("name");            
            String siteName = (nameValue==null?bcfKey:nameValue.toString())+postfix;
            Node site = getSiteNode(siteName);
            Float latitude = Float.parseFloat(properties.get("latitude").toString());
            Float longitude = Float.parseFloat(properties.get("longitude").toString());
            setSiteLocation(site, latitude, longitude);
            for(String key : properties.keySet()){
                if(key.equals("latitude")||key.equals("longitude")){
                    continue;
                }
                Object old = site.getProperty(key, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("lapdLinkName"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("name")){
                    newValue = siteName;
                }                
                if(old == null || !old.equals(newValue)){
                    site.setProperty(key, newValue);
                    if(key.equals("name")){
                        luceneInd.index(site, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
                    }
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
            String postfix = "";
            if(isExternal()){
                postfix = " (external)";
            }
            HashMap<String, StringBuilder> properties = getProperties();    
            String sectorName = properties.get("name").toString()+postfix;
            Node sector = getSectorNode(sectorName);
            for(String key : properties.keySet()){
                Object old = sector.getProperty(key, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("segmentName")
                        ||key.equals("address")||key.equals("nwName"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("name")){
                    newValue = ((String)newValue)+postfix;
                }
                if(old == null || !old.equals(newValue)){
                    sector.setProperty(key, newValue);
                    if(key.equals("name")){
                        luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(basename, key, NodeTypes.SECTOR), newValue);
                    }
                }
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for Radio Node. TODO Implement this.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class RadioTag extends ManagedObjectTag{

        /**
         * Constructor.
         * @param parent
         */
        protected RadioTag(IXmlTag parent, Attributes attributes) {
            super(parent,attributes);
        }

        @Override
        protected void saveData() {
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
            HashMap<String, StringBuilder> properties = getProperties();
            StringBuilder propValue = properties.get("linkedCellDN");
            if(propValue==null){
                return;
            }
            setDistName(propValue.toString());
            Node site = getSiteNode(getBCFKey());
            int degr = getIntValue(properties.get("latDegrees"));
            int min = getIntValue(properties.get("latMinutes"));
            int sec = getIntValue(properties.get("latSeconds"));
            Float latitude = buildCoord(degr, min, sec);
            degr = getIntValue(properties.get("lonDegrees"));
            min = getIntValue(properties.get("lonMinutes"));
            sec = getIntValue(properties.get("lonSeconds"));
            Float longitude = buildCoord(degr, min, sec);
            setSiteLocation(site, latitude, longitude);
            
            Node sector = getSectorNode(getBTSKey());
            String key = "beamwidth";
            Integer value = getIntValue(properties.get("antHorHalfPwrBeam"));
            setIndexProperty(headers, sector, key, value);
            value = getIntValue(properties.get("antBearing"));
            key = "azimuth";
            setIndexProperty(headers, sector, key, value);
        }
        
        private Integer getIntValue(StringBuilder chars){
            return Integer.parseInt(chars.toString());
        }
        
        private Float buildCoord(int degrees, int min, int sec){
            float mins = min+((float)sec)/60.0f;
            return ((float)degrees)+mins/60.0f;
        }
    }
    
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
            HashMap<String, StringBuilder> properties = getProperties();
            Node server = getSectorNode(getBTSKey());
            StringBuilder propValue = properties.get("targetCellDN");
            if(propValue==null){
                return;
            }
            setDistName(propValue.toString());
            StringBuilder nameValue = properties.get("name");
            String name = nameValue==null?getBTSKey():nameValue.toString();
            Node neighbour = getSectorNode(name);
            getNeighbourNode(); //initialize neighbors
            Relationship relation = server.createRelationshipTo(neighbour, NetworkRelationshipTypes.NEIGHBOUR);
            relation.setProperty(INeoConstants.NEIGHBOUR_NAME, filename);
            for(String key : properties.keySet()){
                if(key.equals("name")||key.equals("targetCellDN")){
                    continue;
                }
                Object value = properties.get(key).toString();
                if(!key.equals("address")){
                    value = Integer.parseInt((String)value);
                    intNeibProperties.add(key);
                }
                relation.setProperty(key, value);
                allNeibProperties.add(key);
            }
            String servCounName = NeoUtils.getNeighbourPropertyName(filename);
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
    }
   
    /**
     * <p>
     * Tag for property.
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class PropertyTag extends AbstractTag{
        
        private static final String TAG_ATTR_NAME = "name";
        public static final String TAG_NAME = "p";
        
        private String name;

        /**
         * Constructor.
         * @param parent parent tag
         * @param aNode Node for save property.
         * @param attributes tag attributes
         */
        protected PropertyTag(IXmlTag parent, Attributes attributes) {
            super(TAG_NAME, parent);
            name = attributes.getValue(TAG_ATTR_NAME);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            return this;
        }
        
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (localName.equals(getName())) {
                ((ManagedObjectTag)parent).addProperty(name, chars);
                return parent;
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
        }
        
    }
}
