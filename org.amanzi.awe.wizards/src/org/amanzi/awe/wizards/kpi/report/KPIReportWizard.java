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

package org.amanzi.awe.wizards.kpi.report;

import java.util.Formatter;

import org.amanzi.awe.wizards.AnalysisType;
import org.amanzi.awe.wizards.AnalysisWizard;
import org.amanzi.awe.wizards.pages.SelectAggregationPage;
import org.amanzi.awe.wizards.pages.SelectDatasetPage;
import org.amanzi.awe.wizards.pages.SelectKPIPage;
import org.amanzi.awe.wizards.pages.SelectPropertyPage;
import org.amanzi.awe.wizards.utils.ScriptUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class KPIReportWizard extends AnalysisWizard implements INewWizard, IWizard {
    

    @Override
    public void addPages() {
        SelectDatasetPage selectDatasetPage = new SelectDatasetPage();
        SelectKPIPage selectKPIPage = new SelectKPIPage();
        SelectAggregationPage selectAggregationPage=new SelectAggregationPage();
        addPage(selectKPIPage);
        addPage(selectDatasetPage);
        addPage(selectAggregationPage);
        // addPage(selectPropertyPage);
    }

    @Override
    public boolean performFinish() {
        LOGGER.debug("DatasetType " + getDatasetType());
        LOGGER.debug("SelectedDataset " + getSelectedDataset());
        String site = getSelectedSite();
        LOGGER.debug("SelectedSite " + site);
        LOGGER.debug("SelectedKPI " + getSelectedKPI());
        LOGGER.debug("DatasetScript " + getDatasetScript());
        LOGGER.debug("KpiScript " + getKpiScript());
        
        String datasetScript = new Formatter().format(getDatasetScript(), getSelectedDataset(), getSelectedSite()).toString();
        String reportScript = ScriptUtils.generateKPIReportScript(getSelectedKPI(), datasetScript, getKpiScript(),getAggregation());
        LOGGER.debug(reportScript);
        openReportEditor(reportScript);
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("KPI Report");
        setAnalysisType(AnalysisType.ANALYZE_KPIS);
    }

}
