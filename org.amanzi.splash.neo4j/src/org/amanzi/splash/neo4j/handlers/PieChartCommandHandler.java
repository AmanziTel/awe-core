package org.amanzi.splash.neo4j.handlers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import org.amanzi.splash.neo4j.ui.AbstractSplashEditor;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PieChartCommandHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public PieChartCommandHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		try {
			window.getActivePage().showView("org.amanzi.splash.views.SplashChartsView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NeoSplashUtil.logn("Title: " + window.getActivePage().getActiveEditor().getTitle());
		
		AbstractSplashEditor editor = (AbstractSplashEditor) window.getActivePage().getActiveEditor();
		
		//editor.plotCellsPieChart();
		
		
		
		
		return null;
	}
	 
	

}
