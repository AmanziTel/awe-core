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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.neighbours.gpeh.Calculator3GPPdBm.ValueType;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.preferences.NeoCorePreferencesConstants;
import org.amanzi.neo.core.utils.export.CommonExporter;
import org.amanzi.neo.core.utils.export.IExportHandler;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.utils.Pair;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
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
    
    /** The LOGGER. */
    public static Logger LOGGER = Logger.getLogger(org.amanzi.awe.neighbours.gpeh.GpehReportCreator.class);
    /** The service. */
    private final GraphDatabaseService service;

    /** The lucene service. */
    private final LuceneIndexService luceneService;

    /** The gpeh. */
    private final Node gpeh;

    /** The monitor. */
    @SuppressWarnings("unused")
    private IProgressMonitor monitor;

    /** The min max. */
    @SuppressWarnings("unused")
    private final Pair<Long, Long> minMax;
    
    /** The max range. */
    private int maxRange;
    
    /** The network. */
    private final Node network;
    private char separator;
    private char quote;
    private String extension;

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
        this.network = network;
        this.gpeh = gpeh;
        this.service = service;
        this.luceneService = luceneService;
        this.separator = '\t';
        this.quote = 0;//0- no quote see class au.com.bytecode.opencsv.CSVWriter
        monitor = new NullProgressMonitor();
        maxRange = NeoCorePlugin.getDefault().getPreferenceStore().getInt(NeoCorePreferencesConstants.MAX_SECTOR_DISTANSE);
        if (maxRange == 0) {
            maxRange = 30000;
        }
        minMax = NeoUtils.getMinMaxTimeOfDataset(gpeh, service);
        extension=".txt";
    }

    /**
     * Gets the extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     *
     * @param extension the new extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Gets the separator.
     *
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Sets the separator.
     *
     * @param separator the new separator
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Gets the quote.
     *
     * @return the quote
     */
    public char getQuote() {
        return quote;
    }

    /**
     * Sets the quote.
     *
     * @param quote the new quote
     */
    public void setQuote(char quote) {
        this.quote = quote;
    }

    /**
     * Export to csv.
     *
     * @param outputDir the output dir
     * @param report the report type
     * @param period the time period
     * @param monitor the monitor
     */
    public void exportToCSV(File outputDir, GpehReportType report, CallTimePeriods period, IProgressMonitor monitor) {
        if (haveMultipleReports(report)) {
            List<IExportProvider> providers = defineProviders(report, period);
            for (IExportProvider provider : providers) {
                if (provider.isValid()) {
                    IExportHandler handler = createCSVHandler(outputDir, provider, monitor, separator, quote,report, period);
                    Transaction tx = service.beginTx();
                    try {
                        CommonExporter export = new CommonExporter(handler, provider);
                        export.process(monitor);
                    } finally {
                        tx.finish();
                        monitor.worked(1);
                    }
                }
            }
        } else {
            File output = new File(outputDir, generateReportName(report, period));
            IExportHandler handler = new CommonCSVHandler(output, monitor, separator, quote);
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
    }

    /**
     * Creates the csv handler.
     *
     * @param outputDir the output dir
     * @param provider the provider
     * @param monitor the monitor
     * @param c the c
     * @param quote2 
     * @param report the report
     * @param period the period
     * @return the i export handler
     */
    private IExportHandler createCSVHandler(File outputDir, IExportProvider provider, IProgressMonitor monitor, char separator, char quote, GpehReportType report, CallTimePeriods period) {
        String fileName = generateReportName(report, period);
        if (provider instanceof NBAPWattExportProvider) {
            fileName = "WATT_" + fileName;
        }
        return new CommonCSVHandler(new File(outputDir, fileName), monitor, separator,quote);
    }

    /**
     * Define providers.
     *
     * @param report the report
     * @param period the period
     * @return the list
     */
    private List<IExportProvider> defineProviders(GpehReportType report, CallTimePeriods period) {
        switch (report) {
        case NBAP_DL_TX_CARRIER_POWER:
            return getCellDlTxCarrierPowerProvider(period);
        case NBAP_NON_HS_POWER:
            return getCellNonHsPowerPowerProvider(period);
        case NBAP_HSDS_REQUIRED_POWER:
            return getCellHsdsRequiredPowerProvider(period);
        default:
            return Collections.<IExportProvider> emptyList();
        }

    }

    /**
     * Have multiple reports.
     *
     * @param report the report
     * @return true, if successful
     */
    private boolean haveMultipleReports(GpehReportType report) {
        return report == GpehReportType.NBAP_DL_TX_CARRIER_POWER || report == GpehReportType.NBAP_NON_HS_POWER || report == GpehReportType.NBAP_HSDS_REQUIRED_POWER;
    }

    /**
     * Generate report name.
     * 
     * @param report the report
     * @param period the period
     * @return the string
     */
    private String generateReportName(GpehReportType report, CallTimePeriods period) {
        return new StringBuilder(report.getId()).append(extension).toString();
    }

    /**
     * Define provider.
     * 
     * @param report the report type
     * @param period the period
     * @return the i export provider
     */
    private IExportProvider defineProvider(GpehReportType report, CallTimePeriods period) {
        switch (report) {
        case UE_TX_POWER_ANALYSIS:
            return getUeTxPowerCellProvider(period);
        case CELL_RF_CORRELATION:
            return getCellCorrelationProvider(period);
        case IDCM_INTRA:
            return getIntraMatrixProvider();
        case IDCM_INTER:
            return getInterMatrixProvider();
        case CELL_RSCP_ANALYSIS:
            return getCellRSCPProvider(period);
        case CELL_ECNO_ANALYSIS:
            return getCellEcnoProvider(period);
        case NBAP_UL_INTERFERENCE:
            return getUlInterferenceCellProvider(period);
        case SHO_ANALYSIS:
            return getCellShoExportProvider();
        case PILOT_POLUTION:
            return getPilotPolutionsExportProvider();
        case KILLER_CELL:
            return getKillerCellExportProvider();
        default:
            return null;
        }
    }

    /**
     * Gets the cell correlation provider.
     *
     * @param period the period
     * @return the cell correlation provider
     */
    private IExportProvider getCellCorrelationProvider(final CallTimePeriods period) {
        return new CellCorrelationProvider(gpeh, network, service, period, luceneService);
    }

    /**
     * Gets the inter matrix provider.
     *
     * @return the inter matrix provider
     */
    private IExportProvider getInterMatrixProvider() {
        return new InterMatrixProvider(gpeh, network, service, CallTimePeriods.ALL, luceneService);
    }

    /**
     * Gets the cell ecno provider.
     * 
     * @param period the period
     * @return the cell ecno provider
     */
    private IExportProvider getCellEcnoProvider(final CallTimePeriods period) {
        return new CellEcNoProvider(gpeh, network, service, period, luceneService);
        
    }
    /**
     * Gets the cell ecno provider.
     * 
     * @param period the period
     * @return the cell ecno provider
     */
    private IExportProvider getCellShoExportProvider() {
        return new CellShoExportProvider(gpeh, network, service,  luceneService);
        
    }

    /**
     * Gets the pilot polutions export provider.
     *
     * @return the pilot polutions export provider
     */
    private IExportProvider getPilotPolutionsExportProvider() {
        return new PilotPolutionsExportProvider(gpeh, network, service,  luceneService);

    }

    /**
     * Gets the cell rscp provider.
     * 
     * @param period the period
     * @return the cell rscp provider
     */
    private IExportProvider getCellRSCPProvider(final CallTimePeriods period) {
        return new CellRscpProvider(gpeh, network, service, period, luceneService);
    }

    /**
     * Gets the intra matrix provider.
     *
     * @return the intra matrix provider
     */
    private IExportProvider getIntraMatrixProvider() {
        return new IntraMatrixProvider(gpeh, network, service, CallTimePeriods.ALL, luceneService);
    }

    /**
     * Gets the killer cell export provider.
     *
     * @return the killer cell export provider
     */
    private IExportProvider getKillerCellExportProvider() {
        return new KillerCellExportProvider(gpeh, network, service, luceneService);
    }

    /**
     * Gets the ue tx power cell provider.
     * 
     * @param period the period
     * @return the ue tx power cell provider
     */
    private IExportProvider getUlInterferenceCellProvider(final CallTimePeriods period) {
        return new RtwpProvider(gpeh, network, service, period, "Ue tx power analysis", luceneService);
    }

    /**
     * Gets the cell dl tx carrier power provider.
     * 
     * @param period the period
     * @return the cell dl tx carrier power provider
     */
    private List<IExportProvider> getCellDlTxCarrierPowerProvider(final CallTimePeriods period) {
        List<IExportProvider> result = new ArrayList<IExportProvider>();
        result.add(new NBAPWattExportProvider(gpeh, network, service, ValueType.DL_TX_CARRIER_POWER, GpehRelationshipType.TOTAL_DL_TX_POWER, period,
                "Ue tx power analysis", luceneService));
        result.add(new NbapDbmExportProvider(gpeh, network, service, ValueType.DL_TX_CARRIER_POWER, GpehRelationshipType.TOTAL_DL_TX_POWER, period,
                "Ue tx power analysis", luceneService));
        return result;
    }

    /**
     * Gets the cell hsds required power provider.
     *
     * @param period the period
     * @return the cell hsds required power provider
     */
    private List<IExportProvider> getCellHsdsRequiredPowerProvider(final CallTimePeriods period) {
        List<IExportProvider> result = new ArrayList<IExportProvider>();
        result.add(new NBAPWattExportProvider(gpeh, network, service, ValueType.HSDSCH_REQUIRED_POWER, GpehRelationshipType.HS_DL_TX_RequiredPower, period,
                "HSDSCH_REQUIRED_POWER analysis", luceneService));
        result.add(new NbapDbmExportProvider(gpeh, network, service, ValueType.HSDSCH_REQUIRED_POWER, GpehRelationshipType.HS_DL_TX_RequiredPower, period,
                "HSDSCH_REQUIRED_POWER analysis", luceneService));
        return result;
    }

    /**
     * Gets the cell non hs power power provider.
     *
     * @param period the period
     * @return the cell non hs power power provider
     */
    private List<IExportProvider> getCellNonHsPowerPowerProvider(final CallTimePeriods period) {
        List<IExportProvider> result = new ArrayList<IExportProvider>();
        result.add(new NBAPWattExportProvider(gpeh, network, service, ValueType.NON_HS_POWER, GpehRelationshipType.R99_DL_TX_POWER, period,
                "NON_HS_POWER analysis", luceneService));
        result.add(new NbapDbmExportProvider(gpeh, network, service, ValueType.NON_HS_POWER, GpehRelationshipType.R99_DL_TX_POWER, period,
                "NON_HS_POWER analysis", luceneService));
        return result;
    }

    /**
     * Gets the ue tx power cell provider.
     * 
     * @param period the period
     * @return the ue tx power cell provider
     */
    private IExportProvider getUeTxPowerCellProvider(final CallTimePeriods period) {
        return new ExportProvider3GPP(gpeh, network, service, ValueType.UETXPOWER, GpehRelationshipType.UE_TX_POWER, period, "Ue tx power analysis",
                luceneService);
    }


    /**
     * Sets the monitor.
     *
     * @param monitor the new monitor
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }
}
