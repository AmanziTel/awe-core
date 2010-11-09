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

package org.amanzi.splash.ui;

import org.amanzi.neo.services.Pair;
import org.amanzi.splash.chart.ChartType;
import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * <p>
 * Action bar contributor for Splash Spreadsheet editor
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class SplashEditorContributor extends EditorActionBarContributor {
    /**
     * Splash plugin id
     */
    private static final String AMANZI_SPLASH_PLUGIN_ID = "org.amanzi.splash";
    private AddBarChart addBarChartAction;
    private AddPieChart addPieChartAction;

    @Override
    public void contributeToToolBar(IToolBarManager toolBarManager) {
        super.contributeToToolBar(toolBarManager);
        addBarChartAction = new AddBarChart();
        addPieChartAction = new AddPieChart();
        toolBarManager.add(addBarChartAction);
        toolBarManager.add(addPieChartAction);
    }

    @Override
    public void setActiveEditor(IEditorPart targetEditor) {
        if (targetEditor instanceof AbstractSplashEditor) {
            AbstractSplashEditor editor = (AbstractSplashEditor)targetEditor;
            addBarChartAction.setEditor(editor);
            addPieChartAction.setEditor(editor);

        }
    }

    /**
     * Action creates a bar chart for a data selected 
     * 
     * @author Pechko_E
     * @since 1.0.0
     */
    private class AddBarChart extends Action {
        
        private AbstractSplashEditor editor;

        /**
         * Constructor
         */
        public AddBarChart() {
            super("Plot Bar Chart", AbstractUIPlugin.imageDescriptorFromPlugin(AMANZI_SPLASH_PLUGIN_ID, "/icons/barchart.png"));
        }

        /**
         * @param editor The editor to set.
         */
        public void setEditor(AbstractSplashEditor editor) {
            this.editor = editor;
        }

        @Override
        public void run() {
            Pair<Cell[], Cell[]> chartData = editor.getChartData();
            Charts.openChartEditor(editor.plotChart(chartData.l(), chartData.r(), ChartType.BAR),
                    NeoSplashUtil.AMANZI_NEO4J_SPLASH_CHART_EDITOR);
        }

    }

    /**
     * Action creates a pie chart for a data selected 
     * 
     * @author Pechko_E
     * @since 1.0.0
     */
    private class AddPieChart extends Action {
        private AbstractSplashEditor editor;

        /**
         * Constructor
         */
        public AddPieChart() {
            super("Plot Pie Chart", AbstractUIPlugin.imageDescriptorFromPlugin(AMANZI_SPLASH_PLUGIN_ID, "/icons/piechart.png"));
        }

        /**
         * @param editor The editor to set.
         */
        public void setEditor(AbstractSplashEditor editor) {
            this.editor = editor;
        }

        @Override
        public void run() {
            Pair<Cell[], Cell[]> chartData = editor.getChartData();
            Charts.openChartEditor(editor.plotChart(chartData.l(), chartData.r(), ChartType.PIE),
                    NeoSplashUtil.AMANZI_NEO4J_SPLASH_PIE_CHART_EDITOR);
        }

    }
}
