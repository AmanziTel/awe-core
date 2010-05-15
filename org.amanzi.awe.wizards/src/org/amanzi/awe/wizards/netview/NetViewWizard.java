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

package org.amanzi.awe.wizards.netview;

import java.util.Formatter;

import org.amanzi.awe.wizards.AnalysisType;
import org.amanzi.awe.wizards.AnalysisWizard;
import org.amanzi.awe.wizards.pages.SelectAggregationPage;
import org.amanzi.awe.wizards.pages.SelectAnalysisTypePage;
import org.amanzi.awe.wizards.pages.SelectDatasetPage;
import org.amanzi.awe.wizards.pages.SelectKPIPage;
import org.amanzi.awe.wizards.pages.SelectPropertyPage;
import org.amanzi.awe.wizards.utils.ScriptUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class NetViewWizard extends AnalysisWizard implements INewWizard, IWizard {

    private SelectPropertyPage selectPropertyPage;
    private SelectAnalysisTypePage selectAnalysisTypePage;
    private SelectKPIPage selectKPIPage;
    private SelectDatasetPage selectDatasetPage;
    private SelectAggregationPage selectAggregationPage;

    @Override
    public void addPages() {
        selectAnalysisTypePage = new SelectAnalysisTypePage();
        selectKPIPage = new SelectKPIPage();
        selectDatasetPage = new SelectDatasetPage();
        selectPropertyPage = new SelectPropertyPage(SelectPropertyPage.PAGE_ID, 3);
        selectAggregationPage = new SelectAggregationPage();
        addPage(selectAnalysisTypePage);
        addPage(selectKPIPage);
        addPage(selectDatasetPage);
        addPage(selectPropertyPage);
        addPage(selectAggregationPage);
    }

    @Override
    public boolean performFinish() {
        String ds = getSelectedDataset();
        String datasetScript = new Formatter().format(getDatasetScript(), ds, getSelectedSite()).toString();
        switch (getAnalysisType()) {
        case ANALYZE_COUNTERS:
            String[] counters = selectPropertyPage.getSelection();
            String reportScript = ScriptUtils.generateNetViewScriptForCounters(counters, ds,datasetScript, getAggregation());
            LOGGER.debug(reportScript);
            openReportEditor(reportScript);
            break;
        case ANALYZE_KPIS:
            reportScript = ScriptUtils.generateNetViewScript(getSelectedKPI(), ds,datasetScript, getKpiScript(), getAggregation());
            LOGGER.debug(reportScript);
            openReportEditor(reportScript);
            break;
        default:
        }
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("NetView");
    }

    @Override
    public boolean isNeedsSelectSite() {
        return true;
    }

    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

}
