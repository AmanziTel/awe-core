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

public class JFreePieChartWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private DefaultPieDataset  dataset;
	private JFreeChart jfc;
	

	public JFreePieChartWindow(String[] Categories, Double[] Values)
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
	private DefaultPieDataset createDataset(String[] Categories, Double[] Values) {
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (int i=0;i<Categories.length;i++){
			dataset.setValue(Categories[i], Values[i]);
		}
		
		return dataset;
	}

	/**
	 * This method creates a chart.
	 * 
	 * @param dataset
	 * dataset provides the data to be displayed in the chart. The
	 * parameter is provided by the 'createDataset()' method.
	 * @return A chart.
	 */
	private JFreeChart createChart(DefaultPieDataset dataset) {

		JFreeChart chart = ChartFactory
				.createPieChart3D(
				"",
				dataset, true, true, true);

		return chart;
	}

	
	public void setValue(int index, Double numDouble)
	{
		String title = Categories[index]; 
		dataset.setValue(title, numDouble);
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
