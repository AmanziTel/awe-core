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

import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
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
	
	protected boolean isDone = false;
	
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
	
	boolean executingAfpEngine = false;
	

    @Override
    public boolean performFinish() {

    	if(!executingAfpEngine) {
    		model.saveUserData();
    	
    		pages[8].refreshPage();
    		getContainer().showPage(pages[8]);
    		executingAfpEngine = true;
    	
    		return false;
    	}
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
    	
    	isDone = false;
    	System.out.println("Current Page -- " + page.getClass().toString());
    	if(nextPage != null) {
//        	System.out.println("Next Page -- " + nextPage.getClass().toString());
    		model.saveUserData();
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
    		//model.executeAfpEngine((AfpProgressPage)page, parameters);
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

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
    

}
