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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.afp.AfpNeighbourSubType;
import org.amanzi.awe.afp.files.ControlFile;
import org.amanzi.awe.afp.providers.AbstractTxFileHandler;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.utils.importer.CommonImporter;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.amanzi.neo.services.utils.Utils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
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
public class AfpLoader {

    /** The Constant CELL_IND. */
    public static final Integer CELL_IND = 1;

    /** The file. */
    private final ControlFile file;

    /** The root name. */
    private final String rootName;

    /** The afp root. */
    protected Node afpRoot;

    /** The afp cell. */
    private Node afpCell;
    protected Display display;
    /** The lucene ind. */
    private LuceneIndexService luceneInd;

    private NetworkService networkService;
    private NodeToNodeRelationModel n2nModel;

    private long started = System.currentTimeMillis();
    protected Node afpDataset = null;

    private IStatistic statistic;

    private NetworkModel networkModel;

    protected GraphDatabaseService neo;
    protected Transaction mainTx;
    protected int commitSize = 5000;

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
        luceneInd = NeoServiceProviderUi.getProvider().getIndexService();
        networkService = NeoServiceFactory.getInstance().getNetworkService();

    }

    /**
     * Define root.
     */
    protected void defineRoot() {
        defineRoot(null);
    }

    protected void defineRoot(String projectName) {
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
        if (projectName == null) {
            projectName = ApplicationGIS.getActiveProject().getName();
        }
        afpRoot = NeoUtils.findorCreateRootInActiveProject(projectName, rootName, creater, neo);
        statistic = StatisticManager.getStatistic(afpRoot);
        networkModel = new NetworkModel(afpRoot);

    }

    private boolean isMonitorCancelled(IProgressMonitor monitor) {
        if (monitor.isCanceled()) {
            mainTx.failure();
            commit(false);
            return true;
        }

        return false;
    }

    private Display currentDisplay = null;

    private final void runInDisplay(Runnable runnable) {
        if (display != null) {
            if (currentDisplay == null) {
                currentDisplay = PlatformUI.getWorkbench().getDisplay();
            }
            currentDisplay.asyncExec(runnable);
        } else {
            runnable.run();
        }
    }

    protected final void error(final String line) {
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(line);
            }
        });
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
                mainTx = neo.beginTx();
            } else {
                mainTx = null;
            }
        }
    }

    /**
     * Run.
     * 
     * @param monitor the monitor
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void run(IProgressMonitor monitor) throws IOException {
        monitor.beginTask("Load AFP data", 7);
        runAfpLoader(monitor, null);
    }

    public void runAfpLoader(IProgressMonitor monitor, String projectName) {
        if (file.getCellFile() == null) {
            error("Not found Cite file");
            return;
        }
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "AfpLoader");
        try {
            defineRoot(projectName);
            if (file.getCellFile() != null) {
                if (isMonitorCancelled(monitor))
                    return;
                loadCellFile(file.getCellFile(), false);
            }
            commit(true);
            monitor.worked(1);
            if (file.getForbiddenFile() != null) {
                if (isMonitorCancelled(monitor))
                    return;
                loadForbiddenFile(file.getForbiddenFile());
            }

            commit(true);
            monitor.worked(1);
            if (file.getNeighbourFile() != null) {
                if (isMonitorCancelled(monitor))
                    return;
                loadNeighbourFile(file.getNeighbourFile(), afpCell);
            }

            commit(true);
            monitor.worked(1);
            if (file.getExceptionFile() != null) {
                if (isMonitorCancelled(monitor))
                    return;
                loadExceptionFile(file.getExceptionFile(), afpCell);
            }
            commit(true);
            monitor.worked(1);
            if (file.getInterferenceFile() != null) {
                if (isMonitorCancelled(monitor))
                    return;
                loadInterferenceFile(file.getInterferenceFile(), afpCell);
            }
            commit(true);
            monitor.worked(1);

            statistic.save();
        } finally {
            commit(false);
        }

    }

    /**
     * @param interferenceFile
     * @param monitor
     */
    private void loadInterferenceFile(File interferenceFile, Node afpCell) {
        this.afpCell = afpCell;
        Node afpInterference = NeoUtils.findNeighbour(afpCell, interferenceFile.getName(), neo);
        if (afpInterference == null) {
            Transaction tx = neo.beginTx();
            try {
                afpInterference = neo.createNode();
                afpInterference.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.NEIGHBOUR.getId());
                AfpNeighbourSubType.INTERFERENCE.setTypeToNode(afpInterference, neo);
                afpInterference.setProperty(INeoConstants.PROPERTY_NAME_NAME, interferenceFile.getName());
                afpCell.createRelationshipTo(afpInterference, NetworkRelationshipTypes.INTERFERENCE_DATA);
                tx.success();
            } finally {
                tx.finish();
            }
        }
        CommonImporter importer = new CommonImporter(new InterferenceFileHandler(afpInterference, neo), new TxtFileImporter(
                interferenceFile));
        importer.process();
    }

    /**
     * @param exceptionFile
     * @param monitor
     */
    private void loadExceptionFile(File exceptionFile, Node afpCell) {
        this.afpCell = afpCell;
        n2nModel = networkModel.getException(exceptionFile.getName());
        CommonImporter importer = new CommonImporter(new ExceptionFileHandler(n2nModel, neo), new TxtFileImporter(exceptionFile));
        importer.process();
    }

    /**
     * Load neighbour file.
     * 
     * @param neighbourFile the neighbour file
     * @param monitor the monitor
     */
    public void loadNeighbourFile(File neighbourFile, Node afpCell) {
        this.afpCell = afpCell;
        n2nModel = networkModel.getNeighbours(neighbourFile.getName());
        CommonImporter importer = new CommonImporter(new NeighbourFileHandler(n2nModel, neo), new TxtFileImporter(neighbourFile));
        importer.process();
    }

    /**
     * Load forbidden file.
     * 
     * @param forbiddenFile the forbidden file
     * @param monitor the monitor
     */
    private void loadForbiddenFile(File forbiddenFile) {

        CommonImporter importer = new CommonImporter(new ForbiddenFileHandler(afpCell, neo), new TxtFileImporter(forbiddenFile));
        importer.process();
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
    protected void loadCellFile(File cellFile, boolean isNodeIdBased) {
        // TODO define root of cell file. If we create virtual dataset for it what we should store
        // in main part?
        afpCell = afpRoot;

        // create the virtual data set node
        // if(afpDataset != null) {
        // Transaction tx = this.neo.beginTx();
        // try {
        // Node child = this.neo.createNode();
        // child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.FREQ);
        // child.setProperty(INeoConstants.PROPERTY_NAME_NAME, (new Date()).toString());
        // afpDataset.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
        // tx.success();
        // prevFreqNode = child;
        // } finally {
        // tx.finish();
        // }
        //
        // // prevFreqNode = networkService.addFREQNode(afpDataset, (new Date()).toString(), null);
        //
        // AweConsolePlugin.info("Created new Freq plan node ");
        // }
        CommonImporter importer = new CommonImporter(new CellFileHandler(afpCell, neo, isNodeIdBased),
                new TxtFileImporter(cellFile));
        importer.process();
    }

    /**
     * Flush indexes.
     */
    protected void flushIndexes() {
    }

    /**
     * Gets the root nodes.
     * 
     * @return the root nodes
     */
    public Node[] getRootNodes() {
        return new Node[] {afpCell};
    }

    /**
     * Adds the child.
     * 
     * @param parent the parent
     * @param type the type
     * @param name the name
     * @param indexName the index name
     * @return the node
     */
    private Node addChild(Node parent, NodeTypes type, String name, String indexName) {
        Node child = null;
        child = neo.createNode();
        child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type.getId());
        child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        luceneInd.index(child, NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, type), indexName);
        if (parent != null) {
            parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            // debug("Added '" + name + "' as child of '" +
            // parent.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        }
        return child;
    }

    /**
     * @param sector the sector whose proxy is to be created
     * @param lastSector sector whose proxy was created last
     * @param rootNode the list(neighbours/interference/exception) node corresponding to this proxy
     * @param type the relationship type for proxySector
     * @return
     */
    private Node createProxySector(Node sector, Node lastSector, Node rootNode, NetworkRelationshipTypes type) {

        Node proxySector;

        Transaction tx = neo.beginTx();
        try {
            proxySector = neo.createNode();
            String sectorName = sector.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            String proxySectorName = NeoUtils.getNodeName(rootNode) + "/" + sectorName;
            proxySector.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS.getId());
            proxySector.setProperty(INeoConstants.PROPERTY_NAME_NAME, proxySectorName);

            // TODO: bad way. fix it to check lastSector.equals(rootNode)
            if (lastSector == null || lastSector.equals(rootNode))
                rootNode.createRelationshipTo(proxySector, NetworkRelationshipTypes.CHILD);
            else
                lastSector.createRelationshipTo(proxySector, NetworkRelationshipTypes.NEXT);

            sector.createRelationshipTo(proxySector, type);

            luceneInd.index(proxySector, NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME,
                    NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);

            tx.success();
        } finally {
            tx.finish();
        }

        return proxySector;
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
        private long time;
        boolean isNodeIdBased;
        private FrequencyPlanModel planModel;

        /**
         * Instantiates a new cell file handler.
         * 
         * @param rootNode the root node
         * @param service the service
         */
        public CellFileHandler(Node rootNode, GraphDatabaseService service, boolean isNodeIdBased) {
            super(rootNode, service);
            time = System.currentTimeMillis();
            planModel = networkModel.getFrequencyModel(Long.toString(time));
            this.isNodeIdBased = isNodeIdBased;
        }

        private String getSampleSectorName(String siteName, String sectorNo) {
            Node site = luceneInd.getSingleNode(
                    NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
            if (site != null) {
                // find a sector under this site
                for (Relationship r : site.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                    Node n = r.getEndNode();
                    if (n.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NodeTypes.SECTOR.getId())) {
                        // get sector name stored
                        String name = (String)n.getProperty(INeoConstants.PROPERTY_NAME_NAME, "");

                        return name;
                    }
                }
            }
            return null;
        }

        private boolean checkConvertSectorNo2Char(String sectorName) {
            if (sectorName != null) {
                char secno = sectorName.charAt(sectorName.length() - 1);
                return Character.isLetter(secno);
            }
            return false;
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
                if (isNodeIdBased) {
                    long nodeId = Long.parseLong(field[i++]);
                    Node trxNode = service.getNodeById(nodeId);
                    // Node sector = service.getNodeById(nodeId);
                    String trxName = field[i++];
                    if (trxName.contains("-"))
                        trxName = trxName.split("-")[0];
                    Integer nonrelevant = Integer.valueOf(field[i++]);
                    Integer numberoffreqenciesrequired = Integer.valueOf(field[i++]);
                    Integer numberoffrequenciesgiven = Integer.valueOf(field[i++]);
                    int frq = 0;
                    // for (int j = 0; j < frq.length; j++) {
                    frq = Integer.valueOf(field[i++]);
                    // }

                    // Traverser traverser = Utils.getTrxTraverser(sector);
                    Transaction tx = service.beginTx();
                    try {
                        // for (Node trx: traverser){
                        // if (trx.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(trxName)){
                        // trxNode = trx;
                        // break;
                        // }
                        // }

                        // if (trxNode == null){
                        // trxNode = addChild(sector, NodeTypes.TRX, trxName, trxName);
                        // }
                        trxNode.setProperty("nonrelevant", nonrelevant);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "nonrelevant", nonrelevant);
                        trxNode.setProperty("numberoffreqenciesrequired", numberoffreqenciesrequired);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "numberoffreqenciesrequired",
                                numberoffreqenciesrequired);
                        trxNode.setProperty("numberoffrequenciesgiven", numberoffrequenciesgiven);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "nonrelevant", nonrelevant);
                        // String band = (String)sector.getProperty("band", "");
                        // if (band.contains(" "))
                        // band = band.split("\\s")[1];
                        AweConsolePlugin.info("Adding TRX and FREQ for sector node id " + nodeId);

                        // for (int j = 0; j < frq.length; j++){
                        Node planNode = planModel.getPlanNode(trxNode);

                        planNode.setProperty(INeoConstants.AFP_PROPERTY_ORIGINAL_NAME, false);
                        // }

                        tx.success();
                    } finally {
                        tx.finish();
                    }

                }

                else {
                    String sectorFullName = field[i++];
                    String siteName = sectorFullName.substring(0, sectorFullName.length() - 1);
                    String sectorNo = sectorFullName.substring(sectorFullName.length() - 1);
                    String trxName = field[i++];
                    Integer nonrelevant = Integer.valueOf(field[i++]);
                    Integer numberoffreqenciesrequired = Integer.valueOf(field[i++]);
                    Integer numberoffrequenciesgiven = Integer.valueOf(field[i++]);
                    Integer[] frq = new Integer[numberoffrequenciesgiven];
                    for (int j = 0; j < frq.length; j++) {
                        frq[j] = Integer.valueOf(field[i++]);
                    }

                    boolean convertSectorNo2Char = false;
                    boolean reduceSectorName = false;
                    int sectorNameLength = 0;
                    String sampleSectorName = getSampleSectorName(siteName, sectorNo);
                    if (sampleSectorName != null) {
                        convertSectorNo2Char = checkConvertSectorNo2Char(sampleSectorName);

                        if (sampleSectorName.length() < (siteName + sectorNo).length()) {
                            reduceSectorName = true;
                            sectorNameLength = sampleSectorName.length();
                        }
                    }

                    Transaction tx = service.beginTx();
                    try {
                        Node site = luceneInd.getSingleNode(
                                NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE),
                                siteName);
                        if (site == null) {
                            site = addChild(afpCell, NodeTypes.SITE, siteName, siteName);
                        }
                        String sectorName;
                        if (convertSectorNo2Char) {
                            char c = field[0].charAt(sectorFullName.length() - 1);
                            c = (char)((c - '1') + 'A');
                            sectorName = siteName + c;
                        } else {
                            sectorName = sectorFullName;// siteName + field[1];
                        }
                        if (reduceSectorName) {
                            sectorName = sectorName.substring(sectorName.length() - sectorNameLength);
                        }
                        Node sector = luceneInd.getSingleNode(
                                NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR),
                                sectorName);
                        if (sector == null) {
                            sector = addChild(site, NodeTypes.SECTOR, sectorName, sectorName);
                        }

                        Node trxNode = null;
                        Traverser traverser = Utils.getTrxTraverser(sector);
                        for (Node trx : traverser) {
                            if (trx.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(trxName)) {
                                trxNode = trx;
                                break;
                            }
                        }

                        if (trxNode == null) {
                            trxNode = addChild(sector, NodeTypes.TRX, trxName, trxName);
                        }
                        trxNode.setProperty("nonrelevant", nonrelevant);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "nonrelevant", nonrelevant);
                        trxNode.setProperty("numberoffreqenciesrequired", numberoffreqenciesrequired);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "numberoffreqenciesrequired",
                                numberoffreqenciesrequired);
                        trxNode.setProperty("numberoffrequenciesgiven", numberoffrequenciesgiven);
                        statistic.indexValue(rootName, NodeTypes.TRX.getId(), "nonrelevant", nonrelevant);
                        String band = (String)sector.getProperty("band", "");
                        if (band.contains(" "))
                            band = band.split("\\s")[1];
                        AweConsolePlugin.info("Adding TRX and FREQ for sector " + sectorName);

                        for (int j = 0; j < frq.length; j++) {
                            Node planNode = planModel.getPlanNode(trxNode);
                        }
                        // Traverser traverser = Utils.getTrxTraverser(sector);
                        // int j = 0;
                        // for (Node trx: traverser){
                        // if(j >= frq.length)
                        // break;
                        // Node planNode = Utils.createPlan(trx, new int[]{j}, Long.toString(time),
                        // service);
                        // if(prevFreqNode != null) {
                        // prevFreqNode.createRelationshipTo(planNode,
                        // NetworkRelationshipTypes.NEXT);
                        // }
                        // prevFreqNode = planNode;
                        // j++;
                        // }
                        // for(int j=0; j< frq.length;j++) {
                        //
                        // Node trx = Utils.findOrCreateCarrier(sector, j, band,
                        // service);//networkService.getTRXNode(sector, ""+j, 0);
                        // // prevFreqNode = networkService.addFREQNode(trx, ""+frq[j],
                        // prevFreqNode);
                        // Node planNode = Utils.createPlan(trx, new int[]{j}, Long.toString(time),
                        // service);
                        // if(prevFreqNode != null) {
                        // prevFreqNode.createRelationshipTo(planNode,
                        // NetworkRelationshipTypes.NEXT);
                        // }
                        // prevFreqNode = planNode;
                        // }
                        tx.success();
                    } finally {
                        tx.finish();
                    }
                }
            } catch (Exception e) {
                String errStr = String.format("Can't parse line: %s", line);
                AweConsolePlugin.error(errStr);
                Logger.getLogger(this.getClass()).error(errStr, e);
            }
        }

    }

    /**
     * <p>
     * ForbiddenFileHandler handle import of Forbidden File
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class ForbiddenFileHandler extends AbstractTxFileHandler {

        /** The header. */

        /**
         * Instantiates a new cell file handler.
         * 
         * @param rootNode the root node
         * @param service the service
         */
        public ForbiddenFileHandler(Node rootNode, GraphDatabaseService service) {
            super(rootNode, service);
        }

        /**
         * Store line.
         * 
         * @param line the line
         */
        @Override
        protected void storeLine(String line) {
            try {
                // TODO debug - in example do not have necessary file
                String[] field = line.split("\\s");
                int i = 0;
                String siteName = field[i++];
                Integer sectorNo = Integer.valueOf(field[i++]);
                Integer numberofforbidden = Integer.valueOf(field[i++]);
                Integer[] forbList = new Integer[numberofforbidden];
                for (int j = 0; j < forbList.length; j++) {
                    forbList[j] = Integer.valueOf(field[i++]);
                }
                String sectorName = siteName + field[1];
                Transaction tx = service.beginTx();
                try {
                    Node sector = luceneInd.getSingleNode(
                            NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR),
                            sectorName);
                    if (sector == null) {
                        error("Forbidden Frquencies File. Not found in network sector " + sectorName);
                        return;
                    }
                    statistic.indexValue(rootName, NodeTypes.SECTOR.getId(), "numberofforbidden", numberofforbidden);
                    sector.setProperty("numberofforbidden", numberofforbidden);
                    statistic.indexValue(rootName, NodeTypes.SECTOR.getId(), "forb_fr_list", forbList);
                    sector.setProperty("forb_fr_list", forbList);
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

    /**
     * <p>
     * NeighbourFileHandler handle import of Forbidden File
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class NeighbourFileHandler extends AbstractTxFileHandler {

        /** The serve. */
        private Node serve;
        private String neighName;
        private Node lastSector;
        private final NodeToNodeRelationModel n2nModel;

        /**
         * Instantiates a new cell file handler.
         * 
         * @param n2nModel the root node
         * @param service the service
         */
        public NeighbourFileHandler(NodeToNodeRelationModel n2nModel, GraphDatabaseService service) {
            super(n2nModel.getNetworkNode(), service);
            this.n2nModel = n2nModel;
            neighName = n2nModel.getName();
        }

        /**
         * Inits the.
         */
        @Override
        public void init() {
            super.init();
            serve = null;
            lastSector = rootNode;

        }

        @Override
        public void finish() {
            super.finish();
            statistic.setTypeCount(neighName, NodeTypes.PROXY.getId(), n2nModel.getProxyCount());
            statistic.setTypeCount(neighName, NodeTypes.NODE_NODE_RELATIONS.getId(), n2nModel.getRelationCount());
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
                String name = field[i++].trim();
                String siteName = field[i++];
                Integer sectorNo = Integer.valueOf(field[i++]);
                if (name.equals("CELL")) {
                    serve = defineSector(siteName, field[2]);
                } else {
                    if (serve == null) {
                        error("Not found serve cell for neighbours: " + line);
                        return;
                    } else {
                        Node ngh = defineSector(siteName, field[2]);
                        if (ngh != null) {
                            n2nModel.getRelation(serve, ngh);
                        }
                    }
                }
            } catch (Exception e) {
                String errStr = String.format("Can't parse line: %s", line);
                AweConsolePlugin.error(errStr);
                Logger.getLogger(this.getClass()).error(errStr, e);
            }
        }

        /**
         * Define serve.
         * 
         * @param siteName the site name
         * @param field the field
         * @return the node
         */
        private Node defineSector(String siteName, String field) {
            String sectorName = siteName.trim() + field.trim();
            Node sector = luceneInd.getSingleNode(
                    NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error("Neighbours File. Not found sector " + sectorName);
            }
            return sector;
        }

    }

    /**
     * <p>
     * InterferenceFileHandler handle import of Interference File
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class InterferenceFileHandler extends AbstractTxFileHandler {

        /** The serve. */
        private Node serve;
        private Set<String> numericProp;
        private Set<String> allProp;
        private String interferenceName;
        private Node lastSector;

        /**
         * Instantiates a new cell file handler.
         * 
         * @param rootNode the root node
         * @param service the service
         */
        public InterferenceFileHandler(Node rootNode, GraphDatabaseService service) {
            super(rootNode, service);
            interferenceName = NeoUtils.getNodeName(rootNode);
        }

        /**
         * Inits the.
         */
        @Override
        public void init() {
            super.init();
            serve = null;
            numericProp = new HashSet<String>();
            allProp = new HashSet<String>();
        }

        @Override
        public void finish() {
            super.finish();
            rootNode.setProperty(INeoConstants.LIST_NUMERIC_PROPERTIES, numericProp.toArray(new String[0]));
            rootNode.setProperty(INeoConstants.LIST_NUMERIC_PROPERTIES, allProp.toArray(new String[0]));
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
                String name = field[i++].trim();
                if (name.equals("SUBCELL")) {
                    String sectorName = field[6];
                    String sectorNo = sectorName.substring(sectorName.length() - 1);
                    if (!sectorNo.matches("\\d")) {
                        int diff = Character.getNumericValue(sectorName.charAt(sectorName.length() - 1))
                                - Character.getNumericValue('A') + 1;
                        sectorName = sectorName.substring(0, sectorName.length() - 1) + diff;
                    }
                    serve = defineServe(sectorName);
                    serve.setProperty("nonrelevant1", Integer.valueOf(field[i++]));
                    serve.setProperty("nonrelevant2", Integer.valueOf(field[i++]));
                    serve.setProperty("total-cell-area", Double.valueOf(field[i++]));
                    serve.setProperty("total-cell-traffic", Double.valueOf(field[i++]));
                    serve.setProperty("numberofinterferers", Integer.valueOf(field[i++]));
                } else if (name.equals("INT")) {
                    if (serve == null) {
                        error("Not found serve cell for neighbours: " + line);
                        return;
                    } else {
                        String sectorName = field[7];
                        if (!sectorName.substring(sectorName.length() - 1).matches("\\d")) {
                            int sectorNo = Character.getNumericValue(sectorName.charAt(sectorName.length() - 1))
                                    - Character.getNumericValue('A') + 1;
                            sectorName = sectorName.substring(0, sectorName.length() - 1) + sectorNo;
                        }
                        Relationship relation = defineInterferer(sectorName);
                        relation.setProperty("nonrelevant1", Integer.valueOf(field[i++]));
                        relation.setProperty("nonrelevant2", Integer.valueOf(field[i++]));
                        relation.setProperty("co-channel-interf-area", Double.valueOf(field[i++]));
                        relation.setProperty("co-channel-interf-traffic", Double.valueOf(field[i++]));
                        relation.setProperty("adj-channel-interf-area", Double.valueOf(field[i++]));
                        relation.setProperty("adj-channel-interf-traffic", Double.valueOf(field[i++]));
                    }
                }
            } catch (Exception e) {
                String errStr = String.format("Can't parse line: %s", line);
                AweConsolePlugin.error(errStr);
                Logger.getLogger(this.getClass()).error(errStr, e);
            }
        }

        /**
         * Define neigh.
         * 
         * @param siteName the site name
         * @param field the field
         */
        private Relationship defineInterferer(String sectorName) {
            String proxySectorName = interferenceName + "/" + sectorName;

            Node proxySector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(afpCell,
                    INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
            if (proxySector == null) {
                Node sector = luceneInd.getSingleNode(
                        NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR),
                        sectorName);
                if (sector == null) {
                    error(". Interference File. Not found sector " + sectorName);
                    return null;
                }
                proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.INTERFERENCE);
                lastSector = proxySector;
            }

            Relationship relation = serve.createRelationshipTo(proxySector, NetworkRelationshipTypes.INTERFERS);
            return relation;
        }

        /**
         * Define serve.
         * 
         * @param siteName the site name
         * @param field the field
         * @return the node
         */
        private Node defineServe(String sectorName) {
            String proxySectorName = interferenceName + "/" + sectorName;

            Node proxySector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(afpCell,
                    INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR_SECTOR_RELATIONS), proxySectorName);
            if (proxySector == null) {
                Node sector = luceneInd.getSingleNode(
                        NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR),
                        sectorName);
                if (sector == null) {
                    error(". Interference File. Not found sector " + sectorName);
                    return null;
                }
                proxySector = createProxySector(sector, lastSector, rootNode, NetworkRelationshipTypes.INTERFERENCE);
                lastSector = proxySector;
            }

            return proxySector;
        }

    }

    /**
     * <p>
     * NeighbourFileHandler handle import of Forbidden File
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class ExceptionFileHandler extends AbstractTxFileHandler {

        /** The serve. */
        private Node serve;
        private String exceptionName;
        private final NodeToNodeRelationModel n2nModel;

        /**
         * Instantiates a new cell file handler.
         * 
         * @param n2nModel the root node
         * @param service the service
         */
        public ExceptionFileHandler(NodeToNodeRelationModel n2nModel, GraphDatabaseService service) {
            super(n2nModel.getNetworkNode(), service);
            this.n2nModel = n2nModel;
            exceptionName = n2nModel.getName();
        }

        /**
         * Inits the.
         */
        @Override
        public void init() {
            super.init();
            serve = null;
        }

        @Override
        public void finish() {
            super.finish();
            statistic.setTypeCount(exceptionName, NodeTypes.PROXY.getId(), n2nModel.getProxyCount());
            statistic.setTypeCount(exceptionName, NodeTypes.NODE_NODE_RELATIONS.getId(), n2nModel.getRelationCount());
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
                String sectorName = siteName + field[1];
                serve = defineSector(sectorName);
                if (serve == null) {
                    return;
                }
                siteName = field[i++];
                sectorNo = Integer.valueOf(field[i++]);
                sectorName = siteName + field[3];
                Node neigh = defineSector(sectorName);
                if (neigh == null) {
                    return;
                }
                Relationship relation = n2nModel.getRelation(serve, neigh);
                String fl = field[i++];
                relation.setProperty("new_spacing", fl);
                statistic.indexValue(exceptionName, NodeTypes.NODE_NODE_RELATIONS.getId(), "new_spacing", fl);
            } catch (Exception e) {
                String errStr = String.format("Can't parse line: %s", line);
                AweConsolePlugin.error(errStr);
                Logger.getLogger(this.getClass()).error(errStr, e);
            }
        }

        /**
         * Define Exception.
         * 
         * @param siteName the site name
         * @param field the field
         */
        private Node defineSector(String sectorName) {
            Node sector = luceneInd.getSingleNode(
                    NeoUtils.getLuceneIndexKeyByProperty(afpCell, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorName);
            if (sector == null) {
                error("Exception File. Not found sector " + sectorName);
            }
            return sector;
        }

    }

}
