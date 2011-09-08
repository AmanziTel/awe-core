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

package org.amanzi.awe.report.charts;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.report.ReportPlugin;
import org.amanzi.awe.report.model.Chart;
import org.amanzi.neo.services.nodes.CellNode;
import org.amanzi.neo.services.nodes.ChartItemNode;
import org.amanzi.neo.services.nodes.ChartNode;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author user
 * @since 1.0.0
 */
public class Charts {
    private static final String FACTORY_ID = Charts.class.getName();
    /** Color COLOR_LEFT_PROPERTY field */
    
    public static IEditorPart getActiveEditor() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
        return activePage.getActiveEditor();
    }

    public static String getFactoryId() {
        return FACTORY_ID;
    }

    public static DefaultCategoryDataset createBarChartDataset(ChartNode chartNode) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int countBad = 0;
        List<ChartItemNode> allChartItems = chartNode.getAllChartItems();
        Collections.sort(allChartItems);
        for (ChartItemNode node : allChartItems) {
            double nodeValue = 0.0;
            try {
                String value = (String)node.getValueNode().getValue();
                if (value.length() > 0) {
                    nodeValue = Double.parseDouble((String)node.getValueNode().getValue());
                }
            } catch (NumberFormatException e) {
                countBad++;
            }
            dataset.addValue(nodeValue, node.getChartItemSeries(), (String)node.getCategoryNode().getValue());
        }
        if (countBad > 0) {
            displayDataParsingError(countBad);
        }
        return dataset;
    }

    private static void displayDataParsingError(final int countBad) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                ErrorDialog.openError(display.getActiveShell(), "Invalid input", "There were " + countBad
                        + " data parsing errors in creating the chart!", new Status(Status.ERROR, ReportPlugin.PLUGIN_ID,
                        NumberFormatException.class.getName()));
            }

        });
    }

    /**
     * This method creates a chart.
     * 
     * @param dataset. dataset provides the data to be displayed in the chart. The parameter is
     *        provided by the 'createDataset()' method.
     * @return A chart.
     */
    public static JFreeChart createBarChart(DefaultCategoryDataset dataset) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart("", // chart title
                "", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot)chart.getPlot();

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        return chart;
    }

    /**
     * Creates a bar chart
     * 
     * @param reportChart chart model
     * @return a JFreeChart
     */
    public static JFreeChart createBarChart(Chart reportChart) {

        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(reportChart.getTitle(), // chart title
                reportChart.getDomainAxisLabel(), // domain axis label
                reportChart.getRangeAxisLabel(), // range axis label
                ((CategoryPlot)reportChart.getPlot()).getDataset(), // data
                reportChart.getOrientation(), // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot)chart.getPlot();

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        if (reportChart.getRenderer() != null) {
            plot.setRenderer((CategoryItemRenderer)reportChart.getRenderer());
        }
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
        return chart;
    }

    /**
     * This method creates a chart.
     * 
     * @param t dataset provides the data to be displayed in the chart. The parameter is provided by
     *        the 'createDataset()' method.
     * @return A chart.
     */
    public static JFreeChart createPieChart(DefaultPieDataset dataset) {

        JFreeChart chart = ChartFactory.createPieChart3D("", dataset, true, true, true);

        return chart;
    }

    public static DefaultPieDataset createPieChartDataset(ChartNode chartNode) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            List<ChartItemNode> allChartItems = chartNode.getAllChartItems();
            Collections.sort(allChartItems);
            for (ChartItemNode node : allChartItems) {
                dataset.setValue((String)node.getCategoryNode().getValue(), Double.parseDouble((String)node.getValueNode()
                        .getValue()));
            }
        } catch (final NumberFormatException e) {
            showErrorDlg(e);
        }
        return dataset;
    }

    /**
     * @param e
     */
    private static void showErrorDlg(final NumberFormatException e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                ErrorDialog.openError(display.getActiveShell(), "Invalid input", "Chart can't be created due to invalid input!",
                        new Status(Status.ERROR, ReportPlugin.PLUGIN_ID, NumberFormatException.class.getName(), e));
            }

        });
    }

    public static void openChartEditor(final IEditorInput editorInput, final String editorId) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchPage page = workbench.getWorkbenchWindows()[0].getActivePage();
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    // NeoSplashUtil.logn("Try to open editor " + editorId);
                    page.openEditor(editorInput, editorId);
                } catch (PartInitException e) {
                    // NeoSplashUtil.logn(e.getMessage());
                }
            }
        });
    }

    /**
     * @param categories
     * @param values
     * @return
     */
    public static DefaultCategoryDataset getBarChartDataset(ArrayList<CellNode> categories, ArrayList<CellNode> values) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int n = categories.size();
        int m = values.size();
        int k = m / n;
        try {
            for (int i = 0; i < n; i++) {
                CellNode catNode = categories.get(i);
                for (int j = 0; j < k; j++) {
                    CellNode valNode = values.get(i + n * j);
                    dataset.addValue(Double.parseDouble((String)valNode.getValue()), "Series " + j, (String)catNode.getValue());
                }
            }
        } catch (final NumberFormatException e) {
            showErrorDlg(e);
        }

        return dataset;
    }

    /**
     * @param categories
     * @param values
     * @return
     */
    public static DefaultCategoryDataset getBarChartDataset(ArrayList<Node> nodes, String categoriesProperty,
            String[] valuesProperties) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int n = valuesProperties.length;
        try {
            for (Node node : nodes) {
                if (node.hasProperty(categoriesProperty)) {
                    Object catName = node.getProperty(categoriesProperty);
                    String stringVal;
                    if (catName instanceof String) {
                        stringVal = (String)catName;
                    } else {
                        stringVal = catName.toString();
                    }
                    for (int i = 0; i < n; i++) {
                        String property = valuesProperties[i];
                        if (node.hasProperty(property)) {
                            Object value = node.getProperty(property);
                            double doubleVal = Double.NaN;
                            if (value instanceof Double) {
                                doubleVal = (Double)value;
                            } else if (value instanceof Integer) {
                                doubleVal = ((Integer)value).doubleValue();

                            } else if (value instanceof String) {
                                doubleVal = Double.parseDouble((String)value);
                            }
                            dataset.addValue(doubleVal, property, stringVal);
                        }
                    }
                }
            }
        } catch (final NumberFormatException e) {
            showErrorDlg(e);
        }

        return dataset;
    }

    public static void applyDefaultSettings(Plot plot, Dataset dataset, int dsNum) {
        if (plot instanceof XYPlot) {
            XYPlot xyplot = (XYPlot)plot;
            if (xyplot.getDomainAxis() == null)
                xyplot.setDomainAxis(new DateAxis("Time"));
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairLockedOnData(false);
            xyplot.setRangeCrosshairVisible(false);
            xyplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

            if (dataset instanceof TimeSeriesCollection) {

                xyplot.setDataset(dsNum, (TimeSeriesCollection)dataset);

                StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
                standardxyitemrenderer.setBaseShapesFilled(true);
                xyplot.setRenderer(dsNum, standardxyitemrenderer);
                // if (dsNum == 0) {
                // xyplot.getRenderer(0).setSeriesPaint(0, new Color(0, 0, 0, 0));
                // } else if (dsNum == 1) {
                // xyplot.getRenderer(1).setSeriesPaint(0, COLOR_LEFT_PROPERTY);
                // } else if (dsNum == 2) {
                // xyplot.getRenderer(2).setSeriesPaint(0, COLOR_RIGHT_PROPERTY);
                // }
                ValueAxis rangeAxis = xyplot.getRangeAxis();
                if (rangeAxis == null) {
                    rangeAxis = new NumberAxis("Value");
                    ((NumberAxis)rangeAxis).setAutoRangeIncludesZero(false);
                    xyplot.setRangeAxis(dsNum, rangeAxis);
                }
                xyplot.setRangeAxisLocation(dsNum, AxisLocation.BOTTOM_OR_LEFT);
                xyplot.mapDatasetToRangeAxis(dsNum, dsNum);
            } else if (dataset instanceof EventDataset) {
                EventDataset eventDataset = (EventDataset)dataset;
                xyplot.setDataset(dsNum, eventDataset);

                XYBarRenderer eventRenderer = new EventRenderer(eventDataset);
                xyplot.setRenderer(dsNum, eventRenderer);

                NumberAxis rangeAxis = new NumberAxis("Events");
                rangeAxis.setVisible(false);
                xyplot.setRangeAxis(dsNum, rangeAxis);
            } else if (dataset instanceof XYBarDataset) {
                XYBarDataset xydataset = (XYBarDataset)dataset;
                xyplot.setDataset(dsNum, xydataset);

                XYBarRenderer xyRenderer = new XYBarRenderer();
                xyRenderer.setBarPainter(new StandardXYBarPainter());
                xyplot.setRenderer(dsNum, xyRenderer);

                ValueAxis rangeAxis = xyplot.getRangeAxis();
                if (rangeAxis == null)
                    rangeAxis = new NumberAxis("Axis Name");
                rangeAxis.setVisible(true);
                xyplot.setRangeAxis(dsNum, rangeAxis);
                // xyplot.setDomainAxis(new DateAxis());
                xydataset.setBarWidth(0.0);
            }
        } else if (plot instanceof CategoryPlot) {
            // get a reference to the plot for further customisation...
            CategoryPlot categoryPlot = (CategoryPlot)plot;

            // set the range axis to display integers only...
            NumberAxis rangeAxis = new NumberAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            categoryPlot.setRangeAxis(rangeAxis);

            // disable bar outlines...
            BarRenderer renderer = (BarRenderer)categoryPlot.getRenderer();
            if (renderer == null) {
                renderer = new BarRenderer();
            }
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
            renderer.setSeriesPaint(0, gp0);
            categoryPlot.setRenderer(renderer);

            CategoryAxis domainAxis = new CategoryAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
            categoryPlot.setDomainAxis(domainAxis);
        }
    }

    public static void applyDefaultSettingsToDataset(Plot plot, Dataset dataset, int dsNum) {
        if (plot instanceof XYPlot) {
            XYPlot xyplot = (XYPlot)plot;
            if (dataset instanceof TimeSeriesCollection) {

                xyplot.setDataset(dsNum, (TimeSeriesCollection)dataset);

                StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
                standardxyitemrenderer.setBaseShapesFilled(true);
                xyplot.setRenderer(dsNum, standardxyitemrenderer);

                ValueAxis rangeAxis = xyplot.getRangeAxis();
                if (rangeAxis == null) {
                    rangeAxis = new NumberAxis("Value");
                    ((NumberAxis)rangeAxis).setAutoRangeIncludesZero(false);
                    xyplot.setRangeAxis(dsNum, rangeAxis);
                    xyplot.setRangeAxisLocation(dsNum, AxisLocation.BOTTOM_OR_LEFT);
                    xyplot.mapDatasetToRangeAxis(dsNum, dsNum);
                }
            } else if (dataset instanceof EventDataset) {
                EventDataset eventDataset = (EventDataset)dataset;
                xyplot.setDataset(dsNum, eventDataset);

                XYBarRenderer eventRenderer = new EventRenderer(eventDataset);
                xyplot.setRenderer(dsNum, eventRenderer);

                NumberAxis rangeAxis = new NumberAxis("Events");
                rangeAxis.setVisible(false);
                xyplot.setRangeAxis(dsNum, rangeAxis);
            } else if (dataset instanceof XYBarDataset) {
                XYBarDataset xydataset = (XYBarDataset)dataset;
                xyplot.setDataset(dsNum, xydataset);

                XYBarRenderer xyRenderer = new XYBarRenderer();
                xyRenderer.setBarPainter(new StandardXYBarPainter());
                xyplot.setRenderer(dsNum, xyRenderer);

                ValueAxis rangeAxis = xyplot.getRangeAxis();
                if (rangeAxis == null)
                    rangeAxis = new NumberAxis("Value");
                rangeAxis.setVisible(true);
                xyplot.setRangeAxis(dsNum, rangeAxis);
            }
        } else if (plot instanceof DialPlot) {
            DialPlot dialPlot = (DialPlot)plot;
            applySettingsToDialPlot(dialPlot, (ValueDataset)dataset, "Value");
        }
    }

    public static void applyDefaultSettingsToPlot(Plot plot) {
        if (plot instanceof XYPlot) {
            XYPlot xyplot = (XYPlot)plot;
            if (xyplot.getDomainAxis() == null)
                xyplot.setDomainAxis(new DateAxis("Time"));
            xyplot.setDomainCrosshairVisible(true);
            xyplot.setDomainCrosshairLockedOnData(false);
            xyplot.setRangeCrosshairVisible(false);
            xyplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        } else if (plot instanceof CategoryPlot) {
            // get a reference to the plot for further customisation...
            CategoryPlot categoryPlot = (CategoryPlot)plot;

            // set the range axis to display integers only...
            NumberAxis rangeAxis = new NumberAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            categoryPlot.setRangeAxis(rangeAxis);

            // disable bar outlines...

            BarRenderer renderer = (BarRenderer)categoryPlot.getRenderer();
            if (renderer == null) {
                renderer = new BarRenderer();
            }
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
            renderer.setSeriesPaint(0, gp0);
            categoryPlot.setRenderer(renderer);

            CategoryAxis domainAxis = new CategoryAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
            categoryPlot.setDomainAxis(domainAxis);
        }
    }

    public static void applyMainVisualSettings(Plot plot, String rangeAxisLablel, String domainAxisLabel,
            PlotOrientation orientation) {
        if (plot instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot)plot;
            if (rangeAxisLablel != null)
                categoryPlot.getRangeAxis().setLabel(rangeAxisLablel);
            categoryPlot.getDomainAxis().setLabel(domainAxisLabel);
            categoryPlot.setOrientation(orientation);
        } else if (plot instanceof XYPlot) {
            XYPlot xyPlot = (XYPlot)plot;
            if (rangeAxisLablel != null)
                xyPlot.getRangeAxis().setLabel(rangeAxisLablel);
            ValueAxis domainAxis = xyPlot.getDomainAxis();
            if (domainAxis==null){
                domainAxis=new DateAxis("Time");
                xyPlot.setDomainAxis(domainAxis);
            }
            if (domainAxisLabel != null) {
                domainAxis.setLabel(domainAxisLabel);
            }
            xyPlot.setOrientation(orientation);
        }
    }

    public static JFreeChart createChart(Chart chart) {
        switch (chart.getChartType()) {
        case BAR:
            return Charts.createBarChart(chart);
        case PIE:
            return Charts.createPieChart(chart);
        case LINE:
            return Charts.createLineChart(chart);
        case TIME:
        case COMBINED:
        case DIAL:
        default:
            return new JFreeChart(chart.getTitle(), null/* use default font */, chart.getPlot(), chart.isShowLegend());
        }
    }

    private static JFreeChart createLineChart(Chart chart) {
        return null;
    }

    private static JFreeChart createPieChart(Chart chart) {
        return ChartFactory.createPieChart3D("", ((PiePlot)chart.getPlot()).getDataset(), true, true, true);
    }

    private static void applySettingsToDialPlot(DialPlot dialplot, ValueDataset dataset, String anno) {
        dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
        dialplot.setDataset(0, dataset);
        StandardDialFrame standarddialframe = new StandardDialFrame();
        standarddialframe.setBackgroundPaint(Color.lightGray);
        standarddialframe.setForegroundPaint(Color.darkGray);
        dialplot.setDialFrame(standarddialframe);
        GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(),
                new Color(170, 170, 220));
        DialBackground dialbackground = new DialBackground(gradientpaint);
        dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        dialplot.setBackground(dialbackground);
        DialTextAnnotation dialtextannotation = new DialTextAnnotation(anno);
        // dialtextannotation.setFont(new Font("Dialog", 1, 14));
        dialtextannotation.setRadius(0.69999999999999996D);
        dialplot.addLayer(dialtextannotation);

        DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
        // dialvalueindicator.setFont(new Font("Dialog", 0, 10));
        dialvalueindicator.setOutlinePaint(Color.darkGray);
        dialvalueindicator.setNumberFormat(new DecimalFormat("#0.0"));
        dialplot.addLayer(dialvalueindicator);

        double value = dataset.getValue().doubleValue();
        int scaleFactor = value <= 10D ? 1 : 10;

        StandardDialScale standarddialscale = new StandardDialScale(0D, 10D * scaleFactor, -120D, -300D, 1D * scaleFactor, 4);
        standarddialscale.setTickRadius(0.88D);
        standarddialscale.setTickLabelOffset(0.14999999999999999D);
        standarddialscale.setTickLabelFormatter(new DecimalFormat("#"));
        // standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
        dialplot.addScale(0, standarddialscale);
        standarddialscale.setMinorTickPaint(Color.red);

        // standarddialscale1.setTickLabelFont(new Font("Dialog", 0, 10));
        dialplot.mapDatasetToScale(0, 0);

        StandardDialRange standarddialrange = new StandardDialRange(0D, 2D* scaleFactor, Color.green);
        standarddialrange.setScaleIndex(0);
        StandardDialRange standarddialrange1 = new StandardDialRange(2D* scaleFactor, 5D* scaleFactor, Color.yellow);
        standarddialrange.setScaleIndex(0);
        StandardDialRange standarddialrange2 = new StandardDialRange(5D* scaleFactor, 10D * scaleFactor, Color.red);
        standarddialrange.setScaleIndex(0);

        dialplot.addLayer(standarddialrange);
        dialplot.addLayer(standarddialrange1);
        dialplot.addLayer(standarddialrange2);
//        org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin(1);
//        pin.setRadius(0.55000000000000004D);
//        dialplot.addPointer(pin);
        org.jfree.chart.plot.dial.DialPointer.Pointer pointer = new org.jfree.chart.plot.dial.DialPointer.Pointer(0);
        dialplot.addPointer(pointer);
        DialCap dialcap = new DialCap();
        dialplot.setCap(dialcap);

    }
}
