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
import org.amanzi.awe.statistics.database.entity.StatisticsGroup;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class GridWizardPageStep2 extends WizardPage {
    private static final String SELECT_LEVEL = "Select %s:";
    private static final String SELECT_KPI = "Select KPI:";
    private static final String EXPORT_CHART_TO_PDF = "Export chart";
    private static final String SELECT_CHART_TYPE = "Select chart type:";
    private static final String BAR = "bar";
    private static final String LINE = "line";
    private static final String DIAL = "dial";
    private Button btnBar;
    private Button btnLine;
    private Button btnDial;
    private Button btn10Worst;

    private JFreeChart jfreechart;
    private Combo cmbSites;
    private Composite container;
    private Combo cmbKPIs;
    private ChartComposite chartComposite;
    private Button btnExportChart;
    private Chart chart;
    private Label lblSelectNetworkElement;
    private Button btnAll;
    private Button btnIndividual;
    private Button btnTop10;
    private Button btnTop20;
    private Button btnTop30;

    protected GridWizardPageStep2() {
        super("GridWizardPageStep2");
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(4, false));

        lblSelectNetworkElement = new Label(container, SWT.NONE);
        // lblSelectNetworkElement.setText(SELECT_LEVEL);
        lblSelectNetworkElement.setLayoutData(new GridData());
        final SelectionAdapter listener = new SelectionAdapter() {
            boolean deselectEvent = true;

            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println(e);
                updateChart();
                if (!deselectEvent) {
                    deselectEvent = true;
                } else {
                    deselectEvent = false;
                }
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

        Group resultType = new Group(container, SWT.NONE);
        resultType.setText("Select which results to export:");
        resultType.setLayout(new GridLayout());

        btnAll = new Button(resultType, SWT.RADIO);
        btnAll.setText("all sites/cells");
        btnAll.setLayoutData(new GridData());
        btnAll.setSelection(true);

        btnIndividual = new Button(resultType, SWT.RADIO);
        btnIndividual.setText("selected site/cell");
        btnIndividual.setLayoutData(new GridData());

        btnTop10 = new Button(resultType, SWT.RADIO);
        btnTop10.setText("top 10 sites/cells");
        btnTop10.setLayoutData(new GridData());

        btnTop20 = new Button(resultType, SWT.RADIO);
        btnTop20.setText("top 20 sites/cells");
        btnTop20.setLayoutData(new GridData());

        btnTop30 = new Button(resultType, SWT.RADIO);
        btnTop30.setText("top 30 sites/cells");
        btnTop30.setLayoutData(new GridData());

        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        resultType.setLayoutData(gd);
        Group chartTypeGroup = new Group(container, SWT.NONE);
        chartTypeGroup.setText(SELECT_CHART_TYPE);
        chartTypeGroup.setLayout(new GridLayout());

        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;

        final SelectionAdapter changeChartTypeListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println(e);
                updateKPIs();
                updateChart();
                updateChoices();
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

        // chartComposite.pack();

        setPageComplete(true);
        setControl(container);
    }

    protected void updateChoices() {
        if (btnDial.getSelection()) {
            enableChoices(true);
            btnTop10.setSelection(true);
        } else {
            enableChoices(false);
            if (isWorstSitesReportRequired()) {
                btnAll.setSelection(true);
            }

        }
    }

    /**
     *
     */
    private void enableChoices(boolean enable) {
        btnAll.setEnabled(!enable);
        btnIndividual.setEnabled(!enable);
        btnTop10.setEnabled(enable);
        btnTop20.setEnabled(enable);
        btnTop30.setEnabled(enable);
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

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
            gridReportWizard.loadData();
            gridReportWizard.buildStatistics();
            lblSelectNetworkElement.setText(String.format(SELECT_LEVEL, gridReportWizard.getNetworkLevel()));
            updateNetworkElements();
            // List<String> identityProperty =
            // getIdentityProperty(gridReportWizard.getDatasetNode(), "site");
            // Collections.sort(identityProperty);
            // cmbSites.setItems(identityProperty.toArray(new String[] {}));

            cmbSites.setText(cmbSites.getItem(0));
            // cmbSites.add("all", 0);
            // System.out.println("identityProperty: " + identityProperty);

            updateKPIs();
            updateChart();
            container.pack();
        }
        super.setVisible(visible);
    }

    private void updateNetworkElements() {
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        Statistics statistics = gridReportWizard.getStatistics();
        cmbSites.removeAll();
        for (StatisticsGroup group : statistics.getGroups().values()) {
            cmbSites.add(group.getGroupName());
        }
        cmbSites.setText(cmbSites.getItem(0));
    }

    /**
     * Is export to PDF of N worst sites results required
     * 
     * @return true if export to PDF of 10 worst sites results is required
     */
    public boolean isWorstSitesReportRequired() {
        return btnTop10.getSelection() || btnTop20.getSelection() || btnTop30.getSelection();
    }

    /**
     * Gets quantity of sites/cells per report
     * 
     * @return quantity of sites/cells per report
     */
    public Integer elementsPerReport() {
        if (btnTop10.getSelection()) {
            return 10;
        } else if (btnTop20.getSelection()) {
            return 20;
        } else if (btnTop30.getSelection()) {
            return 30;
        }
        return cmbSites.getItemCount();
    }
    /**
     * Is export to PDF of an individual (selected) site results required
     * 
     * @return true if export to PDF of an individual (selected) site results required
     */
    public boolean isIndividualReportRequired() {
        return btnIndividual.getSelection();
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
        System.out.println("update chart");
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        Statistics statistics = gridReportWizard.getStatistics();
        String aggregation = gridReportWizard.getAggregation().getId();
        String siteName = cmbSites.getText();
        String kpiName = cmbKPIs.getText();
        ChartType chartType = getChartType();
        chart = ChartUtilities.createReportChart(siteName,kpiName,chartType);
        long t = System.currentTimeMillis();
        switch (chartType) {
        case COMBINED:
            ChartUtilities.updateCombinedChart(statistics, aggregation, siteName, kpiName, chart);
            break;
        case TIME:
            ChartUtilities.updateTimeChart(statistics, aggregation, siteName, kpiName, chart);
            break;
        case DIAL:
            ChartUtilities.updateDialChart(statistics, siteName, kpiName, chart);
            break;
        default:
            break;
        }
        System.out.println("Dataset created in " + (System.currentTimeMillis() - t) / 1000 + " sec");
        t = System.currentTimeMillis();
        jfreechart = Charts.createChart(chart);
        GridData gd = new GridData();
        gd.horizontalSpan = 4;
        gd.heightHint = 200;
        gd.widthHint = chartType.equals(ChartType.DIAL) ? 200 : 600;
        chartComposite.setLayoutData(gd);
        chartComposite.setChart(jfreechart);
        chartComposite.redraw();
        // chartComposite.forceRedraw();
        System.out.println("Chart was redrawn in " + (System.currentTimeMillis() - t) / 1000 + " sec");
        container.layout();
        container.pack();
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

    public String getNetworkElement() {
        return cmbSites.getText();
    }
}
