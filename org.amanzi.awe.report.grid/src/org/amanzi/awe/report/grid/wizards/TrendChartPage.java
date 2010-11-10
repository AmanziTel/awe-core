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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
public class TrendChartPage extends WizardPage {

    private JFreeChart jfreechart;
    private Composite container;
    private ChartComposite chartComposite;
    private Chart chart;

    protected TrendChartPage() {
        super("GridWizardPageStep2");
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new FormLayout());

        chartComposite = new ChartComposite(container, SWT.NONE, jfreechart);
        FormData fd = new FormData();
        fd.top=new FormAttachment(0,10);
        fd.left=new FormAttachment(0,10);
        fd.bottom=new FormAttachment(100,-10);
        fd.height=300;
        fd.width=400;
        chartComposite.setLayoutData(fd);

        

        // chartComposite.pack();

        setPageComplete(true);
        setControl(container);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateChart();
            container.pack();
        }
        super.setVisible(visible);
    }

    /**
     */
    private void updateChart() {
        System.out.println("update chart");
        GridReportWizard gridReportWizard = ((GridReportWizard)getWizard());
        Statistics statistics = gridReportWizard.getNetworkStatistics();
        String aggregation = gridReportWizard.getAggregation().getId();
        String kpiName = gridReportWizard.getKpi();
        long startTime = gridReportWizard.getStartTime();
        long endTime = gridReportWizard.getEndTime();
        ChartType chartType = ChartType.TIME;
        chart = ChartUtilities.createReportChart(kpiName, chartType);
        long t = System.currentTimeMillis();
        ChartUtilities.updateTimeChart(statistics, aggregation, "unknown", kpiName, chart,startTime,endTime,5d);
        
        System.out.println("Dataset created in " + (System.currentTimeMillis() - t) / 1000 + " sec");
        t = System.currentTimeMillis();
        jfreechart = Charts.createChart(chart);
        chartComposite.setChart(jfreechart);
        chartComposite.redraw();
        // chartComposite.forceRedraw();
        System.out.println("Chart was redrawn in " + (System.currentTimeMillis() - t) / 1000 + " sec");
        container.layout();
        container.pack();
    }

   
}
