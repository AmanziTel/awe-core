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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.neo4j.index.lucene.LuceneIndexService;
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
    
    private String basename;    
    private final ReadContentHandler handler;
    private CountingFileInputStream inputStream;
    private int currentJobPr;
    private IProgressMonitor monitor;
    
    private List<String> sitesFromOterNetwork = new ArrayList<String>();
    
    private Node networkNode;
    private Node lastSite;
    private HashMap<String, Node> bscMap = new HashMap<String, Node>();
    private HashMap<String, Node> sectorMap = new HashMap<String, Node>();
    private HashMap<String, Node> siteMap = new HashMap<String, Node>();
    private final LuceneIndexService luceneInd;    
    
    /**
     * Constructor.
     * @param fileName file name
     * @param network network(gis) name
     * @param display Display
     */
    public NokiaTopologyLoader(String fileName, String network, Display display) {
        initialize("Network", null, fileName, display);
        basename = network;
        handler = new ReadContentHandler(new LoadFactory());
        luceneInd = NeoServiceProvider.getProvider().getIndexService();
        //TODO other.
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
        try{
            Node gis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
            networkNode = findOrCreateNetworkNode(gis);
            File file = new File(filename);
            monitor.subTask(basename);
            try {
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
        }finally{
            commit(true);
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
            info(String.format("parsed %s bytes", inputStream.tell()));
            monitor.worked(pr - currentJobPr);
            currentJobPr = pr;
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
                return new RadioTag(this,attributes);
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
                return new SkipTag(this); //TODO ?
            }
            throw new IllegalArgumentException("Wrong tag: " + localName);
        }
        
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (localName.equals(getName())) {
                saveData();
                commit(true);//TODO after some nodes?
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
            String bcfKey = getBCFKey();
            Node site = siteMap.get(bcfKey);
            if(site == null){
                site = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if(site == null){
                    Node parent = getBSCNode(getBSCKey());
                    site = addChild(parent, NodeTypes.SITE, siteName);
                    //TODO set SiteType
                    (lastSite == null ? networkNode : lastSite).createRelationshipTo(site, GeoNeoRelationshipTypes.NEXT);
                    lastSite = site;
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
            String bscName = properties.get("name").toString();
            Node bsc = getBSCNode(bscName);            
            for(String key : properties.keySet()){
                Object old = bsc.getProperty(key, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("neSwRelease"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(old == null || !old.equals(newValue)){
                    bsc.setProperty(key, newValue);
                    if(key.equals("name")){
                        luceneInd.index(bsc, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), newValue);
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
            if(getBSCKey().equals("BSC-0")){
                sitesFromOterNetwork.add(getBCFKey());
                return; //ignore data from other network. TODO rework after answer.
            }
            HashMap<String, StringBuilder> properties = getProperties();
            StringBuilder nameValue = properties.get("name");
            String siteName = nameValue==null?getBCFKey():nameValue.toString();
            Node site = getSiteNode(siteName);
            for(String key : properties.keySet()){
                Object old = site.getProperty(key, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("lapdLinkName")
                        ||key.equals("latitude")||key.equals("longitude"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(key.equals("latitude")||key.equals("longitude")){
                    newValue = Double.parseDouble((String)newValue);
                }
                if(old == null || !old.equals(newValue)){
                    site.setProperty(key, newValue);
                    if(key.equals("name")){
                        luceneInd.index(site, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), newValue);
                    }
                }
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for Sector Node.
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
            if(sitesFromOterNetwork.contains(getBCFKey())){
                return; //ignore data from other network. TODO rework after answer.
            }
            HashMap<String, StringBuilder> properties = getProperties();            
            String btsKey = getBTSKey();
            Node sector = sectorMap.get(btsKey);
            if(sector == null){
                String sectorName = properties.get("segmentName").toString();
                sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
                if(sector == null){
                    Node parent = getSiteNode(getBCFKey());
                    sector = addChild(parent, NodeTypes.SECTOR, sectorName);
                }
                sectorMap.put(getBTSKey(), sector);
            }
            for(String key : properties.keySet()){
                if(key.equals("segmentName")){
                    continue;
                }
                String nodeKey = key;
                if(key.equals("name") ){
                    nodeKey = "name (BTS "+btsKey+")"; //TODO other different data.
                }
                Object old = sector.getProperty(nodeKey, null);
                Object newValue = properties.get(key).toString();
                if(!(key.equals("name")||key.equals("address")||key.equals("nwName"))){                    
                    newValue = Integer.parseInt((String)newValue);
                }
                if(old == null || !old.equals(newValue)){
                    sector.setProperty(nodeKey, newValue);
                }
            }
        }
        
    }
    
    /**
     * <p>
     * Tag for Radio Node.
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
