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

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.ChartNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.splash.chart.Charts;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class ChartEditorInput implements IEditorInput, IPersistableElement {
	
	private static final String RUBY_PROJECT = "RUBY_PROJECT";
    private static final String CHART_NAME = "CHART_NAME";
    private String chartName;
	private String projectName;

	/**
     * @param chartName
     * @param projectName
     */
    public ChartEditorInput(String projectName, String chartName) {
        super();
        this.chartName = chartName;
        this.projectName = projectName;
    }

    public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

    /**
     * @return Returns the projectName.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName The projectName to set.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public boolean exists() {
        boolean isExist = (Boolean) ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {

            private boolean result;

            public Object getValue() {
                return result;
            }

            public void run() {
                AweProjectService projectService = NeoCorePlugin.getDefault().getProjectService();
                RubyProjectNode rubyProject = projectService.findRubyProject(projectName);
                ChartNode node = projectService.getChartByName(rubyProject, chartName);
                result = node != null;
            }
        });
        return isExist;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return chartName;
    }

    @Override
    public IPersistableElement getPersistable() {
        return this;
    }

    @Override
    public String getToolTipText() {
        return chartName;
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public String getFactoryId() {
        return Charts.getFactoryId();
    }

    @Override
    public void saveState(IMemento memento) {
        memento.putString(RUBY_PROJECT, projectName);
        memento.putString(CHART_NAME, chartName);
    }


	public static ChartEditorInput createEditorInput(IMemento memento ){
	String rubyProjectName = memento.getString(RUBY_PROJECT);
    String chartName = memento.getString(CHART_NAME);
    return new ChartEditorInput(rubyProjectName,chartName);
}
	
	

}
