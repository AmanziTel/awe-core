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
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.INeoDbService;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.core.parser.IDataElement;
import org.amanzi.neo.services.statistic.IStatistic;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

/**
 * <p>
 *Abstract saver
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class AbstractSaver<T extends IDataElement> implements ISaver<T> {
    protected static final String ALL_NODE_TYPES="all_node_types";
    private final LinkedHashMap<String, ArrayList<MultiPropertyIndex< ? >>> indexes = new LinkedHashMap<String, ArrayList<MultiPropertyIndex< ? >>>();
    private final LinkedHashMap<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>> mappedIndexes = new LinkedHashMap<String, LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>>>();
    private final LinkedHashMap<String, HashSet<String>> statToAnalyse = new LinkedHashMap<String, HashSet<String>>();
    private final HashSet<String> ignoredProperties=new HashSet<String>();
    private boolean indexesInitialized = false;
    private PrintStream outputStream;
    protected Node rootNode;
    protected Transaction mainTx;
    protected IStatistic statistic;
    protected T element;
    @Override
    public PrintStream getPrintStream() {
        if (outputStream==null){
            return System.out;
        }
        return outputStream;
    }
    public void init(T element) {
        this.element = element;
        ignoredProperties.add(INeoConstants.PROPERTY_TYPE_NAME);
    };
    @Override
    public void setPrintStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
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
    *
    */
   protected void startMainTx() {
       mainTx=getService().beginTx();
   }
    /**
     * Indexes mapped
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
    protected void removeIndex(String nodeType, MultiPropertyIndex< ? > index) {
        ArrayList<MultiPropertyIndex< ? >> indList = indexes.get(nodeType);
        if (indList != null) {
            indList.remove(index);
        }

    }
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
    private void removeMappedIndex(String key, String nodeType, MultiPropertyIndex< ? > index) {
        LinkedHashMap<String, HashSet<MultiPropertyIndex< ? >>> mapIn = mappedIndexes.get(key);
        if (mapIn != null) {
            HashSet<MultiPropertyIndex< ? >> indList = mapIn.get(nodeType);
            if (indList != null) {
                indList.remove(index);
            }
        }
    }
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

    protected  void addAnalysedNodeTypes(String key, String nodeType) {
        HashSet<String> nodetypes = statToAnalyse.get(key);
        if (nodetypes==null){
            nodetypes=new HashSet<String>();
            statToAnalyse.put(key, nodetypes);
        }
        if (nodetypes.contains(ALL_NODE_TYPES)){
            return;
        }
        nodetypes.add(nodeType);
    }
    protected void indexStat(String key,Node node){
        HashSet<String> nodeTypes = statToAnalyse.get(key);
        if (nodeTypes==null){
            return;
        }
        String nodeType = NeoUtils.getNodeType(node, "");
        if (!nodeTypes.contains(ALL_NODE_TYPES)){
            if (!nodeTypes.contains(nodeType)){
                return;
            }
        }
        for (String keys:node.getPropertyKeys()){
            if (!ignoredProperties.contains(keys)){
                statistic.indexValue(key, nodeType, keys, node.getProperty(keys));
            }
        }
    }

    protected INeoDbService getService() {
        return DatabaseManager.getInstance().getCurrentDatabaseService();
    }

    protected IndexService getIndexService() {
       return DatabaseManager.getInstance().getIndexService();
   }
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

    protected void commit(boolean restart) {
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
    protected void println(String s) {
        getPrintStream().println(s);
    }

    protected void info(String info) {
        println(info);
    }

    protected void error(String error) {
        println(error);

    }

    protected void exception(Throwable exception) {
        exception.printStackTrace(getPrintStream());

    }

    protected void exception(String s, Throwable exception) {
        println(s);
        exception(exception);
    }
    protected Node addChild(Node parent, NodeTypes type, String name) {
        return addChild(parent, type, name, name);
    }


    protected Node addChild(Node parent, NodeTypes type, String name, String indexName) {
        Node child = null;
        Transaction tx = getService().beginTx();
        try {
            child = getService().createNode();
            child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type.getId());
            child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            getIndexService().index(child, NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, type), indexName);
            if (parent != null) {
                parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            }
            tx.success();
            return child;
        } finally {
            tx.finish();
        }
    }
}
