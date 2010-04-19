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
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.amanzi.neo.loader.sax_parsers.PropertyCollector;
import org.amanzi.neo.loader.sax_parsers.ReadContentHandler;
import org.amanzi.neo.loader.sax_parsers.SkipTag;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
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

// TODO: Auto-generated Javadoc
//TODO create abstract XML loader?
//TODO merge methods, which works with neighbour

/**
 * <p>
 * Loader for UTRAN files
 * </p>
 * .
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class UTRANLoader extends AbstractLoader {
    private static final String FORMAT_STR = "Node %s. Property %s. Old valus %s. New value %s not saved";
    /** String EXT_GSM field */
    private static final String EXT_GSM = "extGSM";
    private static final String UTRAN_SEC_TYPE = "utran";
    private static final String GSM_SEC_TYPE = "gsm";

    /** The Constant KEY_EVENT. */
    private static final int KEY_EVENT = 1;

    /** The Constant REG_EXP_XML. */
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";

    /** The Constant siteProperty. */
    private static final List<String> siteProperty;

    /** The Constant UTRAN_NEIGHBOUR_NAME. */
    private static final String UTRAN_NEIGHBOUR_NAME = "utran relation";

    /** The Constant GSM_NEIGHBOUR_NAME. */
    private static final String GSM_NEIGHBOUR_NAME = "external gsm relation";
    static {
        siteProperty = new LinkedList<String>();
        siteProperty.add("latitude");
        siteProperty.add("sectorAntennasRef");
        siteProperty.add("sectorAntennaRef");
        siteProperty.add("longitude");
        siteProperty.add("geoDatum");
    }
    private final Map<String,Integer[]>idMap=new HashMap<String,Integer[]>();
    /** The network. */
    public Node network;

    /** The headers. */
    private final LinkedHashMap<String, Header> headers;

    /** The handler. */
    private final ReadContentHandler handler;
    // private Node ossNode;
    // private Node fileNode;
    /** The counter. */
    private int counter;

    /** The counter all. */
    private long counterAll;

    /** The in. */
    private CountingFileInputStream in;

    /** The current job pr. */
    private int currentJobPr;

    /** The monitor. */
    private IProgressMonitor monitor;

    /** The gis. */
    private Node gis;

    /** The rnc. */
    private Node rnc;
    /** The index. */
    private final LuceneIndexService index;

    /** The utran neighbour node. */
    private Node utranNeighbourNode;

    /** The gsm neighbour node. */
    private Node gsmNeighbourNode;

    /** The utran all neib properties. */
    private final Set<String> utranAllNeibProperties = new HashSet<String>();

    /** The utran int neib properties. */
    private final Set<String> utranIntNeibProperties = new HashSet<String>();

    /** The utran double neib properties. */
    private final Set<String> utranDoubleNeibProperties = new HashSet<String>();

    /** The gsm all neib properties. */
    private final Set<String> gsmAllNeibProperties = new HashSet<String>();

    /** The gsm int neib properties. */
    private final Set<String> gsmIntNeibProperties = new HashSet<String>();

    /** The gsm double neib properties. */
    private final Set<String> gsmDoubleNeibProperties = new HashSet<String>();

    public Node rncExtGsmSite;
    private int perc;

    /**
     * Constructor.
     * 
     * @param file - file name
     * @param datasetName - dataset name
     * @param display - Display
     */
    public UTRANLoader(String file, String datasetName, Display display) {
        initialize("UTRANLoader", null, file, display);
        basename = datasetName;
        utranNeighbourNode = null;
        headers = getHeaderMap(KEY_EVENT).headers;
        handler = new ReadContentHandler(new Factory());
        index = NeoServiceProvider.getProvider().getIndexService();
        try {
            addIndex(NodeTypes.SITE.getId(), NeoUtils.getLocationIndexProperty(basename));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Instantiates a new uTRAN loader.
     * 
     * @param fileName the file name
     * @param network the network
     * @param display the display
     * @param index the index
     * @param servise the servise
     */
    public UTRANLoader(String fileName, String network, Display display, LuceneIndexService index, GraphDatabaseService servise) {
        initialize("UTRANLoader", servise, fileName, display);
        basename = network;
        utranNeighbourNode = null;
        headers = getHeaderMap(KEY_EVENT).headers;
        handler = new ReadContentHandler(new Factory());
        if (index == null) {
            this.index = NeoServiceProvider.getProvider().getIndexService();
        } else {
            this.index = index;
        }
        try {
            addIndex(NodeTypes.SITE.getId(), NeoUtils.getLocationIndexProperty(basename));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * storing XML in database.
     * 
     * @param file - XML file
     * @throws SAXException the sAX exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void storeFile(File file) throws SAXException, IOException {

        currentJobPr = 0;
        gis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
        updateTx();
        network = findOrCreateNetworkNode(gis);
        updateTx();
        perc = 0;
        idMap.clear();
        createIdMap(file);
        perc = 50;
        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(handler);
        in = new CountingFileInputStream(file);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
    }

    /**
     * @param file
     * @throws SAXException
     * @throws IOException
     */
    private void createIdMap(File file) throws SAXException, IOException {
        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(new ReadContentHandler(new FactoryId()));
        in = new CountingFileInputStream(file);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
    }

    /**
     * update monitor description.
     */
    public void updateMonitor() {
        int pr = in.percentage() / 2 + perc;
        if (pr > currentJobPr) {
            info(String.format("parsed %s bytes\tcreated nodes %s", in.tell(), counterAll));
            monitor.worked(pr - currentJobPr);
            currentJobPr = pr;
        }
    }

    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        counter = 0;
        counterAll = 0;
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.subTask(basename);
        // addIndex(MI_TYPE, NeoUtils.getTimeIndexProperty(basename));

        this.monitor = monitor;
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            // monitor = SubMonitor.convert(monitor, sizeAll);
            // ossNode = LoaderUtils.findOrCreateOSSNode(OssType.UTRAN, basename, neo);
            File file = new File(filename);
            List<File> fileList;
            if (file.isDirectory()) {
                fileList = LoaderUtils.getAllFiles(filename, new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory() || Pattern.matches(REG_EXP_XML, pathname.getName());
                    }
                });
            } else {
                fileList = new ArrayList<File>();
                if (Pattern.matches(REG_EXP_XML, file.getName())) {
                    fileList.add(file);
                }
            }
            int allJob = fileList.size() * 100;
            int currentPos = 0;
            monitor.beginTask("Load UTRAN files", allJob);
            for (File singleFile : fileList) {
                try {
                    monitor.subTask(file.getName());
                    System.out.println(file.getName());
                    storeFile(singleFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    NeoLoaderPlugin.error("Wrong parse file: " + file.getName());
                    NeoLoaderPlugin.exception(e);
                }
            }
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
        }
    }

    /**
     * Adds the utran neighour.
     * 
     * @param server the server
     * @param neighbour the neighbour
     * @return the relationship
     */
    private Relationship addUtranNeighour(Node server, Node neighbour) {
        getUtranNeighbourNode();
        return addNeighbourLink(server, neighbour, UTRAN_NEIGHBOUR_NAME);
    }

    /**
     * Adds the gsm neighour.
     * 
     * @param server the server
     * @param neighbour the neighbour
     * @return the relationship
     */
    private Relationship addGsmNeighour(Node server, Node neighbour) {
        getGSMNeighbourNode();
        return addNeighbourLink(server, neighbour, GSM_NEIGHBOUR_NAME);
    }

    /**
     * Adds the neighbour link.
     * 
     * @param server the server
     * @param neighbour the neighbour
     * @param neighbourName the neighbour name
     * @return the relationship
     */
    private Relationship addNeighbourLink(Node server, Node neighbour, String neighbourName) {
        Transaction tx = neo.beginTx();
        try {
            Relationship relation = NeoUtils.getNeighbourRelation(server, neighbour, neighbourName, neo);
            if (relation == null) {
                relation = server.createRelationshipTo(neighbour, NetworkRelationshipTypes.NEIGHBOUR);
                relation.setProperty(INeoConstants.NEIGHBOUR_NAME, neighbourName);
                String servCounName = NeoUtils.getNeighbourPropertyName(neighbourName);
                updateCount(server, servCounName);
                tx.success();
            }
            return relation;
        } finally {
            tx.finish();
        }
    }

    /**
     * Updates count of properties.
     * 
     * @param serverNode node
     * @param name name of properties
     */
    private void updateCount(Node serverNode, String name) {
        serverNode.setProperty(name, (Integer)serverNode.getProperty(name, 0) + 1);
    }

    /**
     * Gets the storing node.
     * 
     * @param key the key
     * @return the storing node
     */
    @Override
    protected Node getStoringNode(Integer key) {
        return gis;
    }

    /**
     * Need parce headers.
     * 
     * @return true, if successful
     */
    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    /**
     * Parses the line.
     * 
     * @param line the line
     */
    @Override
    protected void parseLine(String line) {
    }

    /**
     * Finish up.
     */
    @Override
    protected void finishUp() {
        super.finishUp();
        saveStatistics();
        getGisProperties(basename).saveBBox();
        getGisProperties(basename).saveCRS();
        try {
            DriveLoader.finishUpGis(getGisProperties(basename).getGis());
        } catch (MalformedURLException e) {
            // TODO Handle MalformedURLException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Save statistics.
     */
    private void saveStatistics() {
        saveStat(utranNeighbourNode, utranAllNeibProperties, utranIntNeibProperties, utranDoubleNeibProperties);
        saveStat(gsmNeighbourNode, gsmAllNeibProperties, gsmIntNeibProperties, gsmDoubleNeibProperties);
    }

    /**
     * Save statistics.
     * 
     * @param statNode the stat node
     * @param allPropMap the all prop map
     * @param intMap the int map
     * @param doubleMap the double map
     */
    private void saveStat(Node statNode, Set<String> allPropMap, Set<String> intMap, Set<String> doubleMap) {
        if (statNode == null) {
            return;
        }
        Transaction tx = neo.beginTx();
        try {
            statNode.setProperty(INeoConstants.LIST_ALL_PROPERTIES, allPropMap.toArray(new String[0]));
            statNode.setProperty(INeoConstants.LIST_DOUBLE_PROPERTIES, doubleMap.toArray(new String[0]));
            statNode.setProperty(INeoConstants.LIST_INTEGER_PROPERTIES, intMap.toArray(new String[0]));
            HashSet<String> num = new HashSet<String>(intMap);
            num.addAll(doubleMap);
            statNode.setProperty(INeoConstants.LIST_NUMERIC_PROPERTIES, num.toArray(new String[0]));
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Update tx.
     */
    protected void updateTx() {
        counter++;
        counterAll++;
        if (counter > getCommitSize()) {
            commit(true);
            counter = 0;
        }
    }
    protected void updateTx(int count) {
        counter+=count;
        counterAll+=count;
        if (counter > getCommitSize()) {
            commit(true);
            counter = 0;
        }
    }
    /**
     * Gets the utran neighbour node.
     * 
     * @return the utran neighbour node
     */
    private Node getUtranNeighbourNode() {
        if (utranNeighbourNode == null) {
            utranNeighbourNode = getNeighbour(gis, UTRAN_NEIGHBOUR_NAME);
        }
        return utranNeighbourNode;
    }

    /**
     * Gets the gSM neighbour node.
     * 
     * @return the gSM neighbour node
     */
    private Node getGSMNeighbourNode() {
        if (gsmNeighbourNode == null) {
            gsmNeighbourNode = getNeighbour(gis, GSM_NEIGHBOUR_NAME);
        }
        return gsmNeighbourNode;
    }

    /**
     * get neighbour.
     * 
     * @param network network node
     * @param neighName the neigh name
     * @return neighbour node
     */
    private Node getNeighbour(Node network, String neighName) {
        Node result = NeoUtils.findNeighbour(network, neighName, neo);
        if (result != null) {
            return result;
        }
        Transaction tx = neo.beginTx();
        try {
            result = neo.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, neighName);
            network.createRelationshipTo(result, NetworkRelationshipTypes.NEIGHBOUR_DATA);
            tx.success();
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * <p>
     * Factory for UTRAN xml files
     * </p>
     * .
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class Factory implements IXmlTagFactory {

        /**
         * Creates the instance.
         * 
         * @param tagName the tag name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (tagName.equals(BulkCmConfigDataFile.TAG_NAME)) {
                return new BulkCmConfigDataFile(attributes);
            }
            return null;
        }

    }

    public class FactoryId implements IXmlTagFactory {

        /**
         * Creates the instance.
         * 
         * @param tagName the tag name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (tagName.equals("UtranCell")) {
                return new UtranTagCiLac(attributes);
            }
            return null;
        }

        public class UtranTagCiLac extends AbstractTag {

            private String id;
            private PropertyCollector collector;

            /**
             * @param tagName
             * @param parent
             */
            protected UtranTagCiLac(Attributes attributes) {
                super("UtranCell", null);
                id = attributes.getValue("id");
            }

            @Override
            public IXmlTag startElement(String localName, Attributes attributes) {
                updateMonitor();
                IXmlTag result = this;
                if (TagAttributes.TAG_NAME.equals(localName)) {
                    collector = new PropertyCollector(localName, this, true);
                    result = collector;
                } else {
                    handleCollector();
                    return null;
                }
                return result;

            }

            @Override
            public IXmlTag endElement(String localName, StringBuilder chars) {
                handleCollector();
                return super.endElement(localName, chars);
            }

            private void handleCollector() {
                if (collector == null) {
                    return;
                }
                Integer ci = null;
                Integer lac = null;
                Map<String, String> map = collector.getPropertyMap();
                String ciObj = map.get("cId");
                if (ciObj != null) {
                    ci = Integer.valueOf(ciObj);
                    map.remove("cId");
                }
                String lacObj = map.get("lac");
                if (lacObj != null) {
                    lac = Integer.valueOf(lacObj);
                    map.remove("lac");
                }
                idMap.put(id, new Integer[] {ci, lac});

                collector = null;
            }

        }
    }

    /**
     * <p>
     * Handler "bulkCmConfigDataFile" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class BulkCmConfigDataFile extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "bulkCmConfigDataFile";

        /**
         * Instantiates a new bulk cm config data file.
         * 
         * @param attributes the attributes
         */
        protected BulkCmConfigDataFile(Attributes attributes) {
            super(TAG_NAME, null);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            IXmlTag result = this;
            if (FileHeader.TAG_NAME.equals(localName)) {
                result = new FileHeader(attributes, this);
            } else if (ConfigData.TAG_NAME.equals(localName)) {
                result = new ConfigData(attributes, this);
            } else if (FileFooter.TAG_NAME.equals(localName)) {
                result = new FileFooter(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

    }

    /**
     * <p>
     * Handler "fileHeader" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class FileHeader extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "fileHeader";

        /**
         * Instantiates a new file header.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected FileHeader(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * The Class AbstractNeoTag.
     */
    public abstract class AbstractNeoTag extends org.amanzi.neo.loader.sax_parsers.AbstractNeoTag {

        /**
         * Constructor.
         * 
         * @param tagName - tag name
         * @param parent - parent AbstractNeoTag
         * @param attributes - attributes of tag
         */
        protected AbstractNeoTag(String tagName, AbstractNeoTag parent, Attributes attributes) {
            super(tagName, parent, attributes);
        }

        /**
         * Constructor.
         * 
         * @param tagName - tag name
         * @param parent - parent node
         * @param lastChild -last child of parent node, if null, then child will be found
         * @param attributes - attributes of tag
         */
        protected AbstractNeoTag(String tagName, Node parent, Node lastChild, Attributes attributes) {
            super(tagName, parent, lastChild, attributes);
        }

        /**
         * Adds the child.
         * 
         * @param childNode the child node
         */
        @Override
        protected void addChild(org.amanzi.neo.loader.sax_parsers.AbstractNeoTag childNode) {
            getNode().createRelationshipTo(childNode.getNode(), GeoNeoRelationshipTypes.CHILD);
            lastChild = childNode.getNode();
        }

        /**
         * Adds the child.
         * 
         * @param parent the parent
         * @param lastChild the last child
         */
        @Override
        protected void addChild(Node parent, Node lastChild) {
            parent.createRelationshipTo(getNode(), GeoNeoRelationshipTypes.CHILD);
        }

        /**
         * Creates the node.
         * 
         * @param attributes the attributes
         * @return the node
         */
        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, getName());
            storeAttributes(node, attributes);
            String name = attributes.getValue("id");
            if (!StringUtils.isEmpty(name)) {
                NeoUtils.setNodeName(node, name, null);
            }
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "fileFooter" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class FileFooter extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "fileFooter";

        /**
         * Instantiates a new file footer.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected FileFooter(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>
     * Handler "configData" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ConfigData extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "configData";

        /**
         * Instantiates a new config data.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected ConfigData(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (SubNetwork.TAG_NAME.equals(localName)) {
                result = new SubNetwork(attributes, this);
            } else if (MeContext.TAG_NAME.equals(localName)) {
                result = new MeContext(attributes, this);
            } else if (ManagedElement.TAG_NAME.equals(localName)) {
                result = new ManagedElement(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

    }

    /**
     * <p>
     * Handler "SubNetwork" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class SubNetwork extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "SubNetwork";

        /**
         * Instantiates a new sub network.
         * 
         * @param attributes the attributes
         * @param parentNode the parent node
         */
        protected SubNetwork(Attributes attributes, IXmlTag parentNode) {
            super(TAG_NAME, parentNode);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new TagAttributes(attributes, this, false);
            } else if (SubNetwork.TAG_NAME.equals(localName)) {
                result = new SubNetwork(attributes, this);
            } else if (MeContext.TAG_NAME.equals(localName)) {
                result = new MeContext(attributes, this);
            } else if (ManagementNode.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new ManagementNode(attributes, this);
            } else if (IRPAgent.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new IRPAgent(attributes, this);
            } else if (ExternalUtranCell.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new ExternalUtranCell(attributes, this);
            } else if (ExternalGsmCell.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new ExternalGsmCell(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

    }

    /**
     * <p>
     * Handler "attributes" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class TagAttributes extends AbstractTag {

        /** The stack. */
        Stack<String> stack;

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "attributes";

        /** The need index. */
        private boolean needIndex;

        /**
         * Instantiates a new tag attributes.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected TagAttributes(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent);
            stack = new Stack<String>();
            needIndex = true;
        }

        /**
         * Instantiates a new tag attributes.
         * 
         * @param attributes the attributes
         * @param parent the parent
         * @param needIndex the need index
         */
        public TagAttributes(Attributes attributes, AbstractNeoTag parent, boolean needIndex) {
            this(attributes, parent);
            this.needIndex = needIndex;
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            stack.push(localName);
            return this;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (stack.isEmpty()) {
                if (localName.equals(TAG_NAME)) {
                    return parent;
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                if (!stack.pop().equals(localName)) {
                    throw new UnsupportedOperationException();
                } else {
                    if (chars.length() > 0) {
                        if (needIndex) {
                            setIndexPropertyNotParcedValue(headers, ((AbstractNeoTag)parent).getNode(), localName, chars.toString());
                        } else {
                            ((AbstractNeoTag)parent).getNode().setProperty(localName, chars.toString());
                        }
                    }
                }
                return this;
            }
        }

    }

    /**
     * <p>
     * Handler "MeContext" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class MeContext extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "MeContext";

        /**
         * Instantiates a new me context.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected MeContext(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new TagAttributes(attributes, this);
            } else if (ManagedElement.TAG_NAME.equals(localName)) {
                result = new ManagedElement(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }


  
    /**
     * <p>
     * Handler "ManagedElement" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ManagedElement extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "ManagedElement";

        /**
         * Instantiates a new managed element.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected ManagedElement(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// TagAttributes(attributes, this);
            } else if (RncFunction.TAG_NAME.equals(localName)) {
                result = new RncFunction(attributes, this);
            } else if (NodeBFunction.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new NodeBFunction(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "RncFunction" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class RncFunction extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "RncFunction";

        /** The collector. */
        private PropertyCollector collector;

        /** The id. */
        private final String id;

        /**
         * Instantiates a new rnc function.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected RncFunction(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
            collector = null;
            id = attributes.getValue("id");
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                    collector = new PropertyCollector(localName, this, true);
                    result = collector;// new TagAttributes(attributes, this);
            } else {
                handleCollector();
                if (UtranCell.TAG_NAME.equals(localName)) {
                    result = new UtranCell(attributes, this);
                } else if (IubLink.TAG_NAME.equals(localName)) {
                    result = new SkipTag(this);// new IubLink(attributes, this);
                } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                    result = new SkipTag(this);// new VsDataContainer(attributes, this);
                } else {
                    throw new IllegalArgumentException("Wrong tag: " + localName);
                }
            }
            return result;
        }

        /**
         * Handle collector.
         */
        private void handleCollector() {
            if (collector == null) {
                return;
            }
            rnc = findOrCreateRNC(id, collector);
            rncExtGsmSite=null;
            collector = null;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "UtranCell" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranCell extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "UtranCell";

        /** The attr. */
        private final Attributes attr;
        /** The collector. */
        PropertyCollector collector;

        /** The cell name. */
        String cellName;

        /** The sector. */
        Node sector;

        /** The data collector. */
        private PropertyCollector dataCollector;

        /** The site. */
        private Node site;

        /**
         * Instantiates a new utran cell.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected UtranCell(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
            this.attr = attributes;
            cellName = attributes.getValue("id");
            collector = null;
            dataCollector = null;
            site = null;
                Integer[] cilac = idMap.get(cellName);
                sector=NeoUtils.findSector(basename,cilac[0] ,cilac[1],cellName, true,index,neo);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                    collector = new PropertyCollector(localName, this, true);
                    result = collector;
            } else {
                handleCollector();
                // handleDataCollector();
                if (VsDataContainer.TAG_NAME.equals(localName)) {
                    result = new SkipTag(this);
                    // dataCollector = new PropertyCollector(localName, this, true);
                    // result = dataCollector;
                } else {
                    if (UtranRelation.TAG_NAME.equals(localName)) {
                            result = new UtranRelation(attributes, this);
                    } else if (GsmRelation.TAG_NAME.equals(localName)) {
                            result = new GsmRelation(attributes, this);
                    } else {
                        throw new IllegalArgumentException("Wrong tag: " + localName);
                    }
                }
            }
            return result;
        }

        /**
         * Handle data collector.
         */
        private void handleDataCollector() {
            if (dataCollector == null) {
                return;
            }
            PropertyCollector attrCol = dataCollector.getSubCollectorByName("attributes");
            Map<String, String> map = attrCol.getPropertyMap();
            String type = map.get("vsDataType");
            if (type.equals("vsDataUtranCell")) {
                if (site.hasProperty(INeoConstants.PROPERTY_LON_NAME)) {
                    return;
                }
                PropertyCollector col = attrCol.getSubCollectorByName("vsDataUtranCell");
                if (col != null) {
                    col = col.getSubCollectorByName("antennaPosition");
                    if (col != null) {
                        map = col.getPropertyMap();
                        String lon = map.get("longitude");
                        Float longitude = null;
                        if (lon != null) {
                            longitude = Float.parseFloat(lon) / 3600;
                            site.setProperty(INeoConstants.PROPERTY_LON_NAME, longitude.doubleValue());
                        }

                        String lat = map.get("latitude");
                        Float latitude = null;
                        if (lat != null) {
                            latitude = Float.parseFloat(lat) / 3600;
                            site.setProperty(INeoConstants.PROPERTY_LAT_NAME, latitude.doubleValue());
                        }
                        if (lat != null && lon != null) {
                            GisProperties gisProperties = getGisProperties(basename);
                            gisProperties.updateBBox(latitude, longitude);
                            if (gisProperties.getCrs() == null && latitude != null && longitude != null) {
                                gisProperties.checkCRS(latitude, longitude, null);
                                if (!isTest() && gisProperties.getCrs() != null) {
                                    CoordinateReferenceSystem crs = askCRSChoise(gisProperties);
                                    if (crs != null) {
                                        gisProperties.setCrs(crs);
                                        gisProperties.saveCRS();
                                    }
                                }
                            }
                            index(site);
                        }
                    }
                }
            } else {
                // TODO what we should storing like properties of sector node?
                // storeProperty(sector,map);
            }
            dataCollector = null;
        }

        /**
         * Handle collector.
         */
        private void handleCollector() {
            if (collector == null) {
                return;
            }
            Integer ci=null;
            Integer lac = null;
            Map<String, String> map = collector.getPropertyMap();
            String ciObj = map.get("cId");
            if (ciObj != null) {
                ci = Integer.valueOf(ciObj);
                map.remove("cId");
            }
            String lacObj = map.get("lac");
            if (lacObj != null) {
                lac = Integer.valueOf(lacObj);
                map.remove("lac");
            }
            idMap.put(cellName, new Integer[]{ci,lac});
            sector=findOrCreateSector(cellName,ci,lac);
            Transaction tx = neo.beginTx();
            try {
                if (!sector.hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING)) {
                    site = findOrCreateSite(collector);
                    site.createRelationshipTo(sector, GeoNeoRelationshipTypes.CHILD);
                    tx.success();
                } else {
                    site = sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
                }
                storeProperty(sector, map);
            } finally {
                tx.finish();
            }
            collector = null;

        }

        /**
         * Store property.
         * 
         * @param node the node
         * @param map the map
         */
        private void storeProperty(Node node, Map<String, String> map) {
            Transaction tx = neo.beginTx();
            try {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (!node.hasProperty(key)) {
                        setIndexPropertyNotParcedValue(headers, node, key, entry.getValue());
                    } else {
                        String old = node.getProperty(key).toString();
                        info(String.format(FORMAT_STR, cellName, key, old, entry.getValue()));
                    }
                }
            } finally {
                tx.finish();
            }
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            handleCollector();
            // handleDataCollector();
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "UtranRelation" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranRelation extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "UtranRelation";

        /** The adj sector. */
        private final Node adjSector;

        /** The collector. */
        private PropertyCollector collector;

        /** The relation. */
        private final Relationship relation;

        /**
         * Instantiates a new utran relation.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected UtranRelation(Attributes attributes, UtranCell parent) {
            super(TAG_NAME, parent);
            String adjUtranCellName = attributes.getValue("id");
            Integer[] cilac = idMap.get(adjUtranCellName);
            Integer ci = cilac == null ? null : cilac[0];
            Integer lac = cilac == null ? null : cilac[1];
            adjSector = findOrCreateSector(adjUtranCellName, ci, lac);
            relation = addUtranNeighour(parent.sector, adjSector);
            collector = null;
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            handleCollector();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// new TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                collector = new PropertyCollector(localName, this, true);
                result = collector;// new SkipTag(this);// VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            handleCollector();
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Handle collector.
         */
        private void handleCollector() {
            if (collector == null) {
                return;
            }
            PropertyCollector col = collector.getRecurciveSubCollectorByName("vsDataUtranRelation");
            if (col != null) {
                Map<String, String> map = col.getPropertyMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    storeUtranRelationProperty(relation, entry.getKey(), entry.getValue());
                }
            }
            collector = null;
        }
    }

    /**
     * <p>
     * Handler "GsmRelation" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class GsmRelation extends AbstractTag {
        /** The adj sector. */
        private final Node adjSector;

        /** The collector. */
        private  PropertyCollector collector;

        /** The relation. */
        private final Relationship relation;

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "GsmRelation";

        /**
         * Instantiates a new gsm relation.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected GsmRelation(Attributes attributes, UtranCell parent) {
            super(TAG_NAME, parent);
            String adjUtranCellName = attributes.getValue("id");
            adjSector = findOrCreateExternalGSMSector(adjUtranCellName,null,null);
            relation = addGsmNeighour(parent.sector, adjSector);
            collector = null;
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            handleCollector();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new SkipTag(this);// TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                collector = new PropertyCollector(localName, this, true);
                result = collector;
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            handleCollector();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        /**
         * Handle collector.
         */
        private void handleCollector() {
            if (collector == null) {
                return;
            }
            PropertyCollector col = collector.getRecurciveSubCollectorByName("vsDataGsmRelation");
            if (col != null) {
                Map<String, String> map = col.getPropertyMap();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    storeGsmRelationProperty(relation, entry.getKey(), entry.getValue());
                }
            }
            collector = null;
        }
    }

    /**
     * <p>
     * Handler "IubLink" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class IubLink extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "IubLink";

        /**
         * Instantiates a new iub link.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected IubLink(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "NodeBFunction" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class NodeBFunction extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "NodeBFunction";

        /**
         * Instantiates a new node b function.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected NodeBFunction(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "ManagementNode" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ManagementNode extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "ManagementNode";

        /**
         * Instantiates a new management node.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected ManagementNode(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (IRPAgent.TAG_NAME.equals(localName)) {
                result = new IRPAgent(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "IRPAgent" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class IRPAgent extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "IRPAgent";

        /**
         * Instantiates a new iRP agent.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected IRPAgent(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (NotificationIRP.TAG_NAME.equals(localName)) {
                result = new NotificationIRP(attributes, this);
            } else if (AlarmIRP.TAG_NAME.equals(localName)) {
                result = new AlarmIRP(attributes, this);
            } else if (BulkCmIRP.TAG_NAME.equals(localName)) {
                result = new BulkCmIRP(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "BulkCmIRP" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class BulkCmIRP extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "BulkCmIRP";

        /**
         * Instantiates a new bulk cm irp.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected BulkCmIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "AlarmIRP" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class AlarmIRP extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "AlarmIRP";

        /**
         * Instantiates a new alarm irp.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected AlarmIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "NotificationIRP" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class NotificationIRP extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "NotificationIRP";

        /**
         * Instantiates a new notification irp.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected NotificationIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "ExternalUtranCell" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ExternalUtranCell extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "ExternalUtranCell";

        /**
         * Instantiates a new external utran cell.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected ExternalUtranCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "ExternalGsmCell" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ExternalGsmCell extends AbstractNeoTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "ExternalGsmCell";

        /**
         * Instantiates a new external gsm cell.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected ExternalGsmCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "VsDataContainer" tag
     * </p>
     * .
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class VsDataContainer extends AbstractTag {

        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "VsDataContainer";

        /** The collector. */
        PropertyCollector collector;

        /** The attributes. */
        private Attributes attributes;

        /**
         * Instantiates a new vs data container.
         * 
         * @param attributes the attributes
         * @param parent the parent
         */
        protected VsDataContainer(Attributes attributes, IXmlTag parent) {
            super(TAG_NAME, parent);
            collector = null;
        }

        /**
         * Start element.
         * 
         * @param localName the local name
         * @param attributes the attributes
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            this.attributes = attributes;
            updateMonitor();
            IXmlTag result = new SkipTag(this);

            // if (TagAttributes.TAG_NAME.equals(localName)) {
            // collector = new PropertyCollector(localName, this, true);
            // result = collector;
            // } else if (VsDataContainer.TAG_NAME.equals(localName)) {
            // handleCollector();
            // result = new VsDataContainer(attributes, this);
            // } else {
            // throw new IllegalArgumentException("Wrong tag: " + localName);
            // }
            return result;
        }

        /**
         * End element.
         * 
         * @param localName the local name
         * @param chars the chars
         * @return the IXmlTag tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                handleCollector();
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Handle collector.
         */
        private void handleCollector() {
            if (collector == null) {
                return;
            }
            // if (true) {
            // return;
            // }
            // // TODO debug
            // String type = collector.getPropertyMap().get("vsDataType");
            // if (type.equals("vsDataSector")) {
            // Node site = findOrCreateSite(collector);
            // Node sector = findOrCreateSector(site, collector, attributes.getValue("id"));
            // }
            collector = null;
        }

    }

    /**
     * Find or create sector.
     * 
     * @param cellName the cell name
     * @param lac 
     * @param ci 
     * @return the node
     */
    public Node findOrCreateSector(String cellName, Integer ci, Integer lac) {
        Transaction tx = neo.beginTx();
        try {
            Node sector = NeoUtils.findSector(basename, ci, lac, cellName, true, index, neo);
            if (sector == null) {
                sector = neo.createNode();
                sector.setProperty(INeoConstants.SECTOR_TYPE, UTRAN_SEC_TYPE);
                NodeTypes.SECTOR.setNodeType(sector, neo);
                NeoUtils.setNodeName(sector, cellName, neo);
                String indexName;
                indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR);
                index.index(sector, indexName, cellName);
                if (ci!=null){
                    indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR);
                    setIndexProperty(headers, sector, INeoConstants.PROPERTY_SECTOR_CI, ci);
                    index.index(sector, indexName, ci);
                }
                if (lac!=null){
                    indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR);
                    setIndexProperty(headers, sector, INeoConstants.PROPERTY_SECTOR_LAC, lac);
                    index.index(sector, indexName, lac);
                }
                tx.success();
                updateTx();
            }
            return sector;
        } finally {
            tx.finish();
        }
    }


    /**
     * Store gsm relation property.
     *
     * @param relation the relation
     * @param key the key
     * @param value the value
     */
    //TODO merge with storeUtranRelation...
    public void storeGsmRelationProperty(Relationship relation, String key, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        Object valueToSave;
        if (gsmAllNeibProperties.contains(key)) {
            if (gsmIntNeibProperties.contains(key)) {
                valueToSave = Integer.valueOf(value);
            } else if (gsmDoubleNeibProperties.contains(key)) {
                valueToSave = Double.valueOf(value);
            } else {
                valueToSave = value;
            }
        } else {
            gsmAllNeibProperties.add(key);
            value = value.trim();
            if (Pattern.matches("^\\d+$", value)) {
                gsmIntNeibProperties.add(key);
                valueToSave = Integer.valueOf(value);
            } else if (Pattern.matches("^\\d+\\.\\d*$", value)) {
                gsmDoubleNeibProperties.add(key);
                valueToSave = Double.valueOf(value);
            } else {
                valueToSave = value;
            }

        }
        setProperty(relation, key, valueToSave);
    }


    /**
     * Find or create external gsm sector.
     *
     * @param cellName the cell name
     * @return the node
     */
    public Node findOrCreateExternalGSMSector(String cellName, Integer ci, Integer lac) {
        Transaction tx = neo.beginTx();
        try {
            Node sector = NeoUtils.findSector(basename, ci, lac, cellName, true, index, neo);
            if (sector == null) {
                sector = neo.createNode();
                int count=1;
                NodeTypes.SECTOR.setNodeType(sector, neo);
                NeoUtils.setNodeName(sector, cellName, neo);
                if (rncExtGsmSite==null){
                    rncExtGsmSite=neo.createNode();
                    count++;
                    NodeTypes.SITE.setNodeType(rncExtGsmSite, neo);
                   NeoUtils.setNodeName(rncExtGsmSite, "externalGSm",neo);
                   rnc.createRelationshipTo(rncExtGsmSite,GeoNeoRelationshipTypes.CHILD);
                }
                rncExtGsmSite.createRelationshipTo(sector,GeoNeoRelationshipTypes.CHILD);
                String indexName;
                indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR);
                index.index(sector, indexName, cellName);
                if (ci!=null){
                    indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR);
                    setIndexProperty(headers, sector, INeoConstants.PROPERTY_SECTOR_CI, ci);
                    index.index(sector, indexName, ci);
                }
                if (lac!=null){
                    indexName=NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR);
                    setIndexProperty(headers, sector, INeoConstants.PROPERTY_SECTOR_LAC, lac);
                    index.index(sector, indexName, lac);
                }
                tx.success();
                updateTx(count);
            }
            return sector;
        } finally {
            tx.finish();
        }
    }

    /**
     * Store utran relation property.
     * 
     * @param relation the relation
     * @param key the key
     * @param value the value
     */
    public void storeUtranRelationProperty(Relationship relation, String key, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        Object valueToSave;
        if (utranAllNeibProperties.contains(key)) {
            if (utranIntNeibProperties.contains(key)) {
                valueToSave = Integer.valueOf(value);
            } else if (utranDoubleNeibProperties.contains(key)) {
                valueToSave = Double.valueOf(value);
            } else {
                valueToSave = value;
            }
        } else {
            utranAllNeibProperties.add(key);
            value = value.trim();
            if (Pattern.matches("^\\d+$", value)) {
                utranIntNeibProperties.add(key);
                valueToSave = Integer.valueOf(value);
            } else if (Pattern.matches("^\\d+\\.\\d*$", value)) {
                utranDoubleNeibProperties.add(key);
                valueToSave = Double.valueOf(value);
            } else {
                valueToSave = value;
            }

        }
        setProperty(relation, key, valueToSave);

    }

    /**
     * Find or create rnc.
     * 
     * @param id the id
     * @param collector the collector
     * @return the node
     */
    public Node findOrCreateRNC(String id, PropertyCollector collector) {
        Transaction tx = neo.beginTx();
        try {
            String indexName = NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR);
            String rncName = collector.getPropertyMap().get("userLabel");
            Node rnc = index.getSingleNode(indexName, rncName);
            if (rnc == null) {
                rnc = neo.createNode();
                NodeTypes.RNC.setNodeType(rnc, neo);
                NeoUtils.setNodeName(rnc, rncName, neo);
                for (Map.Entry<String, String> entry : collector.getPropertyMap().entrySet()) {
                    String key = entry.getKey();
                    setProperty(rnc, key, entry.getValue());
                }
                // setProperty(rnc, "rnc_id",id);
                network.createRelationshipTo(rnc, GeoNeoRelationshipTypes.CHILD);
                tx.success();
                index.index(rnc, indexName, rncName);
                updateTx();
            }
            return rnc;
        } finally {
            tx.finish();
        }
    }

    /**
     * Find or create site.
     * 
     * @param collector the collector
     * @return the node
     */
    public Node findOrCreateSite(PropertyCollector collector) {
        PropertyCollector dataCol = collector;// collector.getSubCollectors().iterator().next();
        String ref = dataCol.getPropertyMap().get("utranCellIubLink");
        if (ref == null) {
            // TODO remove after debug
            System.err.println("site id not found");
            return null;
        }

        Pattern pat = Pattern.compile("(^.*IUB_)(\\d+)([^\\d]*$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(ref);
        if (!matcher.find(0)) {
            System.err.println("err");
        }
        String id = matcher.group(2);
        String indName = NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE);
        Node node = index.getSingleNode(indName, id);
        if (node == null) {
            node = neo.createNode();
            node.setProperty(INeoConstants.PROPERTY_NAME_NAME, id);
            rnc.createRelationshipTo(node, GeoNeoRelationshipTypes.CHILD);
            NodeTypes.SITE.setNodeType(node, neo);
            index.index(node, indName, id);
            updateTx();
        }
        return node;
    }
}
