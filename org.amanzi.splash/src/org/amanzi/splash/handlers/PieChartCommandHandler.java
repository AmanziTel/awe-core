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
package org.amanzi.splash.handlers;


import org.amanzi.neo.services.Pair;
import org.amanzi.splash.chart.ChartType;
import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.AbstractSplashEditor;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

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
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		AbstractSplashEditor editor = (AbstractSplashEditor) window.getActivePage().getActiveEditor();
        Pair<Cell[], Cell[]> chartData = editor.getChartData();
        Charts.openChartEditor(editor.plotChart(chartData.l(), chartData.r(), ChartType.PIE),
                NeoSplashUtil.AMANZI_NEO4J_SPLASH_PIE_CHART_EDITOR);

		return null;
	}
	 
	

}
