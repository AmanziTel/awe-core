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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.report.charts.ChartType;
import org.amanzi.awe.report.charts.Charts;
import org.amanzi.awe.report.grid.util.ChartUtilities;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.awe.report.model.Report;
import org.amanzi.awe.report.pdf.PDFPrintingEngine;
import org.amanzi.awe.statistics.database.entity.Statistics;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;

import com.google.protobuf.DescriptorProtos.FieldOptions.CType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridWizardPageStep3 extends WizardPage {
    private static final String SELECT_SITE = "Select site:";
    private static final String TEN_WORST_SITES_REPORT = "Create PDF report for 10 worst sites";
    private static final String EXPORT_STATISTICS_TO_EXCEL = "Export KPIs to Excel";
    private static final String EXPORT_STATISTICS_TO_PDF = "Export KPIs to PDF";
    private static final String SELECT_KPI = "Select KPI:";
    private static final String EXPORT_CHART_TO_PDF = "Export chart";
    private static final String SELECT_CHART_TYPE = "Select chart type:";
    private static final String BAR = "bar";
    private static final String LINE = "line";
    private static final String DIAL = "dial";
    private Button btnBar;
    private Button btnLine;
    private Button btnDial;

    private Button btnXLS;
    private Button btnPDF;
    private Button btn10Worst;
    private JFreeChart jfreechart;
    private Combo cmbSites;
    private Composite container;
    private Combo cmbKPIs;
    private ChartComposite chartComposite;
    private Button btnExportChart;
    private Chart chart;

    protected GridWizardPageStep3() {
        super("GridWizardPageStep3");
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        final Label lblSelectSite = new Label(container, SWT.NONE);
        lblSelectSite.setText(SELECT_SITE);
        lblSelectSite.setLayoutData(new GridData());
        final SelectionAdapter listener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateChart();
            }
        };
        cmbSites = new Combo(container, SWT.NONE);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 3, 1);
        cmbSites.setLayoutData(gd);
        cmbSites.addSelectionListener(listener);

        final Label lblSelectKPI = new Label(container, SWT.NONE);
        lblSelectKPI.setText(SELECT_KPI);
        lblSelectKPI.setLayoutData(new GridData());

        cmbKPIs = new Combo(container, SWT.NONE);
        gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING, true, false, 3, 1);
        cmbKPIs.setLayoutData(gd);
        cmbKPIs.addSelectionListener(listener);
        Group chartTypeGroup = new Group(container, SWT.NONE);
        chartTypeGroup.setText(SELECT_CHART_TYPE);
        chartTypeGroup.setLayout(new GridLayout());

        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 3;

        final SelectionAdapter changeChartTypeListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateKPIs();
                updateChart();
            }
        };
        chartTypeGroup.setLayoutData(gd);

        btnBar = new Button(chartTypeGroup, SWT.RADIO);
        btnBar.setSelection(true);
        btnBar.setText(BAR);
        btnBar.setLayoutData(new GridData());
        btnBar.addSelectionListener(changeChartTypeListener);

        btnLine = new Button(chartTypeGroup, SWT.RADIO);
        btnLine.setText(LINE);
        btnLine.setLayoutData(new GridData());
        btnLine.addSelectionListener(changeChartTypeListener);

        btnDial = new Button(chartTypeGroup, SWT.RADIO);
        btnDial.setText(DIAL);
        btnDial.setLayoutData(new GridData());
        btnDial.addSelectionListener(changeChartTypeListener);

        chartComposite = new ChartComposite(container, SWT.NONE, jfreechart);
        gd = new GridData();
        gd.horizontalSpan = 4;
        gd.heightHint = 200;
        gd.widthHint = 200;
        chartComposite.setLayoutData(gd);

        Composite settings = new Composite(container, SWT.NONE);
        gd = new GridData();
        gd.horizontalSpan = 4;
        settings.setLayoutData(gd);
        settings.setLayout(new GridLayout());

        btnExportChart = new Button(settings, SWT.PUSH);
        btnExportChart.setText(EXPORT_CHART_TO_PDF);
        btnExportChart.setSelection(true);
        btnExportChart.setLayoutData(new GridData());
        btnExportChart.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                exportChartToPdf();
            }
        });

        btnXLS = new Button(settings, SWT.CHECK);
        btnXLS.setText(EXPORT_STATISTICS_TO_EXCEL);
        btnXLS.setSelection(true);
        btnXLS.setLayoutData(new GridData());

        btnPDF = new Button(settings, SWT.CHECK);
        btnPDF.setText(EXPORT_STATISTICS_TO_PDF);
        btnPDF.setLayoutData(new GridData());

        btn10Worst = new Button(settings, SWT.CHECK);
        btn10Worst.setText(TEN_WORST_SITES_REPORT);
        btn10Worst.setLayoutData(new GridData());

        // chartComposite.pack();

        setPageComplete(true);
        setControl(container);
    }

    protected void exportChartToPdf() {
        GridReportWizard wiz = (GridReportWizard)getWizard();
        Report report = new Report("KPI report");
        report.addPart(chart);
        long t = System.currentTimeMillis();
        String outputDirectory = wiz.getOutputDirectory();
        report.setFile(outputDirectory + File.separatorChar + cmbSites.getText() + " - " + cmbKPIs.getText() + " " + t + ".pdf");
        PDFPrintingEngine printingEngine = new PDFPrintingEngine();
        printingEngine.printReport(report);
        System.out.println("Chart was exported in " + (System.currentTimeMillis() - t) / 1000 + " seconds");
    }

    /**
     * Is export to Excel required
     * 
     * @return true if export to XLS is required
     */
    public boolean isExportToXlsRequired() {
        return btnXLS.getSelection();
    }

    /**
     * Is export to PDF required
     * 
     * @return true if export to PDF is required
     */
    public boolean isExportToPdfRequired() {
        return btnPDF.getSelection();
    }

    /**
     * Is export to PDF of 10 worst sites results required
     * 
     * @return true if export to PDF of 10 worst sites results is required
     */
    public boolean is10WorstSitesReportRequired() {
        return btn10Worst.getSelection();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
            gridReportWizard.loadData();
            gridReportWizard.buildStatistics();

            List<String> identityProperty = getIdentityProperty(gridReportWizard.getDatasetNode(), "site");
            Collections.sort(identityProperty);
            cmbSites.setItems(identityProperty.toArray(new String[] {}));

            cmbSites.setText(cmbSites.getItem(0));
            // cmbSites.add("all", 0);
            System.out.println("identityProperty: " + identityProperty);

            updateKPIs();
            updateChart();
            container.pack();
        }
        super.setVisible(visible);
    }

    /**
     * @param gridReportWizard
     */
    private void updateKPIs() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        ArrayList<String> idenKPIs = gridReportWizard.getIdenKPIs();
        Collections.sort(idenKPIs);
        String previousSelection = cmbKPIs.getText();
        cmbKPIs.removeAll();
        if (!getChartType().equals(ChartType.DIAL)) {
            cmbKPIs.setItems(idenKPIs.toArray(new String[] {}));
        } else {
            for (String kpi : idenKPIs) {
                if (kpi.toLowerCase().contains("rate")) {
                    cmbKPIs.add(kpi);
                }
            }
        }
        final String first = cmbKPIs.getItem(0);
        cmbKPIs.setText(previousSelection == null ? first : (cmbKPIs.indexOf(previousSelection) != -1 ? previousSelection : first));
    }

    /**
     */
    private void updateChart() {
        final GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        final Statistics statistics = gridReportWizard.getStatistics();
        final String aggregation = gridReportWizard.getAggregation().getId();
        final String siteName = cmbSites.getText();
        final String kpiName = cmbKPIs.getText();
        chart = new Chart(siteName);
        chart.addSubtitle(kpiName);
        final ChartType chartType = getChartType();
        chart.setChartType(chartType);
        chart.setDomainAxisLabel("Value");
        chart.setRangeAxisLabel("Time");
        switch (chartType) {
        case COMBINED:
            updateCombinedChart(statistics, aggregation, siteName, kpiName, chart);
            break;
        case TIME:
            updateTimeChart(statistics, aggregation, siteName, kpiName, chart);
            break;
        case DIAL:
            updateDialChart(statistics, siteName, kpiName, chart);
            break;
        default:
            break;
        }
        jfreechart = Charts.createChart(chart);
        chartComposite.setChart(jfreechart);
        if (chartType.equals(ChartType.DIAL)) {
            GridData gd = new GridData();
            gd.horizontalSpan = 4;
            gd.heightHint = 200;
            gd.widthHint = 200;
            chartComposite.setLayoutData(gd);
        } else {
            GridData gd = new GridData();
            gd.horizontalSpan = 4;
            gd.heightHint = 200;
            gd.widthHint = 600;
            chartComposite.setLayoutData(gd);
        }

        chartComposite.forceRedraw();
        container.pack();
    }

    /**
     * @param statistics
     * @param siteName
     * @param kpiName
     * @param chart
     */
    private void updateDialChart(final Statistics statistics, final String siteName, final String kpiName, final Chart chart) {
        DialPlot dialplot = new DialPlot();
        Charts.applyDefaultSettingsToDataset(dialplot, ChartUtilities.createDialChartDataset(statistics, siteName, kpiName), 0);
        Charts.applyMainVisualSettings(dialplot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(dialplot);
    }

    /**
     * @param statistics
     * @param aggregation
     * @param siteName
     * @param kpiName
     * @param chart
     */
    private void updateTimeChart(final Statistics statistics, final String aggregation, final String siteName,
            final String kpiName, final Chart chart) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.TIME);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[1], 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    /**
     * @param statistics
     * @param aggregation
     * @param siteName
     * @param kpiName
     * @param chart
     */
    private void updateCombinedChart(final Statistics statistics, final String aggregation, final String siteName,
            final String kpiName, final Chart chart) {
        TimeSeriesCollection[] chartDataset = ChartUtilities.createChartDataset(statistics, siteName, kpiName, aggregation,
                ChartType.COMBINED);
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new DateAxis());
        Charts.applyDefaultSettingsToDataset(plot, chartDataset[0], 0);
        Charts.applyDefaultSettingsToDataset(plot, new XYBarDataset(chartDataset[1], 1000 * 60 * 60 * 0.5), 1);
        Charts.applyMainVisualSettings(plot, chart.getDomainAxisLabel(), chart.getRangeAxisLabel(), PlotOrientation.VERTICAL);
        chart.setPlot(plot);
    }

    public static List<String> getIdentityProperty(Node dataset, String name) {
        List<String> result = new ArrayList<String>();
        Relationship rel = dataset.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
        if (rel != null) {
            Node propertyNode = rel.getEndNode();
            for (Node node : propertyNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                    ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
                List<String> props = Arrays.asList((String[])node.getProperty("identity_properties"));
                if (props.contains(name)) {
                    for (Relationship relation : node.getRelationships(GeoNeoRelationshipTypes.IDENTITY_PROPERTIES)) {
                        if (relation.getProperty("property").equals(name)) {
                            for (String value : relation.getEndNode().getPropertyKeys()) {
                                result.add(value);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets aggregation
     * 
     * @return aggregation
     */
    public ChartType getChartType() {
        if (btnLine.getSelection()) {
            return ChartType.TIME;
        } else if (btnBar.getSelection()) {
            return ChartType.COMBINED;

        } else {
            return ChartType.DIAL;
        }
    }

    public String getKpi() {
        return cmbKPIs.getText();
    }
}
