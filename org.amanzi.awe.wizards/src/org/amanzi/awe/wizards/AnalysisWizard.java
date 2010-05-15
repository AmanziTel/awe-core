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

package org.amanzi.awe.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.amanzi.awe.wizards.kpi.report.KPIReportWizard;
import org.amanzi.integrator.awe.AWEProjectManager;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public abstract class AnalysisWizard extends Wizard {
    protected static final Logger LOGGER = Logger.getLogger(KPIReportWizard.class);
    private String selectedKPI;
    private String kpiScript;
    private String datasetScript;
    private String datasetType;
    private String selectedDataset;
    private String selectedSite;
    private String aggregation;
    private boolean needsAggregation;
    private boolean needsSelectSite;
    private AnalysisType type;
    private List<String> properties;

    @Override
    public boolean performFinish() {
        return false;
    }

    /**
     * @return Returns the selectedKPI.
     */
    public String getSelectedKPI() {
        return selectedKPI;
    }

    /**
     * @param selectedKPI The selectedKPI to set.
     */
    public void setSelectedKPI(String selectedKPI) {
        this.selectedKPI = selectedKPI;
    }

    /**
     * @return Returns the datasetType.
     */
    public String getDatasetType() {
        return datasetType;
    }

    /**
     * @param datasetType The datasetType to set.
     */
    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    /**
     * @return Returns the selectedDataset.
     */
    public String getSelectedDataset() {
        return selectedDataset;
    }

    /**
     * @param selectedDataset The selectedDataset to set.
     */
    public void setSelectedDataset(String selectedDataset) {
        this.selectedDataset = selectedDataset;
    }

    /**
     * @return Returns the selectedSite.
     */
    public String getSelectedSite() {
        return selectedSite;
    }

    /**
     * @param selectedSite The selectedSite to set.
     */
    public void setSelectedSite(String selectedSite) {
        this.selectedSite = selectedSite;
    }

    /**
     * @return Returns the needsAggregation.
     */
    public boolean isNeedsAggregation() {
        return needsAggregation;
    }

    /**
     * @param needsAggregation The needsAggregation to set.
     */
    public void setNeedsAggregation(boolean needsAggregation) {
        this.needsAggregation = needsAggregation;
    }

    /**
     * @return Returns the datasetScript.
     */
    public String getDatasetScript() {
        return datasetScript;
    }

    /**
     * @param datasetScript The datasetScript to set.
     */
    public void setDatasetScript(String datasetScript) {
        this.datasetScript = datasetScript;
    }

    /**
     * @return Returns the kpiScript.
     */
    public String getKpiScript() {
        return kpiScript;
    }

    /**
     * @param kpiScript The kpiScript to set.
     */
    public void setKpiScript(String kpiScript) {
        this.kpiScript = kpiScript;
    }

    protected void openReportEditor(String reportScript) {
        IFile file;
        try {
            int i = 0;
            // find or create AWE and RDT project
            String aweProjectName = AWEProjectManager.getActiveProjectName();
            IRubyProject rubyProject;
            try {
                rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, aweProjectName);
            } catch (CoreException e2) {
                // TODO Handle CoreException
                throw (RuntimeException)new RuntimeException().initCause(e2);
            }

            final IProject project = rubyProject.getProject();

            while ((file = project.getFile(new Path(("report" + i) + ".r"))).exists()) {
                i++;
            }

            InputStream is = new ByteArrayInputStream(reportScript.getBytes());
            file.create(is, true, null);
            is.close();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file),
                    "org.amanzi.awe.report.editor.ReportEditor");
        } catch (Exception e) {
            LOGGER.error("An error occured during report creation", e);
        }
    }

    /**
     * @return Returns the aggregation.
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation The aggregation to set.
     */
    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * @return Returns the type.
     */
    public AnalysisType getAnalysisType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setAnalysisType(AnalysisType type) {
        this.type = type;
    }

    public void setAvailableProperties(List<String> properties) {
        this.properties=properties;
    }

    /**
     * @return Returns the properties.
     */
    public List<String> getAvailableProperties() {
        return properties;
    }

    /**
     * @return Returns the needsSelectSite.
     */
    public boolean isNeedsSelectSite() {
        return needsSelectSite;
    }

    /**
     * @param needsSelectSite The needsSelectSite to set.
     */
    public void setNeedsSelectSite(boolean needsSelectSite) {
        this.needsSelectSite = needsSelectSite;
    }

}
