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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.IRecords;
import org.amanzi.awe.afp.ericsson.Parameters;
import org.amanzi.awe.afp.ericsson.parser.MainRecord.Record;
import org.amanzi.awe.afp.ericsson.parser.RecordTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.utils.Pair;
import org.apache.commons.lang.ObjectUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Bar saver
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class BarSaver extends AbstractHeaderSaver<RecordTransferData> {

    /** The admin values. */
    AdminValues adminValues = null;

    /** The serv cells. */
    Map<String, ServCell> servCells = new HashMap<String, ServCell>();

    private NetworkModel networkModel;

    private NodeToNodeRelationModel interfModel;

    private NodeToNodeRelationModel shadowModel;
    private String firstFileName;
    /**
     * Save.
     * 
     * @param element the element
     */
    @Override
    public void save(RecordTransferData element) {
        if (firstFileName==null){
            firstFileName=element.getFileName();
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
        }
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
            error(String.format("Different CHGR (%s and %s) in cell name %s", chGr, result.chgr, cellName));
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
    private Integer getInteger(Record record, Parameters parameter) {
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
        for (int i = 0; i < val.length; i++) {
            res += (long)val[i] * 256 ^ i;
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

    @Override
    public void finishUp(RecordTransferData element) {
        createMatrix();
        super.finishUp(element);
    }

    private void createMatrix() {
        if (adminValues==null){
            return;
        }
        interfModel=networkModel.getInterferenceMatrix(getInterfMatrixName());
        shadowModel=networkModel.getShadowing(getShadowingMatrixName());
        
        for (ServCell cell : servCells.values()) {
            handleServCell(cell);
        }
    }


    private String getShadowingMatrixName() {
        return "shadowing "+firstFileName;
    }

    /**
     *
     * @return
     */
    private String getInterfMatrixName() {
        return "interference "+firstFileName;
    }

    private void handleServCell(ServCell cell) {
        Node servSector = findNode(cell);
        if (servSector == null) {
            error(String.format("Sector %s not found", cell.cellName));
            return;
        }
        for (InterfCell neigh : cell.cells.values()) {
            handleNeighbohur(cell,servSector, neigh);
        }
    }

    private void handleNeighbohur(ServCell cell, Node servSector, InterfCell neigh) {
        Node neighSector = findNode(servSector,neigh);
        if (neighSector == null) {
            error(String.format("Sector (bsic=%s;arfcn=%s) not found", neigh.bsic, neigh.arfcn));
            return;
        }
        Double factorCo=getFactorCo(adminValues.relss);
        if (factorCo==null){
          error("Incorreect relss="+String.valueOf(adminValues.relss));
          return;
        }
        if (neigh.reparfcn==0){
            error("Incorreect reparfcn=0");
            return;
          }
        double impactCO=100*neigh.timesrelss/neigh.reparfcn*factorCo;
        Double factorAdj=getFactorAdj(adminValues.relss);
        if (factorAdj==null){
            error("Incorreect relss="+String.valueOf(adminValues.relss));
            return;
          }

          double impactAdj=100*neigh.timesrelss2/neigh.reparfcn*factorAdj;
        Relationship rel = interfModel.getRelation(servSector, neighSector);
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "source", "IM source - Interference");
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", impactCO);
        updateProperty(getInterfMatrixName(), NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", impactAdj);
    }

    /**
     *
     * @param relss
     * @return
     */
    private Double getFactorAdj(Integer relss) {
        if (relss==null){
            return null;
        }
        return 16d/(2^(relss/3));
//        else if (relss==18){
//            return 0.25d;
//        }else if (relss==15){
//            return 0.5d;
//        }else if (relss==12){
//            return 1d;
//        }else if (relss==9){
//            return 2d;
//        }else if (relss==6){
//            return 4d;
//        }else if (relss==3){
//            return 8d;
//        }else{
//            return null;
//        }
    }

    private Double getFactorCo(Integer relss) {
        if (relss==null){
            return null;
        }
        return 0.25d/(2^(relss/3));
    }

    private Node findNode(Node servSector, InterfCell neigh) {
       return networkModel.getClosestSector(servSector,neigh.bsic,neigh.arfcn);
    }

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
@Override
public void init(RecordTransferData element) {
    super.init(element);
    networkModel=new NetworkModel(rootNode);
}
    /**
     * <p>
     * The Wrapper for AdminValues
     * </p>
     * .
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
        Map<Pair<Integer, Integer>, InterfCell> cells = new HashMap<Pair<Integer, Integer>, BarSaver.InterfCell>();

    }

    /**
     * <p>
     * Wrapper for interference cell
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class InterfCell {
        /** The bsic. */
        Integer bsic;

        /** The arfcn. */
        Integer arfcn;
        int reparfcn;
        int timesrelss;
        int timesrelss2;
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

        public void addTimesabss(Integer timesabss) {
            if (timesabss != null) {
                this.timesabss += timesabss;
            }
        }

        public void addTimesrelss2(Integer timesrelss2) {
            if (timesrelss2 != null) {
                this.timesrelss2 += timesrelss2;
            }
        }

        public void addTimesrelss(Integer timesrelss) {
            if (timesrelss != null) {
                this.timesrelss += timesrelss;
            }
        }

        public void addReparfcn(Integer reparfcn) {
            if (reparfcn != null) {
                this.reparfcn += reparfcn;
            }
        }
    }
}