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

package org.amanzi.awe.afp.ericsson.saver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.CountableParameters;
import org.amanzi.awe.afp.ericsson.IParameters;
import org.amanzi.awe.afp.ericsson.IRecords;
import org.amanzi.awe.afp.ericsson.Parameters;
import org.amanzi.awe.afp.ericsson.RIRRecords;
import org.amanzi.awe.afp.ericsson.parser.MainRecord.Record;
import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.CoordinatedNode;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.utils.Pair;
import org.amanzi.neo.services.utils.Utils;
import org.apache.commons.lang.ObjectUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import com.vividsolutions.jts.geom.Coordinate;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * BAR RIR saver
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class BarRirSaver extends AbstractHeaderSaver<RecordTransferData> {

    /** The admin values. */
    AdminValues adminValues = null;

    /** The percentile value. */
    Integer percentileValue = null;

    /** The serv cells. */
    Map<String, ServCell> servCells = new HashMap<String, ServCell>();

    /** The rir cells. */
    Map<String, CellRirData> rirCells = new HashMap<String, CellRirData>();

    /** The network model. */
    private NetworkModel networkModel;

    /** The interf model. */
    private NodeToNodeRelationModel interfModel;

    /** The shadow model. */
    private NodeToNodeRelationModel shadowModel;

    /** The first file name. */
    private String firstFileName;

    /**
     * Save.
     * 
     * @param element the element
     */
    @Override
    public void save(RecordTransferData element) {
        if (firstFileName == null) {
            firstFileName = element.getFileName();
        }
        IRecords type = element.getRecord().getEvent().getType();
        if (type instanceof BARRecords) {
            switch ((BARRecords)type) {
            case ADMINISTRATIVE:
                storeAdminValues(element);
                return;
            case ACTIVE_BALIST_RECORDING_CELL_DATA:
                handleCellData(element);
                return;
            case ACTIVE_BALIST_RECORDING_NEIGHBOURING_CELL_DATA:
                handleNeighbour(element);
                return;
            default:
                return;
            }
        } else if (type instanceof RIRRecords) {
            switch ((RIRRecords)type) {
            case ADMINISTRATIVE:
                storeRirAdminValues(element);
                return;
            case RADIO_INTERFERENCE_RECORDING_CELL_DATA:
                handleRirCellData(element);
                return;
            default:
                return;
            }
        }
    }

    /**
     * Store rir admin values.
     * 
     * @param element the element
     */
    private void storeRirAdminValues(RecordTransferData element) {
        if (percentileValue == null) {
            percentileValue = getInteger(element.getRecord().getEvent(), Parameters.PERCENTILE_VALUE);
        }
    }

    /**
     * Handle rir cell data.
     * 
     * @param element the element
     */
    private void handleRirCellData(RecordTransferData element) {
        Record rec = element.getRecord().getEvent();
        String cellName = getString(rec, Parameters.CELL_NAME);
        if (cellName == null) {
            error("Cellname is not found");
            return;
        }
        Integer count = getInteger(rec, Parameters.NUMBER_OF_FREQUENCIES);
        if (count == null) {
            return;
        }
        CellRirData cell = getRirCell(cellName);
        for (int i = 1; i <= count; i++) {
            Integer arfcn = getInteger(rec, new CountableParameters(Parameters.ARFCN, i));
            Integer avemedian = getInteger(rec, new CountableParameters(Parameters.AVMEDIAN, i));
            Integer avpercentile = getInteger(rec, new CountableParameters(Parameters.AVPERCENTILE, i));
            Integer noofmeas = getInteger(rec, new CountableParameters(Parameters.NOOFMEAS, i));
            if (arfcn == null || avemedian == null || avpercentile == null || noofmeas == null) {
                error("incorect rir data");
            }
            cell.addRirData(i, arfcn, avemedian, avpercentile, noofmeas);
        }
    }

    /**
     * Gets the rir cell.
     * 
     * @param cellName the cell name
     * @return the rir cell
     */
    private CellRirData getRirCell(String cellName) {
        CellRirData result = rirCells.get(cellName);
        if (result == null) {
            result = new CellRirData(cellName);
            rirCells.put(cellName, result);
        }
        return result;
    }

    /**
     * Handle neighbour.
     * 
     * @param element the element
     */
    private void handleNeighbour(RecordTransferData element) {
        Record rec = element.getRecord().getEvent();
        String cellName = getString(rec, Parameters.CELL_NAME);
        if (cellName == null) {
            error("Cellname is not found");
            return;
        }
        Integer chGr = getInteger(rec, Parameters.CHGR);
        if (chGr == null) {
            error("chGr is not found");
        }
        Integer bsic = getInteger(rec, Parameters.BSIC);
        Integer arfcn = getInteger(rec, Parameters.ARFCN);
        if (bsic == null || arfcn == null) {
            error("Some of neighbohur id values is null");
            return;
        }
        ServCell cell = getServCell(cellName, chGr);
        InterfCell neigh = cell.getInterfCell(bsic, arfcn);
        neigh.addReparfcn(getInteger(rec, Parameters.REPARFCN));
        neigh.addTimesrelss(getInteger(rec, Parameters.TIMESRELSS));
        neigh.addTimesrelss2(getInteger(rec, Parameters.TIMESRELSS2));
        neigh.addTimesabss(getInteger(rec, Parameters.TIMESABSS));
    }

    /**
     * Handle cell data.
     * 
     * @param element the element
     */
    private void handleCellData(RecordTransferData element) {
        Record rec = element.getRecord().getEvent();
        String cellName = getString(rec, Parameters.CELL_NAME);
        if (cellName == null) {
            error("Cellname is not found");
            return;
        }
        Integer chGr = getInteger(rec, Parameters.CHGR);
        if (chGr == null) {
            error("chGr is not found");
        }
        ServCell cell = getServCell(cellName, chGr);
        cell.addRep(getInteger(rec, Parameters.REP));
    }

    /**
     * Gets the serv cell.
     * 
     * @param cellName the cell name
     * @param chGr the ch gr
     * @return the serv cell
     */
    private ServCell getServCell(String cellName, Integer chGr) {

        ServCell result = servCells.get(cellName);
        if (result == null) {
            result = new ServCell(cellName, chGr);
            servCells.put(cellName, result);
        }
        if (!ObjectUtils.equals(chGr, result.chgr)) {
            // error(String.format("Different CHGR (%s and %s) in cell name %s", chGr, result.chgr,
            // cellName));
        }
        return result;
    }

    /**
     * Gets the string.
     * 
     * @param record the record
     * @param parameter the parameter
     * @return the string
     */
    private String getString(Record record, Parameters parameter) {
        Object val = record.getProperties().get(parameter);
        if (val == null) {
            return null;
        }
        if (val instanceof byte[]) {
            return getString((byte[])val);
        } else {
            error("Incorrect value in records :" + val);
            return null;
        }
    }

    /**
     * Store admin values.
     * 
     * @param element the element
     */
    private void storeAdminValues(RecordTransferData element) {
        Record rec = element.getRecord().getEvent();
        Integer abss = getInteger(rec, Parameters.ABSS);
        Integer relssPM = getInteger(rec, Parameters.RELSS_PLUS_MINUS);
        Integer relss = getInteger(rec, Parameters.RELSS);
        Integer relss2pm = getInteger(rec, Parameters.RELSS2_PLUS_MINUS);
        Integer relss2 = getInteger(rec, Parameters.RELSS2);
        Integer rectime = getInteger(rec, Parameters.RECTIME);
        if (abss == null || relssPM == null || relss == null || relss2pm == null || relss2 == null || rectime == null) {
            error("Some admin values is missing");
            return;
        }
        if (adminValues == null) {
            adminValues = new AdminValues(abss, relssPM, relss, relss2pm, relss2, rectime);
        }
        if (!ObjectUtils.equals(abss, adminValues.abss)) {
            error(String.format("Parameter %s=%s is differ then stored values(=%s)", Parameters.ABSS, abss, adminValues.abss));
            return;
        }
        if (!ObjectUtils.equals(relssPM, adminValues.relssPM)) {
            error(String.format("Parameter %s=%s is differ then stored values(=%s)", Parameters.RELSS_PLUS_MINUS, relssPM, adminValues.relssPM));
            return;
        }
        if (!ObjectUtils.equals(relss, adminValues.relss)) {
            error(String.format("Parameter %s=%s is differ then stored values(=%s)", Parameters.RELSS, relss, adminValues.relss));
            return;
        }
        if (!ObjectUtils.equals(relss2pm, adminValues.relss2PM)) {
            error(String.format("Parameter %s=%s is differ then stored values(=%s)", Parameters.RELSS2_PLUS_MINUS, relss2pm, adminValues.relss2PM));
            return;
        }
        if (!ObjectUtils.equals(relss2, adminValues.relss2)) {
            error(String.format("Parameter %s=%s is differ then stored values(=%s)", Parameters.RELSS2, relss2, adminValues.relss2));
            return;
        }
        adminValues.addRecTime(rectime);

    }

    /**
     * Gets the integer.
     * 
     * @param record the record
     * @param parameter the parameter
     * @return the integer
     */
    private Integer getInteger(Record record, IParameters parameter) {
        Object val = record.getProperties().get(parameter);
        if (val == null) {
            return null;
        }
        if (val instanceof byte[]) {
            return getInteger((byte[])val);
        } else {
            error("Incorrect value in records :" + val);
            return null;
        }
    }

    /**
     * Gets the integer.
     * 
     * @param val the val
     * @return the integer
     */
    private Integer getInteger(byte[] val) {
        if (val.length == 0) {
            return null;
        }
        int res = 0;
        for (int i = val.length - 1; i >= 0; i--) {
            int c = (0x000000FF & ((int)val[i]));
            res = (res << 8) + c;
            // res += c * (Math.pow(256, i));
        }
        return res;
    }

    /**
     * Gets the meta data.
     * 
     * @return the meta data
     */
    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

    /**
     * Fill root node.
     * 
     * @param rootNode the root node
     * @param element the element
     */
    @Override
    protected void fillRootNode(Node rootNode, RecordTransferData element) {
    }

    /**
     * Finish up.
     * 
     * @param element the element
     */
    @Override
    public void finishUp(RecordTransferData element) {
        try {
            createMatrix();
            createShadow();
            createTriangulation();
        } finally {
            super.finishUp(element);
        }
    }

    /**
     *
     */
    private void createTriangulation() {
        Set<NodeToNodeRelationModel> models = networkModel.findAllN2nModels(NodeToNodeTypes.NEIGHBOURS);
        for (NodeToNodeRelationModel model : models) {
            createTriangulation(model);
        }

    }

    private void createTriangulation(NodeToNodeRelationModel model) {
        NodeToNodeRelationModel trModel = networkModel.getTriangulation(getTriangulationName(model));
        for (Node proxyServ : model.getServTraverser(null).nodes()) {
            Node sector = model.findNodeFromProxy(proxyServ);
            Coordinate c1 = networkModel.getCoordinateOfSector(sector);
            if (c1 == null) {
                continue;
            }
            List<CoordinatedNode> nodes = new ArrayList<CoordinatedNode>();
            for (Relationship rel : model.getOutgoingRelations(proxyServ)) {
                Node node = rel.getOtherNode(proxyServ);
                Node nd = model.findNodeFromProxy(node);
                Coordinate c = networkModel.getCoordinateOfSector(nd);
                if (c == null) {
                    continue;
                }
                nodes.add(new CoordinatedNode(nd, c));
            }
            if (nodes.size() < 3) {
                continue;
            }
            sortTriangl(nodes);
            Node lastNode = nodes.get(nodes.size() - 1).getNode();
            for (CoordinatedNode node : nodes) {
                if (lastNode != null) {
                    Relationship rel = trModel.getRelation(lastNode, node.getNode());
                    updateProperty(trModel.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", new Double(0.01d));
                    updateProperty(trModel.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", new Double(0d));
                    updateTx(2, 4);
                }
                lastNode = node.getNode();
            }
        }
        statistic.setTypeCount(trModel.getName(), NodeTypes.NODE_NODE_RELATIONS.getId(), trModel.getRelationCount());
        statistic.setTypeCount(trModel.getName(), NodeTypes.PROXY.getId(), trModel.getProxyCount());
        info(String.format("Created triangulation, number relations: %s", trModel.getRelationCount()));
    }

    /**
     * @param nodes
     */
    private void sortTriangl(List<CoordinatedNode> nodes) {
        final Comparator<CoordinatedNode> comp = new Comparator<CoordinatedNode>() {

            @Override
            public int compare(CoordinatedNode o1, CoordinatedNode o2) {
                int cp = new Double(o1.getCoord().x).compareTo(o2.getCoord().x);
                if (cp == 0) {
                    return new Double(o1.getCoord().y).compareTo(o2.getCoord().y);
                }
                return cp;
            }

        };
        Collections.sort(nodes, comp);
        final Coordinate c1 = nodes.get(0).getCoord();
        final Coordinate c2 = nodes.get(nodes.size() - 1).getCoord();
        final Double k = c2.x - c1.x == 0 ? Double.NaN : (c2.y - c1.y) / (c2.x - c1.x);
        final double b = k == Double.NaN ? Double.NaN : (c2.x * c1.y - c1.x * c2.y) / (c2.x - c1.x);
        Collections.sort(nodes, new Comparator<CoordinatedNode>() {

            @Override
            public int compare(CoordinatedNode o1, CoordinatedNode o2) {
                if (o1.getCoord().equals(c1)) {
                    return -1;
                }
                boolean isUpO1 = k == Double.NaN || o1.getCoord().y > k * o1.getCoord().x + b;
                boolean isUpO2 = k == Double.NaN || o2.getCoord().y > k * o2.getCoord().x + b;
                if (isUpO1) {
                    if (isUpO2) {
                        int cp = new Double(o1.getCoord().x).compareTo(o2.getCoord().x);
                        if (cp == 0) {
                            return new Double(o2.getCoord().y).compareTo(o1.getCoord().y);
                        }
                        return cp;
                    } else {
                        return -1;
                    }
                } else {
                    if (isUpO2) {
                        return 1;
                    } else {
                        int cp = new Double(o2.getCoord().x).compareTo(o1.getCoord().x);
                        if (cp == 0) {
                            return new Double(o1.getCoord().y).compareTo(o2.getCoord().y);
                        }
                        return cp;
                    }
                }
            }

        });
    }

    /**
     * @param model
     * @return
     */
    private String getTriangulationName(NodeToNodeRelationModel model) {
        return model.getName() + "triang";
    }

    /**
     *
     */
    private void createShadow() {
        shadowModel = networkModel.getShadowing(getShadowingMatrixName());
        TraversalDescription td = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                .evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path arg0) {
                        boolean continues = arg0.length() < 1 || NodeTypes.SECTOR != service.getNodeType(arg0.endNode());
                        boolean includes = !continues && arg0.endNode().hasProperty("bcch");
                        return Evaluation.of(includes, continues);
                    }
                });
        String indexName = Utils.getLuceneIndexKeyByProperty(rootNode, "bcch", NodeTypes.SECTOR);
        for (Node serv : td.traverse(rootNode).nodes()) {
            Coordinate c1 = networkModel.getCoordinateOfSector(serv);
            if (c1 == null) {
                continue;
            }
            Integer bcch = (Integer)serv.getProperty("bcch");
            Set<Node> candidates = new HashSet<Node>();

            for (Node candidate : service.getIndexService().getNodes(indexName, bcch)) {
                candidates.add(candidate);
            };
            // TODO optimize
            Node cnd1 = networkModel.getClosestNode(serv, candidates, 20000);
            if (cnd1 == null) {
                continue;
            }
            Relationship rel = shadowModel.getRelation(serv, cnd1);
            updateProperty(getShadowingMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", new Double(50d));
            updateProperty(getShadowingMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", new Double(0d));
            candidates.remove(cnd1);
            Node cnd2 = networkModel.getClosestNode(serv, candidates, 20000);
            if (cnd2 != null) {
                rel = shadowModel.getRelation(serv, cnd2);
                updateProperty(getShadowingMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", new Double(25d));
                updateProperty(getShadowingMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", new Double(0d));

            }
            updateTx(2, 2);
        }
        statistic.setTypeCount(getShadowingMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), shadowModel.getRelationCount());
        statistic.setTypeCount(getShadowingMatrixName(), NodeTypes.PROXY.getId(), shadowModel.getProxyCount());
        info(String.format("Created shadow, number relations: %s", shadowModel.getRelationCount()));
    }

    /**
     * Creates the matrix.
     */
    private void createMatrix() {
        if (adminValues == null) {
            return;
        }
        interfModel = networkModel.getInterferenceMatrix(getInterfMatrixName());

        for (ServCell cell : servCells.values()) {
            handleServCell(cell);
        }
        statistic.setTypeCount(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), interfModel.getRelationCount());
        statistic.setTypeCount(getInterfMatrixName(), NodeTypes.PROXY.getId(), interfModel.getProxyCount());
        info(String.format("Created IM, number relations: %s", interfModel.getRelationCount()));
    }

    /**
     * Gets the shadowing matrix name.
     * 
     * @return the shadowing matrix name
     */
    private String getShadowingMatrixName() {
        return "shadowing " + firstFileName;
    }

    /**
     * Gets the interf matrix name.
     * 
     * @return the interf matrix name
     */
    private String getInterfMatrixName() {
        return "interference " + firstFileName;
    }

    /**
     * Handle serv cell.
     * 
     * @param cell the cell
     */
    private void handleServCell(ServCell cell) {
        Node servSector = findNode(cell);
        if (servSector == null) {
            error(String.format("Sector %s not found", cell.cellName));
            return;
        }
        for (InterfCell neigh : cell.cells.values()) {
            handleNeighbohur(cell, servSector, neigh);
        }
    }

    /**
     * Handle neighbohur.
     * 
     * @param cell the cell
     * @param servSector the serv sector
     * @param neigh the neigh
     */
    private void handleNeighbohur(ServCell cell, Node servSector, InterfCell neigh) {
        Node neighSector = findNode(servSector, neigh);
        if (neighSector == null) {
            error(String.format("Sector (bsic=%s;arfcn=%s) not found", neigh.bsic, neigh.arfcn));
            return;
        }
        Double factorCo = getFactorCo(adminValues.relss);
        if (factorCo == null) {
            error("Incorreect relss=" + String.valueOf(adminValues.relss));
            return;
        }
        if (neigh.reparfcn == 0) {
            error("Incorreect reparfcn=0");
            return;
        }
        double impactCO = 100 * neigh.timesrelss / neigh.reparfcn * factorCo;
        Double factorAdj = getFactorAdj(adminValues.relss2);
        if (factorAdj == null) {
            error("Incorreect relss2=" + String.valueOf(adminValues.relss));
            return;
        }
        double impactAdj = 100 * neigh.timesrelss2 / neigh.reparfcn * factorAdj;
        Relationship rel = interfModel.getRelation(servSector, neighSector);
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "source", "IM source - Interference");
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", impactCO);
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", impactAdj);
    }

    /**
     * Gets the factor adj.
     * 
     * @param relss2 the relss2
     * @return the factor adj
     */
    private Double getFactorAdj(Integer relss2) {
        if (relss2 == null) {
            return null;
        } else if (relss2 == 6) {
            return 0.06d;
        } else if (relss2 == 3) {
            return 0.125d;
        } else if (relss2 == 0) {
            return 0.25d;
        } else {
            return null;
        }
    }

    /**
     * Gets the factor co.
     * 
     * @param relss the relss
     * @return the factor co
     */
    private Double getFactorCo(Integer relss) {
        if (relss == null) {
            return null;
        } else if (relss == 18) {
            return 0.25d;
        } else if (relss == 15) {
            return 0.5d;
        } else if (relss == 12) {
            return 1d;
        } else if (relss == 9) {
            return 2d;
        } else if (relss == 6) {
            return 4d;
        } else if (relss == 3) {
            return 8d;
        } else {
            return null;
        }
    }

    /**
     * Find node.
     * 
     * @param servSector the serv sector
     * @param neigh the neigh
     * @return the node
     */
    private Node findNode(Node servSector, InterfCell neigh) {
        return networkModel.getClosestSector(servSector, neigh.bsic, neigh.arfcn);
    }

    /**
     * Find node.
     * 
     * @param cell the cell
     * @return the node
     */
    private Node findNode(ServCell cell) {
        return networkModel.findSector(cell.cellName);
    }

    /**
     * Gets the root node type.
     * 
     * @return the root node type
     */
    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    /**
     * Gets the type id for gis count.
     * 
     * @param gis the gis
     * @return the type id for gis count
     */
    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }

    /**
     * Gets the string.
     * 
     * @param data the data
     * @return the string
     */
    private String getString(byte[] data) {
        if (data == null) {
            return null;
        }
        int len = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) {
                data[len] = data[i];
                len++;
            }
        }
        return new String(data, 0, len);
    }

    /**
     * Inits the.
     * 
     * @param element the element
     */
    @Override
    public void init(RecordTransferData element) {
        super.init(element);
        startMainTx(1000);
        adminValues = null;
        percentileValue = null;
        servCells.clear();
        firstFileName = null;
        networkModel = new NetworkModel(rootNode);

    }

    /**
     * <p>
     * The Wrapper for AdminValues
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class AdminValues {

        /** The abss. */
        Integer abss;

        /** The relss pm. */
        Integer relssPM;

        /** The relss. */
        Integer relss;

        /** The relss2 pm. */
        Integer relss2PM;

        /** The relss2. */
        Integer relss2;

        /** The rectime. */
        Integer rectime;

        /**
         * Instantiates a new admin values.
         * 
         * @param abss the abss
         * @param relssPM the relss pm
         * @param relss the relss
         * @param relss2pm the relss2pm
         * @param relss2 the relss2
         * @param rectime the rectime
         */
        public AdminValues(Integer abss, Integer relssPM, Integer relss, Integer relss2pm, Integer relss2, Integer rectime) {
            super();
            this.abss = abss;
            this.relssPM = relssPM;
            this.relss = relss;
            relss2PM = relss2pm;
            this.relss2 = relss2;
            this.rectime = rectime;
        }

        /**
         * Adds the rec time.
         * 
         * @param rectime the rectime
         */
        public void addRecTime(Integer rectime) {
            this.rectime += rectime;
        }

    }

    /**
     * <p>
     * Wrapper for cellname
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class ServCell {

        /**
         * Instantiates a new serv cell.
         * 
         * @param cellName the cell name
         * @param chGr the ch gr
         */
        public ServCell(String cellName, Integer chGr) {
            this.cellName = cellName;
            chgr = chGr;
        }

        /**
         * Gets the interf cell.
         * 
         * @param bsic the bsic
         * @param arfcn the arfcn
         * @return the interf cell
         */
        public InterfCell getInterfCell(Integer bsic, Integer arfcn) {
            if (bsic == null || arfcn == null) {
                return null;
            }
            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(bsic, arfcn);
            InterfCell result = cells.get(pair);
            if (result == null) {
                result = new InterfCell(bsic, arfcn);
                cells.put(pair, result);
            }
            return result;
        }

        /**
         * Adds the rep.
         * 
         * @param rep the rep
         */
        public void addRep(Integer rep) {
            if (rep == null) {
                return;
            }
            this.rep += rep;
        }

        /** The cell name. */
        String cellName;

        /** The chgr. */
        Integer chgr;

        /** The rep. */
        int rep = 0;

        /** The cells. */
        Map<Pair<Integer, Integer>, InterfCell> cells = new HashMap<Pair<Integer, Integer>, BarRirSaver.InterfCell>();

    }

    /**
     * <p>
     * Wrapper for interference cell
     * </p>
     * .
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class InterfCell {
        /** The bsic. */
        Integer bsic;

        /** The arfcn. */
        Integer arfcn;

        /** The reparfcn. */
        int reparfcn;

        /** The timesrelss. */
        int timesrelss;

        /** The timesrelss2. */
        int timesrelss2;

        /** The timesabss. */
        int timesabss;

        /**
         * Instantiates a new interf cell.
         * 
         * @param bsic the bsic
         * @param arfcn the arfcn
         */
        public InterfCell(Integer bsic, Integer arfcn) {
            this.bsic = bsic;
            this.arfcn = arfcn;
        }

        /**
         * Adds the timesabss.
         * 
         * @param timesabss the timesabss
         */
        public void addTimesabss(Integer timesabss) {
            if (timesabss != null) {
                this.timesabss += timesabss;
            }
        }

        /**
         * Adds the timesrelss2.
         * 
         * @param timesrelss2 the timesrelss2
         */
        public void addTimesrelss2(Integer timesrelss2) {
            if (timesrelss2 != null) {
                this.timesrelss2 += timesrelss2;
            }
        }

        /**
         * Adds the timesrelss.
         * 
         * @param timesrelss the timesrelss
         */
        public void addTimesrelss(Integer timesrelss) {
            if (timesrelss != null) {
                this.timesrelss += timesrelss;
            }
        }

        /**
         * Adds the reparfcn.
         * 
         * @param reparfcn the reparfcn
         */
        public void addReparfcn(Integer reparfcn) {
            if (reparfcn != null) {
                this.reparfcn += reparfcn;
            }
        }
    }

    /**
     * The Class CellRirData.
     */
    private static class CellRirData {

        /** The cell name. */
        final String cellName;

        /** The data. */
        Map<Integer, RirsData> data = new HashMap<Integer, BarRirSaver.RirsData>();

        /**
         * Instantiates a new cell rir data.
         * 
         * @param cellName the cell name
         */
        public CellRirData(String cellName) {
            super();
            this.cellName = cellName;
        }

        /**
         * Adds the rir data.
         * 
         * @param index the index
         * @param arfcn the arfcn
         * @param avemedian the avemedian
         * @param avpercentile the avpercentile
         * @param noofmeas the noofmeas
         */
        public void addRirData(Integer index, Integer arfcn, Integer avemedian, Integer avpercentile, Integer noofmeas) {
            RirsData rir = data.get(index);
            if (rir == null) {
                rir = new RirsData(arfcn, avemedian, avpercentile, noofmeas);
                data.put(index, rir);
            } else {
                rir.add(arfcn, avemedian, avpercentile, noofmeas);
            }
        }

    }

    /**
     * The Class RirsData.
     */
    private static class RirsData {

        /** The arfcn. */
        int arfcn;

        /** The avmedian. */
        int avemedian;

        /** The avpercentile. */
        int avpercentile;

        /** The noofmeas. */
        int noofmeas;

        /** The num. */
        int num;

        /**
         * Instantiates a new rirs data.
         * 
         * @param arfcn the arfcn
         * @param avmedian the avmedian
         * @param avpercentile the avpercentile
         * @param noofmeas the noofmeas
         */
        public RirsData(int arfcn, int avmedian, int avpercentile, int noofmeas) {
            this.arfcn = arfcn;
            this.avemedian = avmedian;
            this.avpercentile = avpercentile;
            this.noofmeas = noofmeas;
            num = 1;
        }

        /**
         * Adds the.
         * 
         * @param arfcn the arfcn
         * @param avemedian the avemedian
         * @param avpercentile the avpercentile
         * @param noofmeas the noofmeas
         */
        public void add(Integer arfcn, Integer avemedian, Integer avpercentile, Integer noofmeas) {
            if (arfcn != this.arfcn) {
                System.err.println("Incorrect arfcn");
                return;
            }
            this.avemedian += avemedian;
            this.avpercentile += avpercentile;
            this.noofmeas += noofmeas;
            num++;
        }

    }
}