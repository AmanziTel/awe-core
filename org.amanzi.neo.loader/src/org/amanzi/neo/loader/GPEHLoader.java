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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.gpeh.GPEHEvent;
import org.amanzi.neo.loader.gpeh.GPEHMainFile;
import org.amanzi.neo.loader.gpeh.GPEHParser;
import org.amanzi.neo.loader.gpeh.GPEHEvent.Event;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * GPHEHLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHLoader extends AbstractLoader {
    /** int KEY_EVENT field */
    private static final int KEY_EVENT = 1;
    private final static Pattern mainFilePattern = Pattern.compile("(^.*)(_Mp0\\.)(.*$)");
    private Node ossRoot;
    private Pair<Boolean, Node> mainNode;
    private long timestampOfDay;
    private Node eventLastNode;
    private final LinkedHashMap<String, Header> headers;

    /**
     * @param directory
     * @param datasetName
     * @param display
     */
    public GPEHLoader(String directory, String datasetName, Display display) {
        initialize("GPEH", null, directory, display);
        basename = datasetName;
        headers = getHeaderMap(KEY_EVENT).headers;

    }

    @Override
    protected Node getStoringNode(Integer key) {
        return ossRoot;
    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        addIndex(NodeTypes.GPEH_EVENT.getId(), NeoUtils.getTimeIndexProperty(basename));

        Map<String, List<String>> fileList = getGPEHFile(filename);
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            ossRoot = findOrCreateOSSNode();
            int perc = 0;
            int prevPerc = 0;
            int prevLineNumber = 0;
            for (Map.Entry<String, List<String>> entry : fileList.entrySet()) {
                try {
                    String mainFile = entry.getKey();
                    String rootFile = filename + File.separator + mainFile;
                    GPEHMainFile root = GPEHParser.parseMainFile(new File(rootFile));
                    saveRoot(root);
                    eventLastNode = null;
                    for (String subFile : entry.getValue()) {
                        GPEHEvent eventFile = GPEHParser.parseEventFile(new File(filename + File.separator + subFile));
                        saveEvent(eventFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO add more information
                    NeoLoaderPlugin.error(e.getLocalizedMessage());
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
     * @param eventFile
     */
    private void saveEvent(GPEHEvent eventFile) {
        for (Event event : eventFile.getEvents()) {
            saveSingleEvent(event);
        }
    }

    /**
     * @param event
     */
    private void saveSingleEvent(Event event) {
        Transaction tx = neo.beginTx();
        try {
            Node eventNode = neo.createNode();
            eventNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.GPEH_EVENT.getId());
            setIndexProperty(headers, eventNode, INeoConstants.PROPERTY_NAME_NAME, event.getType().name());
            for (Map.Entry<Parameters, Object> entry : event.getProperties().entrySet()) {
                setIndexProperty(headers, eventNode, entry.getKey().name(), entry.getValue());
            }
            Long timestamp = event.getFullTime(timestampOfDay);
            updateTimestampMinMax(KEY_EVENT, timestamp);
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            NeoUtils.addChild(mainNode.getRight(), eventNode, eventLastNode, neo);
            tx.success();
            index(eventNode);
            eventLastNode = eventNode;
        } finally {
            tx.finish();
        }
    }

    /**
     * save main file to node
     * 
     * @param root - main file
     */
    private void saveRoot(GPEHMainFile root) {
        mainNode = NeoUtils.findOrCreateChildNode(neo, ossRoot, root.getName());
        GPEHMainFile.Header header = root.getHeader();
        GregorianCalendar cl = new GregorianCalendar(header.getYear(), header.getMonth(), header.getDay());
        timestampOfDay = cl.getTimeInMillis();
        if (mainNode.getLeft()) {
            Transaction tx = neo.beginTx();
            try {
                Node node = mainNode.getRight();
                node.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.OSS_MAIN.getId());
                setProperty(node, INeoConstants.GPEH_FILE_VER, header.getFileVer());
                setProperty(node, INeoConstants.GPEH_DAY, header.getDay());
                setProperty(node, INeoConstants.GPEH_MONTH, header.getMonth());
                setProperty(node, INeoConstants.GPEH_MINUTE, header.getMinute());
                setProperty(node, INeoConstants.GPEH_SECOND, header.getSecond());
                setProperty(node, INeoConstants.GPEH_YEAR, header.getYear());
                setProperty(node, INeoConstants.GPEH_LOGIC_NAME, header.getNeLogicalName());
                setProperty(node, INeoConstants.GPEH_USER_LABEL, header.getNeUserLabel());
                tx.success();
            } finally {
                tx.finish();
            }
        }
    }

    /**
     * Sets property to node (if value!=null)
     * 
     * @param node - node
     * @param key - property key
     * @param value - value
     */
    protected void setProperty(Node node, String key, Object value) {
        if (value != null) {
            node.setProperty(key, value);
        }
    }

    /**
     *find or create OSS node
     * 
     * @return
     */
    private Node findOrCreateOSSNode() {
        Node oss;
        Transaction tx = neo.beginTx();
        try {
            oss = NeoUtils.findRootNode(NodeTypes.OSS, basename, neo);
            if (oss == null) {
                oss = neo.createNode();
                oss.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.OSS.getId());
                oss.setProperty(INeoConstants.PROPERTY_NAME_NAME, basename);
                oss.setProperty(INeoConstants.PROPERTY_FILENAME_NAME, filename);
                neo.getReferenceNode().createRelationshipTo(oss, GeoNeoRelationshipTypes.CHILD);
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return oss;
    }

    /**
     * Gets map of gpeh files
     * 
     * @param filename - directory
     * @return Map<main file, List of subfiles>
     */
    private Map<String, List<String>> getGPEHFile(String filename) {
        File dir = new File(filename);
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        String[] mainFileList = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return mainFilePattern.matcher(name).matches();
            }
        });
        for (final String mainFile : mainFileList) {
            Matcher matcher = mainFilePattern.matcher(mainFile);
            matcher.find();
            final String mainPart = matcher.group(1);
            List<String> subFiles = new LinkedList<String>();
            for (String file : dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(mainPart) && !name.equals(mainFile);
                };
            })) {
                subFiles.add(file);
            }
            result.put(mainFile, subFiles);
        }
        return result;
    }

    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }
    @Override
    protected void finishUp() {
        super.finishUp();
        for (Map.Entry<Integer, Pair<Long, Long>> entry : timeStamp.entrySet()) {
            Node storeNode = getStoringNode(entry.getKey());
            if (storeNode != null) {
                Long minTimeStamp = entry.getValue().getLeft();
                if (minTimeStamp != null) {
                    storeNode.setProperty(INeoConstants.MIN_TIMESTAMP, minTimeStamp);
                }
                Long maxTimeStamp = entry.getValue().getRight();
                if (maxTimeStamp != null) {
                    storeNode.setProperty(INeoConstants.MAX_TIMESTAMP, maxTimeStamp);
                }
            }
        }

        super.cleanupGisNode();//(datasetNode == null ? file : datasetNode);
    }


}
