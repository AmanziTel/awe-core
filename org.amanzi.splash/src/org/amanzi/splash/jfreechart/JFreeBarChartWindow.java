package org.amanzi.splash.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

public class JFreeBarChartWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private DefaultCategoryDataset dataset;
	private JFreeChart jfc;
	

	public JFreeBarChartWindow(String[] Categories, Double[] Values)
	{
		this.Categories = Categories;
		dataset = createDataset(Categories, Values);
	}
	
	String[] Categories;
	
	
	private int firstRow;
	private int lastRow;
	private int firstColumn;
	private int lastColumn;
	
	public void setDimensions(int firstRow, int lastRow, int firstColumn, int lastColumn){
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
	}
	
	String series = "First";
	
	/**
	 * In this method an instance of JSONObject is created and allocate the
	 * values of the static JSONObject property of ChartReportArea class. The
	 * JSONObject contains the data read from the 'sites.geo_json' file A loop
	 * is then used to read the different elements of the json data. The method
	 * gets the data for the sectors from the first 3 features in the json
	 * object.
	 * 
	 * @return A CategoryDataset containing the data.
	 */
	private DefaultCategoryDataset createDataset(String[] Categories, Double[] Values) {
		// row keys...
        
//        String series2 = "Second";
//        String series3 = "Third";
//
//        // column keys...
//        String category1 = "Category 1";
//        String category2 = "Category 2";
//        String category3 = "Category 3";
//        String category4 = "Category 4";
//        String category5 = "Category 5";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i=0;i<Categories.length;i++){
        	dataset.addValue(Values[i], series, Categories[i]);
        }
//        dataset.addValue(1.0, series1, category1);
//        dataset.addValue(4.0, series1, category2);
//        dataset.addValue(3.0, series1, category3);
//        dataset.addValue(5.0, series1, category4);
//        dataset.addValue(5.0, series1, category5);
//
//        dataset.addValue(5.0, series2, category1);
//        dataset.addValue(7.0, series2, category2);
//        dataset.addValue(6.0, series2, category3);
//        dataset.addValue(8.0, series2, category4);
//        dataset.addValue(4.0, series2, category5);
//
//        dataset.addValue(4.0, series3, category1);
//        dataset.addValue(3.0, series3, category2);
//        dataset.addValue(2.0, series3, category3);
//        dataset.addValue(3.0, series3, category4);
//        dataset.addValue(6.0, series3, category5);

        return dataset;

	}

	/**
	 * This method creates a chart.
	 * @param dataset. dataset provides the data to be displayed in the chart. 
	 * The parameter is provided by the 'createDataset()' method.
	 * @return A chart.
	 */
	private JFreeChart createChart(DefaultCategoryDataset dataset) {

		 // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "",       // chart title
            "",               // domain axis label
            "Value",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
//        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
//                0.0f, 0.0f, new Color(0, 64, 0));
//        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
//                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
//        renderer.setSeriesPaint(1, gp1);
//        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
	}

	
	public void setValue(int index, Double numDouble)
	{
		String title = Categories[index]; 
		dataset.setValue(numDouble, series, title);
	}
	
	public void setChar()
	{
		jfc = createChart(dataset);
	}
	
	private JPanel createPanel()
	{
		return new ChartPanel(jfc);
	}
	
	public void Show()
	{
		setContentPane(createPanel());
		setVisible(true);
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}

	public int getFirstColumn() {
		return firstColumn;
	}

	public void setFirstColumn(int firstColumn) {
		this.firstColumn = firstColumn;
	}

	public int getLastColumn() {
		return lastColumn;
	}

	public void setLastColumn(int lastColumn) {
		this.lastColumn = lastColumn;
	}
	
	
	
	
}
