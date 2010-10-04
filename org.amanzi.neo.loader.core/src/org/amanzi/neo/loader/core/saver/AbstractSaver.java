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

package org.amanzi.neo.loader.core.saver;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.statistic.IStatistic;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Abstract saver
 * </p>.
 *
 * @param <T> the generic type
 * @author tsinkel_a
 * @since 1.0.0
 */
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 * @param <T>
 */
public abstract class AbstractSaver<T extends IDataElement> implements ISaver<T> {

    /** The Constant ALL_NODE_TYPES. */
    protected static final String ALL_NODE_TYPES = "all_node_types";

    /** The indexes. */
    private final LinkedHashMap<String, ArrayList<MultiPropertyIndex< ? >>> indexes = new LinkedHashMap<String, ArrayList<MultiPropertyIndex< ? >>>();

    /** The mapped indexes. */
    private final LinkedHashMap<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>> mappedIndexes = new LinkedHashMap<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>>();

    /** The stat to analyse. */
    private final LinkedHashMap<String, HashSet<String>> statToAnalyse = new LinkedHashMap<String, HashSet<String>>();

    /** The ignored properties. */
    private final HashSet<String> ignoredProperties = new HashSet<String>();

    /** The indexes initialized. */
    private boolean indexesInitialized = false;

    /** The output stream. */
    private PrintStream outputStream;

    /** The root node. */
    protected Node rootNode;

    /** The main tx. */
    protected Transaction mainTx;

    /** The statistic. */
    protected IStatistic statistic;

    /** The element. */
    protected T element;

    private long maxTransactionSize;

    private TransactionCounter txCounter;

    /**
     * Gets the prints the stream.
     * 
     * @return the prints the stream
     */
    @Override
    public PrintStream getPrintStream() {
        if (outputStream == null) {
            return System.out;
        }
        return outputStream;
    }

    /**
     * Inits the.
     * 
     * @param element the element
     */
    public void init(T element) {
        this.element = element;
        ignoredProperties.add(INeoConstants.PROPERTY_TYPE_NAME);
    };

    /**
     * Sets the prints the stream.
     * 
     * @param outputStream the new prints the stream
     */
    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Index.
     * 
     * @param node the node
     */
    protected void index(Node node) {
        String nodeType = NeoUtils.getNodeType(node, "");
        ArrayList<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList == null) {
            return;
        }
        for (MultiPropertyIndex< ? > index : indList) {
            try {
                index.add(node);
            } catch (IOException e) {
                // TODO:Log error
                removeIndex(nodeType, index);
            }
        }
    }

    /**
     * Start main tx.
     */
    protected void startMainTx(long maxTransactionSize) {
        this.maxTransactionSize = maxTransactionSize;
        txCounter=new TransactionCounter(maxTransactionSize);
        mainTx = getService().beginTx();
    }

    /**
     * Adds the index.
     * 
     * @param nodeType the node type
     * @param index the index
     */
    protected void addIndex(String nodeType, MultiPropertyIndex< ? > index) {
        ArrayList<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList == null) {
            indList = new ArrayList<MultiPropertyIndex< ? >>();
            indexes.put(nodeType, indList);
        }
        if (!indList.contains(index)) {
            indList.add(index);
        }
    }

    /**
     * Adds the mapped index.
     * 
     * @param key the key
     * @param nodeType the node type
     * @param index the index
     */
    protected void addMappedIndex(String key, String nodeType, MultiPropertyIndex< ? > index) {
        LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>> mappIndex = mappedIndexes.get(key);
        if (mappIndex == null) {
            mappIndex = new LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>();
            mappedIndexes.put(key, mappIndex);
        }
        HashSet<MultiPropertyIndex< ? >> indSet = mappIndex.get(nodeType);
        if (indSet == null) {
            indSet = new HashSet<MultiPropertyIndex< ? >>();
            mappIndex.put(nodeType, indSet);
        }
        indSet.add(index);
    }

    /**
     * Indexes mapped.
     * 
     * @param key - index key
     * @param node - node
     */
    protected void index(String key, Node node) {
        String nodeType = NeoUtils.getNodeType(node, "");
        LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>> indMap = mappedIndexes.get(key);
        if (indMap == null) {
            return;
        }
        HashSet<MultiPropertyIndex< ? >> indList = indMap.get(nodeType);
        if (indList == null) {
            return;
        }
        for (MultiPropertyIndex< ? > index : indList) {
            try {
                index.add(node);
            } catch (IOException e) {
                error(e.getLocalizedMessage());
                removeMappedIndex(key, nodeType, index);
            }
        }
    }

    /**
     * Removes the index.
     * 
     * @param nodeType the node type
     * @param index the index
     */
    protected void removeIndex(String nodeType, MultiPropertyIndex< ? > index) {
        ArrayList<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList != null) {
            indList.remove(index);
        }

    }

    /**
     * Flush indexes.
     */
    protected void flushIndexes() {
        for (Entry<String, ArrayList<MultiPropertyIndex< ? >>> entry : indexes.entrySet()) {

            for (MultiPropertyIndex< ? > index : entry.getValue()) {
                try {
                    index.flush();
                } catch (IOException e) {
                    // TODO:Log error
                    e.printStackTrace();
                    removeIndex(entry.getKey(), index);
                }
            }
        }
        for (Entry<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>> entryInd : mappedIndexes.entrySet()) {
            if (entryInd.getValue() != null) {
                for (Entry<String, HashSet<MultiPropertyIndex< ? >>> entry : entryInd.getValue().entrySet()) {
                    for (MultiPropertyIndex< ? > index : entry.getValue()) {
                        try {
                            index.flush();
                        } catch (IOException e) {
                            // TODO:Log error
                            removeMappedIndex(entryInd.getKey(), entry.getKey(), index);
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the mapped index.
     * 
     * @param key the key
     * @param nodeType the node type
     * @param index the index
     */
    private void removeMappedIndex(String key, String nodeType, MultiPropertyIndex< ? > index) {
        LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>> mapIn = mappedIndexes.get(key);
        if (mapIn != null) {
            HashSet<MultiPropertyIndex< ? >> indList = mapIn.get(nodeType);
            if (indList != null) {
                indList.remove(index);
            }
        }
    }

    /**
     * Initialize indexes.
     */
    protected void initializeIndexes() {
        if (indexesInitialized) {
            return;
        }
        for (Entry<String, ArrayList<MultiPropertyIndex< ? >>> entry : indexes.entrySet()) {
            for (MultiPropertyIndex< ? > index : entry.getValue()) {
                try {
                    index.initialize(getService(), null);
                } catch (IOException e) {
                    // TODO:Log error
                    removeIndex(entry.getKey(), index);
                }
            }
        }
        for (Entry<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>> entryInd : mappedIndexes.entrySet()) {
            if (entryInd.getValue() != null) {
                for (Entry<String, HashSet<MultiPropertyIndex< ? >>> entry : entryInd.getValue().entrySet()) {
                    for (MultiPropertyIndex< ? > index : entry.getValue()) {
                        try {
                            index.initialize(getService(), null);
                        } catch (IOException e) {
                            error(e.getLocalizedMessage());
                            removeMappedIndex(entryInd.getKey(), entry.getKey(), index);
                        }
                    }
                }
            }
        }
        indexesInitialized = true;
    }

    /**
     * Adds the analysed node types.
     * 
     * @param key the key
     * @param nodeType the node type
     */
    protected void addAnalysedNodeTypes(String key, String nodeType) {
        HashSet<String> nodetypes = statToAnalyse.get(key);
        if (nodetypes == null) {
            nodetypes = new HashSet<String>();
            statToAnalyse.put(key, nodetypes);
        }
        if (nodetypes.contains(ALL_NODE_TYPES)) {
            return;
        }
        nodetypes.add(nodeType);
    }

    /**
     * Index stat.
     * 
     * @param key the key
     * @param node the node
     */
    protected void indexStat(String key, Node node) {
        HashSet<String> nodeTypes = statToAnalyse.get(key);
        if (nodeTypes == null) {
            return;
        }
        String nodeType = NeoUtils.getNodeType(node, "");
        if (!nodeTypes.contains(ALL_NODE_TYPES)) {
            if (!nodeTypes.contains(nodeType)) {
                return;
            }
        }
        for (String keys : node.getPropertyKeys()) {
            if (!ignoredProperties.contains(keys)) {
                statistic.indexValue(key, nodeType, keys, node.getProperty(keys));
            }
        }
    }

    /**
     * Update property.
     * 
     * @param key the key
     * @param nodeType the node type
     * @param node the node
     * @param propertyName the property name
     * @param value the value
     */
    protected boolean updateProperty(String key, String nodeType, Node node, String propertyName, Object value) {
        if (!node.hasProperty(propertyName)) {
            return setProperty(key, nodeType, node, propertyName, value);
        }
        return false;
    }

    /**
     * Sets the property.
     * 
     * @param key the key
     * @param nodeType the node type
     * @param node the node
     * @param propertyName the property name
     * @param value the value
     */
    protected boolean setProperty(String key, String nodeType, Node node, String propertyName, Object value) {
        if (value == null||(value instanceof String&&StringUtil.isEmpty((String)value))) {
            return false;
        }
        node.setProperty(propertyName, value);
        return statistic.indexValue(key, nodeType, propertyName, value);
    }

    /**
     * Gets the service.
     * 
     * @return the service
     */
    protected INeoDbService getService() {
        return DatabaseManager.getInstance().getCurrentDatabaseService();
    }

    /**
     * Gets the index service.
     * 
     * @return the index service
     */
    protected IndexService getIndexService() {
        return DatabaseManager.getInstance().getIndexService();
    }

    /**
     * Finish up indexes.
     */
    protected void finishUpIndexes() {
        for (Entry<String, ArrayList<MultiPropertyIndex< ? >>> entry : indexes.entrySet()) {
            for (MultiPropertyIndex< ? > index : entry.getValue()) {
                index.finishUp();
            }
        }
        for (Entry<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>> entryInd : mappedIndexes.entrySet()) {
            if (entryInd.getValue() != null) {
                for (Entry<String, HashSet<MultiPropertyIndex< ? >>> entry : entryInd.getValue().entrySet()) {
                    for (MultiPropertyIndex< ? > index : entry.getValue()) {
                        index.finishUp();
                    }
                }
            }
        }
    }

    /**
     * Commit.
     * 
     * @param restart the restart
     */
    protected void commit(boolean restart) {
        if (txCounter!=null){
            txCounter.flush();
        }
        if (mainTx != null) {
            flushIndexes();
            mainTx.success();
            mainTx.finish();
            // LOGGER.debug("Commit: Memory: "+(Runtime.getRuntime().totalMemory()
            // -
            // Runtime.getRuntime().freeMemory()));
            if (restart) {
                mainTx = getService().beginTx();
            } else {
                mainTx = null;
            }
        }
    }
    public void updateTx(long nodes,long relations){
        if (txCounter.updateTx(nodes, relations)){
            commit(true);
        }
    }

    /**
     * Println.
     * 
     * @param s the s
     */
    protected void println(String s) {
        getPrintStream().println(s);
    }

    /**
     * Info.
     * 
     * @param info the info
     */
    protected void info(String info) {
        println(info);
    }

    /**
     * Error.
     * 
     * @param error the error
     */
    protected void error(String error) {
        println(error);

    }

    /**
     * Exception.
     * 
     * @param exception the exception
     */
    protected void exception(Throwable exception) {
        exception.printStackTrace(getPrintStream());

    }

    /**
     * Exception.
     * 
     * @param s the s
     * @param exception the exception
     */
    protected void exception(String s, Throwable exception) {
        println(s);
        exception(exception);
    }

    /**
     * <p>
     * TransactionCounter
     * </p>
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class TransactionCounter {
        long nodes = 0;
        long nodesAll = 0;
        long relations = 0;
        long relationsAll = 0;
        long maxTotal;


        /**
         * Instantiates a new transaction counter.
         *
         * @param maxTotal the max total
         */
        public TransactionCounter(long maxTotal) {
            super();
            this.maxTotal = maxTotal;
        }

        /**
         * Update tx.
         *
         * @param nodes the nodes
         * @param rel the rel
         * @return true, if successful
         */
        public boolean updateTx(long nodes, long rel) {
            this.nodes += nodes;
            this.nodesAll += nodes;
            this.relations += rel;
            this.relationsAll += rel;
            return nodes + relations >= maxTotal;
        }


        /**
         * Flush.
         */
        public void flush() {
            nodes = 0;
            relations = 0;
        }

    }
}
