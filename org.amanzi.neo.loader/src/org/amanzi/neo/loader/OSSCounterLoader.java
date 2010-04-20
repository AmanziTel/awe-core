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
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class OSSCounterLoader extends AbstractLoader {
    private static final Logger LOGGER = Logger.getLogger(OSSCounterLoader.class);
    // temporary types, after structure definition will be refactored for using NodeTypes
    private final static String MD_TYPE = "md";
    private final static String MI_TYPE = "mi";
    private final static String MV_TYPE = "mv";
    private static final int KEY_EVENT = 1;
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";
    private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private final LinkedHashMap<String, Header> headers;
    private Node ossNode;
    private Node fileNode;
    private final FileContentHandler handler;
    private final Tags[] rootTags;
    private Tags rootTag = null;
    private Node lastChild;
    public Node lastMiChild;
    public Node lastMvChild;
    protected static final  SimpleDateFormat dataFormat=new SimpleDateFormat("yyyyHHmmss");

    /**
     * Constructor
     * 
     * @param directory
     * @param datasetName
     * @param display
     */
    public OSSCounterLoader(String directory, String datasetName, Display display) {
        initialize("OSS_COUNTER", null, directory, display);
        basename = datasetName;
        headers = getHeaderMap(KEY_EVENT).headers;
        handler = new FileContentHandler();
        rootTags = new Tags[] {new MfhTag(), new MdTag()};

    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        addIndex(MI_TYPE, NeoUtils.getTimeIndexProperty(basename));

        List<File> fileList = LoaderUtils.getAllFiles(filename, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || Pattern.matches(REG_EXP_XML, pathname.getName());
            }
        });
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            int sizeAll = fileList.size();
            monitor = SubMonitor.convert(monitor, sizeAll);
            ossNode = LoaderUtils.findOrCreateOSSNode(OssType.COUNTER, basename, neo);
            for (File file : fileList) {
                try {
                    monitor.subTask(file.getName());
                    LOGGER.debug(file.getName() );
                    storeFile(file);
                    commit(true);
                    monitor.worked(1);
                } catch (Exception e) {
                    e.printStackTrace();
                    NeoLoaderPlugin.error("Wrong parse file: " + file.getName());
                    NeoLoaderPlugin.exception(e);
                    // TODO add tx.failure ?
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
    protected void finishUp() {
        super.finishUp();
        sendUpdateEvent(UpdateViewEventType.OSS);
    }

    /**
     *storing XML in database
     * 
     * @param file  - XML file
     * @throws SAXException
     * @throws IOException
     * @throws
     */
    private void storeFile(File file) throws SAXException, IOException {
        String fileNodeName = file.getName();
        Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateChildNode(neo, ossNode, fileNodeName);
        fileNode = fileNodePair.getRight();
        lastChild = null;
        if (fileNodePair.getLeft()) {
            NodeTypes.FILE.setNodeType(fileNode, neo);
        }

        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setFeature(EXTERNAL_DTD_LOADING_FEATURE, false);
        rdr.setContentHandler(handler);
        rdr.parse(new InputSource(new BufferedInputStream(new FileInputStream(file), 64 * 1024)));
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
        // do nothing
    }

    /**
     * <p>
     * SAX parsing handler
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class FileContentHandler extends DefaultHandler {
        public String tag = null;
        public StringBuilder chars;

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (tag == null || rootTag == null) {
                return;
            }
            for (int i = 0; i < length; i++) {
                chars.append(ch[start + i]);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            tag = localName;
            if (rootTag == null) {
                rootTag = findRootTag(tag);
                if (rootTag != null) {
                    chars = new StringBuilder();
                }
                tag = null;
                return;
            } else {
                chars = new StringBuilder();
                rootTag.startElement(tag);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            tag = null;
            if (rootTag != null) {
                if (rootTag.getTagName().equals(localName)) {
                    rootTag = null;
                } else {
                    rootTag.endElement(localName, chars);
                }
            }
        }
    }

    /**
     * find root tag by id
     * 
     * @param localName - tag name;
     * @return tag or null
     */
    public Tags findRootTag(String localName) {
        for (Tags tag : rootTags) {
            if (tag.getTagName().equals(localName)) {
                tag.initialize();
                return tag;
            }
        }
        return null;
    }

    /**
     * <p>
     * abstract TAG of OSS counter XML data
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private abstract static class Tags {
        protected final String tagName;

        /**
         * constructor
         * 
         * @param tagName tag name
         */
        public Tags(String tagName) {
            this.tagName = tagName;
        }

        /**
         * handle start element
         * 
         * @param tag - start tag
         */
        public void startElement(String tag) {
        }

        /**
         *initialize tags
         */
        public void initialize() {
        }

        /**
         * Handle end element
         * 
         * @param tag - tag
         * @param builder - string in current element
         */
        public abstract void endElement(String tag, StringBuilder builder);

        /**
         * @return Returns the tagName.
         */
        public String getTagName() {
            return tagName;
        }

    }

    /**
     * <p>
     * Mfh tag
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class MfhTag extends Tags {
        /** String TAG_NAME field */
        private static final String TAG_NAME = "mfh";

        /**
         * constructor
         */
        public MfhTag() {
            super(TAG_NAME);
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
            if (builder.length() > 0) {
                Transaction tx = neo.beginTx();
                try {
                    if (tag.equals("neid")) {
                        return;
                    }
                    setProperty(fileNode, tag, builder.toString());
                } finally {
                    tx.finish();
                }
            }
        }
    }

    /**
     * <p>
     * Mv tag
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class Mv extends Tags {
        /** String MV field */
        protected static final String TAG_NAME = "mv";
        private final Mi parentNode;
        private Node mvNode;
        private int countR;

        /**
         * constructor
         * 
         * @param parentNode parent Mi tag
         */
        public Mv(Mi parentNode) {
            super(TAG_NAME);
            this.parentNode = parentNode;
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
            if (builder.length() < 1) {
                return;
            }
            if (tag.equals("r")) {
                if (parentNode.properties.size() <= countR) {
                    NeoLoaderPlugin.error("Wrong count of values. Value not stored:"+builder.toString());
                } else {
                    setIndexPropertyNotParcedValue(headers,mvNode, parentNode.properties.get(countR), builder.toString());
                    countR++;
                }
            } else {
                setProperty(mvNode, tag, builder.toString());
            }
        }

        @Override
        public void initialize() {
            super.initialize();
            Transaction tx = neo.beginTx();
            try {
                mvNode = neo.createNode();
                mvNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, MV_TYPE);
                NeoUtils.addChild(parentNode.miNode, mvNode, lastMvChild, neo);
                lastMvChild = mvNode;
            } finally {
                tx.finish();
            }
            countR = 0;
        }
    }

    /**
     * <p>
     * Mi tag handler class
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class Mi extends Tags {
        /** String TAG_NAME field */
        protected static final String TAG_NAME = "mi";
        private final Node parentNode;
        private Node miNode;
        private Mv mv;
        private LinkedList<String> properties;

        public Mi(Node parentNode) {
            super(TAG_NAME);
            this.parentNode = parentNode;
        }

        @Override
        public void startElement(String tag) {
            super.startElement(tag);
            if (tag.equals(Mv.TAG_NAME)) {
                mv = new Mv(this);
                mv.initialize();
            } else if (mv != null) {
                mv.startElement(tag);
            }
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
            if (mv != null) {
                mv.endElement(tag, builder);
            } else if (tag.equals(Mv.TAG_NAME)) {
                mv = null;
            } else if (tag.equals("mt")) {
                properties.add(builder.toString());
            } else {
                setProperty(miNode, tag, builder.toString());
                if (tag.equals("mts")){
                    try {
                        long timestamp = dataFormat.parse(builder.toString()).getTime();
                        setProperty(miNode, INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
                        updateTimestampMinMax(KEY_EVENT, timestamp);
                    } catch (ParseException e) {
                        
                        NeoLoaderPlugin.exception(e);
                    }
                    
                }
            }
        }

        @Override
        public void initialize() {
            super.initialize();
            Transaction tx = neo.beginTx();
            try {
                miNode = neo.createNode();
                NeoUtils.addChild(parentNode, miNode, lastMiChild, neo);
                miNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, MI_TYPE);
                lastMiChild = miNode;
                lastMvChild = null;
            } finally {
                tx.finish();
            }
            properties = new LinkedList<String>();
        }

    }

    /**
     * <p>
     * Md tag handler class
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class MdTag extends Tags {
        /** String TAG_NAME field */
        private static final String TAG_NAME = "md";
        Node mdNode;
        private Mi mi;

        /**
         * constructor
         */
        public MdTag() {
            super(TAG_NAME);
        }

        @Override
        public void initialize() {
            super.initialize();
            Transaction tx = neo.beginTx();
            try {
                mdNode = neo.createNode();
                mdNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, MD_TYPE);
                NeoUtils.addChild(fileNode, mdNode, lastChild, neo);
                lastChild = mdNode;
                lastMiChild = null;
                mi = null;
            } finally {
                tx.finish();
            }
        }

        @Override
        public void startElement(String tag) {
            if (tag.equals(Mi.TAG_NAME)) {
                mi = new Mi(mdNode);
                mi.initialize();
            } else if (mi != null) {
                mi.startElement(tag);
            }
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
            if (mi == null) {
                setProperty(mdNode, tag, builder.toString());
            } else if (tag.equals(Mi.TAG_NAME)) {
                index(mi.miNode);
                mi = null;
            } else {
                mi.endElement(tag, builder);
            }
        }
    }

}
