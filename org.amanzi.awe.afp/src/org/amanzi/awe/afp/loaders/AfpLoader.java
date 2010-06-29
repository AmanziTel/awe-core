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
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.AbstractLoader;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * AFP files loader
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AfpLoader extends AbstractLoader {

    public static final Integer CELL_IND = 1;
    private final ControlFile file;
    private final String rootName;
    private Node afpRoot;
    private Node afpCell;

    /**
     * Instantiates a new afp loader.
     * 
     * @param file the file
     * @param service the service
     */
    public AfpLoader(final String rootName, final ControlFile file, final GraphDatabaseService service) {
        this.rootName = rootName;
        this.file = file;
        this.neo = service;

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
                    NodeTypes.AFP.setNodeType(node, neo);
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
            saveProperties();
        } finally {
            commit(false);
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



    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }

    @Override
    protected String getPrymaryType(Integer key) {
        if (key.equals(CELL_IND)) {
            return NodeTypes.M.getId();
        }
        return null;
    }

    @Override
    protected Node getStoringNode(Integer key) {
        if (key.equals(CELL_IND)) {
            return afpCell;
        }
        return null;
    }

    @Override
    protected void flushIndexes() {
    }
    @Override
    public Node[] getRootNodes() {
        return new Node[] {afpRoot};
    }

    /**
     * <p>
     * CellFileHandler handle import of Cell File
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public class CellFileHandler extends AbstractTxFileHandler {


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
                    Node node = service.createNode();
                    NodeTypes.M.setNodeType(node, service);
                    NeoUtils.setNodeName(node, siteName, service);
                    setIndexProperty(header, node, "site_name", siteName);
                    setIndexProperty(header, node, "sectorNo", sectorNo);
                    setIndexProperty(header, node, "nonrelevant", nonrelevant);
                    setIndexProperty(header, node, "numberoffreqenciesrequired", numberoffreqenciesrequired);
                    setIndexProperty(header, node, "numberoffrequenciesgiven", numberoffrequenciesgiven);
                    node.setProperty("frq", frq);
                    NeoUtils.addChild(rootNode, node, null, service);
//                    index(CELL_KEY, node);
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
