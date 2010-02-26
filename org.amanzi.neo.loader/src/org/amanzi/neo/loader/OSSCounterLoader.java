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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
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
    private static final int KEY_EVENT = 1;
    private final static Pattern mainFilePattern = Pattern.compile("(^.*)(_Mp0\\.)(.*$)");
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";
    private static final String EXTERNAL_DTD_LOADING_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private Node ossRoot;
    private Pair<Boolean, Node> mainNode;
    private long timestampOfDay;
    private Node eventLastNode;
    private final LinkedHashMap<String, Header> headers;
    private Node ossNode;
    private Node fileNode;
    private final FileContentHandler handler;
    private final Tags[]rootTags;
    private  Tags rootTag=null;
    private Node lastChild;

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
        rootTags=new Tags[]{new MfhTag()};

    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        addIndex(NodeTypes.GPEH_EVENT.getId(), NeoUtils.getTimeIndexProperty(basename));

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
            int perc = 0;
            int prevPerc = 0;
            int prevLineNumber = 0;
            ossNode = LoaderUtils.findOrCreateOSSNode(OssType.COUNTER, basename, neo);
            for (File file : fileList) {
                try {
                    storeFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    NeoLoaderPlugin.error("Wrong parse file: " + file.getName());
                    NeoLoaderPlugin.exception(e);
                    // TODO add tx.failure ?
                }
            }
            commit(true);
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
        }
    }

    /**
     *storing XML in database
     * 
     * @param file
     * @throws SAXException
     * @throws IOException
     * @throws
     */
    private void storeFile(File file) throws SAXException, IOException {
        String fileNodeName = file.getName();
        Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateChildNode(neo, ossNode, fileNodeName);
        fileNode = fileNodePair.getRight();
        lastChild=null;
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
        return ossRoot;
    }

    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
        //do nothing
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
        public String tag=null;
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (tag==null){
                return;
            }
            if (rootTag==null){
                rootTag=findRootTag(tag);
                tag=null;
                return;
            }
            StringBuilder builder=new StringBuilder();
            for (int i = 0; i <length; i++) {
                builder.append(ch[start+i]); 
            }
            rootTag.parseCharacters(tag,builder);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
                tag=localName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (rootTag != null) {
                if (rootTag.getTagName().equals(localName)) {
                    rootTag = null;
                } else {
                    rootTag.endElement(localName);
                }
            }
        }
    }

    /**
     * find root tag by id
     * @param localName - tag name;
     * @return tag or null
     */
    public Tags findRootTag(String localName) {
        for (Tags tag:rootTags) {
            if (tag.getTagName().equals(localName)){
                tag.initialize();
                return tag;
            }
        }
        return null;
    }

    private abstract static class Tags{
        protected final String tagName;

        /**
         * constructor
         * @param tagName tag name
         */
        public Tags(String tagName) {
            this.tagName = tagName;
        }

        /**
         *initialize tags
         */
        public void initialize() {
        }

        /**
         *
         * @param localName
         */
        public void endElement(String localName) {
            //do nothing
        }

        /**
         *Parse characters
         * @param tag tag name
         * @param builder - value
         */
        public abstract void parseCharacters(String tag, StringBuilder builder);

        /**
         * @return Returns the tagName.
         */
        public String getTagName() {
            return tagName;
        }
        
        
    }
    private class MfhTag extends Tags{
        /**
         * constructor
         */
        public MfhTag() {
            super("mfh");
        }

        @Override
        public void parseCharacters(String tag, StringBuilder builder) {
            Transaction tx = neo.beginTx();
            try{
                setProperty(fileNode, tag, builder.toString());
            }finally{
                tx.finish();
            }
        }
    }
    private class MdTag extends Tags{
        Node mdNode;
        /**
         * constructor
         */
        public MdTag() {
            super("md");
        }
@Override
public void initialize() {
    super.initialize();
    Transaction tx = neo.beginTx();
    try{
        mdNode=neo.createNode();
        //TODO addTYPE
        NeoUtils.addChild(fileNode, mdNode, lastChild, neo);
        lastChild=mdNode;
    }finally{
        tx.finish();
    }
}
        @Override
        public void parseCharacters(String tag, StringBuilder builder) {
        }
    }
}
