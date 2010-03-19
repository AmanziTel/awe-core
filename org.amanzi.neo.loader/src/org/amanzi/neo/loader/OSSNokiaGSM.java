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
import java.util.LinkedList;
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

/**
 * <p>
 * Loader for NokiaGSM files
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class OSSNokiaGSM extends AbstractLoader {

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
    private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    /**
     * Constructor
     * 
     * @param file - file name
     * @param datasetName - dataset name
     * @param display - Display
     */
    public OSSNokiaGSM(String file, String datasetName, Display display) {
        initialize("OSSNokiaGSM", null, file, display);
        basename = datasetName;
        headers = getHeaderMap(KEY_EVENT).headers;
        headers.put("name",new StringHeader(new Header("name", "name", 0)));
        headers.put("address",new StringHeader(new Header("address", "address", 1)));
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
        rdr.setFeature(EXTERNAL_DTD_LOADING_FEATURE, false);
        in = new CountingFileInputStream(file);
        rdr.parse(new InputSource(new BufferedInputStream(in, 64 * 1024)));
    }

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
            ossNode = LoaderUtils.findOrCreateOSSNode(OssType.NOKIA_GSM, basename, neo);
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

    public class Factory implements IXmlTagFactory {
        @Override
        public IXmlTag createInstance(String tagName, Attributes attributes) {
            if (tagName.equals(CmData.TAG_NAME)) {
                return new CmData(attributes);
            }
            return null;
        }

    }

    /**
     * <p>
     * Handler "CmData" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class CmData extends AbstractNeoTag {
        public static final String TAG_NAME = "cmData";

        protected CmData(Attributes attributes) {
            super(TAG_NAME, fileNode, null, attributes);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            IXmlTag result = this;
            if (TagHeader.TAG_NAME.equals(localName)) {
                result = new TagHeader(attributes, this);
            } else if (ManagedObject.TAG_NAME.equals(localName)) {
                result = new ManagedObject(attributes, this);
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
            NeoUtils.setNodeName(node, getName(), null);
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }

    }

    /**
     * <p>
     * Handler "header" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class TagHeader extends AbstractTag {
        public static final String TAG_NAME = "header";

        protected TagHeader(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent);
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            return new TagAttributes(localName, attributes, (AbstractNeoTag)parent, this);
        }

    }

    /**
     * <p>
     * Handler sub tag for storing inner tags like propertys
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class TagAttributes extends AbstractTag {
        Stack<String> stack;
        private IXmlTag parentToReturn;

        /**
         * @param tagName
         * @param tagName
         */
        protected TagAttributes(String tagName, Attributes attributes, AbstractNeoTag parent) {
            super(tagName, parent);
            stack = new Stack<String>();
            parent.storeAttributes(parent.getNode(), attributes);
            parentToReturn = parent;
        }

        public TagAttributes(String tagName, Attributes attributes, AbstractNeoTag parent, IXmlTag parentForReturn) {
            this(tagName, attributes, parent);
            parentToReturn = parentForReturn;
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
                if (localName.equals(getName())) {
                    return parentToReturn;
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

    // TODO define handle item
    /**
     * <p>
     * Handler "list" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ListTag extends AbstractTag {
        Stack<String> stack;
        private final AbstractNeoTag storedTag;
        List<String> values;
        private final String name;

        protected ListTag(String tagName, Attributes attributes, IXmlTag parent, AbstractNeoTag storedTag) {
            super(tagName, parent);
            stack = new Stack<String>();
            name = attributes.getValue("name");
            this.storedTag = storedTag;
            values = new LinkedList<String>();
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
                if (localName.equals(getName())) {
                    if (name != null) {
                        storedTag.getNode().setProperty(name, values.toArray(new String[0]));
                    }
                    return parent;
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                String tag = stack.pop();
                if (tag.equals("item")) {
                    // do nothing
                } else if (tag.equals("p")) {
                    values.add(chars.toString());
                } else {
                    throw new UnsupportedOperationException(tag);
                }
                return this;
            }
        }

    }

    /**
     * <p>
     * Handler "managedObject" tag
     * </p>
     * 
     * @author Tsinkel_A
     * @since 1.0.0
     */
    public class ManagedObject extends AbstractNeoTag {
        String openTag = null;
        private String name;
        public static final String TAG_NAME = "managedObject";

        protected ManagedObject(Attributes attributes, AbstractNeoTag parent) {
            super(TAG_NAME, parent, attributes);
            openTag = null;
        }

        @Override
        public IXmlTag startElement(String localName, Attributes attributes) {
            if (openTag != null) {
                throw new IllegalArgumentException();
            }
            if (localName.equals("list")) {
                return new ListTag("list", attributes, this, this);
            }
            openTag = localName;
            if (localName.equals("p")) {
                name = attributes.getValue("name");
            } else if (localName.equals("defaults")) {
                name = attributes.getValue("name");
            } else {
                throw new IllegalArgumentException("Wrong tag " + localName);
            }
            return this;
        }

        @Override
        public IXmlTag endElement(String localName, StringBuilder chars) {
            if (chars.length() > 0 && name != null) {
                Node nodeToSave = getNode();
                setIndexPropertyNotParcedValue(headers, nodeToSave, name, chars.toString());
            }
            updateMonitor();
            if (localName.equals(TAG_NAME)) {
                return parent;
            } else {
                if (openTag != null) {
                    openTag = null;
                    return this;
                }
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected Node createNode(Attributes attributes) {
            Node node = neo.createNode();
            NodeTypes.UTRAN_DATA.setNodeType(node, neo);
            node.setProperty(INeoConstants.URTAN_DATA_TYPE, getName());
            storeAttributes(node, attributes);
            updateTx();
            return node;
        }
    }

}
