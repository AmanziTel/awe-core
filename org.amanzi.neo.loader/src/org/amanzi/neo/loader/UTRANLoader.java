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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.sax_parsers.AbstractNeoTag;
import org.amanzi.neo.loader.sax_parsers.AbstractTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTag;
import org.amanzi.neo.loader.sax_parsers.IXmlTagFactory;
import org.amanzi.neo.loader.sax_parsers.ReadContentHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

//TODO create abstract XML loader?

/**
 * <p>
 * Loader for UTRAN files
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class UTRANLoader extends AbstractLoader {
    private static final int KEY_EVENT = 1;
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";
    private final LinkedHashMap<String, Header> headers;
    private final ReadContentHandler handler;
    private Node ossNode;
    private Node fileNode;
    private int counter;
    private long counterAll;
    private CountingFileInputStream in;
    private int currentJobPr;
    private IProgressMonitor monitor;

    /**
     * Constructor
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

    }

    /**
     *storing XML in database
     * 
     * @param file - XML file
     * @throws SAXException
     * @throws IOException
     * @throws
     */
    private void storeFile(File file) throws SAXException, IOException {
        currentJobPr = 0;
        String fileNodeName = file.getName();
        updateTx();
        Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateChildNode(neo, ossNode, fileNodeName);
        fileNode = fileNodePair.getRight();
        if (fileNodePair.getLeft()) {
            NodeTypes.FILE.setNodeType(fileNode, neo);
        }

        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(handler);
        in = new CountingFileInputStream(file);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
    }

    /**
     * update monitor description
     */
    public void updateMonitor() {
        int pr = in.percentage();
        if (pr > currentJobPr) {
            info(String.format("parsed %s bytes\tcreated nodes %s", in.tell(), counterAll));
            monitor.worked(pr - currentJobPr);
            currentJobPr = pr;
        }
    }

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
            ossNode = LoaderUtils.findOrCreateOSSNode(OssType.UTRAN, basename, neo);
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

    @Override
    protected Node getStoringNode(Integer key) {
        return ossNode;
    }

    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }

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
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class Factory implements IXmlTagFactory {
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
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class BulkCmConfigDataFile extends AbstractNeoTag {
        public static final String TAG_NAME = "bulkCmConfigDataFile";

        protected BulkCmConfigDataFile(Attributes attributes) {
            super(TAG_NAME, fileNode, null, attributes);
        }

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

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }

    }

    /**
     * <p>
     * Handler "fileHeader" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class FileHeader extends AbstractNeoTag {
        public static final String TAG_NAME = "fileHeader";

        protected FileHeader(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }

    }

    /**
     * <p>
     * Handler "fileFooter" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class FileFooter extends AbstractNeoTag {
        public static final String TAG_NAME = "fileFooter";

        protected FileFooter(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }

    }

    /**
     * <p>
     * Handler "configData" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ConfigData extends AbstractNeoTag {
        public static final String TAG_NAME = "configData";

        protected ConfigData(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }

    }

    /**
     * <p>
     * Handler "SubNetwork" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class SubNetwork extends AbstractNeoTag {
        public static final String TAG_NAME = "SubNetwork";

        /**
         * @param tagName
         */
        protected SubNetwork(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (SubNetwork.TAG_NAME.equals(localName)) {
                result = new SubNetwork(attributes, this);
            } else if (MeContext.TAG_NAME.equals(localName)) {
                result = new MeContext(attributes, this);
            } else if (ManagementNode.TAG_NAME.equals(localName)) {
                result = new ManagementNode(attributes, this);
            } else if (IRPAgent.TAG_NAME.equals(localName)) {
                result = new IRPAgent(attributes, this);
            } else if (ExternalUtranCell.TAG_NAME.equals(localName)) {
                result = new ExternalUtranCell(attributes, this);
            } else if (ExternalGsmCell.TAG_NAME.equals(localName)) {
                result = new ExternalGsmCell(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "attributes" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class TagAttributes extends AbstractTag {
        Stack<String> stack;
        public static final String TAG_NAME = "attributes";

        protected TagAttributes(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent);
            stack = new Stack<String>();
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            stack.push(localName);
            return this;
        }

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
                        ((AbstractNeoTag)parent).getNode().setProperty(localName, chars.toString());
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
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class MeContext extends AbstractNeoTag {
        public static final String TAG_NAME = "MeContext";

        protected MeContext(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (ManagedElement.TAG_NAME.equals(localName)) {
                result = new ManagedElement(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "ManagedElement" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ManagedElement extends AbstractNeoTag {
        public static final String TAG_NAME = "ManagedElement";

        protected ManagedElement(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            updateMonitor();
            IXmlTag result = this;
            if (TagAttributes.TAG_NAME.equals(localName)) {
                result = new TagAttributes(attributes, this);
            } else if (RncFunction.TAG_NAME.equals(localName)) {
                result = new RncFunction(attributes, this);
            } else if (NodeBFunction.TAG_NAME.equals(localName)) {
                result = new NodeBFunction(attributes, this);
            } else if (VsDataContainer.TAG_NAME.equals(localName)) {
                result = new VsDataContainer(attributes, this);
            } else {
                throw new IllegalArgumentException("Wrong tag: " + localName);
            }
            return result;
        }

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "RncFunction" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class RncFunction extends AbstractNeoTag {
        public static final String TAG_NAME = "RncFunction";

        protected RncFunction(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "UtranCell" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranCell extends AbstractNeoTag {
        public static final String TAG_NAME = "UtranCell";

        /**
         * @param tagName
         */
        protected UtranCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "UtranRelation" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class UtranRelation extends AbstractNeoTag {
        public static final String TAG_NAME = "UtranRelation";

        protected UtranRelation(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "GsmRelation" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class GsmRelation extends AbstractNeoTag {
        public static final String TAG_NAME = "GsmRelation";

        protected GsmRelation(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "IubLink" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class IubLink extends AbstractNeoTag {
        public static final String TAG_NAME = "IubLink";

        /**
         * @param tagName
         */
        protected IubLink(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "NodeBFunction" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class NodeBFunction extends AbstractNeoTag {
        public static final String TAG_NAME = "NodeBFunction";

        protected NodeBFunction(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "ManagementNode" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ManagementNode extends AbstractNeoTag {
        public static final String TAG_NAME = "ManagementNode";

        protected ManagementNode(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "IRPAgent" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class IRPAgent extends AbstractNeoTag {
        public static final String TAG_NAME = "IRPAgent";

        /**
         * @param tagName
         */
        protected IRPAgent(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "BulkCmIRP" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class BulkCmIRP extends AbstractNeoTag {
        public static final String TAG_NAME = "BulkCmIRP";

        /**
         * @param tagName
         */
        protected BulkCmIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "AlarmIRP" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class AlarmIRP extends AbstractNeoTag {
        public static final String TAG_NAME = "AlarmIRP";

        protected AlarmIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "NotificationIRP" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class NotificationIRP extends AbstractNeoTag {
        public static final String TAG_NAME = "NotificationIRP";

        protected NotificationIRP(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "ExternalUtranCell" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ExternalUtranCell extends AbstractNeoTag {
        public static final String TAG_NAME = "ExternalUtranCell";

        protected ExternalUtranCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "ExternalGsmCell" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ExternalGsmCell extends AbstractNeoTag {
        public static final String TAG_NAME = "ExternalGsmCell";

        /**
         * @param tagName
         */
        protected ExternalGsmCell(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

    /**
     * <p>
     * Handler "VsDataContainer" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class VsDataContainer extends AbstractNeoTag {
        public static final String TAG_NAME = "VsDataContainer";

        /**
         * @param tagName
         */
        protected VsDataContainer(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
        }

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

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, TAG_NAME);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }
}
