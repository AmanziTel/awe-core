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

package org.amanzi.awe.afp.wizards;

import java.io.IOException;
import java.util.HashMap;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.ControlFileProperties;
import org.amanzi.awe.afp.executors.AfpProcessExecutor;
import org.amanzi.awe.afp.loaders.AfpLoader;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * <p>
 * Wizard for import AFP data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpImportWizard extends Wizard implements IImportWizard {

    private AfpLoadWizardPage loadPage;
    private GraphDatabaseService servise;
    protected HashMap<String, String> parameters;

    @Override
    public boolean performFinish() {
    	parameters = new HashMap<String, String>();
    	parameters.put(ControlFileProperties.SITE_SPACING, loadPage.siteSpacing.getText().toString());
    	parameters.put(ControlFileProperties.CELL_SPACING, loadPage.cellSpacing.getText().toString());
    	parameters.put(ControlFileProperties.REG_NBR_SPACING, loadPage.regNbrSpacing.getText().toString());
    	parameters.put(ControlFileProperties.MIN_NEIGBOUR_SPACING, loadPage.minNbrSpacing.getText().toString());
    	parameters.put(ControlFileProperties.SECOND_NEIGHBOUR_SPACING, loadPage.secondNbrSpacing.getText().toString());
    	parameters.put(ControlFileProperties.QUALITY, String.valueOf(loadPage.qualityScale.getSelection()));
    	parameters.put(ControlFileProperties.G_MAX_RT_PER_CELL, loadPage.gMaxRTperCell.getText().toString());
    	parameters.put(ControlFileProperties.G_MAX_RT_PER_SITE, loadPage.gMaxRTperSite.getText().toString());
    	parameters.put(ControlFileProperties.HOPPING_TYPE, loadPage.hoppingType.getText().toString());
    	parameters.put(ControlFileProperties.NUM_GROUPS, loadPage.nrOfGroups.getText().toString());
    	parameters.put(ControlFileProperties.CELL_CARDINALITY, loadPage.cellCardinality.getText().toString());
    	parameters.put(ControlFileProperties.CARRIERS, loadPage.carriers.getText().toString());
    	parameters.put(ControlFileProperties.USE_GROUPING, loadPage.useGrouping.getSelection() ? "1" : "0");
    	parameters.put(ControlFileProperties.EXIST_CLIQUES, loadPage.existCliques.getSelection() ? "1" : "0");
    	parameters.put(ControlFileProperties.RECALCULATE_ALL, loadPage.recalculateAll.getSelection() ? "1" : "0");
    	parameters.put(ControlFileProperties.USE_TRAFFIC, loadPage.useTraffic.getSelection() ? "1" : "0");
    	parameters.put(ControlFileProperties.USE_SO_NEIGHBOURS, loadPage.useSONbrs.getSelection() ? "1" : "0");
    	parameters.put(ControlFileProperties.DECOMPOSE_CLIQUES, loadPage.decomposeInCliques.getSelection() ? "1" : "0");
    	  	
    	
    	
    	if (loadPage.getFileName() == null || loadPage.getFileName().trim().isEmpty()){
    		Job job2 = new AfpProcessExecutor("Execute Afp Process", loadPage.datasetNode, servise, parameters);
            job2.schedule();
    	}
    	
    	
   
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Automatic Frequency Planning");
        servise = NeoServiceProviderUi.getProvider().getService();
        loadPage = new AfpLoadWizardPage("loadPage", servise);
        addPage(loadPage);
    }

}
