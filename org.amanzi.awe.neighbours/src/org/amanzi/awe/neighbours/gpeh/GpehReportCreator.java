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

package org.amanzi.awe.neighbours.gpeh;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.awe.neighbours.gpeh.GpehReportModel.IntraFrequencyICDM;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.GpehReportUtil.MatrixProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexHits;
import org.neo4j.index.lucene.LuceneIndexService;

// TODO: Auto-generated Javadoc
/**
 * TODO Purpose of
 * <p>
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportCreator {

    /** The LOGGER. */
    public static Logger LOGGER = Logger.getLogger(org.amanzi.awe.neighbours.gpeh.GpehReportCreator.class);
    /** The service. */
    private final GraphDatabaseService service;

    /** The model. */
    private final GpehReportModel model;

    /** The lucene service. */
    private final LuceneIndexService luceneService;

    /** The network. */
    private final Node network;

    /** The gpeh. */
    private final Node gpeh;
    
    /** The monitor. */
    private IProgressMonitor monitor;
    
    /** The count row. */
    private int countRow;

    /**
     * Instantiates a new gpeh report creator.
     * 
     * @param network the network
     * @param gpeh the gpeh
     * @param service the service
     * @param luceneService the lucene service
     */
    public GpehReportCreator(Node network, Node gpeh, GraphDatabaseService service, LuceneIndexService luceneService) {
        this.network = network;
        this.gpeh = gpeh;
        this.service = service;
        this.luceneService = luceneService;
        monitor=new NullProgressMonitor();
        model = new GpehReportModel(network, gpeh, service);
    }

    /**
     * Gets the report model.
     * 
     * @return the report model
     */
    public GpehReportModel getReportModel() {
        if (model.getRoot() == null) {
            Transaction tx = service.beginTx();
            try {
                createReportModel();
                tx.success();
            } finally {
                tx.finish();
            }
        }
        return model;
    }

    /**
     * Creates the report model.
     */
    private void createReportModel() {
        if (model.getRoot() != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        Node reports = service.createNode();
        model.getGpeh().createRelationshipTo(reports, ReportsRelations.REPORTS);
        model.getNetwork().createRelationshipTo(reports, ReportsRelations.REPORTS);
        model.findRootNode();
    }

    /**
     * Creates the matrix.
     */
    public void createMatrix() {
        if (model.getIntraFrequencyICDM() != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        Transaction tx = service.beginTx();
        try {
            createReportModel();
            Node intraFMatrix = service.createNode();
            Node interFMatrix = service.createNode();
            Node iRATMatrix = service.createNode();
            model.getRoot().createRelationshipTo(intraFMatrix, ReportsRelations.ICDM_INTRA_FR);
            model.getRoot().createRelationshipTo(interFMatrix, ReportsRelations.ICDM_INTER_FR);
            model.getRoot().createRelationshipTo(iRATMatrix, ReportsRelations.ICDM_IRAT);
            String eventIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getGpehEventsName(), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.GPEH_EVENT);
            String scrCodeIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getNetworkName(), GpehReportUtil.PRIMARY_SCR_CODE, NodeTypes.SECTOR);
            long countEvent=0;
            countRow=0;
            long time=System.currentTimeMillis();
            for (Node eventNode : luceneService.getNodes(eventIndName, Events.RRC_MEASUREMENT_REPORT.name())) {
                countEvent++;
                Set<Node> activeSet = getActiveSet(eventNode);
                Set<RrcMeasurement> measSet = getRncMeasurementSet(eventNode);
                MeasurementCell bestCell = getBestCell(activeSet, measSet);
                if (bestCell == null) {
                    LOGGER.debug(String.format("Event node: %s, not found best cell", eventNode));
                    continue;
                }
                String type = (String)eventNode.getProperty(GpehReportUtil.MR_TYPE, "");
                Node tableRoot;
                if (type.equals(GpehReportUtil.MR_TYPE_INTERF)) {
                    tableRoot = interFMatrix;
                 // TODO remove after
                    continue;
                } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {
                    tableRoot = intraFMatrix;
                } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
                    tableRoot = iRATMatrix;
                    // TODO remove after
                    LOGGER.debug("Event node " + eventNode + " with type " + type + " was passed");
                    continue;
                } else {
                    LOGGER.debug("Event node " + eventNode + " with type " + type + " was passed");
                    continue;
                }
                for (RrcMeasurement measurement : measSet) {
                    if (measurement.getScrambling() == null || measurement.getScrambling().equals(bestCell.getMeasurement().getScrambling())
                            || measurement.getEcNo() == null) {
                        continue;
                    }
                    MeasurementCell sector = findClosestSector(bestCell, measurement, scrCodeIndName);
                    if (sector == null) {
                        LOGGER.debug("Sector not found for PSC " + measurement.getScrambling());
                        continue;
                    }
                    Node tableNode = findOrCreateTableNode(bestCell, sector, tableRoot, type);
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    handleTableNode(tableNode,type,bestCell,sector);
                }
                
                long time2 = System.currentTimeMillis()-time;
                    monitor.setTaskName(String.format("Handle %s events, create table rows %s, ttotal time: %s, average time: %s",countEvent,countRow,time2,time2/countEvent));
            }
            tx.success();
            model.findIntraFrequencyICDM();
        } finally {
            tx.finish();
        }

    }


    /**
     * Handle table node.
     *
     * @param tableNode the table node
     * @param type the type
     * @param bestCell the best cell
     * @param sector the sector
     */
    private void handleTableNode(Node tableNode, String type, MeasurementCell bestCell, MeasurementCell sector) {
        if (type.equals(GpehReportUtil.MR_TYPE_INTERF)) {
            // TODO implement
            LOGGER.error("Not handled InterFreq event");
            return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {
             handleIntraFrTableNode(tableNode,bestCell,sector);
             return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
            // TODO implement
            LOGGER.error("Not handled Irat event");
            return;
        } else {
            throw new IllegalArgumentException();
        }
        
    }



    /**
     * Handle intra fr table node.
     *
     * @param tableNode the table node
     * @param bestCell the best cell
     * @param sector the sector
     */
    private void handleIntraFrTableNode(Node tableNode, MeasurementCell bestCell, MeasurementCell sector) {
        Transaction tx = service.beginTx();
        try{
            //Physical distance in meters 
            if (!tableNode.hasProperty(MatrixProperties.DISTANCE)&&model.getCrs()!=null){
                double dist = CRS.distance(bestCell.getCoordinate(), sector.getCoordinate(), model.getCrs());
                tableNode.setProperty(MatrixProperties.DISTANCE, dist);
            }
            //Defined NBR   TRUE when Interfering Cell is defined neighboring cell,
            //FALSE when Interfering Cell is not defined as neighboring cell

            if (!tableNode.hasProperty(MatrixProperties.DEFINED_NBR)){
                Set<Relationship> relations = NeoUtils.getRelations(bestCell.getCell(), sector.getCell(), NetworkRelationshipTypes.NEIGHBOUR, service);
                boolean def=!relations.isEmpty();
                tableNode.setProperty(MatrixProperties.DEFINED_NBR,def);
            }
            //Tier Distance - not created
            
            //# of MR for best cell
            //can find - calculate count of relation
            
            //# of MR for Interfering cell
            //can find - calculate count of relation
            
            double deltaDbm = (double)Math.abs(bestCell.getMeasurement().getEcNo()-sector.getMeasurement().getEcNo())/2;
            if (deltaDbm<=3){
                updateCounter(tableNode,MatrixProperties.EC_NO_DELTA_PREFIX+1);
            }else if (deltaDbm<=6){
                updateCounter(tableNode,MatrixProperties.EC_NO_DELTA_PREFIX+2);
                
            }else if (deltaDbm<=9){
                updateCounter(tableNode,MatrixProperties.EC_NO_DELTA_PREFIX+3);
                
            }else if (deltaDbm<=12){
                updateCounter(tableNode,MatrixProperties.EC_NO_DELTA_PREFIX+4);
                
            }else if (deltaDbm<=15){
                updateCounter(tableNode,MatrixProperties.EC_NO_DELTA_PREFIX+5);
            }else{
                LOGGER.error("Node "+tableNode+" large delta "+deltaDbm);
            }
            if (bestCell.getMeasurement().getRscp()!=null&&sector.getMeasurement().getRscp()!=null){
                double deltaRscp= (double)Math.abs(bestCell.getMeasurement().getRscp()-sector.getMeasurement().getRscp())/1;  
                if (deltaRscp<=3){
                    updateCounter(tableNode,MatrixProperties.RSCP_DELTA_PREFIX+1);
                }else if (deltaRscp<=6){
                    updateCounter(tableNode,MatrixProperties.RSCP_DELTA_PREFIX+2);
                }else if (deltaRscp<=9){
                    updateCounter(tableNode,MatrixProperties.RSCP_DELTA_PREFIX+3);
                }else if (deltaRscp<=12){
                    updateCounter(tableNode,MatrixProperties.RSCP_DELTA_PREFIX+4);
                }else if (deltaRscp<=15){
                    updateCounter(tableNode,MatrixProperties.RSCP_DELTA_PREFIX+5);
                }else{
                    LOGGER.error("Node "+tableNode+" large delta "+deltaDbm);
                }
            }else{
                LOGGER.error("No found rscp"+bestCell+"\t"+sector); 
            }
            int deltaPosition = sector.getMeasurement().getPosition()-bestCell.getMeasurement().getPosition();
            if (deltaPosition<0){
                LOGGER.error("wrong best cell position: "+bestCell.getMeasurement().getPosition());  
                deltaPosition=-deltaPosition;
            }
            if (sector.getMeasurement().getPosition()==1){
                if (deltaDbm<=6){
                    updateCounter(tableNode,MatrixProperties.POSITION_PREFIX+1); 
                }else{
                    LOGGER.error("found sector with position 2 but wrong delta"+deltaDbm);  
                }
            }else  if (sector.getMeasurement().getPosition()==2){
                if (deltaDbm<=6){
                    updateCounter(tableNode,MatrixProperties.POSITION_PREFIX+2); 
                }else{
                    LOGGER.error("found sector with position 3 but wrong delta"+deltaDbm);  
                }
            }else  if (sector.getMeasurement().getPosition()==3){
                if (deltaDbm<=8){
                    updateCounter(tableNode,MatrixProperties.POSITION_PREFIX+3); 
                }else{
                    LOGGER.error("found sector with position 3 but wrong delta"+deltaDbm);  
                }
            }else  if (sector.getMeasurement().getPosition()==4){
                if (deltaDbm<=8){
                    updateCounter(tableNode,MatrixProperties.POSITION_PREFIX+4); 
                }else{
                    LOGGER.error("found sector with position 3 but wrong delta"+deltaDbm);  
                }
            }else  if (sector.getMeasurement().getPosition()==5){
                if (deltaDbm<=8){
                    updateCounter(tableNode,MatrixProperties.POSITION_PREFIX+5); 
                }else{
                    LOGGER.error("found sector with position 3 but wrong delta"+deltaDbm);  
                }
            }
            tx.success();
        } catch (Exception e) {
            // TODO Handle FactoryException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }finally{
            tx.finish();
        }
        
    }


    /**
     * Update counter.
     *
     * @param tableNode the table node
     * @param propertyName the property name
     */
    private void updateCounter(Node tableNode, String propertyName) {
        Integer c=(Integer)tableNode.getProperty(propertyName,0);
        tableNode.setProperty(propertyName, ++c);
    }

    /**
     * Find or create table node.
     * 
     * @param bestCell the best cell
     * @param sector the sector
     * @param tableRoot the table root
     * @param type the type
     * @return the node
     */
    private Node findOrCreateTableNode(MeasurementCell bestCell, MeasurementCell sector, Node tableRoot, String type) {
        String id = GpehReportUtil.getTableId(String.valueOf(bestCell.getCell().getId()), String.valueOf(sector.getCell().getId()));
        String indexName = GpehReportUtil.getMatrixLuceneIndexName(model.getNetworkName(), model.getGpehEventsName(), type);
        Transaction tx = service.beginTx();
        try {
            Node result = luceneService.getSingleNode(indexName, id);
            if (result == null) {
                assert !"main".equals(Thread.currentThread().getName());
                result=service.createNode();
                tableRoot.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
                Relationship rel = result.createRelationshipTo(bestCell.getCell(), ReportsRelations.BEST_CELL);
                rel.setProperty(GpehReportUtil.REPORTS_ID, indexName);
                rel = result.createRelationshipTo(sector.getCell(), ReportsRelations.SECOND_SELL);
                rel.setProperty(GpehReportUtil.REPORTS_ID, indexName);
                luceneService.index(result, indexName, id);
                countRow++;
                tx.success();
            }
            return result;
        } finally {
            tx.finish();
        }
    }

    /**
     * Find closest sector.
     * 
     * @param bestCell the best cell
     * @param measurement the measurement
     * @param scrCodeIndName the PSC
     * @return the measurement cell
     */
    private MeasurementCell findClosestSector(MeasurementCell bestCell, RrcMeasurement measurement, String scrCodeIndName) {
        if (bestCell.getLat() == null || bestCell.getLon() == null) {
            LOGGER.debug("bestCell " + bestCell.getCell() + " do not have location");
            return null;
        }
        MeasurementCell result = null;
        IndexHits<Node> nodes = luceneService.getNodes(scrCodeIndName, String.valueOf(measurement.getScrambling()));
        for (Node sector : nodes) {
            if (result == null) {
                result = new MeasurementCell(sector, measurement);
                if (setupLocation(result)) {
                    result.setDistance(calculateDistance(bestCell, result));
                } else {
                    LOGGER.debug("sector " + result.getCell() + " do not have location");
                    result = null;
                }
            } else {
                MeasurementCell candidate = new MeasurementCell(sector, measurement);
                if (setupLocation(candidate)) {
                    candidate.setDistance(calculateDistance(bestCell, candidate));
                    if (candidate.getDistance() < result.getDistance()) {
                        result = candidate;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Calculate distance.
     * 
     * @param bestCell the best cell
     * @param candidate the candidate
     * @return the distance between sectors
     */
    private Double calculateDistance(MeasurementCell bestCell, MeasurementCell candidate) {
        return Math.sqrt(Math.pow(bestCell.getLat() - candidate.getLat(), 2) + Math.pow(bestCell.getLon() - candidate.getLon(), 2));
    }

    /**
     * Gets the best cell.
     * 
     * @param activeSet the active set
     * @param measSet the meas set
     * @return the best cell
     */
    private MeasurementCell getBestCell(Set<Node> activeSet, Set<RrcMeasurement> measSet) {
        MeasurementCell bestCell = null;
        if (activeSet.isEmpty()) {
            return bestCell;
        }
        for (RrcMeasurement meas : measSet) {
            if (meas.getScrambling() == null || meas.getEcNo() == null) {
                continue;
            }
            if (bestCell == null || bestCell.getMeasurement().getEcNo() < meas.getEcNo()) {
                Node cell = findInActiveSet(activeSet, meas);
                if (cell != null) {
                    bestCell = new MeasurementCell(cell, meas);
                }
            }
        }
        setupLocation(bestCell);
        return bestCell;
    }

    /**
     * Setup location.
     * 
     * @param cell the cell
     * @return true, if successful
     */
    private boolean setupLocation(MeasurementCell cell) {
        if (cell != null) {
            // define location
            Relationship rel = cell.getCell().getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
            if (rel != null) {
                Node site = rel.getOtherNode(cell.getCell());
                Double lat = (Double)site.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
                Double lon = (Double)site.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
                cell.setLat(lat);
                cell.setLon(lon);
                return lat != null && lon != null;
            }
        }
        return false;
    }

    /**
     * Find in active set.
     * 
     * @param activeSet the active set
     * @param meas the meas
     * @return the node
     */
    private Node findInActiveSet(Set<Node> activeSet, RrcMeasurement meas) {
        if (meas.getScrambling() == null) {
            return null;
        }
        for (Node node : activeSet) {
            if (node.getProperty(GpehReportUtil.PRIMARY_SCR_CODE, "").equals(String.valueOf(meas.getScrambling()))) {
                return node;
            }
        }
        return null;
    }

    /**
     * Gets the rnc measurement set.
     * 
     * @param eventNode the event node
     * @return the rnc measurement set
     */
    private Set<RrcMeasurement> getRncMeasurementSet(Node eventNode) {
        Set<RrcMeasurement> result = new TreeSet<RrcMeasurement>(new Comparator<RrcMeasurement>() {

            @Override
            public int compare(RrcMeasurement o1, RrcMeasurement o2) {
                if (o1.getEcNo()==null){
                    return 1;
                }
                if (o2.getEcNo()==null){
                    return -1;
                };
                return o2.getEcNo().compareTo(o1.getEcNo());
            }
        });
        int id = 0;
        String psc;
        Integer rscp;
        Integer ecNo;
        Integer bsic;
        while (true) {
            id++;
            psc = (String)eventNode.getProperty(GpehReportUtil.GPEH_RRC_SCRAMBLING_PREFIX + id, null);
            rscp = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_RSCP_PREFIX + id, null);
            ecNo = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_ECNO_PREFIX + id, null);
            bsic = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_BSIC_PREFIX + id, null);
            if (psc != null || rscp != null || ecNo != null || bsic != null) {
                result.add(new RrcMeasurement(psc, rscp, ecNo, bsic));
            } else {
                break;
            }
        }
        int i=0;
        for (RrcMeasurement meas:result){
            meas.setPosition(++i); 
        }
        return result;
    }

    /**
     * Gets the active set.
     * 
     * @param eventNode the event node
     * @return the active set
     */
    private Set<Node> getActiveSet(Node eventNode) {
        Set<Node> result = new HashSet<Node>();
        for (int id = 1; id <= 4; id++) {
            Integer ci = (Integer)eventNode.getProperty("EVENT_PARAM_C_ID_" + id, null);
            Integer rnc = (Integer)eventNode.getProperty("EVENT_PARAM_RNC_ID_" + id, null);
            if (ci == null || rnc == null) {
                continue;
            }
            Node asNode = NeoUtils.findSector(model.getNetworkName(), ci, String.valueOf(rnc), luceneService, service);
            if (asNode != null) {
                result.add(asNode);
            }
        }
        return result;
    }


    /**
     * Sets the monitor.
     *
     * @param monitor the new monitor
     */
    public void setMonitor(IProgressMonitor monitor) {
        assert monitor!=null;
        this.monitor = monitor;
    }



    /**
     * Creates the inta idcm spread sheet.
     *
     * @param spreadsheetName the spreadsheet name
     * @return the spreadsheet node
     */
    public SpreadsheetNode createIntaIDCMSpreadSheet(String spreadsheetName) {
        createMatrix();
        GpehReportModel mdl = getReportModel();
        IntraFrequencyICDM matrix = mdl.getIntraFrequencyICDM();
        Transaction tx = service.beginTx();
        try{
            SpreadsheetCreator creator = new SpreadsheetCreator(NeoSplashUtil.configureRubyPath(GpehReportUtil.RUBY_PROJECT_NAME), spreadsheetName);
            int column = 0;
            monitor.subTask("create header");
            Cell cellToadd = new Cell(0, column, "", "Serving cell name", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Serving PSC", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Interfering cell name", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Interfering PSC", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Defined NBR", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Distance", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Tier Distance", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "# of MR for best cell", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "# of MR for Interfering cell", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EcNo Delta1", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EcNo Delta2", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EcNo Delta3", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EcNo Delta4", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "EcNo Delta 5", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "RSCP Delta1", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "RSCP Delta2", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "RSCP Delta3", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "RSCP Delta4", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "RSCP Delta5", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Position1", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Position2", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Position3", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Position4", null);
            creator.saveCell(cellToadd);
            column++;
            cellToadd = new Cell(0, column, "", "Position5", null);
            creator.saveCell(cellToadd);
            column++;
            int row=1;
            int saveCount=0;
            long time = System.currentTimeMillis();
            for (Node tblRow : matrix.getRowTraverser()) {
                monitor.subTask("create row "+row);
                column=0;
                String bestCellName=matrix.getBestCellName(tblRow)+"<"+matrix.getBestCell(tblRow).getId()+">";
                cellToadd = new Cell(row, column, "", bestCellName, null);
                creator.saveCell(cellToadd);
                column++;
                String value=matrix.getBestCellPSC(tblRow);
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++;
                
                value=matrix.getInterferingCellName(tblRow);
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++;
                
                value=matrix.getInterferingCellPSC(tblRow);
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++; 
                
                value=String.valueOf(matrix.getNumMRForBestCell(tblRow));
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++; 
                
                value=String.valueOf(matrix.getNumMRForInterferingCell(tblRow));
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++; 
                
                value=String.valueOf(matrix.getNumMRForInterferingCell(tblRow));
                cellToadd = new Cell(row, column, "", value, null);
                creator.saveCell(cellToadd);
                column++;               
                for (int i=1;i<=5;i++){
                    value=String.valueOf(matrix.getDeltaEcNo(i,tblRow));  
                    cellToadd = new Cell(row, column, "", value, null);
                    creator.saveCell(cellToadd);
                    column++; 
                }
                for (int i=1;i<=5;i++){
                    value=String.valueOf(matrix.getDeltaRSCP(i,tblRow));  
                    cellToadd = new Cell(row, column, "", value, null);
                    creator.saveCell(cellToadd);
                    column++; 
                }
                for (int i=1;i<=5;i++){
                    value=String.valueOf(matrix.getPosition(i,tblRow));  
                    cellToadd = new Cell(row, column, "", value, null);
                    creator.saveCell(cellToadd);
                    column++; 
                }
              monitor.setTaskName(String.format("Rows created: %s", row));

                saveCount++;
                if (saveCount>1000){
                    time=System.currentTimeMillis()-time;
                    tx.success();
                    tx.finish();
                    saveCount=0;
                    System.out.println("time of storing 1000 rows: "+time);
                    time = System.currentTimeMillis();
                    tx=service.beginTx();
                }
                row++;
            }
            monitor.setTaskName("modify header");
            tx.success();
            System.out.println(creator.getSpreadsheet().getUnderlyingNode().getId());
            return creator.getSpreadsheet();
        }finally{
            tx.finish();
        }
    }
    
}
