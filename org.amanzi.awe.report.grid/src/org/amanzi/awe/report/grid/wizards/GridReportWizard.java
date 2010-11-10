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

package org.amanzi.awe.report.grid.wizards;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.report.charts.ChartType;
import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.grid.GridReportPlugin;
import org.amanzi.awe.report.grid.export.XlsStatisticsExporter;
import org.amanzi.awe.report.grid.util.ChartUtilities;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistics.builder.StatisticsBuilder;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.engine.KpiBasedHeader;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.awe.statistics.template.Condition;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.template.Threshold;
import org.amanzi.awe.statistics.template.Template.DataType;
import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.neo.loader.grid.IDENLoader;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.utils.Pair;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * Grid NetView wizard
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridReportWizard extends Wizard implements IWizard {
    public enum PeriodType {
        DAILY, HOURLY;
    }

    public enum OutputType {
        XLS, PDF, PNG;
    }

    public enum CategoryType {
        SYSTEM("network"), SITE("site"), CELL("cell");
        private String networkType;

        /**
         * @param networkType
         */
        private CategoryType(String networkType) {
            this.networkType = networkType;
        }

        public String getNetworkType() {
            return networkType;
        }
    }

    public enum AnalysisType {
        KPI, SYSTEM_SUMMARY, SYSTEM_EVENTS;
    }

    public enum Scope {
        ALL, SELECTED, WORST_10, WORST_20, WORST_30;
    }

    // enums
    private OutputType outputType;
    private CategoryType categoryType;
    private AnalysisType analysisType;
    private Scope scope;
    private PeriodType period;

    private static final Ruby ruby = KPIPlugin.getDefault().getRubyRuntime();
    public static final String MODULE_NAME = "KPI::IDEN.";
    public static final String WIZARD_TITLE = "Grid-NetView wizard";
    private boolean isExportToPdfRequired;
    protected boolean isExportToXlsRequired;
    protected boolean isWorstSitesReportRequired;
    private String directory;
    private String outputDirectory;
    protected CallTimePeriods aggregation;
    private GridWizardPageStep1 loadDataPage;
    private GridWizardPageStep2 viewResultPage;
    private GraphDatabaseService service;
    private Node datasetNode;
    protected ArrayList<String> idenKPIs;
    protected DatasetService dsService = NeoServiceFactory.getInstance().getDatasetService();
    protected Statistics statistics;
    protected ArrayList<String> displayNames;
    protected ChartType chartType;
    protected Statistics networkStatistics;
    private boolean isLoaded = false;
    private String kpi;
    protected String networkLevel;
    protected Integer elementsPerReport;
    protected String networkElement;
    protected boolean individualReportRequired;
    private SystemSummaryPage systemSummaryPage;
    private SelectCategoryPage selectCategoryPage;
    private SelectCellPage selectCellPage;
    private SelectSitePage selectSitePage;
    private SelectSystemPage selectSystemPage;
    private SelectOutputTypePage selectOutputTypePage;
    private SelectAnalysisTypePage selectAnalysisTypePage;
    private SelectPeriodPage selectPeriodPage;

    private List<String> selection;
    protected long startTime;
    protected long endTime;

    @Override
    public void addPages() {
        getContainer().getShell().setSize(500, 600);
        loadDataPage = new GridWizardPageStep1(GridWizardPageStep1.class.getName());
        viewResultPage = new GridWizardPageStep2();
        systemSummaryPage = new SystemSummaryPage();
        selectPeriodPage = new SelectPeriodPage();
        selectCategoryPage = new SelectCategoryPage();
        selectAnalysisTypePage = new SelectAnalysisTypePage();
        selectSystemPage = new SelectSystemPage();
        selectSitePage = new SelectSitePage();
        selectCellPage = new SelectCellPage();
        selectOutputTypePage = new SelectOutputTypePage();

        addPage(new LoadDataPage());
        addPage(selectPeriodPage);
        addPage(selectAnalysisTypePage);
        // addPage(new Page3());
        addPage(selectCategoryPage);
        addPage(selectSystemPage);
        // addPage(new Page5());
        addPage(selectSitePage);
        // addPage(new Page6());
        addPage(selectCellPage);
        addPage(systemSummaryPage);
        addPage(selectOutputTypePage);
        addPage(new TrendChartPage());
        addPage(new ResultingPage());
        // old pages
//        addPage(loadDataPage);
//        addPage(viewResultPage);

        setNeedsProgressMonitor(true);
        service = NeoServiceProviderUi.getProvider().getService();

    }

    @Override
    public boolean performFinish() {

//        updateUserChoice();
//        if (!isLoaded) {
//            loadData();
//            buildStatistics();
//        }
//        try {
//            getContainer().run(true, true, new IRunnableWithProgress() {
//
//                @Override
//                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//
//                    try {
//                        switch (getOutputType()) {
//                        case PDF:
//                            generatePdfReports();
//                            break;
//                        case XLS:
//                            break;
//                        case PNG:
//                        }
//                        if (isExportToPdfRequired) {
//                            updateMessage("Exporting statistics to PDF...");
//                            long t = System.currentTimeMillis();
//                            if (isWorstSitesReportRequired) {
//                                exportTopElementsToPdf();
//                            } else if (individualReportRequired) {
//                                generateIndividuaPdfReport();
//                            } else {
//                                // exportToPdfOld(datasetNode, idenKPIs);
//                                generatePdfReports();
//                            }
//                            System.out.println("Finished exporting to pdf in " + (System.currentTimeMillis() - t) / 1000
//                                    + " seconds");
//                        }
//                        if (isExportToXlsRequired) {
//                            exportToXls();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        loadDataPage.setPageComplete(false);
//                        loadDataPage.setErrorMessage("An error occured: " + e.getLocalizedMessage());
//                        // TODO Handle IOException
//                        throw (RuntimeException)new RuntimeException().initCause(e);
//                    }
//
//                }
//
//            });
//        } catch (InvocationTargetException e) {
//            // TODO Handle InvocationTargetException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        } catch (InterruptedException e) {
//            // TODO Handle InterruptedException
//            throw (RuntimeException)new RuntimeException().initCause(e);
//        }
//        final File[] files = getFilesToLoad(directory);
//        final int filesCount = files.length;

        return true;
    }

    /**
     * Adds dial chart
     * 
     * @param report report
     * @param siteName element name
     * @param dataset chart dataset
     */
    private void addDialChart(Report report, String siteName, final DefaultValueDataset dataset) {
        Chart chart = new Chart(siteName);
        chart.addSubtitle(kpi);
        chart.setChartType(ChartType.DIAL);
        chart.setDomainAxisLabel("Value");
        chart.setRangeAxisLabel("Time");
        chart.setWidth(250);
        chart.setHeight(250);
        DialPlot dialplot = new DialPlot();
        Charts.applyDefaultSettingsToDataset(dialplot, dataset, 0);
        Charts.applyMainVisualSettings(dialplot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(dialplot);
        report.addPart(chart);
    }

    /**
     * Gets template file name
     * 
     * @return
     */
    protected String getTemplateFileName() {
//        if (chartType.equals(ChartType.BAR)) {
//            return "bar.xls";
//        }
        return "line.xls";
    }

    /**
     * @param datasetNode
     * @param kpis
     * @throws IOException
     */
    @Deprecated
    protected void exportToPdfOld(Node datasetNode, List<String> kpis) throws IOException {
        URL rubyFolder = FileLocator.toFileURL(GridReportPlugin.getDefault().getBundle().getEntry("ruby"));
        URL scriptURL = FileLocator.toFileURL(GridReportPlugin.getDefault().getBundle().getEntry("ruby/automation.rb"));
        ReportModel reportModel = new ReportModel(new String[] {rubyFolder.getPath()}, new String[] {scriptURL.getPath()});
        // reportModel.updateModel(readScript());
        final Object datasetName = datasetNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        for (String kpi : kpis) {
            String kpiDisplayName = kpi;
            final IRubyObject res = ruby.evalScriptlet(MODULE_NAME + "get_annotation(:" + kpi + ")");
            if (res instanceof RubyHash) {
                RubyHash result = (RubyHash)res;
                for (Object key : result.keySet()) {
                    if ("name".equalsIgnoreCase(key.toString())) {
                        kpiDisplayName = result.get(key).toString();
                        break;
                    }
                }
            }
            if (isWorstSitesReportRequired) {
                if (kpi.contains("rate")) {
                    reportModel.updateModel(String.format("generate_dial_charts('%s','%s',:%s,:%s)", datasetName, kpiDisplayName,
                            aggregation, "time"));
                }
            }
            reportModel.updateModel(String.format("generate_reports('%s','%s',:%s,:%s)", datasetName, kpiDisplayName, aggregation,
                    "time"));
        }
    }

    private File[] getFilesToLoad(String directory) {
        File dir = new File(directory);
        final File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".Z");
            }

        });
        return files;
    }

    /**
     * Updates the wizard's message in UI thread
     * 
     * @param message a new wizard message
     */
    public void updateMessage(final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                loadDataPage.setMessage(message);
            }
        });
    }

    /**
     * Loads data
     */
    void loadData() {
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Loading files from " + directory, 1);
                    updateMessage("Loading files...");
                    IDENLoader gridLoader = new IDENLoader(directory, "grid", null, service);
                    try {
                        gridLoader.run(monitor);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO Handle IOException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                    monitor.done();

                    System.out.println("Finished loading");

                    datasetNode = gridLoader.getDatasetNode();
                    if (datasetNode == null) {
                        datasetNode = dsService.getRootNode(LoaderUiUtils.getAweProjectName(), "ecl_stat.unl.Z", NodeTypes.OSS);
                    }
                    isLoaded = true;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * Builds statistics
     */
    void buildStatistics() {
        updateMessage("Building statistics...");
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    Object[] kpisFound = ((RubyArray)ruby
                            .evalScriptlet("KPI::IDEN.singleton_methods.sort.select{|m| !(Annotations.hidden_methods.include? m)}"))
                            .toArray();
                    idenKPIs = new ArrayList<String>(kpisFound.length);
                    for (Object kpi : kpisFound) {
                        idenKPIs.add(kpi.toString());
                    }
                    displayNames = new ArrayList<String>(idenKPIs.size());

                    StatisticsBuilder builder = new StatisticsBuilder(service, datasetNode, ruby);
                    Template template = new Template("test template", DataType.GRID);

                    System.out.println("kpis found: " + idenKPIs);
                    for (String kpi : idenKPIs) {
                        String kpiDisplayName = kpi;
                        String kpiUnit = "";
                        Number threshold = null;
                        final String fullKpiName = GridReportWizard.MODULE_NAME + kpi;
                        final IRubyObject res = ruby.evalScriptlet(GridReportWizard.MODULE_NAME + "get_annotation(:" + kpi + ")");
                        if (res instanceof RubyHash) {
                            RubyHash result = (RubyHash)res;
                            System.out.println(String.format("Found %s annotations for '%s':", result.keySet().size(), kpi));
                            for (Object key : result.keySet()) {
                                String strKey = key.toString();
                                String strResult = result.get(key).toString();
                                if ("name".equalsIgnoreCase(strKey)) {
                                    kpiDisplayName = strResult;
                                }
                                if ("unit".equalsIgnoreCase(strKey)) {
                                    kpiUnit = strResult;
                                }
                                if ("threshold".equalsIgnoreCase(strKey)) {
                                    threshold = Double.parseDouble(strResult);
                                    System.out.println("Threshold for '" + kpiDisplayName + "': " + threshold);
                                }
                                System.out.println("\t" + key + ":\t" + result.get(key));
                            }
                        }
                        displayNames.add(kpiDisplayName);
                        KpiBasedHeader kpiBasedHeader1 = new KpiBasedHeader(ruby, fullKpiName, kpiDisplayName);
                        template.add(kpiBasedHeader1, AggregationFunctions.AVERAGE, threshold == null ? null : new Threshold(
                                threshold, Condition.LT), kpiDisplayName);

                    }
                    networkStatistics = builder.buildStatistics(template, "network", aggregation, monitor);
                    statistics = builder.buildStatistics(template, getCategoryType().getNetworkType(), aggregation, monitor);
                    System.out.println("Finished building stats");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    /**
     * Builds statistics
     */
    @Deprecated
    void buildStatisticsOld() {
        updateMessage("Building statistics...");
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    Object[] kpisFound = ((RubyArray)ruby
                            .evalScriptlet("KPI::IDEN.singleton_methods.sort.select{|m| !(Annotations.hidden_methods.include? m)}"))
                            .toArray();
                    idenKPIs = new ArrayList<String>(kpisFound.length);
                    for (Object kpi : kpisFound) {
                        idenKPIs.add(kpi.toString());
                    }
                    displayNames = new ArrayList<String>(idenKPIs.size());

                    StatisticsBuilder builder = new StatisticsBuilder(service, datasetNode, ruby);
                    Template template = new Template("test template", DataType.GRID);

                    System.out.println("kpis found: " + idenKPIs);
                    for (String kpi : idenKPIs) {
                        String kpiDisplayName = kpi;
                        String kpiUnit = "";
                        Number threshold = null;
                        final String fullKpiName = GridReportWizard.MODULE_NAME + kpi;
                        final IRubyObject res = ruby.evalScriptlet(GridReportWizard.MODULE_NAME + "get_annotation(:" + kpi + ")");
                        if (res instanceof RubyHash) {
                            RubyHash result = (RubyHash)res;
                            System.out.println(String.format("Found %s annotations for '%s':", result.keySet().size(), kpi));
                            for (Object key : result.keySet()) {
                                if ("name".equalsIgnoreCase(key.toString())) {
                                    kpiDisplayName = result.get(key).toString();
                                }
                                if ("unit".equalsIgnoreCase(key.toString())) {
                                    kpiUnit = result.get(key).toString();
                                }
                                if ("threshold".equalsIgnoreCase(key.toString())) {
                                    threshold = Double.parseDouble(result.get(key).toString());
                                }
                                System.out.println("\t" + key + ":\t" + result.get(key));
                            }
                        }
                        displayNames.add(kpiDisplayName);
                        KpiBasedHeader kpiBasedHeader1 = new KpiBasedHeader(ruby, fullKpiName, kpiDisplayName);
                        template.add(kpiBasedHeader1, AggregationFunctions.AVERAGE, threshold == null ? null : new Threshold(
                                threshold, Condition.LT), kpiDisplayName);

                    }
                    networkStatistics = builder.buildStatistics(template, "network", aggregation, monitor);
                    statistics = builder.buildStatistics(template, networkLevel, aggregation, monitor);
                    System.out.println("Finished building stats");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    // private String readScript() throws IOException {
    // URL scriptURL =
    // FileLocator.toFileURL(GridReportPlugin.getDefault().getBundle().getEntry("ruby/automation.rb"));
    // String path = scriptURL.getPath();
    // StringBuffer sb = new StringBuffer();
    // final Scanner scanner = new Scanner(new File(path));
    // while (scanner.hasNext()) {
    // sb.append(scanner.nextLine()).append("\n");
    // }
    // scanner.close();
    // System.out.println("Script:\n" + sb);
    // return sb.toString();
    // }

    /**
     * Updates user choices (from different pages) in UI thread
     */
    private void updateUserChoice() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                // directory = loadDataPage.getDirectory();
                // outputDirectory = loadDataPage.getOutputDirectory();
                // isExportToPdfRequired = loadDataPage.isExportToPdfRequired();
                // isExportToXlsRequired = loadDataPage.isExportToXlsRequired();
                // networkLevel = loadDataPage.getNetworkLevel();
                //
                // aggregation = loadDataPage.getAggregation();
                // chartType = viewResultPage.getChartType();
                // // kpi = viewResultPage.getKpi();
                // networkElement = viewResultPage.getNetworkElement();
                //
                // isWorstSitesReportRequired = viewResultPage.isWorstSitesReportRequired();
                // elementsPerReport = viewResultPage.elementsPerReport();
                //
                // individualReportRequired = viewResultPage.isIndividualReportRequired();
            }
        });
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        updateUserChoice();
        return super.getNextPage(page);
    }

    public GraphDatabaseService getService() {
        return service;
    }

    public String getDirectory() {
        return directory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public static Ruby getRuby() {
        return ruby;
    }

    public Node getDatasetNode() {
        return datasetNode;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Statistics getNetworkStatistics() {
        return networkStatistics;
    }

    public CallTimePeriods getAggregation() {
        return aggregation;
    }

    public String getNetworkLevel() {
        return networkLevel;
    }

    public ArrayList<String> getIdenKPIs() {
        return displayNames;
    }

    public ChartType getChartType() {
        return ChartType.COMBINED;
    }

    /**
     * Generates reports for all network elements
     */
    public void generatePdfReports() {
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    long t = System.currentTimeMillis();
                    final Map<String, Map<String, TimeSeries[]>> datasets;
                    if (selection != null) {
                        Double threshold = kpi.toLowerCase().contains("rate") ? 5d : null;
                        // TODO use threshold from template
                        datasets = ChartUtilities.createChartDatasets(getStatistics(), getAggregation().getId(), kpi,
                                getChartType(), selection, startTime, endTime, threshold);
                    } else {
                        datasets = ChartUtilities.createChartDatasets(getStatistics(), getAggregation().getId(), getChartType());
                    }
                    System.out.println("Finished generating datasets in " + (System.currentTimeMillis() - t) / 1000 + " seconds");
                    Map<String, Report> reportsPerKPI = new HashMap<String, Report>();
                    for (Entry<String, Map<String, TimeSeries[]>> entry : datasets.entrySet()) {
                        String siteName = entry.getKey();
                        for (Entry<String, TimeSeries[]> e : entry.getValue().entrySet()) {
                            String kpiName = e.getKey();
                            Report report = reportsPerKPI.get(kpiName);
                            if (report == null) {
                                report = new Report("KPI report");
                                // report.addPart(chart);
                                String outputDirectory = getOutputDirectory();
                                report.setFile(outputDirectory + File.separatorChar + kpiName + ".pdf");
                                reportsPerKPI.put(kpiName, report);
                            }

                            Chart chart = new Chart(siteName);
                            chart.addSubtitle(kpiName);
                            ChartType chartType = ChartType.COMBINED;// TODO
                            chart.setChartType(chartType);
                            chart.setDomainAxisLabel("Value");
                            chart.setRangeAxisLabel("Time");
                            chart.setWidth(400);
                            chart.setHeight(300);
                            TimeSeries[] series = e.getValue();
                            switch (chartType) {
                            case COMBINED:
                                addDataToCombinedChart(chart, series);
                                break;
                            case TIME:
                                addDataToTimeChart(chart, series);
                                break;
                            // case DIAL:
                            // DialPlot dialplot = new DialPlot();
                            // Charts.applyDefaultSettingsToDataset(dialplot,
                            // ChartUtilities.createDialChartDataset(statistics, siteName,
                            // kpiName), 0);
                            // Charts.applyMainVisualSettings(dialplot, chart.getDomainAxisLabel(),
                            // chart.getRangeAxisLabel(),
                            // PlotOrientation.VERTICAL);
                            // chart.setPlot(dialplot);
                            // break;
                            default:
                                break;
                            }
                            report.addPart(chart);
                            // report.put(siteName, chart);
                        }

                    }
                    // save reports
                    PDFPrintingEngine printingEngine = new PDFPrintingEngine();
                    for (Report report : reportsPerKPI.values()) {
                        printingEngine.printReport(report);

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    /**
     * Adds data to a time(line) chart
     * 
     * @param chart chart
     * @param series series array
     */
    private void addDataToTimeChart(Chart chart, TimeSeries[] series) {
        TimeSeriesCollection values = new TimeSeriesCollection();
        values.addSeries(series[1]);
        TimeSeriesCollection thresholds = new TimeSeriesCollection();
        thresholds.addSeries(series[0]);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, thresholds, 0);
        Charts.applyDefaultSettingsToDataset(plot, values, 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    public void exportChartsToJpeg() {
        try {
            final Map<String, Map<String, TimeSeries[]>> datasets;
            if (selection != null) {
                Double threshold = kpi.toLowerCase().contains("rate") ? 5d : null;
                // TODO use threshold from template
                datasets = ChartUtilities.createChartDatasets(getStatistics(), getAggregation().getId(), kpi, getChartType(),
                        selection, startTime, endTime, threshold);
            } else {
                datasets = ChartUtilities.createChartDatasets(getStatistics(), getAggregation().getId(), getChartType());
            }
            for (Entry<String, Map<String, TimeSeries[]>> entry : datasets.entrySet()) {
                String siteName = entry.getKey();
                for (Entry<String, TimeSeries[]> e : entry.getValue().entrySet()) {
                    String kpiName = e.getKey();

                    Chart chart = new Chart(siteName);
                    chart.addSubtitle(kpiName);
                    ChartType chartType = ChartType.COMBINED;// TODO
                    chart.setChartType(chartType);
                    chart.setDomainAxisLabel("Value");
                    chart.setRangeAxisLabel("Time");
                    chart.setWidth(400);
                    chart.setHeight(300);
                    TimeSeries[] series = e.getValue();
                    switch (chartType) {
                    case COMBINED:
                        addDataToCombinedChart(chart, series);
                        break;
                    case TIME:
                        addDataToTimeChart(chart, series);
                        break;
                    default:
                        break;
                    }
                    org.jfree.chart.ChartUtilities.saveChartAsJPEG(new File(outputDirectory + File.separatorChar + siteName + " "
                            + kpiName + ".jpg"), Charts.createChart(chart), 500, 300);
                }

            }
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Adds data to a combined(bar+line) chart
     * 
     * @param chart chart
     * @param series series array
     */
    private void addDataToCombinedChart(Chart chart, TimeSeries[] series) {
        TimeSeriesCollection values = new TimeSeriesCollection();
        values.addSeries(series[1]);
        TimeSeriesCollection thresholds = new TimeSeriesCollection();
        thresholds.addSeries(series[0]);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, thresholds, 0);
        Charts.applyDefaultSettingsToDataset(plot, new XYBarDataset(values, 1000 * 60 * 60 * 0.5), 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    /**
     * Exports top N elements to PDF
     */
    private void exportTopElementsToPdf() {
        final List<Pair<String, DefaultValueDataset>> datasets = ChartUtilities.createDialChartDatasets(statistics, kpi);
        final DefaultValueDataset networkDs = ChartUtilities.createDialChartDataset(networkStatistics, "unknown", kpi);
        Report report = new Report("10 worst " + networkLevel + "s report");
        // report.addPart(chart);
        String outputDirectory = getOutputDirectory();
        report.setFile(outputDirectory + File.separatorChar + elementsPerReport + " worst " + networkLevel + "s for " + kpi
                + ".pdf");
        addDialChart(report, "Network", networkDs);
        int i = 0;
        for (Pair<String, DefaultValueDataset> pair : datasets) {
            addDialChart(report, pair.l(), pair.r());
            if (++i >= elementsPerReport) {
                break;
            }
        }
        PDFPrintingEngine printingEngine = new PDFPrintingEngine();
        printingEngine.printReport(report);
    }

    /**
     *
     */
    private void generateIndividuaPdfReport() {
        Report report = new Report("KPI report");
        String outputDirectory = getOutputDirectory();
        report.setFile(outputDirectory + File.separatorChar + kpi + " " + networkElement + ".pdf");
        Chart chart = ChartUtilities.createReportChart(networkElement, kpi, chartType);
        switch (chartType) {
        case COMBINED:
            ChartUtilities.updateCombinedChart(statistics, aggregation.getId(), networkElement, kpi, chart);
            break;
        case TIME:
            ChartUtilities.updateTimeChart(statistics, aggregation.getId(), networkElement, kpi, chart);
            break;
        default:
        }
        report.addPart(chart);
        // save reports
        PDFPrintingEngine printingEngine = new PDFPrintingEngine();
        printingEngine.printReport(report);
    }

    // pages
    public SystemSummaryPage getSystemSummaryPage() {
        return systemSummaryPage;
    }

    public SelectCategoryPage getSelectCategoryPage() {
        return selectCategoryPage;
    }

    public SelectCellPage getSelectCellPage() {
        return selectCellPage;
    }

    public SelectSitePage getSelectSitePage() {
        return selectSitePage;
    }

    public SelectSystemPage getSelectSystemPage() {
        return selectSystemPage;
    }

    public SelectOutputTypePage getSelectOutputTypePage() {
        return selectOutputTypePage;
    }

    public void setSelection(List<String> selection) {
        this.selection = selection;
    }

    public List<String> getSelection() {
        return selection;
    }

    // choices
    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public PeriodType getPeriod() {
        return period;
    }

    public void setPeriod(PeriodType period) {
        this.period = period;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setAggregation(CallTimePeriods aggregation) {
        this.aggregation = aggregation;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     *
     */
    public void exportToXls() {
        if (datasetNode == null) {
            datasetNode = dsService.getRootNode(LoaderUiUtils.getAweProjectName(), "ecl_stat.unl.Z",
                    NodeTypes.OSS);
        }
        updateMessage("Exporting statistics to excel...");
        long t = System.currentTimeMillis();
        XlsStatisticsExporter exporter = new XlsStatisticsExporter(outputDirectory);

        exporter.export(datasetNode, categoryType.getNetworkType(), aggregation.getId(), getTemplateFileName());
        System.out.println("Finished exporting in " + (System.currentTimeMillis() - t) / 1000 + " seconds");
    }

}
