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
import java.util.Date;
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
import org.eclipse.jface.wizard.WizardPage;
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
	public final static String pageName[] = new String[] 
	      { "Load Network Data",
		    "Optimization Goals",
		    "Available Sources",
		    "Frequency Type",
		    "SY Hopping MALs",
		    "Separation Rules",
		    "Scaling Rules",
		    "Summary",
		    "Optimization Progress"
	      };
	
	//private AfpLoadWizardPage loadPage;
    private AfpWizardPage[] pages = new AfpWizardPage[9];
	
	private AfpModel model;
	
    protected HashMap<String, String> parameters;

    @Override
    public boolean performFinish() {

    	model.saveUserData();
    	
    	/*
    	
    	if (afpNode != null ){
    		Job job2 = new AfpProcessExecutor("Execute Afp Process", afpNode, servise, parameters);
            job2.schedule();
    	}*/
    	
    	
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(title);
    	model = new AfpModel();
    	
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
    }
    
    @Override
    public void addPages(){
    	super.addPages();
    	int i=0;
    	pages[i] = new AfpLoadNetworkPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpOptimizationGoalsPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpAvailableResourcesPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpFrequencyTypePage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpSYHoppingMALsPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpSeparationRulesPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpScalingRulesPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpSummaryPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	pages[i] = new AfpProgressPage(pageName[i],  model, "Step " + i + " -" + pageName[i]);
    	addPage(pages[i]);
    	i++;
    	
    }
    
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
    	IWizardPage nextPage = super.getNextPage(page);
    	
    	if(nextPage != null) {
    		((AfpWizardPage)nextPage).refreshPage();
    	}
    	/*
    		if(page instanceof AfpLoadNetworkPage) {
    			if(pages[1] == null) {
    	        	pages[1] = new AfpOptimizationGoalsPage("Optimization Goals",  model, pageName[1]); 
    	        	((AfpOptimizationGoalsPage)pages[1]).refreshPage();
    	        	nextPage = pages[1];
    			}
    		} else  if(page instanceof AfpOptimizationGoalsPage) {
    			if(pages[2] == null) {
    	        	pages[2] = new AfpAvailableResourcesPage("Available Sources", model, pageName[2]); 
    	        	((AfpAvailableResourcesPage)pages[1]).refreshPage();
    	        	nextPage = pages[2];
    			}
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
    	
    	
    	if (nextPage == summaryPage){
    		summaryPage.refreshPage();
    	}
    	*/
    	
    	if (page instanceof AfpProgressPage){
    		model.executeAfpEngine((AfpProgressPage)page, parameters);
//    		for (int i = 0; i < 5; i++){
//    			model.setTableItems(new String[]{new Date().toString(), "dummy", "dummy", "dummy", "dummy", "dummy"});
//    			System.out.println(new Date().toString());
//    			((AfpProgressPage) page).addProgressTableItem(model.getTableItems());
//    			try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    		}
    	}
    	return nextPage;

    }
    
    @Override
    public boolean canFinish(){
    	return isDone;
    }
    

}
