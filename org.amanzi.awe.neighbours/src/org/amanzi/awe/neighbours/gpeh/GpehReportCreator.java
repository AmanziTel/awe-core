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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.awe.neighbours.gpeh.Calculator3GPPdBm.ValueType;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.AnalysisByPeriods;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellDlTxCarrierPowerAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellEcNoAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellHsdsRequiredPowerAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellNonHsPowerAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellRscpAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellRscpEcNoAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellUeTxPowerAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.CellUlInterferenceAnalisis;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.InterFrequencyICDM;
import org.amanzi.awe.neighbours.gpeh.GpehReportModel.IntraFrequencyICDM;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistic.IStatisticElement;
import org.amanzi.awe.statistic.IStatisticElementNode;
import org.amanzi.awe.statistic.IStatisticHandler;
import org.amanzi.awe.statistic.IStatisticStore;
import org.amanzi.awe.statistic.StatisticByPeriodStructure;
import org.amanzi.awe.statistic.StatisticNeoService;
import org.amanzi.awe.statistic.TimePeriodStructureCreator;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.core.preferences.NeoCorePreferencesConstants;
import org.amanzi.neo.core.utils.GpehReportUtil;
import org.amanzi.neo.core.utils.GpehReportUtil.CellReportsProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.MatrixProperties;
import org.amanzi.neo.core.utils.GpehReportUtil.ReportsRelations;
import org.amanzi.neo.core.utils.NeoArray;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.export.CommonExporter;
import org.amanzi.neo.core.utils.export.IExportHandler;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexHits;
import org.neo4j.index.lucene.LuceneIndexService;

/**
 * <p>
 * Create different gpeh reports
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class GpehReportCreator {
    private final CallTimePeriods baseTime;
    /** The LOGGER. */
    public static Logger LOGGER = Logger.getLogger(org.amanzi.awe.neighbours.gpeh.GpehReportCreator.class);
    /** The service. */
    private final GraphDatabaseService service;

    /** The model. */
    private final GpehReportModel model;

    /** The lucene service. */
    private final LuceneIndexService luceneService;

    /** The gpeh. */
    private final Node gpeh;

    /** The monitor. */
    private IProgressMonitor monitor;

    /** The count row. */
    private int countRow;
    // private CellRscpAnalisis rspAnalyse;
    private CellRscpEcNoAnalisis rspEcNoAnalyse;
    private CellUeTxPowerAnalisis ueTxPAnalyse;
    private final Pair<Long, Long> minMax;
    private int maxRange;
    private long notFullData;

    // private CellEcNoAnalisis ecNoAnalyse;
    /**
     * Instantiates a new gpeh report creator.
     * 
     * @param network the network
     * @param gpeh the gpeh
     * @param service the service
     * @param luceneService the lucene service
     */
    public GpehReportCreator(Node network, Node gpeh, GraphDatabaseService service, LuceneIndexService luceneService) {
        baseTime = CallTimePeriods.QUATER_HOUR;
        this.gpeh = gpeh;
        this.service = service;
        this.luceneService = luceneService;
        monitor = new NullProgressMonitor();
        model = new GpehReportModel(network, gpeh, service);
        maxRange = NeoCorePlugin.getDefault().getPreferenceStore().getInt(NeoCorePreferencesConstants.MAX_SECTOR_DISTANSE);
        if (maxRange == 0) {
            maxRange = 3000;
        }
        minMax = NeoUtils.getMinMaxTimeOfDataset(gpeh, service);
    }

    /**
     * Export to csv
     * 
     * @param output the output dirrectory
     * @param report the report type
     * @param period the time period
     * @param monitor the monitor
     */
    public void exportToCSV(File outputDir, GpehReportType report, CallTimePeriods period, IProgressMonitor monitor) {
        File output = new File(outputDir, generateReportName(report, period));
        IExportHandler handler = new CommonCSVHandler(output, monitor, '\t');
        IExportProvider provider = defineProvider(report, period);
        monitor.worked(1);
        Transaction tx = service.beginTx();
        try {
            if (provider.isValid()) {
                CommonExporter export = new CommonExporter(handler, provider);
                export.process(monitor);
            }
        } finally {
            tx.finish();
            monitor.worked(1);
        }
    }

    /**
     * Generate report name.
     * 
     * @param report the report
     * @param period the period
     * @return the string
     */
    private String generateReportName(GpehReportType report, CallTimePeriods period) {
        return report.getId() + ".csv";
    }

    /**
     * Define provider.
     * 
     * @param report the report type
     * @param period the period
     * @return the i export provider
     */
    private IExportProvider defineProvider(GpehReportType report, CallTimePeriods period) {
        IExportProvider result = null;
        switch (report) {
        case UE_TX_POWER_ANALYSIS:
            createMatrix();
            createUeTxPowerCellReport(period);
            result = getUeTxPowerCellProvider(period);
            return result;
        case CELL_RF_CORRELATION:
            createMatrix();
            createRscpEcNoCellReport(period);
            result = getCellCorrelationProvider(period);
            return result;
        case IDCM_INTRA:
            createMatrix();
            result = getIntraMatrixProvider();
            return result;
        case IDCM_INTER:
            createMatrix();
            return getInterMatrixProvider();
        case CELL_RSCP_ANALYSIS:
            createMatrix();
            createRSCPCellReport(period);
            return getCellRSCPProvider(period);
        case CELL_ECNO_ANALYSIS:
            createMatrix();
            createEcNoCellReport(period);
            return getCellEcnoProvider(period);
        case NBAP_UL_INTERFERENCE:
            createNBapBaseReports();
            createUlInterferenceReport(period);
            return getUlInterferenceCellProvider(period);
        case NBAP_DL_TX_CARRIER_POWER:
            createNBapBaseReports();
            createCellDlTxCarrierPowerAnalisis(period);
            return getCellDlTxCarrierPowerProvider(period);
        case NBAP_NON_HS_POWER:
            createNBapBaseReports();
            createCellNonHsPowerAnalisis(period);
            return getCellNonHsPowerPowerProvider(period);
        case NBAP_HSDS_REQUIRED_POWER:
            createNBapBaseReports();
            createCellHsdsRequiredPowerAnalisis(period);
            return getCellHsdsRequiredPowerProvider(period);
        default:
            return null;
        }
    }

    private IExportProvider getCellCorrelationProvider(final CallTimePeriods period) {
        final CellRscpEcNoAnalisis analyse = getReportModel().getCellRscpEcNoAnalisis(period);

        final Node sourceMainNode = analyse.getMainNode();
        final Pair<Long, Long> minMax = NeoUtils.getMinMaxTimeOfDataset(gpeh, service);
        final List<String> headers = new LinkedList<String>();
        headers.add("Cell Name");
        headers.add("Date");
        headers.add("Time");
        headers.add("Resolution");
        final List<IntRange> ecnoRangeNames = new ArrayList<IntRange>();
        ecnoRangeNames.add(new IntRange("ECNO-18", 0, 12));
        ecnoRangeNames.add(new IntRange("ECNO-15", 13, 18));
        ecnoRangeNames.add(new IntRange("ECNO-12", 19, 24));
        ecnoRangeNames.add(new IntRange("ECNO-9", 25, 30));
        ecnoRangeNames.add(new IntRange("ECNO-6", 31, 36));
        ecnoRangeNames.add(new IntRange("ECNO-0", 37, 48));// TODO check for 49?
        final int ecnoRange = ecnoRangeNames.size();
        final List<IntRange> rscpRangeNames = new ArrayList<IntRange>();
        rscpRangeNames.add(new IntRange("RSCP-105", 0, 10));
        rscpRangeNames.add(new IntRange("RSCP-100", 11, 15));
        rscpRangeNames.add(new IntRange("RSCP-95", 16, 20));
        rscpRangeNames.add(new IntRange("RSCP-90", 21, 25));
        rscpRangeNames.add(new IntRange("RSCP-80", 26, 35));
        rscpRangeNames.add(new IntRange("RSCP-70", 36, 45));
        rscpRangeNames.add(new IntRange("RSCP-25", 46, 90));// TODO check 91
        final int rscpRange = rscpRangeNames.size();
        for (IntRange rscpName : rscpRangeNames) {
            for (IntRange ecnoName : ecnoRangeNames) {
                headers.add(new StringBuilder(ecnoName.getName()).append("_").append(rscpName.getName()).toString());
            }
        }
        // TODO create public class
        return new IExportProvider() {
            Iterator<Relationship> bestCellIteranor = null;
            Iterator<IStatisticElementNode> iter = null;
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String name = "";
            Calendar calendar = Calendar.getInstance();

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public boolean hasNextLine() {
                if (bestCellIteranor == null) {
                    bestCellIteranor = sourceMainNode.getRelationships(Direction.OUTGOING).iterator();

                }
                if (iter == null || !iter.hasNext()) {
                    defineStructIterator();
                }
                return iter != null && iter.hasNext();
            }

            @Override
            public List<Object> getNextLine() {
                // TODO implement
                IStatisticElementNode statNode = iter.next();
                List<Object> result = new ArrayList<Object>();
                result.add(name);

                calendar.setTimeInMillis(statNode.getStartTime());
                
//                System.out.println(statNode.getStartTime());
//                System.out.println(calendar.toString());
//                System.out.println(dateFormat.format(calendar.getTime()));
                
                result.add(dateFormat.format(calendar.getTime()));
                result.add(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                result.add(period.getId());
                if (NeoArray.hasArray(CellRscpEcNoAnalisis.ARRAY_NAME, statNode.getNode(), service)) {
                    NeoArray array = new NeoArray(statNode.getNode(), CellRscpEcNoAnalisis.ARRAY_NAME, service);
                    for (IntRange rscpName : rscpRangeNames) {
                        for (IntRange ecnoName : ecnoRangeNames) {
                            int count = 0;
                            for (int rscp = rscpName.getMin(); rscp <= rscpName.getMax(); rscp++) {
                                for (int ecno = ecnoName.getMin(); ecno <= ecnoName.getMax(); ecno++) {
                                    Number value = (Number)array.getValue(rscp, ecno);
                                    if (value == null) {
                                        value = 0;
                                    }
                                    count += value.intValue();
                                }
                            }
                            result.add(count);
                        }
                    }
                } else {
                    for (int rscp = 0; rscp < rscpRange; rscp++) {
                        for (int ecno = 0; ecno < ecnoRange; ecno++) {
                            result.add(0);
                        }
                    }
                }

                return result;
            }

            private void defineStructIterator() {
                while (bestCellIteranor.hasNext()) {
                    Relationship rel = bestCellIteranor.next();
                    String bestCellId = StatisticNeoService.getBestCellId(rel.getType().name());
                    if (bestCellId != null) {
                        Node sector = service.getNodeById(Long.parseLong(bestCellId));
                        name = (String)sector.getProperty("userLabel", "");
                        if (StringUtil.isEmpty(name)) {
                            name = NeoUtils.getNodeName(sector, service);
                        }
                        StatisticByPeriodStructure structure = analyse.getStatisticStructure(bestCellId);
                        iter = structure.getStatNedes(minMax.getLeft(), minMax.getRight()).iterator();
                        if (iter.hasNext()) {
                            return;
                        }
                    }
                }
            }

            @Override
            public List<String> getHeaders() {
                return headers;
            }

            @Override
            public String getDataName() {
                return "CELL RF CORRELATION ANALYSIS";
            }
        };

    }

    private IExportProvider getInterMatrixProvider() {
        GpehReportModel mdl = getReportModel();
        final InterFrequencyICDM matrix = mdl.getInterFrequencyICDM();
        final List<String> headers = new LinkedList<String>();
        headers.add("Serving cell name");
        headers.add("Serving PSC");
        headers.add("Serving cell UARFCN");
        headers.add("Overlapping cell name");
        headers.add("Interfering PSC");
        headers.add("Overlapping cell UARCFN");
        headers.add("Defined NBR");
        headers.add("Distance");
        headers.add("Tier Distance");
        headers.add("# of MR for best cell");
        headers.add("# of MR for Interfering cell");
        headers.add("EcNo 1");
        headers.add("EcNo 2");
        headers.add("EcNo 3");
        headers.add("EcNo 4");
        headers.add("EcNo 5");
        headers.add("RSCP1_14");
        headers.add("RSCP2_14");
        headers.add("RSCP3_14");
        headers.add("RSCP4_14");
        headers.add("RSCP5_14");
        headers.add("RSCP1_10");
        headers.add("RSCP2_10");
        headers.add("RSCP3_10");
        headers.add("RSCP4_10");
        headers.add("RSCP5_10");

        // TODO create public class
        return new IExportProvider() {
            Iterator<Node> rowIterator = null;

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public boolean hasNextLine() {
                if (rowIterator == null) {
                    rowIterator = matrix.getRowTraverser().iterator();
                }
                return rowIterator.hasNext();
            }

            @Override
            public List<Object> getNextLine() {
                List<Object> result = new ArrayList<Object>();
                Node tblRow = rowIterator.next();
                // Serving cell name
                String bestCellName = matrix.getBestCellName(tblRow);
                result.add(bestCellName);
                // psc
                String value = matrix.getBestCellPSC(tblRow);
                result.add(bestCellName);
                // UARFCN
                result.add(matrix.getBestCellUARFCN(tblRow));
                // Interfering cell name
                value = matrix.getInterferingCellName(tblRow);
                result.add(bestCellName);
                // Interfering PSC
                value = matrix.getInterferingCellPSC(tblRow);
                result.add(value);
                // UARFCN
                result.add(matrix.getInterferingCellUARFCN(tblRow));
                // Defined NBR
                result.add(matrix.isDefinedNbr(tblRow));
                // Distance
                result.add(matrix.getDistance(tblRow));
                // Tier Distance
                value = String.valueOf("N/A");
                result.add(value);
                // # of MR for best cell
                result.add(matrix.getNumMRForBestCell(tblRow));
                // # of MR for Interfering cell
                result.add(matrix.getNumMRForInterferingCell(tblRow));
                // EcNo 1-5
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getEcNo(i, tblRow));
                }
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getRSCP(i, 14, tblRow));
                }
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getRSCP(i, 10, tblRow));
                }
                return result;
            }

            @Override
            public List<String> getHeaders() {
                return headers;
            }

            @Override
            public String getDataName() {
                return "INTRA_ICDM";
            }
        };

    }

    /**
     * Gets the cell ecno provider.
     * 
     * @param period the period
     * @return the cell ecno provider
     */
    private IExportProvider getCellEcnoProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellEcNoAnalisis(period), CellEcNoAnalisis.ARRAY_NAME, ValueType.ECNO, period, minMax.getLeft(),
                minMax.getRight());
        return new TimePeriodStructureProvider("Cell EcNo Analysis", element, service);
    }

    /**
     * Gets the cell rscp provider.
     * 
     * @param period the period
     * @return the cell rscp provider
     */
    private IExportProvider getCellRSCPProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellRscpAnalisis(period), CellRscpAnalisis.ARRAY_NAME, ValueType.RSCP, period, minMax.getLeft(),
                minMax.getRight());
        return new TimePeriodStructureProvider("CELL RSCP ANALYSIS", element, service);
    }

    private IExportProvider getIntraMatrixProvider() {
        GpehReportModel mdl = getReportModel();
        final IntraFrequencyICDM matrix = mdl.getIntraFrequencyICDM();
        final List<String> headers = new LinkedList<String>();
        headers.add("Serving cell name");
        headers.add("Serving PSC");
        headers.add("Interfering cell name");
        headers.add("Interfering PSC");
        headers.add("Defined NBR");
        headers.add("Distance");
        headers.add("Tier Distance");
        headers.add("# of MR for best cell");
        headers.add("# of MR for Interfering cell");
        headers.add("EcNo Delta1");
        headers.add("EcNo Delta2");
        headers.add("EcNo Delta3");
        headers.add("EcNo Delta4");
        headers.add("EcNo Delta5");
        headers.add("RSCP Delta1");
        headers.add("RSCP Delta2");
        headers.add("RSCP Delta3");
        headers.add("RSCP Delta4");
        headers.add("RSCP Delta5");
        headers.add("Position1");
        headers.add("Position2");
        headers.add("Position3");
        headers.add("Position4");
        headers.add("Position5");

        // TODO create public class
        return new IExportProvider() {
            Iterator<Node> rowIterator = null;

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public boolean hasNextLine() {
                if (rowIterator == null) {
                    rowIterator = matrix.getRowTraverser().iterator();
                }
                return rowIterator.hasNext();
            }

            @Override
            public List<Object> getNextLine() {
                List<Object> result = new ArrayList<Object>();
                Node tblRow = rowIterator.next();
                // Serving cell name
                String bestCellName = matrix.getBestCellName(tblRow);
                result.add(bestCellName);
                // psc
                String value = matrix.getBestCellPSC(tblRow);
                result.add(value);
                // Interfering cell name
                result.add(matrix.getInterferingCellName(tblRow));
                // Interfering PSC
                result.add(matrix.getInterferingCellPSC(tblRow));
                // Defined NBR
                result.add(matrix.isDefinedNbr(tblRow));
                // Distance
                result.add(matrix.getDistance(tblRow));
                // Tier Distance
                value = String.valueOf("N/A");
                result.add(value);
                // # of MR for best cell
                result.add(matrix.getNumMRForBestCell(tblRow));
                // # of MR for Interfering cell
                result.add(matrix.getNumMRForInterferingCell(tblRow));
                // Delta EcNo 1-5
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getDeltaEcNo(i, tblRow));
                }
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getDeltaRSCP(i, tblRow));
                }
                for (int i = 1; i <= 5; i++) {
                    result.add(matrix.getPosition(i, tblRow));
                }
                return result;
            }

            @Override
            public List<String> getHeaders() {
                return headers;
            }

            @Override
            public String getDataName() {
                return "INTRA_ICDM";
            }
        };

    }

    /**
     * Gets the ue tx power cell provider.
     * 
     * @param period the period
     * @return the ue tx power cell provider
     */
    private IExportProvider getUlInterferenceCellProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellUlInterferenceAnalisis(period), CellUlInterferenceAnalisis.ARRAY_NAME, ValueType.UL_INTERFERENCE,
                period, minMax.getLeft(), minMax.getRight());
        // return new TimePeriodStructureProvider("UL noise rise", element, service);
        return new WrappedTimePeriodProvider("UL noise rise", element, service, 5, false);
    }

    /**
     * Gets the cell dl tx carrier power provider.
     * 
     * @param period the period
     * @return the cell dl tx carrier power provider
     */
    private IExportProvider getCellDlTxCarrierPowerProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellDlTxCarrierPowerAnalisis(period), CellDlTxCarrierPowerAnalisis.ARRAY_NAME,
                ValueType.DL_TX_CARRIER_POWER, period, minMax.getLeft(), minMax.getRight());
        return new NBAPWattProvider("DL_TX_CARRIER_POWER analysis", element, service);
    }

    private IExportProvider getCellHsdsRequiredPowerProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellHsdsRequiredPowerAnalisis(period), CellHsdsRequiredPowerAnalisis.ARRAY_NAME,
                ValueType.HSDSCH_REQUIRED_POWER, period, minMax.getLeft(), minMax.getRight());
        return new NBAPWattProvider("HSDSCH_REQUIRED_POWER analysis", element, service);
    }
    private IExportProvider getCellNonHsPowerPowerProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellNonHsPowerAnalisis(period), CellNonHsPowerAnalisis.ARRAY_NAME,
                ValueType.NON_HS_POWER, period, minMax.getLeft(), minMax.getRight());
        return new NBAPWattProvider("NON_HS_POWER analysis", element, service);
    }

    /**
     * Gets the ue tx power cell provider.
     * 
     * @param period the period
     * @return the ue tx power cell provider
     */
    private IExportProvider getUeTxPowerCellProvider(final CallTimePeriods period) {
        TimePeriodElement element = new TimePeriodElement(getReportModel().getCellUeTxPowerAnalisis(period), CellUeTxPowerAnalisis.ARRAY_NAME, ValueType.UETXPOWER, period,
                minMax.getLeft(), minMax.getRight());
        return new TimePeriodStructureProvider("Ue tx power analysis", element, service);
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

    public void createEcNoCellReport(CallTimePeriods periods) {
        if (model.getCellEcNoAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        Transaction tx = service.beginTx();
        CellRscpEcNoAnalisis sourceModel;
        Node parentNode;
        try {
            createMatrix();
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellEcNoAnalisis.ECNO_PRFIX));
            sourceModel = model.getCellRscpEcNoAnalisis(periods);
            tx.success();
        } finally {
            tx.finish();
        }
        if (sourceModel == null) {
            createRscpEcNoCellReport(periods);
            sourceModel = model.getCellRscpEcNoAnalisis(periods);
        }
        IStatisticStore store = new GPEHEcNoStorer();
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellEcNoAnalisis(periods);
    }

    /**
     * Gets the previos period.
     * 
     * @param periods the periods
     * @return the previos period
     */
    private CallTimePeriods getPreviosPeriod(CallTimePeriods periods) {
        return periods == CallTimePeriods.HOURLY ? CallTimePeriods.QUATER_HOUR : CallTimePeriods.HOURLY;
    }

    public static void main(String[] args) {
        System.out.println(new Date(1268553599024l));
    }

    /**
     * Creates the period based structure.
     * 
     * @param periods the periods
     * @param parentNode the parent node
     * @param sourceModel the source model
     * @param store the store
     */
    protected void createPeriodBasedStructure(CallTimePeriods periods, Node parentNode, AnalysisByPeriods sourceModel, IStatisticStore store) {
        Node sourceMainNode = sourceModel.getMainNode();
        Transaction tx = service.beginTx();
        try {
            int count = 0;
            for (Relationship rel : sourceMainNode.getRelationships(Direction.OUTGOING)) {
                String bestCellId = StatisticNeoService.getBestCellId(rel.getType().name());
                if (bestCellId != null) {
                    StatisticByPeriodStructure sourceStruc = new StatisticByPeriodStructure(rel.getOtherNode(sourceMainNode), service);
                    GPEHStatisticHandler handler = new GPEHStatisticHandler(sourceStruc);

                    TimePeriodStructureCreator creator = new TimePeriodStructureCreator(parentNode, bestCellId, minMax.getLeft(), minMax.getRight(), periods, handler, store,
                            service);
                    count += creator.createStructure().getCreatedNodes();
                    if (count > 2000) {
                        count = 0;
                        tx.success();
                        tx.finish();
                        tx = service.beginTx();
                    }
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public void createRscpEcNoCellReport(CallTimePeriods periods) {
        if (model.getCellRscpEcNoAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createMatrix();
        Transaction tx = service.beginTx();
        Node parentNode;

        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellRscpEcNoAnalisis.PRFIX));

            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellRscpEcNoAnalisis sourceModel = model.getCellRscpEcNoAnalisis(previosPeriod);
        if (sourceModel == null) {
            createEcNoCellReport(previosPeriod);
            sourceModel = model.getCellRscpEcNoAnalisis(previosPeriod);
        }
        IStatisticStore store = new GPEHRscpEcNoStorer();
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellRscpEcNoAnalisis(periods);

    }

    /**
     * Creates the rscp cell report.
     * 
     * @param periods the periods
     */
    public void createRSCPCellReport(CallTimePeriods periods) {
        if (model.getCellRscpAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createMatrix();
        Transaction tx = service.beginTx();
        Node parentNode;
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellRscpAnalisis.RSCP_PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CellRscpEcNoAnalisis sourceModel = model.getCellRscpEcNoAnalisis(periods);
        if (sourceModel == null) {
            createRscpEcNoCellReport(periods);
            sourceModel = model.getCellRscpEcNoAnalisis(periods);
        }
        IStatisticStore store = new GPEHRSCPStorer();
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellRscpAnalisis(periods);
    }

    public void createNBapBaseReports() {
        if (model.getCellUlInterferenceAnalisis(baseTime) != null) {
            return;
        }
        Transaction tx = service.beginTx();
        try {
            createReportModel();
            // CellUlInterferenceAnalisis hour node
            Node parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellUlInterferenceAnalisis.PRFIX));
            // CellDlTxCarrierPowerAnalisis hour node
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellDlTxCarrierPowerAnalisis.PRFIX));
            // CellNonHsPowerAnalisis hour node
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellNonHsPowerAnalisis.PRFIX));
            // CellHsdsRequiredPowerAnalisis hour node
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellHsdsRequiredPowerAnalisis.PRFIX));

            // CellDlTxCodePowerAnalisis dlTxCodePower =
            // model.findCellDlTxCodePowerAnalisis(CallTimePeriods.HOURLY);
            // dlTxCodePower.setUseCache(true);
            CellUlInterferenceAnalisis ulInterference = model.findCellUlInterferenceAnalisis(baseTime);
            CellDlTxCarrierPowerAnalisis dlTxCarrierPowerAnalisis = model.findCellDlTxCarrierPowerAnalisis(baseTime);
            CellNonHsPowerAnalisis cellNonHsPowerAnalisis = model.findCellNonHsPowerAnalisis(baseTime);
            CellHsdsRequiredPowerAnalisis cellHsdsRequiredPowerAnalisis = model.findCellHsdsRequiredPowerAnalisis(baseTime);

            String eventIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getGpeh(), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.GPEH_EVENT);
            long countEvent = 0;
            countRow = 0;
            long countTx = 0;
            for (Node eventNode : luceneService.getNodes(eventIndName, Events.INTERNAL_RADIO_QUALITY_MEASUREMENTS_RNH.name())) {
                countEvent++;
                // Set<Node> activeSet = getActiveSet(eventNode);
                Integer type = (Integer)eventNode.getProperty(Parameters.EVENT_PARAM_MEASURED_ENTITY.name(), null);
                if (type == null || type < 2 || type > 5) {
                    continue;
                }
                Integer value = (Integer)eventNode.getProperty(Parameters.EVENT_PARAM_MEASURED_VALUE.name(), null);
                if (value == null) {
                    continue;
                }
                Node bestSector = getBestSector(eventNode);
                if (bestSector == null) {
                    LOGGER.error(String.format("Event node: %s, not found best cell", eventNode));
                    continue;
                }
                Node tableRoot;
                // TODO optimize code for using methods
                switch (type) {
                case 2:
                    if (value > 621) {
                        LOGGER.error(String.format("Event node: %s, wrong vlue for UL_INTERFERENCE : %s", eventNode, value));
                        continue;
                    }
                    tableRoot = ulInterference.getMainNode();
                    Node tableNode = findOrCreateTableNode(bestSector, tableRoot, "2");
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    String bestCellName = String.valueOf(bestSector.getId());
                    StatisticByPeriodStructure statisticStructure = ulInterference.getStatisticStructure(bestCellName);
                    if (statisticStructure == null) {
                        GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
                        TimePeriodStructureCreator creator = new TimePeriodStructureCreator(tableRoot, bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                                handler, service);
                        statisticStructure = creator.createStructure();
                    }
                    Long timeNode = NeoUtils.getNodeTime(eventNode);
                    IStatisticElementNode node = statisticStructure.getStatisticNode(timeNode);
                    NeoArray neoArray = new NeoArray(node.getNode(), CellUlInterferenceAnalisis.ARRAY_NAME, 1, service);
                    Node arrNode = neoArray.findOrCreateNode(value);
                    Integer count = (Integer)neoArray.getValueFromNode(arrNode);
                    count = count == null ? 1 : count + 1;
                    neoArray.setValueToNode(arrNode, count);
                    arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_UL_INTERFERENCE);
                    break;
                case 3:
                    if (value > 1000) {
                        LOGGER.error(String.format("Event node: %s, wrong vlue for UL_INTERFERENCE : %s", eventNode, value));
                        continue;
                    }
                    tableRoot = dlTxCarrierPowerAnalisis.getMainNode();
                    tableNode = findOrCreateTableNode(bestSector, tableRoot, "3");
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    bestCellName = String.valueOf(bestSector.getId());
                    statisticStructure = dlTxCarrierPowerAnalisis.getStatisticStructure(bestCellName);
                    if (statisticStructure == null) {
                        GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
                        TimePeriodStructureCreator creator = new TimePeriodStructureCreator(tableRoot, bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                                handler, service);
                        statisticStructure = creator.createStructure();
                    }
                    timeNode = NeoUtils.getNodeTime(eventNode);
                    node = statisticStructure.getStatisticNode(timeNode);
                    neoArray = new NeoArray(node.getNode(), CellDlTxCarrierPowerAnalisis.ARRAY_NAME, 1, service);
                    arrNode = neoArray.findOrCreateNode(value);
                    count = (Integer)neoArray.getValueFromNode(arrNode);
                    count = count == null ? 1 : count + 1;
                    neoArray.setValueToNode(arrNode, count);
                    arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_DL_TX_CARRIER_POWER);
                    break;
                case 4:
                    if (value > 1000) {
                        LOGGER.error(String.format("Event node: %s, wrong vlue for UL_INTERFERENCE : %s", eventNode, value));
                        continue;
                    }
                    tableRoot = cellNonHsPowerAnalisis.getMainNode();
                    tableNode = findOrCreateTableNode(bestSector, tableRoot, "3");
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    bestCellName = String.valueOf(bestSector.getId());
                    statisticStructure = dlTxCarrierPowerAnalisis.getStatisticStructure(bestCellName);
                    if (statisticStructure == null) {
                        GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
                        TimePeriodStructureCreator creator = new TimePeriodStructureCreator(tableRoot, bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                                handler, service);
                        statisticStructure = creator.createStructure();
                    }
                    timeNode = NeoUtils.getNodeTime(eventNode);
                    node = statisticStructure.getStatisticNode(timeNode);
                    neoArray = new NeoArray(node.getNode(), CellNonHsPowerAnalisis.ARRAY_NAME, 1, service);
                    arrNode = neoArray.findOrCreateNode(value);
                    count = (Integer)neoArray.getValueFromNode(arrNode);
                    count = count == null ? 1 : count + 1;
                    neoArray.setValueToNode(arrNode, count);
                    arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_NON_HS_POWER);
                    break;
                case 5:
                    if (value > 1000) {
                        LOGGER.error(String.format("Event node: %s, wrong vlue for UL_INTERFERENCE : %s", eventNode, value));
                        continue;
                    }
                    tableRoot = cellHsdsRequiredPowerAnalisis.getMainNode();
                    tableNode = findOrCreateTableNode(bestSector, tableRoot, "3");
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    bestCellName = String.valueOf(bestSector.getId());
                    statisticStructure = dlTxCarrierPowerAnalisis.getStatisticStructure(bestCellName);
                    if (statisticStructure == null) {
                        GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
                        TimePeriodStructureCreator creator = new TimePeriodStructureCreator(tableRoot, bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                                handler, service);
                        statisticStructure = creator.createStructure();
                    }
                    timeNode = NeoUtils.getNodeTime(eventNode);
                    node = statisticStructure.getStatisticNode(timeNode);
                    neoArray = new NeoArray(node.getNode(), CellHsdsRequiredPowerAnalisis.ARRAY_NAME, 1, service);
                    arrNode = neoArray.findOrCreateNode(value);
                    count = (Integer)neoArray.getValueFromNode(arrNode);
                    count = count == null ? 1 : count + 1;
                    neoArray.setValueToNode(arrNode, count);
                    arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_HSDSCH_REQUIRED_POWER);
                    break;
                default:
                    LOGGER.debug("Event node " + eventNode + " with type " + type + " was passed");
                    continue;
                }
                if (++countTx > 2000) {
                    countTx = 0;
                    tx.success();
                    tx.finish();
                    tx = service.beginTx();
                }
            }
            tx.success();
            model.findMatrixNodes();
        } finally {
            tx.finish();
        }

    }

    /**
     * Creates the matrix.
     */
    public void createMatrix() {
        if (model.getIntraFrequencyICDM() != null) {
            return;
        }

        LOGGER.setLevel(Level.ERROR);
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
            // minMax = NeoUtils.getMinMaxTimeOfDataset(gpeh, service);
            // RSCP_ECNO hour node
            Node parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellRscpEcNoAnalisis.PRFIX));
            // UeTxPower hour node
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, baseTime.getId());
            model.getRoot().createRelationshipTo(parentNode, baseTime.getPeriodRelation(CellUeTxPowerAnalisis.PRFIX));

            rspEcNoAnalyse = model.findCellRscpEcNoAnalisis(baseTime);
            rspEcNoAnalyse.setUseCache(true);
            ueTxPAnalyse = model.findCellUeTxPowerAnalisis(baseTime);
            ueTxPAnalyse.setUseCache(true);
            // final String id = GpehReportUtil.getMatrixLuceneIndexName(model.getNetworkName(),
            // model.getGpehEventsName(), GpehReportUtil.MR_TYPE_INTRAF);

            String eventIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getGpeh(), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.GPEH_EVENT);
            String scrCodeIndName = NeoUtils.getLuceneIndexKeyByProperty(model.getNetwork(), GpehReportUtil.PRIMARY_SCR_CODE, NodeTypes.SECTOR);
            long countEvent = 0;
            long notfoundBestCell = 0;
            long notfoundlocation = 0;
            notFullData = 0;
            long skipped = 0;
            countRow = 0;
            long time = System.currentTimeMillis();
            long countTx = 0;
            for (Node eventNode : luceneService.getNodes(eventIndName, Events.RRC_MEASUREMENT_REPORT.name())) {
                long timeEv = System.currentTimeMillis();
                countEvent++;
                // Set<Node> activeSet = getActiveSet(eventNode);
                Node bestSector = getBestSector(eventNode);
                if (bestSector == null) {
                    LOGGER.warn(String.format("Event node: %s, not found best cell", eventNode));
                    notfoundBestCell++;
                    continue;
                }
                MeasurementCell bestCell = new MeasurementCell(bestSector);
                String type = (String)eventNode.getProperty(GpehReportUtil.MR_TYPE, "");
                if (!setupLocation(bestCell) && !GpehReportUtil.MR_TYPE_UE_INTERNAL.equals(type)) {
                    LOGGER.warn(String.format("Event node: %s, not found location for best cell %s", eventNode, bestCell));
                    notfoundlocation++;
                    continue;
                }
                Set<RrcMeasurement> measSet = getRncMeasurementSet(eventNode, bestCell);
                // if (bestCell == null) {
                // LOGGER.debug(String.format("Event node: %s, not found best cell", eventNode));
                // continue;
                // }
                Node tableRoot;
                if (type.equals(GpehReportUtil.MR_TYPE_INTERF)) {
                    tableRoot = interFMatrix;
                } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {
                    tableRoot = intraFMatrix;
                } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
                    tableRoot = iRATMatrix;
                    skipped++;
                    // TODO remove after
                    LOGGER.debug("Event node " + eventNode + " with type " + type + " was passed");
                    continue;
                } else if (type.equals(GpehReportUtil.MR_TYPE_UE_INTERNAL)) {
                    tableRoot = ueTxPAnalyse.getMainNode();
                    Node tableNode = findOrCreateTableNode(bestCell.getCell(), tableRoot, type);
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    analyseBestCelltxPower(tableNode, bestCell, eventNode);
                    continue;
                } else {
                    LOGGER.error("Event node " + eventNode + " with type " + type + " was passed");
                    skipped++;
                    continue;
                }
                for (RrcMeasurement measurement : measSet) {
                    if (measurement.getScrambling() == null/*
                                                            * || (bestCell.getMeasurement() != null
                                                            * && measurement.getScrambling().equals(
                                                            * bestCell
                                                            * .getMeasurement().getScrambling()))
                                                            */
                            || measurement.getEcNo() == null) {
                        notFullData++;
                        continue;
                    }
                    MeasurementCell sector = findClosestSector(bestCell, measurement, scrCodeIndName);
                    if (sector == null || sector.getCell().equals(bestCell.getCell())) {
                        LOGGER.debug("Sector not found for PSC " + measurement.getScrambling());
                        notFullData++;
                        continue;
                    }
                    Node tableNode = findOrCreateTableNode(bestCell, sector, tableRoot, type);
                    tableNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_MATRIX_EVENT);
                    long nf = notFullData;
                    handleTableNode(tableNode, type, bestCell, sector, eventNode);
                    if (nf != notFullData) {
                        notFullData = nf + 1l;
                    }
                    if (++countTx > 2000) {
                        countTx = 0;
                        tx.success();
                        tx.finish();
                        tx = service.beginTx();
                    }
                }
                timeEv = System.currentTimeMillis() - timeEv;
                LOGGER.info("time\t" + timeEv);
                long time2 = System.currentTimeMillis() - time;
                monitor.setTaskName(String
                        .format("Handle %s events, passed %s (skipped: %s, no location %s, not found sest cell %s, no full data %s), create table rows %s, ttotal time: %s, average time: %s",
                                countEvent, notfoundBestCell + notfoundBestCell + notfoundlocation + skipped, skipped, notfoundlocation, notfoundBestCell, notFullData, countRow,
                                time2, time2 / countEvent));
            }
            tx.success();
            long time2 = System.currentTimeMillis() - time;
            LOGGER.error(String.format(
                    "Handle %s events, passed %s (skipped: %s, no location %s, not found sest cell %s, no full data %s), create table rows %s, ttotal time: %s, average time: %s",
                    countEvent, notfoundBestCell + notfoundBestCell + notfoundlocation + skipped, skipped, notfoundlocation, notfoundBestCell, notFullData, countRow, time2, time2
                            / countEvent));
            model.findMatrixNodes();
        } finally {
            tx.finish();
        }

    }

    /**
     * @param bestCell
     * @param tableRoot
     * @param type
     * @return
     */
    private Node findOrCreateTableNode(Node bestCell, Node tableRoot, String type) {
        String id = String.valueOf(bestCell.getId());
        String indexName = GpehReportUtil.getMatrixLuceneIndexName(model.getNetworkName(), model.getGpehEventsName(), type);
        Transaction tx = service.beginTx();
        try {
            Node result = luceneService.getSingleNode(indexName, id);
            if (result == null) {
                assert !"main".equals(Thread.currentThread().getName());
                result = service.createNode();
                tableRoot.createRelationshipTo(result, GeoNeoRelationshipTypes.CHILD);
                Relationship rel = result.createRelationshipTo(bestCell, ReportsRelations.BEST_CELL);
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
     * Gets the best sector.
     * 
     * @param eventNode the event node
     * @return the best sector
     */
    private Node getBestSector(Node eventNode) {
        Integer ci = (Integer)eventNode.getProperty("EVENT_PARAM_C_ID_1", null);
        Integer rnc = (Integer)eventNode.getProperty("EVENT_PARAM_RNC_ID_1", null);
        if (ci == null || rnc == null) {
            return null;
        }
        return NeoUtils.findSector(model.getNetwork(), ci, String.valueOf(rnc), luceneService, service);
    }

    /**
     * Handle table node.
     * 
     * @param tableNode the table node
     * @param type the type
     * @param bestCell the best cell
     * @param sector the sector
     * @param eventNode the event node
     */
    private void handleTableNode(Node tableNode, String type, MeasurementCell bestCell, MeasurementCell sector, Node eventNode) {
        if (type.equals(GpehReportUtil.MR_TYPE_INTERF)) {
            handleInterFrTableNode(tableNode, bestCell, sector);
            return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_INTRAF)) {
            handleIntraFrTableNode(tableNode, bestCell, sector);
            analyseBestCell(tableNode, bestCell, sector, eventNode);
            return;
        } else if (type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
            // TODO implement
            LOGGER.error("Not handled Irat event");
            return;
        } else {
            throw new IllegalArgumentException();
        }

    }

    private void analyseBestCelltxPower(Node tableNode, MeasurementCell bestCell, Node eventNode) {
        if (bestCell.getMeasurement() == null) {
            LOGGER.error(String.format("tableNode node: %s, not found measurment for best cell %s", tableNode, bestCell));
            notFullData++;
            return;
        }
        long logTime = System.currentTimeMillis();
        String bestCellName = String.valueOf(bestCell.getCell().getId());
        StatisticByPeriodStructure statisticStructure = ueTxPAnalyse.getStatisticStructure(bestCellName);
        if (statisticStructure == null) {
            GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
            TimePeriodStructureCreator creator = new TimePeriodStructureCreator(ueTxPAnalyse.getMainNode(), bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                    handler, service);
            statisticStructure = creator.createStructure();
        }
        Integer uepwr = bestCell.getMeasurement().getUeTxPower();
        Long time = NeoUtils.getNodeTime(eventNode);
        // TODO testing!
        if (uepwr != null) {
            IStatisticElementNode node = statisticStructure.getStatisticNode(time);
            NeoArray neoArray = new NeoArray(node.getNode(), CellUeTxPowerAnalisis.ARRAY_NAME, 1, service);
            Node arrNode = neoArray.findOrCreateNode(uepwr);
            Integer count = (Integer)neoArray.getValueFromNode(arrNode);
            count = count == null ? 1 : count + 1;
            neoArray.setValueToNode(arrNode, count);
            arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_UE_TX_POWER_EVENT);
        } else {
            notFullData++;
            LOGGER.info("uePwr NotFound " + bestCellName);
        }

    }

    /**
     * Analyse best cell.
     * 
     * @param tableNode the table node
     * @param bestCell the best cell
     * @param sector the sector
     * @param eventNode the event node
     */
    private void analyseBestCell(Node tableNode, MeasurementCell bestCell, MeasurementCell sector, Node eventNode) {

        if (bestCell.getMeasurement() == null) {
            LOGGER.error(String.format("tableNode node: %s, not found measurment for best cell %s", tableNode, bestCell));
            notFullData++;
            return;
        }
        if (bestCell.getMeasurement().getEcNo() == null || bestCell.getMeasurement().getRscp() == null) {
            LOGGER.error(String.format("tableNode node: %s, best cell %s: not found all necessary measurments: ecno=%s, rscp=%s", tableNode, bestCell, bestCell.getMeasurement()
                    .getEcNo(), bestCell.getMeasurement().getRscp()));
            notFullData++;
            return;
        }
        long logTime = System.currentTimeMillis();
        String bestCellName = String.valueOf(bestCell.getCell().getId());
        Long time = NeoUtils.getNodeTime(eventNode);
        StatisticByPeriodStructure statisticStructure = rspEcNoAnalyse.getStatisticStructure(bestCellName);
        if (statisticStructure == null) {
            GPEHFakeStatHandler handler = new GPEHFakeStatHandler();
            TimePeriodStructureCreator creator = new TimePeriodStructureCreator(rspEcNoAnalyse.getMainNode(), bestCellName, minMax.getLeft(), minMax.getRight(), baseTime, handler,
                    handler, service);
            statisticStructure = creator.createStructure();
        }
        IStatisticElementNode node = statisticStructure.getStatisticNode(time);
        Integer rscpValue = bestCell.getMeasurement().getRscp();
        // for others reports we should store store information about all events;
        // if (rscpValue == null) {
        // rscpValue = -1;
        // }
        Integer ecNoValue = bestCell.getMeasurement().getEcNo();
        // if (ecNoValue == null) {
        // ecNoValue = -1;
        // }
        NeoArray neoArray = new NeoArray(node.getNode(), CellRscpEcNoAnalisis.ARRAY_NAME, 2, service);
        Node arrNode = neoArray.findOrCreateNode(rscpValue, ecNoValue);
        Integer count = (Integer)neoArray.getValueFromNode(arrNode);
        count = count == null ? 1 : count + 1;
        neoArray.setValueToNode(arrNode, count);
        arrNode.createRelationshipTo(eventNode, ReportsRelations.SOURCE_RSCP_ECNO_EVENT);

        logTime = System.currentTimeMillis() - logTime;
        if (logTime > 5) {
            LOGGER.info("STAT CREATE TIME " + logTime);
        }

    }

    /**
     * Handle inter fr table node.
     * 
     * @param tableNode the table node
     * @param bestCell the best cell
     * @param sector the sector
     */
    private void handleInterFrTableNode(Node tableNode, MeasurementCell bestCell, MeasurementCell sector) {
        Transaction tx = service.beginTx();
        try {
            // Physical distance in meters
            if (!tableNode.hasProperty(MatrixProperties.DISTANCE) && model.getCrs() != null) {
                double dist = JTS.orthodromicDistance(bestCell.getCoordinate(), sector.getCoordinate(), model.getCrs());
                tableNode.setProperty(MatrixProperties.DISTANCE, dist);
            }
            // Defined NBR TRUE when Interfering Cell is defined neighboring cell,
            // FALSE when Interfering Cell is not defined as neighboring cell

            if (!tableNode.hasProperty(MatrixProperties.DEFINED_NBR)) {
                Set<Relationship> relations = NeoUtils.getRelations(bestCell.getCell(), sector.getCell(), NetworkRelationshipTypes.NEIGHBOUR, service);
                boolean def = !relations.isEmpty();
                tableNode.setProperty(MatrixProperties.DEFINED_NBR, def);
            }
            // Tier Distance - not created

            // # of MR for best cell
            // can find - calculate count of relation

            // # of MR for Interfering cell
            // can find - calculate count of relation

            Integer ecNo = sector.getMeasurement().getEcNo();

            if (ecNo >= 37) {// >=-6dB
                updateCounter(tableNode, MatrixProperties.EC_NO_PREFIX + 1);
            }
            if (ecNo >= 31) {// >=-9
                updateCounter(tableNode, MatrixProperties.EC_NO_PREFIX + 2);

            }
            if (ecNo >= 25) {// >=-12
                updateCounter(tableNode, MatrixProperties.EC_NO_PREFIX + 3);

            }
            if (ecNo >= 19) {// >=-15
                updateCounter(tableNode, MatrixProperties.EC_NO_PREFIX + 4);

            }
            if (ecNo >= 13) {// >=-18
                updateCounter(tableNode, MatrixProperties.EC_NO_PREFIX + 5);
            }
            if (sector.getMeasurement().getRscp() != null) {
                Integer rscp = sector.getMeasurement().getRscp();
                if (ecNo > 21) {// >-14
                    if (rscp < 11) {// <-105
                        updateCounter(tableNode, "RSCP1_14");// MatrixProperties.getRSCPECNOPropertyName(1,
                        // 14));
                    }
                    if (rscp < 21) {// <-95
                        updateCounter(tableNode, "RSCP2_14");// MatrixProperties.getRSCPECNOPropertyName(2,
                        // 14));
                    }
                    if (rscp < 31) {// <-85
                        updateCounter(tableNode, "RSCP3_14");// MatrixProperties.getRSCPECNOPropertyName(3,
                        // 14));
                    }
                    if (rscp < 41) {// <-75
                        updateCounter(tableNode, "RSCP4_14");// MatrixProperties.getRSCPECNOPropertyName(4,
                        // 14));
                    }
                    if (rscp >= 41) {// >=-75
                        updateCounter(tableNode, "RSCP5_14");// MatrixProperties.getRSCPECNOPropertyName(5,
                        // 14));
                    }
                }
                if (ecNo > 29) {// >-10
                    if (rscp < 11) {// <-105
                        updateCounter(tableNode, "RSCP1_10");// MatrixProperties.getRSCPECNOPropertyName(1,
                        // 10));
                    }
                    if (rscp < 21) {// <-95
                        updateCounter(tableNode, "RSCP2_10");// MatrixProperties.getRSCPECNOPropertyName(2,
                        // 10));
                    }
                    if (rscp < 31) {// <-85
                        updateCounter(tableNode, "RSCP3_10");// MatrixProperties.getRSCPECNOPropertyName(3,
                        // 10));
                    }
                    if (rscp < 41) {// <-75
                        updateCounter(tableNode, "RSCP4_10");// MatrixProperties.getRSCPECNOPropertyName(4,
                        // 10));
                    }
                    if (rscp >= 41) {// >=-75
                        updateCounter(tableNode, "RSCP5_10");// MatrixProperties.getRSCPECNOPropertyName(5,
                        // 10));
                    }
                }
            } else {
                LOGGER.error("No found rscp" + bestCell + "\t" + sector);
                notFullData++;
            }
            tx.success();
        } catch (Exception e) {
            // TODO Handle FactoryException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            tx.finish();
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
        try {
            // Physical distance in meters
            if (!tableNode.hasProperty(MatrixProperties.DISTANCE) && model.getCrs() != null) {
                double dist = JTS.orthodromicDistance(bestCell.getCoordinate(), sector.getCoordinate(), model.getCrs());
                tableNode.setProperty(MatrixProperties.DISTANCE, dist);
            }
            // Defined NBR TRUE when Interfering Cell is defined neighboring cell,
            // FALSE when Interfering Cell is not defined as neighboring cell

            if (!tableNode.hasProperty(MatrixProperties.DEFINED_NBR)) {
                Set<Relationship> relations = NeoUtils.getRelations(bestCell.getCell(), sector.getCell(), NetworkRelationshipTypes.NEIGHBOUR, service);
                boolean def = !relations.isEmpty();
                tableNode.setProperty(MatrixProperties.DEFINED_NBR, def);
            }

            // Tier Distance - not created

            // # of MR for best cell
            // can find - calculate count of relation

            // # of MR for Interfering cell
            // can find - calculate count of relation
            if (bestCell.getMeasurement() == null) {
                LOGGER.warn(String.format("tableNode node: %s, not found measurment for best cell %s", tableNode, bestCell));
                notFullData++;
                return;
            }
            double deltaDbm = -1;
            if (bestCell.getMeasurement().getEcNo() != null && sector.getMeasurement().getEcNo() != null) {
                deltaDbm = (double)Math.abs(bestCell.getMeasurement().getEcNo() - sector.getMeasurement().getEcNo()) / 2;
                if (deltaDbm <= 3) {
                    updateCounter(tableNode, MatrixProperties.EC_NO_DELTA_PREFIX + 1);
                }
                if (deltaDbm <= 6) {
                    updateCounter(tableNode, MatrixProperties.EC_NO_DELTA_PREFIX + 2);

                }
                if (deltaDbm <= 9) {
                    updateCounter(tableNode, MatrixProperties.EC_NO_DELTA_PREFIX + 3);

                }
                if (deltaDbm <= 12) {
                    updateCounter(tableNode, MatrixProperties.EC_NO_DELTA_PREFIX + 4);

                }
                if (deltaDbm <= 15) {
                    updateCounter(tableNode, MatrixProperties.EC_NO_DELTA_PREFIX + 5);
                }
            } else {
                LOGGER.warn("No found ecno" + bestCell + "\t" + sector);
            }
            if (bestCell.getMeasurement().getRscp() != null && sector.getMeasurement().getRscp() != null) {
                double deltaRscp = (double)Math.abs(bestCell.getMeasurement().getRscp() - sector.getMeasurement().getRscp()) / 1;
                if (deltaRscp <= 3) {
                    updateCounter(tableNode, MatrixProperties.RSCP_DELTA_PREFIX + 1);
                }
                if (deltaRscp <= 6) {
                    updateCounter(tableNode, MatrixProperties.RSCP_DELTA_PREFIX + 2);
                }
                if (deltaRscp <= 9) {
                    updateCounter(tableNode, MatrixProperties.RSCP_DELTA_PREFIX + 3);
                }
                if (deltaRscp <= 12) {
                    updateCounter(tableNode, MatrixProperties.RSCP_DELTA_PREFIX + 4);
                }
                if (deltaRscp <= 15) {
                    updateCounter(tableNode, MatrixProperties.RSCP_DELTA_PREFIX + 5);
                }
            } else {
                LOGGER.warn("No found rscp" + bestCell + "\t" + sector);
            }
            if (deltaDbm >= 0) {
                int deltaPosition = sector.getMeasurement().getPosition() - bestCell.getMeasurement().getPosition();
                if (deltaPosition < 0) {
                    LOGGER.warn("wrong best cell position: " + bestCell.getMeasurement().getPosition());
                    deltaPosition = -deltaPosition;
                }
                if (sector.getMeasurement().getPosition() == 1) {
                    if (deltaDbm <= 6) {
                        updateCounter(tableNode, MatrixProperties.POSITION_PREFIX + 1);
                    } else {
                        LOGGER.info("found sector with position 2 but wrong delta" + deltaDbm);
                    }
                } else if (sector.getMeasurement().getPosition() == 2) {
                    if (deltaDbm <= 6) {
                        updateCounter(tableNode, MatrixProperties.POSITION_PREFIX + 2);
                    } else {
                        LOGGER.info("found sector with position 3 but wrong delta" + deltaDbm);
                    }
                } else if (sector.getMeasurement().getPosition() == 3) {
                    if (deltaDbm <= 8) {
                        updateCounter(tableNode, MatrixProperties.POSITION_PREFIX + 3);
                    } else {
                        LOGGER.info("found sector with position 3 but wrong delta" + deltaDbm);
                    }
                } else if (sector.getMeasurement().getPosition() == 4) {
                    if (deltaDbm <= 8) {
                        updateCounter(tableNode, MatrixProperties.POSITION_PREFIX + 4);
                    } else {
                        LOGGER.info("found sector with position 3 but wrong delta" + deltaDbm);
                    }
                } else if (sector.getMeasurement().getPosition() == 5) {
                    if (deltaDbm <= 8) {
                        updateCounter(tableNode, MatrixProperties.POSITION_PREFIX + 5);
                    } else {
                        LOGGER.info("found sector with position 3 but wrong delta" + deltaDbm);
                    }
                }
            }
            tx.success();
        } catch (Exception e) {
            // TODO Handle FactoryException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
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
        Integer c = (Integer)tableNode.getProperty(propertyName, 0);
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
                result = service.createNode();
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
                    // TODO check correct distance! maybe use CRS for this
                    result.setDistance(calculateDistance(bestCell, result));
                    if (result.getDistance() > maxRange) {
                        LOGGER.debug("sector " + result.getCell() + " have too big distance: " + result.getDistance());
                        result = null;
                    }
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
     * @param type the type
     * @return the best cell
     */
    @Deprecated
    private MeasurementCell getBestCell(Set<Node> activeSet, Set<RrcMeasurement> measSet, String type) {
        if (type.equals(GpehReportUtil.MR_TYPE_INTERF) || type.equals(GpehReportUtil.MR_TYPE_IRAT)) {
            Iterator<Node> iterator = activeSet.iterator();
            MeasurementCell bestCell = iterator.hasNext() ? new MeasurementCell(iterator.next()) : null;
            setupLocation(bestCell);
            return bestCell;
        }
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
     * @param bestCell
     * @return the rnc measurement set
     */
    private Set<RrcMeasurement> getRncMeasurementSet(Node eventNode, MeasurementCell bestCell) {
        Set<RrcMeasurement> result = new TreeSet<RrcMeasurement>(new Comparator<RrcMeasurement>() {

            @Override
            public int compare(RrcMeasurement o1, RrcMeasurement o2) {
                if (o1.getEcNo() == null) {
                    return 1;
                }
                if (o2.getEcNo() == null) {
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
        Integer ueTxPower;
        Object scrBestCell = bestCell.getCell().getProperty(GpehReportUtil.PRIMARY_SCR_CODE, "");
        int i = 0;
        while (true) {
            id++;
            psc = (String)eventNode.getProperty(GpehReportUtil.GPEH_RRC_SCRAMBLING_PREFIX + id, null);
            rscp = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_RSCP_PREFIX + id, null);
            ecNo = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_ECNO_PREFIX + id, null);
            bsic = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_BSIC_PREFIX + id, null);
            ueTxPower = (Integer)eventNode.getProperty(GpehReportUtil.GPEH_RRC_MR_UE_TX_POWER_PREFIX + id, null);
            if (psc != null || rscp != null || ecNo != null || bsic != null || ueTxPower != null) {
                RrcMeasurement rrcMeasurement = new RrcMeasurement(psc, rscp, ecNo, bsic, ueTxPower);
                if (bestCell.getMeasurement() == null && rrcMeasurement.getScrambling() != null && scrBestCell.equals(rrcMeasurement.getScrambling())) {
                    bestCell.setMeasurement(rrcMeasurement);
                }
                result.add(rrcMeasurement);
            } else {
                break;
            }
        }
        for (RrcMeasurement meas : result) {
            meas.setPosition(++i);
        }
        if (bestCell.getMeasurement() != null) {
            result.remove(bestCell.getMeasurement());
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
        Set<Node> result = new LinkedHashSet<Node>();
        for (int id = 1; id <= 4; id++) {
            Integer ci = (Integer)eventNode.getProperty("EVENT_PARAM_C_ID_" + id, null);
            Integer rnc = (Integer)eventNode.getProperty("EVENT_PARAM_RNC_ID_" + id, null);
            if (ci == null || rnc == null) {
                continue;
            }
            Node asNode = NeoUtils.findSector(model.getNetwork(), ci, String.valueOf(rnc), luceneService, service);
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
        assert monitor != null;
        this.monitor = monitor;
    }

    /**
     * <p>
     * fake 1H handler - do nothing
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class GPEHFakeStatHandler implements IStatisticHandler, IStatisticStore {

        @Override
        public IStatisticElement getStatisics(final Long periodTime, final Long periodEnd) {
            return new IStatisticElement() {

                @Override
                public long getStartTime() {
                    return periodTime;
                }

                @Override
                public CallTimePeriods getPeriod() {
                    return baseTime;
                }

                @Override
                public long getEndTime() {
                    return periodEnd;
                }
            };
        }

        @Override
        public void storeStatisticElement(IStatisticElement statElem, Node node) {
        }

        @Override
        public int getStoredNodesCount() {
            return 0;
        }

    }

    /**
     * The Class StructureCell.
     */
    public static class StructureCell {

        /** The stat elem. */
        final IStatisticElement statElem;

        /** The node. */
        final Node node;

        /** The rncp ar. */
        final int[] rncpAr;

        /**
         * Instantiates a new structure cell.
         * 
         * @param statElem the stat elem
         * @param node the node
         */
        public StructureCell(IStatisticElement statElem, Node node) {
            super();
            this.statElem = statElem;
            this.node = node;
            rncpAr = new int[92];
        }

        /**
         * Store rnsp.
         */
        public void storeRNSP() {
            node.setProperty(CellReportsProperties.RNSP_ARRAY, rncpAr);
        }

        /**
         * Gets the stat elem.
         * 
         * @return the stat elem
         */
        public IStatisticElement getStatElem() {
            return statElem;
        }

        /**
         * Gets the node.
         * 
         * @return the node
         */
        public Node getNode() {
            return node;
        }

        /**
         * Gets the rncp ar.
         * 
         * @return the rncp ar
         */
        public int[] getRncpAr() {
            return rncpAr;
        }

    }

    public static class GPEHStatisticHandler implements IStatisticHandler {

        private final StatisticByPeriodStructure sourceStruc;

        /**
         * @param sourceStruc
         */
        public GPEHStatisticHandler(StatisticByPeriodStructure sourceStruc) {
            this.sourceStruc = sourceStruc;
        }

        @Override
        public IStatisticElement getStatisics(Long periodTime, Long periodEnd) {
            return new StatisticSetElement(periodTime, periodEnd, null, sourceStruc.getStatNedes(periodTime, periodEnd));
        }

    }

    public static class StatisticSetElement implements IStatisticElement {

        private final long endTime;
        private final CallTimePeriods period;
        private final long startTime;
        private final Set<IStatisticElementNode> sources;

        public StatisticSetElement(long startTime, long endTime, CallTimePeriods period, Set<IStatisticElementNode> sources) {
            super();
            this.endTime = endTime;
            this.period = period;
            this.startTime = startTime;
            this.sources = sources;
        }

        @Override
        public long getEndTime() {
            return endTime;
        }

        @Override
        public CallTimePeriods getPeriod() {
            return period;
        }

        @Override
        public long getStartTime() {
            return startTime;
        }

        /**
         * @return Returns the sources.
         */
        public Set<IStatisticElementNode> getSources() {
            return sources;
        }

    }

    public class GPEHRSCPStorer implements IStatisticStore {

        private int createdNodes = 0;

        @Override
        public void storeStatisticElement(IStatisticElement statElem, Node node) {
            createdNodes = 0;
            StatisticSetElement source = (StatisticSetElement)statElem;
            NeoArray array = new NeoArray(node, CellRscpAnalisis.ARRAY_NAME, 1, service);

            Set<NeoArray> arraySet = new HashSet<NeoArray>(0);
            for (IStatisticElementNode singlElement : source.getSources()) {
                if (NeoArray.hasArray(CellRscpEcNoAnalisis.ARRAY_NAME, singlElement.getNode(), service)) {
                    arraySet.add(new NeoArray(singlElement.getNode(), CellRscpEcNoAnalisis.ARRAY_NAME, 2, service));
                }
            }
            if (!arraySet.isEmpty()) {
                for (int rscp = 0; rscp <= 91; rscp++) {
                    Node arrayNode = null;
                    int count = 0;
                    for (int ecNo = 0; ecNo <= 49; ecNo++) {
                        for (NeoArray sArray : arraySet) {
                            Node sourceNode = sArray.getNode(rscp, ecNo);
                            if (sourceNode != null) {
                                Object value = sArray.getValueFromNode(sourceNode);
                                if (value != null) {
                                    count += (Integer)value;
                                    if (arrayNode == null && count >= 0) {
                                        arrayNode = array.findOrCreateNode(rscp);
                                        arrayNode.createRelationshipTo(sourceNode, ReportsRelations.SOURCE);
                                        createdNodes++;
                                    }
                                }
                            }
                        }
                    }
                    if (arrayNode != null) {
                        array.setValueToNode(arrayNode, count);
                    }
                }
            }
        }

        @Override
        public int getStoredNodesCount() {
            return createdNodes;
        }
    }

    public class TimeStructureStorer implements IStatisticStore {
        private final ValueType type;
        private final String arrayName;
        private int createdNodes = 0;

        public TimeStructureStorer(ValueType type, String arrayName) {
            this.type = type;
            this.arrayName = arrayName;
        }

        @Override
        public void storeStatisticElement(IStatisticElement statElem, Node node) {
            createdNodes = 0;
            StatisticSetElement source = (StatisticSetElement)statElem;
            NeoArray array = new NeoArray(node, arrayName, 1, service);

            Set<NeoArray> arraySet = new HashSet<NeoArray>(0);
            for (IStatisticElementNode singlElement : source.getSources()) {
                if (NeoArray.hasArray(arrayName, singlElement.getNode(), service)) {
                    arraySet.add(new NeoArray(singlElement.getNode(), arrayName, 1, service));
                }
            }
            int max = type.getMax3GPP();
            if (!arraySet.isEmpty()) {
                for (int u3GGP = type.getMin3GPP(); u3GGP <= max; u3GGP++) {
                    Node arrayNode = null;
                    int count = 0;
                    for (NeoArray sArray : arraySet) {
                        Node sourceNode = sArray.getNode(u3GGP);
                        if (sourceNode != null) {
                            Object value = sArray.getValueFromNode(sourceNode);
                            if (value != null) {
                                count += (Integer)value;
                                if (arrayNode == null && count >= 0) {
                                    arrayNode = array.findOrCreateNode(u3GGP);
                                    arrayNode.createRelationshipTo(sourceNode, ReportsRelations.SOURCE);
                                    createdNodes++;
                                }
                            }
                        }
                    }
                    if (arrayNode != null) {
                        array.setValueToNode(arrayNode, count);
                    }
                }
            }
        }

        @Override
        public int getStoredNodesCount() {
            return createdNodes;
        }
    }

    public class GPEHEcNoStorer implements IStatisticStore {
        private int createdNodes = 0;

        @Override
        public void storeStatisticElement(IStatisticElement statElem, Node node) {
            createdNodes = 0;
            StatisticSetElement source = (StatisticSetElement)statElem;
            NeoArray array = new NeoArray(node, CellEcNoAnalisis.ARRAY_NAME, 1, service);

            Set<NeoArray> arraySet = new HashSet<NeoArray>(0);
            for (IStatisticElementNode singlElement : source.getSources()) {
                if (NeoArray.hasArray(CellRscpEcNoAnalisis.ARRAY_NAME, singlElement.getNode(), service)) {
                    arraySet.add(new NeoArray(singlElement.getNode(), CellRscpEcNoAnalisis.ARRAY_NAME, 2, service));
                }
            }
            if (!arraySet.isEmpty()) {
                for (int ecNo = 0; ecNo <= 49; ecNo++) {
                    Node arrayNode = null;
                    int count = 0;
                    for (int rscp = 0; rscp <= 91; rscp++) {
                        for (NeoArray sArray : arraySet) {
                            Node sourceNode = sArray.getNode(rscp, ecNo);
                            if (sourceNode != null) {
                                Object value = sArray.getValueFromNode(sourceNode);
                                if (value != null) {
                                    count += (Integer)value;
                                    if (arrayNode == null && count >= 0) {
                                        arrayNode = array.findOrCreateNode(ecNo);
                                        arrayNode.createRelationshipTo(sourceNode, ReportsRelations.SOURCE);
                                        createdNodes++;
                                    }
                                }
                            }
                        }
                    }
                    if (arrayNode != null) {
                        array.setValueToNode(arrayNode, count);
                    }
                }
            }
        }

        @Override
        public int getStoredNodesCount() {
            return createdNodes;
        }
    }

    public class GPEHRscpEcNoStorer implements IStatisticStore {
        private int createdNodes = 0;

        @Override
        public void storeStatisticElement(IStatisticElement statElem, Node node) {
            createdNodes = 0;
            StatisticSetElement source = (StatisticSetElement)statElem;
            NeoArray array = new NeoArray(node, CellRscpEcNoAnalisis.ARRAY_NAME, 2, service);

            Set<NeoArray> arraySet = new HashSet<NeoArray>(0);
            for (IStatisticElementNode singlElement : source.getSources()) {
                if (NeoArray.hasArray(CellRscpEcNoAnalisis.ARRAY_NAME, singlElement.getNode(), service)) {
                    arraySet.add(new NeoArray(singlElement.getNode(), CellRscpEcNoAnalisis.ARRAY_NAME, 2, service));
                }
            }
            if (!arraySet.isEmpty()) {
                for (int ecNo = 0; ecNo <= 49; ecNo++) {
                    int count = 0;
                    for (int rscp = 0; rscp <= 91; rscp++) {
                        Node arrayNode = null;
                        for (NeoArray sArray : arraySet) {
                            Node sourceNode = sArray.getNode(rscp, ecNo);
                            if (sourceNode != null) {
                                Object value = sArray.getValueFromNode(sourceNode);
                                if (value != null) {
                                    count += (Integer)value;
                                    if (arrayNode == null && count >= 0) {
                                        arrayNode = array.findOrCreateNode(rscp, ecNo);
                                        arrayNode.createRelationshipTo(sourceNode, ReportsRelations.SOURCE);
                                        createdNodes++;
                                    }
                                }
                            }
                        }
                        if (arrayNode != null) {
                            array.setValueToNode(arrayNode, count);
                        }
                    }

                }
            }
        }

        @Override
        public int getStoredNodesCount() {
            return createdNodes;
        }
    }

    public void createUlInterferenceReport(CallTimePeriods periods) {
        if (model.getCellUlInterferenceAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createNBapBaseReports();
        Node parentNode;
        Transaction tx = service.beginTx();
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellUlInterferenceAnalisis.PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellUlInterferenceAnalisis sourceModel = model.getCellUlInterferenceAnalisis(previosPeriod);
        if (sourceModel == null) {
            createUlInterferenceReport(previosPeriod);
            sourceModel = model.getCellUlInterferenceAnalisis(previosPeriod);
        }
        IStatisticStore store = new TimeStructureStorer(ValueType.UL_INTERFERENCE, CellUlInterferenceAnalisis.ARRAY_NAME);
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellUlInterferenceAnalisis(periods);

    }

    public void createCellDlTxCarrierPowerAnalisis(CallTimePeriods periods) {
        if (model.getCellDlTxCarrierPowerAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createNBapBaseReports();
        Transaction tx = service.beginTx();
        Node parentNode;
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellDlTxCarrierPowerAnalisis.PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellDlTxCarrierPowerAnalisis sourceModel = model.getCellDlTxCarrierPowerAnalisis(previosPeriod);
        if (sourceModel == null) {
            createCellDlTxCarrierPowerAnalisis(previosPeriod);
            sourceModel = model.getCellDlTxCarrierPowerAnalisis(previosPeriod);
        }
        IStatisticStore store = new TimeStructureStorer(ValueType.DL_TX_CARRIER_POWER, CellDlTxCarrierPowerAnalisis.ARRAY_NAME);
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellDlTxCarrierPowerAnalisis(periods);
    }

    public void createCellNonHsPowerAnalisis(CallTimePeriods periods) {
        if (model.getCellNonHsPowerAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createNBapBaseReports();
        Transaction tx = service.beginTx();
        Node parentNode;
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellNonHsPowerAnalisis.PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellNonHsPowerAnalisis sourceModel = model.getCellNonHsPowerAnalisis(previosPeriod);
        if (sourceModel == null) {
            createCellNonHsPowerAnalisis(previosPeriod);
            sourceModel = model.getCellNonHsPowerAnalisis(previosPeriod);
        }
        IStatisticStore store = new TimeStructureStorer(ValueType.NON_HS_POWER, CellNonHsPowerAnalisis.ARRAY_NAME);
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellNonHsPowerAnalisis(periods);
    }

    public void createCellHsdsRequiredPowerAnalisis(CallTimePeriods periods) {
        if (model.getCellHsdsRequiredPowerAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createNBapBaseReports();
        Transaction tx = service.beginTx();
        Node parentNode;
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellHsdsRequiredPowerAnalisis.PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellHsdsRequiredPowerAnalisis sourceModel = model.getCellHsdsRequiredPowerAnalisis(previosPeriod);
        if (sourceModel == null) {
            createCellHsdsRequiredPowerAnalisis(previosPeriod);
            sourceModel = model.getCellHsdsRequiredPowerAnalisis(previosPeriod);
        }
        IStatisticStore store = new TimeStructureStorer(ValueType.HSDSCH_REQUIRED_POWER, CellHsdsRequiredPowerAnalisis.ARRAY_NAME);
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellHsdsRequiredPowerAnalisis(periods);
    }

    public void createUeTxPowerCellReport(CallTimePeriods periods) {
        if (model.getCellUeTxPowerAnalisis(periods) != null) {
            return;
        }
        assert !"main".equals(Thread.currentThread().getName());
        createMatrix();
        Node parentNode;
        Transaction tx = service.beginTx();
        try {
            parentNode = service.createNode();
            parentNode.setProperty(CellReportsProperties.PERIOD_ID, periods.getId());
            model.getRoot().createRelationshipTo(parentNode, periods.getPeriodRelation(CellUeTxPowerAnalisis.PRFIX));
            tx.success();
        } finally {
            tx.finish();
        }
        CallTimePeriods previosPeriod = getPreviosPeriod(periods);
        CellUeTxPowerAnalisis sourceModel = model.getCellUeTxPowerAnalisis(previosPeriod);
        if (sourceModel == null) {
            createUeTxPowerCellReport(previosPeriod);
            sourceModel = model.getCellUeTxPowerAnalisis(previosPeriod);
        }
        IStatisticStore store = new TimeStructureStorer(ValueType.UETXPOWER, CellUeTxPowerAnalisis.ARRAY_NAME);
        createPeriodBasedStructure(periods, parentNode, sourceModel, store);
        model.findCellUeTxPowerAnalisis(periods);

    }

    /**
     * <p>
     * Contains information about 3GPP range
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class IntRange {
        private String name;
        private int min;
        private int max;

        /**
         * @param name
         * @param min
         * @param max
         */
        public IntRange(String name, int min, int max) {
            super();
            this.name = name;
            this.min = min;
            this.max = max;
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name.
         * 
         * @param name the new name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the min.
         * 
         * @return the min
         */
        public int getMin() {
            return min;
        }

        /**
         * Sets the min.
         * 
         * @param min the new min
         */
        public void setMin(int min) {
            this.min = min;
        }

        /**
         * Gets the max.
         * 
         * @return the max
         */
        public int getMax() {
            return max;
        }

        /**
         * Sets the max.
         * 
         * @param max the new max
         */
        public void setMax(int max) {
            this.max = max;
        }

    }
/**
 * 
 * <p>
 *Time period provider
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
    public static class WrappedTimePeriodProvider extends TimePeriodStructureProvider {

        private final boolean downLevel;

        /**
         * Instantiates a new wrapped time period element.
         *
         * @param reportName the report name
         * @param element the element
         * @param service the service
         * @param cellLen the cell len - count of 3gpp value in one cell
         * @param downLevel the down level - use downd or up range for header
         */
        public WrappedTimePeriodProvider(String reportName, TimePeriodElement element, GraphDatabaseService service, int cellLen, boolean downLevel) {
            super(reportName, element, service);
            this.cellLen = cellLen;
            this.downLevel = downLevel;
        }

        private final int cellLen;

        @Override
        protected void createArrayHeader() {
            ValueType type = element.getValueType();
            for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i += cellLen) {
                headers.add(String.valueOf(downLevel ? type.getLeftBound(i) : type.getRightBound(i)));
            }
        }

        @Override
        protected void processArray(IStatisticElementNode statNode, List<Object> result) {
            ValueType type = element.getValueType();
            if (NeoArray.hasArray(element.getArrayName(), statNode.getNode(), service)) {
                NeoArray array = new NeoArray(statNode.getNode(), element.getArrayName(), service);
                for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i += cellLen) {
                    int to = i + cellLen;
                    if (to > type.getMax3GPP()) {
                        to = type.getMax3GPP() + 1;
                    }
                    int count = 0;
                    for (int j = i; j <= to; j++) {
                        Integer value = (Integer)array.getValue(j);
                        if (value != null) {
                            count += value;
                        }
                    }
                    result.add(count);
                }
            } else {
                for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i += cellLen) {
                    result.add(0);
                }
            }
        }

    }

    /**
     * <p>
     * Wrapper for parameters for create structure provider
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class TimePeriodElement {
        private AnalysisByPeriods reports;
        private String arrayName;
        private ValueType valueType;
        private CallTimePeriods period;
        private long minTime;
        private long maxTime;

        /**
         * Instantiates a new time period element.
         * 
         * @param reports the reports
         * @param arrayName the array name
         * @param valueType the value type
         * @param period the period
         * @param minTime the min time
         * @param maxTime the max time
         */
        public TimePeriodElement(AnalysisByPeriods reports, String arrayName, ValueType valueType, CallTimePeriods period, long minTime, long maxTime) {
            super();
            this.reports = reports;
            this.arrayName = arrayName;
            this.valueType = valueType;
            this.period = period;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }

        /**
         * Gets the min time.
         * 
         * @return the min time
         */
        public long getMinTime() {
            return minTime;
        }

        /**
         * Sets the min time.
         * 
         * @param minTime the new min time
         */
        public void setMinTime(long minTime) {
            this.minTime = minTime;
        }

        /**
         * Gets the max time.
         * 
         * @return the max time
         */
        public long getMaxTime() {
            return maxTime;
        }

        /**
         * Sets the max time.
         * 
         * @param maxTime the new max time
         */
        public void setMaxTime(long maxTime) {
            this.maxTime = maxTime;
        }

        /**
         * Gets the period.
         * 
         * @return the period
         */
        public CallTimePeriods getPeriod() {
            return period;
        }

        /**
         * Sets the period.
         * 
         * @param period the new period
         */
        public void setPeriod(CallTimePeriods period) {
            this.period = period;
        }

        /**
         * Gets the reports.
         * 
         * @return the reports
         */
        public AnalysisByPeriods getReports() {
            return reports;
        }

        public void setReports(AnalysisByPeriods reports) {
            this.reports = reports;
        }

        /**
         * Gets the array name.
         * 
         * @return the array name
         */
        public String getArrayName() {
            return arrayName;
        }

        /**
         * Sets the array name.
         * 
         * @param arrayName the new array name
         */
        public void setArrayName(String arrayName) {
            this.arrayName = arrayName;
        }

        /**
         * Gets the value type.
         * 
         * @return the value type
         */
        public ValueType getValueType() {
            return valueType;
        }

        /**
         * Sets the value type.
         * 
         * @param valueType the new value type
         */
        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }

    }

    public static class NBAPWattProvider extends TimePeriodStructureProvider {

        private Integer power;
        /**
         * @param reportName
         * @param element
         * @param service
         */
        public NBAPWattProvider(String reportName, TimePeriodElement element, GraphDatabaseService service) {
            super(reportName, element, service);
        }

        @Override
        protected void createHeader() {
            super.createHeader();
            headers.add(1, "maximumTransmissionPower(W)");
        }

        @Override
        protected void createArrayHeader() {
            for (double i = 0.1; i <1; i += 0.1) {
                headers.add(String.format("%1.1f",i));
            }
            for (int i = 1; i <= 100; i ++) {
                headers.add(String.valueOf(i));
            }
        }
        @Override
        public List<Object> getNextLine() {
            power = (Integer)sector.getProperty("maximumTransmissionPower",null);
            List<Object> result = super.getNextLine();
            result.add(1,power);
            return result;
        }
        @Override
        protected void processArray(IStatisticElementNode statNode, List<Object> result) {
            int startElem = result.size();
            for (int i = 0; i < 110; i ++) {
                result.add(0);
            }
            if (power==null){
                return;
            }
            double maxTrPowWatt = Math.pow(10,-3)*Math.pow(10,power/100);
            ValueType type = element.getValueType();
            if (NeoArray.hasArray(element.getArrayName(), statNode.getNode(), service)) {
                NeoArray array = new NeoArray(statNode.getNode(), element.getArrayName(), service);
                for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i++) {
                    int txpower = (int)Math.ceil(maxTrPowWatt*i/1000*10);//(txpower=TxPower*10 i - 0-1000 i/1000); 
                    if (txpower<0||txpower>1000){
                        LOGGER.error(String.format("Cell %s. Wrong TxPower %s", name,txpower/10));
                        continue;
                    }
                    Object value = array.getValue(i);
                    if (value != null&&((Integer)value)!=0) {
                        int ind=0;
                        if (txpower<10){
                            ind=startElem+txpower;
                        }else{
                            ind=startElem+8+(int)Math.ceil(txpower/10);
                        }
                        result.set(ind,(Integer) result.get(ind)+(Integer)value);
                    }
                    
                }
            }
        }
    }

    /**
     * <p>
     * Time period structure provider
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class TimePeriodStructureProvider implements IExportProvider {

        private Iterator<Relationship> bestCellIteranor = null;
        private Iterator<IStatisticElementNode> iter = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        final SimpleDateFormat dateFormat2 = new SimpleDateFormat("HHmm");
        protected String name = "";
        private final Calendar calendar = Calendar.getInstance();
        private final String reportName;
        protected List<String> headers = null;// new ArrayList<String>();
        protected TimePeriodElement element;
        protected final GraphDatabaseService service;
        protected Node sector;

        /**
         * Instantiates a new time period structure provider.
         * 
         * @param element the element
         */
        public TimePeriodStructureProvider(String reportName, TimePeriodElement element, GraphDatabaseService service) {
            this.reportName = reportName;
            this.element = element;
            this.service = service;
        }

        /**
         * Creates the header.
         */
        protected void createHeader() {
            headers.clear();
            headers.add("Cell Name");
            headers.add("Date");
            headers.add("Time");
            headers.add("Resolution");
            createArrayHeader();
        }

        /**
         * Creates the array header.
         */
        protected void createArrayHeader() {
            ValueType type = element.getValueType();
            for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i++) {

                headers.add(String.valueOf(type.getLeftBound(i)));
            }
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean hasNextLine() {
            if (bestCellIteranor == null) {
                bestCellIteranor = element.getReports().getMainNode().getRelationships(Direction.OUTGOING).iterator();
            }
            if (iter == null || !iter.hasNext()) {
                defineStructIterator();
            }
            return iter != null && iter.hasNext();
        }

        @Override
        public List<Object> getNextLine() {
            IStatisticElementNode statNode = iter.next();

            List<Object> result = new ArrayList<Object>();
            result.add(name);
            calendar.setTimeInMillis(statNode.getStartTime());
            result.add(dateFormat.format(calendar.getTime()));
            result.add(dateFormat2.format(calendar.getTime()));
            result.add(element.getReports().getPeriod().getId());
            processArray(statNode, result);
            return result;
        }

        /**
         * Process array.
         * 
         * @param statNode the stat node
         * @param result the result
         */
        protected void processArray(IStatisticElementNode statNode, List<Object> result) {
            ValueType type = element.getValueType();
            if (NeoArray.hasArray(element.getArrayName(), statNode.getNode(), service)) {
                NeoArray array = new NeoArray(statNode.getNode(), element.getArrayName(), service);
                for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i++) {
                    Object value = array.getValue(i);
                    if (value == null) {
                        value = 0;
                    }
                    result.add(value);
                }
            } else {
                for (int i = type.getMin3GPP(); i <= type.getMax3GPP(); i++) {
                    result.add(0);
                }
            }
        }

        /**
         * Define struct iterator.
         */
        private void defineStructIterator() {
            while (bestCellIteranor.hasNext()) {
                Relationship rel = bestCellIteranor.next();
                String bestCellId = StatisticNeoService.getBestCellId(rel.getType().name());
                if (bestCellId != null) {
                    sector = service.getNodeById(Long.parseLong(bestCellId));
                    name = (String)sector.getProperty("userLabel", "");
                    if (StringUtil.isEmpty(name)) {
                        name = NeoUtils.getNodeName(sector, service);
                    }
                    StatisticByPeriodStructure structure = element.getReports().getStatisticStructure(bestCellId);
                    iter = structure.getStatNedes(element.getMinTime(), element.getMaxTime()).iterator();
                    if (iter.hasNext()) {
                        return;
                    }
                }
            }
        }

        @Override
        public List<String> getHeaders() {
            if (headers == null) {
                headers = new ArrayList<String>();
                createHeader();
            }
            return headers;
        }

        @Override
        public String getDataName() {
            return reportName;
        }
    };
}
