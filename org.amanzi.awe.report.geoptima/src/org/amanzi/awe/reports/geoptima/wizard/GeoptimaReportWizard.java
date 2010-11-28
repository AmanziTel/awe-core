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

package org.amanzi.awe.reports.geoptima.wizard;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.report.charts.ChartType;
import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.model.ReportModel;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.reports.geoptima.GeoptimaReportsPlugin;
import org.amanzi.awe.statistic.CallTimePeriods;
import org.amanzi.awe.statistics.builder.StatisticsBuilder;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.statistics.utils.ChartUtilities;
import org.amanzi.awe.statistics.utils.ScriptUtils;
import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.awe.views.reuse.Distribute;
import org.amanzi.awe.views.reuse.Select;
import org.amanzi.awe.views.reuse.views.DefaultColorer;
import org.amanzi.awe.views.reuse.views.ReuseAnalyserModel;
import org.amanzi.neo.loader.TEMSLoader;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jruby.Ruby;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.Predicate;

import com.vividsolutions.jts.geom.Envelope;

/**
 * GeOptima report wizard that automatically loads the data, creates necessary distribution analysis
 * and generates reports
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GeoptimaReportWizard extends Wizard implements IWizard {
    private static final Logger LOGGER = Logger.getLogger(GeoptimaReportWizard.class);
    public static final String WIZARD_TITLE = "GeOptimA Report Wizard";
    private SelectDataPage selectDataPage;
    private ReportModel reportModel;
    private static final String MESSAGE = "%s (Step %d of %d)";
    private static final int STEP_COUNT = 4;
    private int step = 0;
    private List<Node> datasetNodes = new ArrayList<Node>();
    private GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
    private Node dataset;
    private List<Statistics> stats=new ArrayList<Statistics>();
    protected Template template;;

    @Override
    public void addPages() {
        getContainer().getShell().setSize(600, 300);
        selectDataPage = new SelectDataPage(SelectDataPage.class.getName());
        addPage(selectDataPage);
    }

    @Override
    public boolean performFinish() {
        setNeedsProgressMonitor(true);
        initializeReportEngine();

        // build distribution analysis for all numeric properties except lat, long and time
        createDistribution(service);
        // TODO build distribution analysis for some string properties like event_id
        // TODO zoom to business districts
        // addLayersToMap(service);
        generateReportsForDistribution();
        createStatistics();
        generateReportsForStatistics();
        return true;
    }

    /**
     * Initializes the report engine with additional file
     */
    private void initializeReportEngine() {
        try {
//            nextStep();
            URL entry = Platform.getBundle(GeoptimaReportsPlugin.PLUGIN_ID).getEntry("ruby");
            URL scriptURL = FileLocator.toFileURL(GeoptimaReportsPlugin.getDefault().getBundle().getEntry("ruby/automation.rb"));
            String path = scriptURL.getPath();
            reportModel = new ReportModel(new String[] {FileLocator.resolve(entry).getFile()}, new String[] {path});
        } catch (Exception e1) {
            LOGGER.error(e1.getLocalizedMessage(), e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        }
    }

    /**
     * Generates reports for all network elements
     */
    public void generateReportsForStatistics() {
        nextStep();
        selectDataPage.setTitle(String.format(MESSAGE, "Creating reports for stats", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    long t = System.currentTimeMillis();
                    monitor.beginTask("Generating PDf reports for statistics...", stats.size()*template.getColumns().size());
                    for (Statistics statistics : stats) {
                        final Map<String, Map<String, TimeSeries[]>> datasets;
                        datasets = ChartUtilities.createChartDatasets(statistics, getAggregation().getId(), template);
                        System.out.println("Finished generating datasets in " + (System.currentTimeMillis() - t) / 1000
                                + " seconds");
                        Map<String, Report> reportsPerKPI = new HashMap<String, Report>();
                        for (Entry<String, Map<String, TimeSeries[]>> entry : datasets.entrySet()) {
                            String groupName = entry.getKey();
                            for (Entry<String, TimeSeries[]> e : entry.getValue().entrySet()) {
                                String kpiName = e.getKey();
                                Report report = reportsPerKPI.get(kpiName);
                                if (report == null) {
                                    report = new Report("KPI report");
                                    report.setFile(kpiName +" for "+statistics.getName().replace(",", "-")+ ".pdf");
                                    reportsPerKPI.put(kpiName, report);
                                }

                                Chart chart = new Chart(groupName);
                                chart.addSubtitle(kpiName);
                                ChartType chartType = ChartType.COMBINED;
                                chart.setChartType(chartType);
                                chart.setDomainAxisLabel("Value");
                                chart.setRangeAxisLabel("Time");
                                chart.setWidth(400);
                                chart.setHeight(300);
                                TimeSeries[] series = e.getValue();

                                addDataToTimeChart(chart, series);
                                report.addPart(chart);
                            }

                        }
                        // save reports
                        PDFPrintingEngine printingEngine = new PDFPrintingEngine();
                        for (Report report : reportsPerKPI.values()) {
                            printingEngine.printReport(report);
                            monitor.worked(1);
                        }
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
        DateAxis dateAxis = new DateAxis();
////        dateAxis.setAutoTickUnitSelection(true);
////        dateAxis.setAutoRange(true);
//        TickUnits tickUnits = new TickUnits();
//        tickUnits.add(new DateTickUnit(DateTickUnitType.MONTH,1,new SimpleDateFormat("MMMMM")));
//        dateAxis.setStandardTickUnits(tickUnits);
//        dateAxis.setVerticalTickLabels(true);

        plot.setDomainAxis(dateAxis);
        Charts.applyDefaultSettingsToDataset(plot, thresholds, 0);
        Charts.applyDefaultSettingsToDataset(plot, values, 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }
    /**
     * Generates reports for all 'drive' layers found in map
     */
    private void generateReportsForDistribution() {
        try {
            nextStep();
            selectDataPage.setTitle(String.format(MESSAGE, "Creating reports", step, STEP_COUNT));
            selectDataPage.setMessage("");
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    reportModel.updateModel("automation");
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
        }
    }

    /**
     * Adds layers to map
     * 
     * @param filesCount the quantity of files that were loaded
     * @param service database service
     */
    private void addLayersToMap(final int filesCount, final GraphDatabaseService service) {
        try {
            nextStep();
            selectDataPage.setTitle(String.format(MESSAGE, "Adding layers to map", step, STEP_COUNT));
            selectDataPage.setMessage("");
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Add layers to map", filesCount);
                    IService curService;
                    try {
                        curService = LoaderUiUtils.getMapService();
                        LOGGER.debug("MapService " + curService);
                        IMap map = ApplicationGIS.getActiveMap();
                        // map.getBounds(null).init(120.848, 121.147, 14.641, 14.765);
                        ViewportModel viewportModel = (ViewportModel)map.getViewportModel();
                        // // viewportModel.zoomToBox(new Envelope(120.0,121.1,14.6,14.7));
                        viewportModel.setBounds(new Envelope(120.848, 121.147, 14.641, 14.765));
                        // // viewportModel.setBounds(new Envelope(120.0, 121.1, 14.6, 14.7));
                        LinkedHashMap<String, Node> allDatasetNodes = NeoUtils.getAllDatasetNodes(service);
                        LOGGER.debug("There were created " + allDatasetNodes.size() + " datasets");
                        for (Entry<String, Node> entry : allDatasetNodes.entrySet()) {
                            Node node = entry.getValue();
                            // add node to map
                            System.out.print("Adding dataset '" + node.getProperty("name") + "' to map...");
                            monitor.subTask("Adding dataset '" + node.getProperty("name") + "' to map");
                            List<ILayer> layerList = new ArrayList<ILayer>();
                            List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                            Node gisNode = NeoUtils.getGisNodeByDataset(node);
                            LOGGER.debug("gisNode " + gisNode);
                            IGeoResource iGeoResource = LoaderUiUtils.getResourceForGis(curService, map, gisNode);
                            LOGGER.debug("iGeoResource " + iGeoResource);
                            if (iGeoResource != null) {
                                listGeoRes.add(iGeoResource);
                            }
                            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
                            // LoaderUtils.zoomToLayer(layerList);
                            LOGGER.debug("Finished.");
                            monitor.worked(1);
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                        // TODO Handle IOException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            // TODO Handle InterruptedException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Finds all csv files of the directory specified
     * 
     * @param directory
     * @return
     */
    private File[] getFilesToLoad(String directory) {
        File dir = new File(directory);
        final File[] files = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".csv");
            }

        });
        return files;
    }

    private void createStatistics() {
        nextStep();
        selectDataPage.setTitle(String.format(MESSAGE, "Buiding stats", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    template = ScriptUtils.createTemplateForScript("geoptima/formulas.rb", "KPI::Geoptima", "default",
                            Template.DataType.TEMS);
                    Ruby ruby = KPIPlugin.getDefault().getRubyRuntime();
                    StatisticsBuilder builder = new StatisticsBuilder(service, dataset, ruby);
                    stats.add(builder.buildStatistics(template, "imei", getAggregation(), monitor));
                    stats.add(builder.buildStatistics(template, "sector", getAggregation(), monitor));
                }
            });
        } catch (Exception e) {
            // TODO Handle InterruptedException
            LOGGER.error(e.getLocalizedMessage(), e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Creates distribution analysis for all datasets loaded
     * <p>
     * TODO process only newly loaded datasets
     * 
     * @param filesCount
     * @param service
     */
    private void createDistribution(final GraphDatabaseService service) {
        nextStep();
        selectDataPage.setTitle(String.format(MESSAGE, "Buiding distribution stats", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Creation distributions", 1);

                    String datasetName = dataset.getProperty("name").toString();
                    monitor.subTask(datasetName.toString());

                    String[] fields = new String[] {"signal_strength", "imei"};
                    // String[] fields = new String[] {"signal_strength", "imei", "rxqual",
                    // "ec_io"};
                    // String[] fields =
                    // PropertyHeader.getPropertyStatistic(node).getNumericFields(NodeTypes.M.getId());

                    for (String field : fields) {
                        updateMessage("Dataset:\n" + datasetName + "\nBuiding stats for '" + field);
                        ReuseAnalyserModel model = new ReuseAnalyserModel(new HashMap<String, String[]>(),
                                getPropertyReturnableEvaluator(dataset), service);
                        Transaction tx = NeoUtils.beginTransaction();
                        try {
                            model.setCurrenTransaction(tx);
                            Node aggregation = model.findOrCreateAggregateNode(dataset, field, false, Distribute.AUTO.toString(),
                                    Select.EXISTS.toString(), monitor);
                            tx = model.getCurrenTransaction();
                            tx.success();
                            DefaultColorer.addColors(aggregation, service);
                        } catch (Exception e) {
                            LOGGER.error(e.getLocalizedMessage(), e);
                        } finally {
                            monitor.done();
                            tx.finish();
                        }
                    }
                    ReuseAnalyserModel model = new ReuseAnalyserModel(new HashMap<String, String[]>(),
                            getPropertyReturnableEvaluator(dataset), service);
                    Transaction tx = NeoUtils.beginTransaction();
                    try {
                        model.setCurrenTransaction(tx);
                        Node aggregation = model.findOrCreateAggregateNode(dataset, "event_id", true, Distribute.AUTO.toString(),
                                Select.EXISTS.toString(), monitor);
                        DefaultColorer.addColors(aggregation, service);
                        tx = model.getCurrenTransaction();
                        tx.success();
                    } catch (Exception e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    } finally {
                        monitor.done();
                        tx.finish();
                    }
                }
            });
        } catch (Exception e) {
            // TODO Handle InterruptedException
            LOGGER.error(e.getLocalizedMessage(), e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Loads files
     * 
     * @param files files to be loaded
     */
    private void loadFiles(final File[] files) {
        nextStep();
        final int n = files.length;
        selectDataPage.setTitle(String.format(MESSAGE, "Loading data", step, STEP_COUNT));
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        datasetNodes.clear();
                        int i = 0;
                        for (File file : files) {
                            i++;
                            String filepath = file.getPath();
                            updateMessage("Loading " + i + " of " + n + ":" + filepath);

                            TEMSLoader loader = new TEMSLoader(null, filepath, getShell().getDisplay(), file.getName());
                            loader.setLimit(100);
                            loader.run(monitor);
                            Node datasetNode = loader.getDatasetNode();
                            if (datasetNode == null) {
                                LOGGER.debug("ds==null for " + file.getName());
                            } else {
                                datasetNodes.add(datasetNode);
                            }
                        }
                    } catch (IOException e) {
                        // TODO Handle IOException
                        LOGGER.error(e.getLocalizedMessage(), e);
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                }
            });
        } catch (Exception e) {
            // TODO Handle Exception
            LOGGER.error(e.getLocalizedMessage(), e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Creates a predicate that is necessary for distribution model creation
     * 
     * @param node
     * @return the predicate
     */
    private Predicate<org.neo4j.graphdb.Path> getPropertyReturnableEvaluator(final Node node) {
        return new Predicate<org.neo4j.graphdb.Path>() {

            @Override
            public boolean accept(org.neo4j.graphdb.Path item) {
                return item.endNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(NeoUtils.getPrimaryType(node));
            }
        };
    }

    /**
     * Increases step counter
     */
    private void nextStep() {
        step++;
    }

    /**
     * Updates the wizard's message in UI thread
     * 
     * @param message a new wizard message
     */
    private void updateMessage(final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                selectDataPage.setMessage(message);

            }
        });
    }

    /**
     * @return Returns the service.
     */
    public GraphDatabaseService getService() {
        return service;
    }

    /**
     * @return Returns the dataset.
     */
    public Node getDataset() {
        return dataset;
    }

    /**
     * @param dataset The dataset to set.
     */
    public void setDataset(Node dataset) {
        this.dataset = dataset;
    }

    /**
     *
     * @return
     */
    private CallTimePeriods getAggregation() {
        return CallTimePeriods.HOURLY;
    }

}
