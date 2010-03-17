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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.sax_parser.FileContentHandler;
import org.amanzi.neo.loader.sax_parser.Tags;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * Loader for utran Loaders files
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class UTRANLoader extends AbstractLoader {
    private static final int KEY_EVENT = 1;
    private Node lastConfig = null;
    protected static final String REG_EXP_XML = "^.+\\.((xml)|(XML))$";
    private final LinkedHashMap<String, Header> headers;
    private final FileContentHandler handler;
    private Node ossNode;
    private Node fileNode;
    private Node lastChild;
    private int counter;

    /**
     * Constructor
     * 
     * @param file - file name
     * @param datasetName - dataset name
     * @param display - Display
     */
    public UTRANLoader(String file, String datasetName, Display display) {
        initialize("OSS_COUNTER", null, file, display);
        basename = datasetName;
        headers = getHeaderMap(KEY_EVENT).headers;
        handler = new FileContentHandler(new Tags[] {new FileHeader(), new ConfigData()});

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
        String fileNodeName = file.getName();
        updateTx();
        Pair<Boolean, Node> fileNodePair = NeoUtils.findOrCreateChildNode(neo, ossNode, fileNodeName);
        fileNode = fileNodePair.getRight();
        lastChild = null;
        lastConfig = null;
        if (fileNodePair.getLeft()) {
            NodeTypes.FILE.setNodeType(fileNode, neo);
        }

        XMLReader rdr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        rdr.setContentHandler(handler);
        rdr.parse(new InputSource(new BufferedInputStream(new FileInputStream(file), 64 * 1024)));
    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        counter = 0;
        if (monitor != null)
            monitor.subTask(basename);
        // addIndex(MI_TYPE, NeoUtils.getTimeIndexProperty(basename));

        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            // monitor = SubMonitor.convert(monitor, sizeAll);
            ossNode = LoaderUtils.findOrCreateOSSNode(OssType.COUNTER, basename, neo);
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
            for (File singleFile : fileList) {
                try {
                    monitor.subTask(file.getName());
                    System.out.println(file.getName());
                    storeFile(singleFile);
                    monitor.worked(1);
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

    private class FileHeader extends Tags {
        FileHeader() {
            super("fileHeader");
        }

        @Override
        public void startElement(String tag, Attributes attributes) {
            super.startElement(tag, attributes);
            if (tag.equals(tagName)) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    fileNode.setProperty(attributes.getLocalName(i), attributes.getValue(i));
                }
            }
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
        }
    }
    private class SubNetwork extends Tags {
        SubNetwork child;
        
        SubNetwork(Tags parent) {
            super("SubNetwork");
            SubNetwork subNode=null;
        }
        @Override
        public void startElement(String tag, Attributes attributes) {
            super.startElement(tag, attributes);
            if (tag.equals("")){
                
            }
        }
        @Override
        public void endElement(String tag, StringBuilder builder) {
        }
        
    }
    private class ConfigData extends Tags {
        private Tags subNetwork;

        ConfigData() {
            super("configData");
        }

        @Override
        public void startElement(String tag, Attributes attributes) {
            if (tag.equals(tagName)) {
                updateTx();
                Node node = neo.createNode();
                NodeTypes.URBAN_CONFIG.setNodeType(node, neo);
                NeoUtils.addChild(fileNode, node, lastConfig, neo);
                lastConfig = node;
                super.startElement(tag, attributes);
                for (int i = 0; i < attributes.getLength(); i++) {
                    fileNode.setProperty(attributes.getLocalName(i), attributes.getValue(i));
                }
            } else {
                if (tag.equals("SubNetwork")){
                    subNetwork=new SubNetwork(this);
                }
            }
        }

        @Override
        public void endElement(String tag, StringBuilder builder) {
        }
    }

    protected void updateTx() {
        counter++;
        if (counter > getCommitSize()) {
            commit(true);
            counter = 0;
        }
    }
}
