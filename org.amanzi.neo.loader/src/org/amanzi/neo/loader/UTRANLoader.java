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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
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
import org.neo4j.index.lucene.LuceneIndexService;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

//TODO create abstract XML loader?

/**
 * <p>
 * Loader for UTRAN files
 * </p>.
 *
 * @author Cinkel_A
 * @since 1.0.0
 */
public class UTRANLoader extends AbstractLoader {
    
    /** The Constant KEY_EVENT. */
    private static final int KEY_EVENT = 1;
    
    /** The Constant REG_EXP_XML. */
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";
    
    /** The Constant SUBNETWORK_TYPE. */
    private static final NodeTypes SUBNETWORK_TYPE = NodeTypes.BSC;;
    
    /** The Constant siteProperty. */
    private static final List<String> siteProperty;
    static {
        siteProperty = new LinkedList<String>();
        siteProperty.add("latitude");
        siteProperty.add("sectorAntennasRef");
        siteProperty.add("sectorAntennaRef");
        siteProperty.add("longitude");
        siteProperty.add("geoDatum");
    }
    
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
    
    /** The index. */
    private final LuceneIndexService index;
    
    /** The sub network. */
    public Node subNetwork;

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
        headers = getHeaderMap(KEY_EVENT).headers;
        handler = new ReadContentHandler(new Factory());
        if(index == null){
            this.index = NeoServiceProvider.getProvider().getIndexService();
        }  else{
            this.index =index;
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
        String fileNodeName = file.getName();
        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(handler);
        in = new CountingFileInputStream(file);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
    }

    /**
     * update monitor description.
     */
    public void updateMonitor() {
        int pr = in.percentage();
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

    /**
     * <p>
     * Factory for UTRAN xml files
     * </p>.
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (tagName.equals(BulkCmConfigDataFile.TAG_NAME)) {
                return new BulkCmConfigDataFile(attributes);
            }
            return null;
        }

    }

    /**
     * <p>
     * Handler "bulkCmConfigDataFile" tag
     * </p>.
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>
     * Handler "configData" tag
     * </p>.
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (SubNetwork.TAG_NAME.equals(localName)) {
                result = new SubNetwork(attributes, network, this);
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
     * </p>.
     *
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class SubNetwork extends AbstractNeoTag {
        
        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "SubNetwork";
        
        /** The link necessary. */
        private boolean linkNecessary;
        
        /** The parent to return. */
        private final IXmlTag parentToReturn;

        /**
         * Adds the child.
         *
         * @param parent the parent
         * @param lastChild the last child
         */
        @Override
        protected void addChild(Node parent, Node lastChild) {
            if (linkNecessary) {
                super.addChild(parent, lastChild);
            }
        }

        /**
         * Adds the child.
         *
         * @param childNode the child node
         */
        @Override
        protected void addChild(org.amanzi.neo.loader.sax_parsers.AbstractNeoTag childNode) {
            if (childNode instanceof SubNetwork) {
                if (((SubNetwork)childNode).linkNecessary) {
                    super.addChild(childNode);
                }
            } else {
                super.addChild(childNode);
            }
        }

        /**
         * Instantiates a new sub network.
         *
         * @param attributes the attributes
         * @param mainNode the main node
         * @param parentToReturn the parent to return
         */
        protected SubNetwork(Attributes attributes, Node mainNode, IXmlTag parentToReturn) {
            super(TAG_NAME, mainNode, null, attributes);
            this.parentToReturn = parentToReturn;
            linkNecessary = false;
        }

        /**
         * Instantiates a new sub network.
         *
         * @param attributes the attributes
         * @param parent the parent
         */
        protected SubNetwork(Attributes attributes, SubNetwork parent) {
            super(TAG_NAME, parent, attributes);
            this.parentToReturn = parent;
            linkNecessary = false;
        }

        /**
         * Creates the node.
         *
         * @param attributes the attributes
         * @return the node
         */
        @Override
        protected Node createNode(Attributes attributes) {
            String name = attributes.getValue("id");
            Node node = findSubNetwork(name);
            linkNecessary = false;
            if (node == null) {
                node = neo.createNode();
                linkNecessary = true;
                SUBNETWORK_TYPE.setNodeType(node, neo);
                storeAttributes(node, attributes);
                if (!StringUtils.isEmpty(name)) {
                    NeoUtils.setNodeName(node, name, null);
                }
                updateTx();
                index.index(node, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, SUBNETWORK_TYPE), name);
            }
            return node;
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            subNetwork = getNode();
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this, false);
            } else if (SubNetwork.TAG_NAME.equals(localName)) {
                result = new SubNetwork(attributes, this);
            } else if (MeContext.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new MeContext(attributes, this);
            } else if (ManagementNode.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new ManagementNode(attributes, this);
            } else if (IRPAgent.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new IRPAgent(attributes, this);
            } else if (ExternalUtranCell.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new ExternalUtranCell(attributes, this);
            } else if (ExternalGsmCell.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new ExternalGsmCell(attributes, this);
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parentToReturn;
            } else {
                throw new UnsupportedOperationException();
            }
        }

    }

    /**
     * <p>
     * Handler "attributes" tag
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * Default handler (temporary realization) skip all tag except VsDataContainer;
     * </p>.
     *
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class DefaultHandlerTag implements IXmlTag {
        
        /** The parent. */
        private final IXmlTag parent;

        /**
         * Instantiates a new default handler tag.
         *
         * @param parent the parent
         */
        public DefaultHandlerTag(IXmlTag parent) {
            this.parent = parent;

        }

        /**
         * End element.
         *
         * @param localName the local name
         * @param chars the chars
         * @return the i xml tag
         */
        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            return parent;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        @Override
        public String getName() {
            return this.getClass().getName();
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            IXmlTag result = this;
            if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                result = new DefaultHandlerTag(this);
            }
            return result;
        }
    }

    /**
     * <p>
     * Handler "ManagedElement" tag
     * </p>.
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// TagAttributes(attributes, this);
            } else if (RncFunction.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new RncFunction(attributes, this);
            } else if (NodeBFunction.TAG_NAME.equals(localName)) {
                result = new DefaultHandlerTag(this);// new NodeBFunction(attributes, this);
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
         * @return the i xml tag
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
     * </p>.
     *
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class RncFunction extends AbstractNeoTag {
        
        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "RncFunction";

        /**
         * Instantiates a new rnc function.
         *
         * @param attributes the attributes
         * @param parent the parent
         */
        protected RncFunction(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (UtranCell.TAG_NAME.equals(localName)) {
                result = new UtranCell(attributes, this);
            } else if (IubLink.TAG_NAME.equals(localName)) {
                result = new IubLink(attributes, this);
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
         * @return the i xml tag
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
     * </p>.
     *
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranCell extends AbstractNeoTag {
        
        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "UtranCell";

        /**
         * Instantiates a new utran cell.
         *
         * @param attributes the attributes
         * @param parent the parent
         */
        protected UtranCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (UtranRelation.TAG_NAME.equals(localName)) {
                result = new UtranRelation(attributes, this);
            } else if (GsmRelation.TAG_NAME.equals(localName)) {
                result = new GsmRelation(attributes, this);
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
         * @return the i xml tag
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
     * Handler "UtranRelation" tag
     * </p>.
     *
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranRelation extends AbstractNeoTag {
        
        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "UtranRelation";

        /**
         * Instantiates a new utran relation.
         *
         * @param attributes the attributes
         * @param parent the parent
         */
        protected UtranRelation(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
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
         * @return the i xml tag
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
     * Handler "GsmRelation" tag
     * </p>.
     *
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class GsmRelation extends AbstractNeoTag {
        
        /** The Constant TAG_NAME. */
        public static final String TAG_NAME = "GsmRelation";

        /**
         * Instantiates a new gsm relation.
         *
         * @param attributes the attributes
         * @param parent the parent
         */
        protected GsmRelation(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        /**
         * Start element.
         *
         * @param localName the local name
         * @param attributes the attributes
         * @return the i xml tag
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
         * @return the i xml tag
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
     * Handler "IubLink" tag
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
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
         * @return the i xml tag
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
     * </p>.
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
         * @return the i xml tag
         */
        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            this.attributes = attributes;
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                collector = new PropertyCollector(localName, this, true);
                result = collector;
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                handleCollector();
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
         * @return the i xml tag
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
            String type = collector.getPropertyMap().get("vsDataType");
            if (type.equals("vsDataSector")) {
                Node site = findOrCreateSite(collector);
                Node sector = findOrCreateSector(site, collector, attributes.getValue("id"));
            }
            collector = null;
        }

    }

    /**
     * Find sub network.
     *
     * @param name the name
     * @return the node
     */
    public Node findSubNetwork(String name) {
        return index.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, SUBNETWORK_TYPE), name);
    }

    /**
     * Find or create sector.
     *
     * @param site the site
     * @param collector the collector
     * @param postfix the postfix
     * @return the node
     */
    public Node findOrCreateSector(Node site, PropertyCollector collector, String postfix) {
        String name = NeoUtils.getNodeName(site) + postfix;
        String indexName = NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR);
        Node sector = index.getSingleNode(indexName, name);
        if (sector == null) {
            sector = neo.createNode();
            NodeTypes.SECTOR.setNodeType(sector, neo);
            sector.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            PropertyCollector dataCol = collector.getSubCollectors().iterator().next();
            Map<String, String> map = dataCol.getPropertyMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (siteProperty.contains(key)) {
                    continue;
                }
                if (key.equals("beamDirection")) {
                    key = "azimuth";
                    Integer valueInt = Integer.parseInt(entry.getValue());
                    setIndexProperty(headers, sector, key, valueInt);
                } else {
                    setIndexPropertyNotParcedValue(headers, sector, key, entry.getValue());
                }
            }
            index.index(sector, indexName, name);
            site.createRelationshipTo(sector, GeoNeoRelationshipTypes.CHILD);
            updateTx();
        } else {
            // TODO remove after debug
            if (!site.equals(sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector))) {
                throw new RuntimeException("Wrong site for sector\t" + name);
            }
        }
        return sector;
    }

    /**
     * Find or create site.
     *
     * @param collector the collector
     * @return the node
     */
    public Node findOrCreateSite(PropertyCollector collector) {
        PropertyCollector dataCol = collector.getSubCollectors().iterator().next();
        String ref = dataCol.getPropertyMap().get("sectorAntennasRef");
        if (ref == null) {
            ref = dataCol.getPropertyMap().get("sectorAntennaRef");
            if (ref == null) {
                // TODO remove after debug
                System.out.println(ref);
            }
        }

        Pattern pat = Pattern.compile("(^.*MeContext=)([^,]*)(,.*$)");
        Matcher matcher = pat.matcher(ref);
        matcher.find();
        String id = matcher.group(2);
        String indName = NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE);
        Node node = index.getSingleNode(indName, id);
        if (node == null) {
            node = neo.createNode();
            pat = Pattern.compile("(^.*SubNetwork=)([^,]*)(,MeContext.*$)");
            matcher = pat.matcher(ref);
            Float latitude = null;
            Float longitude = null;

            String lon = dataCol.getPropertyMap().get("longitude");
            if (lon != null) {
                longitude = Float.parseFloat(lon) / 3600;
                node.setProperty(INeoConstants.PROPERTY_LON_NAME, longitude.doubleValue());
            }

            String lat = dataCol.getPropertyMap().get("latitude");
            if (lat != null) {
                latitude = Float.parseFloat(lat) / 3600;
                node.setProperty(INeoConstants.PROPERTY_LAT_NAME, latitude.doubleValue());
            }
            GisProperties gisProperties = getGisProperties(basename);
            gisProperties.updateBBox(latitude, longitude);
            if (gisProperties.getCrs() == null && latitude != null && longitude != null) {
                String crsHint = dataCol.getPropertyMap().get("geoDatum");
                gisProperties.checkCRS(latitude, longitude, crsHint);
                if (!isTest() && gisProperties.getCrs() != null) {
                    CoordinateReferenceSystem crs = askCRSChoise(gisProperties);
                    if (crs != null) {
                        gisProperties.setCrs(crs);
                        gisProperties.saveCRS();
                    }
                }
            }
            matcher.find();
            String subId = matcher.group(2);
            Node subNetwork = index.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), subId);

            node.setProperty(INeoConstants.PROPERTY_NAME_NAME, id);
            subNetwork.createRelationshipTo(node, GeoNeoRelationshipTypes.CHILD);
            NodeTypes.SITE.setNodeType(node, neo);
            index.index(node, indName, id);
            index(node);
            updateTx();
        }
        return node;
    }
}
