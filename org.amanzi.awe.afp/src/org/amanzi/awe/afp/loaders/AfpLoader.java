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
package org.amanzi.awe.afp.loaders;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.AbstractLoader;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * AFP files loader
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AfpLoader extends AbstractLoader {

    /** The Constant CELL_IND. */
    public static final Integer CELL_IND = 1;

    /** The file. */
    private final ControlFile file;

    /** The root name. */
    private final String rootName;

    /** The afp root. */
    private Node afpRoot;

    /** The afp cell. */
    private Node afpCell;

    private LuceneIndexService luceneInd;

    /**
     * Instantiates a new afp loader.
     * 
     * @param rootName the root name
     * @param file the file
     * @param service the service
     */
    public AfpLoader(final String rootName, final ControlFile file, final GraphDatabaseService service) {
        this.rootName = rootName;
        this.file = file;
        this.neo = service;
        luceneInd = NeoServiceProvider.getProvider().getIndexService();

    }


    /**
     * Define root.
     */
    protected void defineRoot() {
        RunnableWithResult<Node> creater = new RunnableWithResult<Node>() {

            private Node node = null;

            @Override
            public void run() {

                Transaction tx = neo.beginTx();
                try {
                    node = neo.createNode();
                    NodeTypes.NETWORK.setNodeType(node, neo);
                    NeoUtils.setNodeName(node, rootName, neo);
                    for (Map.Entry<String, String> entry : file.getPropertyMap().entrySet()) {
                        node.setProperty(entry.getKey(), entry.getValue());
                    }
                    tx.success();
                } finally {
                    tx.finish();
                }

            }

            @Override
            public Node getValue() {
                return node;
            }
        };
        String projectName = ApplicationGIS.getActiveProject().getName();
        afpRoot = NeoUtils.findorCreateRootInActiveProject(projectName, rootName, creater, neo);
    }

    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AfpLoader");
        try {
            defineRoot();
            if (file.getCellFile() != null) {
                loadCellFile(file.getCellFile(), monitor);
            }
            commit(true);
            if (file.getForbiddenFile() != null) {
                loadForbiddenFile(file.getForbiddenFile(),monitor);
            }            
            saveProperties();
        } finally {
            commit(false);
        }

    }


    /**
     * Load forbidden file.
     * 
     * @param forbiddenFile the forbidden file
     * @param monitor the monitor
     */
    private void loadForbiddenFile(File forbiddenFile, IProgressMonitor monitor) {
        Node afpForb = findRoot(forbiddenFile.getName());

    }

    /**
     * Find root.
     * 
     * @param name the name
     * @return the node
     */
    private Node findRoot(String name) {
        Transaction tx = neo.beginTx();
        try {
            // afpRoot
            return null;
        } finally {

        }
    }


    /**
     * Load cell file.
     * 
     * @param cellFile the cell file
     * @param monitor the monitor
     */
    private void loadCellFile(File cellFile, IProgressMonitor monitor) {
        // TODO define root of cell file. If we create virtual dataset for it what we should store
        // in main part?
        afpCell = afpRoot;
        CommonImporter importer = new CommonImporter(new CellFileHandler(afpCell, neo), new TxtFileImporter(cellFile));
        importer.process();
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
     * Gets the prymary type.
     * 
     * @param key the key
     * @return the prymary type
     */
    @Override
    protected String getPrymaryType(Integer key) {
        if (key.equals(CELL_IND)) {
            return NodeTypes.M.getId();
        }
        return null;
    }

    /**
     * Gets the storing node.
     * 
     * @param key the key
     * @return the storing node
     */
    @Override
    protected Node getStoringNode(Integer key) {
        if (key.equals(CELL_IND)) {
            return afpCell;
        }
        return null;
    }

    /**
     * Flush indexes.
     */
    @Override
    protected void flushIndexes() {
    }

    /**
     * Gets the root nodes.
     * 
     * @return the root nodes
     */
    @Override
    public Node[] getRootNodes() {
        return new Node[] {afpRoot};
    }

    private Node addChild(Node parent, NodeTypes type, String name, String indexName) {
        Node child = null;
        child = neo.createNode();
        child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type.getId());
        child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        luceneInd.index(child, NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, type), indexName);
        if (parent != null) {
            parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            debug("Added '" + name + "' as child of '" + parent.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        }
        return child;
    }
    /**
     * <p>
     * CellFileHandler handle import of Cell File
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class CellFileHandler extends AbstractTxFileHandler {


        /** The header. */
        private LinkedHashMap<String, Header> header;

        /**
         * Instantiates a new cell file handler.
         * 
         * @param rootNode the root node
         * @param service the service
         */
        public CellFileHandler(Node rootNode, GraphDatabaseService service) {
            super(rootNode, service);
            header = getHeaderMap(CELL_IND).headers;
        }

        /**
         * Store line.
         * 
         * @param line the line
         */
        @Override
        protected void storeLine(String line) {
            try {
                String[] field = line.split("\\s");
                int i = 0;
                String siteName = field[i++];
                Integer sectorNo = Integer.valueOf(field[i++]);
                Integer nonrelevant = Integer.valueOf(field[i++]);
                Integer numberoffreqenciesrequired = Integer.valueOf(field[i++]);
                Integer numberoffrequenciesgiven = Integer.valueOf(field[i++]);
                Integer[] frq = new Integer[numberoffrequenciesgiven];
                for (int j = 0; j < frq.length; j++) {
                    frq[j] = Integer.valueOf(field[i++]);
                }
                Transaction tx = service.beginTx();
                try {
                    Node site = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                    if (site==null){
                        site = addChild(afpCell, NodeTypes.SITE, siteName,siteName);
                    }
                    String sectorName = siteName + field[1];
                    Node sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
                    if (sector == null) {
                        sector = addChild(site, NodeTypes.SECTOR, sectorName, siteName);
                    }
                    setIndexProperty(header, sector, "nonrelevant", nonrelevant);
                    setIndexProperty(header, sector, "numberoffreqenciesrequired", numberoffreqenciesrequired);
                    setIndexProperty(header, sector, "numberoffrequenciesgiven", numberoffrequenciesgiven);
                    sector.setProperty("frq", frq);
                    tx.success();
                } finally {
                    tx.finish();
                }
            } catch (Exception e) {
                String errStr = String.format("Can't parse line: %s", line);
                AweConsolePlugin.error(errStr);
                Logger.getLogger(this.getClass()).error(errStr, e);
            }
        }

    }

}
