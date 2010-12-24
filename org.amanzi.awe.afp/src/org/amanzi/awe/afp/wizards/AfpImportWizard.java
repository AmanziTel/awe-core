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
import org.amanzi.awe.afp.models.AfpFrequencyDomainModel;
import org.amanzi.awe.afp.models.AfpHoppingMALDomainModel;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * <p>
 * Wizard for import AFP data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class AfpImportWizard extends Wizard implements IImportWizard {
	
	protected static boolean isDone = false;
	
	public final static String title = "Automatic Frequency Planning";
	public final static String page0Name = "Step 0 - Load Network Data";
    public final static String page1Name = "Step 1 - Optimization Goals";
    public final static String page2Name = "Step 2 - Available Sources";
    public final static String page3Name = "Step 3 - Frequency Type";
    public final static String page4Name = "Step 4 - SY Hopping MALs";
    public final static String page5Name = "Step 5 - Separation Rules";
    public final static String page6Name = "Step 6 - Scaling Rules";
    public final static String page7Name = "Step 7 - Summary";
    public final static String page8Name = "Step 8 - Optimization Progress";
	
	//private AfpLoadWizardPage loadPage;
    private AfpOptimizationGoalsPage goalsPage;
    AfpAvailableResourcesPage resourcesPage;
	private AfpFrequencyTypePage frequencyPage;
	private AfpSYHoppingMALsPage hoppingMALsPage;
	private AfpSeparationRulesPage separationsPage;
	private AfpScalingRulesPage scalingPage;
	private AfpSummaryPage summaryPage;
	private AfpProgressPage progressPage;
	
	private AfpModel model;
	
    protected HashMap<String, String> parameters;

    @Override
    public boolean performFinish() {

    	model.saveUserData();
    	
    	parameters = new HashMap<String, String>();
    	parameters.put(ControlFileProperties.SITE_SPACING, "2");
    	parameters.put(ControlFileProperties.CELL_SPACING, "0");
    	parameters.put(ControlFileProperties.REG_NBR_SPACING, "1");
    	parameters.put(ControlFileProperties.MIN_NEIGBOUR_SPACING, "0");
    	parameters.put(ControlFileProperties.SECOND_NEIGHBOUR_SPACING, "1");
    	parameters.put(ControlFileProperties.QUALITY, "100");
    	parameters.put(ControlFileProperties.G_MAX_RT_PER_CELL, "1");
    	parameters.put(ControlFileProperties.G_MAX_RT_PER_SITE, "1");
    	parameters.put(ControlFileProperties.HOPPING_TYPE, "1");
    	parameters.put(ControlFileProperties.NUM_GROUPS, "6");
    	parameters.put(ControlFileProperties.CELL_CARDINALITY, "61");
    	parameters.put(ControlFileProperties.CARRIERS, "6 1 2 3 4 5 6");
    	parameters.put(ControlFileProperties.USE_GROUPING, "1");
    	parameters.put(ControlFileProperties.EXIST_CLIQUES, "0");
    	parameters.put(ControlFileProperties.RECALCULATE_ALL, "1" );
    	parameters.put(ControlFileProperties.USE_TRAFFIC, "1");
    	parameters.put(ControlFileProperties.USE_SO_NEIGHBOURS, "1");
    	parameters.put(ControlFileProperties.DECOMPOSE_CLIQUES, "0");
    	  	
    	/*
    	
    	if (afpNode != null ){
    		Job job2 = new AfpProcessExecutor("Execute Afp Process", afpNode, servise, parameters);
            job2.schedule();
    	}*/
    	model.executeAfpEngine(parameters);
    	
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(title);
    	model = new AfpModel();
    }
    
    @Override
    public void addPages(){
    	super.addPages();
    	addPage(new AfpLoadNetworkPage("Load Network",  model));
    	goalsPage = new AfpOptimizationGoalsPage("Optimization Goals",  model); 
    	addPage(goalsPage);
    	resourcesPage = new AfpAvailableResourcesPage("Available Sources", model); 
    	addPage(resourcesPage);
    	frequencyPage = new AfpFrequencyTypePage("Frequency Type", model);
    	addPage(frequencyPage);
    	hoppingMALsPage = new AfpSYHoppingMALsPage("SY Hopping MALs", model);
    	addPage(hoppingMALsPage);
    	separationsPage = new AfpSeparationRulesPage("Separation Rules", model); 
    	addPage(separationsPage);
    	scalingPage = new AfpScalingRulesPage("Scaling Rules", model);
    	addPage(scalingPage);
    	summaryPage = new AfpSummaryPage("Summary", model); 
    	addPage(summaryPage);
    	progressPage = new AfpProgressPage("Progress", model);
    	addPage(progressPage);
//    	addPage(new AfpTableExample("Example"));
    }
    
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
    	IWizardPage nextPage = super.getNextPage(page);
    	if (nextPage == goalsPage){
    		System.out.println("Next: Goals Page");
    		goalsPage.refreshPage();
    	}
    	
    	if (nextPage == resourcesPage){
    		System.out.println("Next: Resourcess Page");
    		resourcesPage.refreshPage();
    	}
    	
    	if (nextPage == frequencyPage){
    		System.out.println("Next: Frequency Page");
    		frequencyPage.refreshPage();
    	}
    	
    	if (nextPage == hoppingMALsPage){
    		System.out.println("Next: Hopping Page");
    		hoppingMALsPage.refreshPage();
    	}
    	
    	if (nextPage == separationsPage){
    		System.out.println("Next: Separations Page");
    		separationsPage.refreshPage();
    	}
    	
//    	if (nextPage == scalingPage){
//    		scalingPage.refreshPage();
//    	}
    	
    	if (nextPage == summaryPage){
    		summaryPage.refreshPage();
    	}
    	
    	
    	return super.getNextPage(page);

    }
    
    @Override
    public boolean canFinish(){
    	return isDone;
    }
    

}
