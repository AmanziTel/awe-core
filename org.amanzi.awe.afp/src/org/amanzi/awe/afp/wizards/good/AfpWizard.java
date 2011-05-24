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

package org.amanzi.awe.afp.wizards.good;

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.models.AfpModelNew;
import org.amanzi.neo.services.network.NetworkModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AfpWizard extends Wizard implements IImportWizard {
    
    public final static String WIZARD_TITLE = "Automatic Frequency Planning";
    
    private class SaveDataJob extends Job {
        
        /**
         * @param name
         */
        public SaveDataJob() {
            super("Save AFP Scenario data");
            setSystem(true);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            AfpWizard.this.getModel().saveData();
            
            return new Status(IStatus.OK, Activator.PLUGIN_ID, getName());
        }
        
    };
    
    private class CreateScenarioJob extends Job {
        
        private String scenarioName;
        
        private NetworkModel networkModel;
        
        /**
         * @param name
         */
        public CreateScenarioJob(String scenarioName, NetworkModel networkModel) {
            super("Create new AFP Scenario");
            this.scenarioName = scenarioName;
            this.networkModel = networkModel;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            AfpModelNew model = new AfpModelNew(scenarioName, networkModel);
            model.countFreeDomains();
            AfpWizard.this.setModel(model);
            
            return new Status(IStatus.OK, Activator.PLUGIN_ID, getName());
        }
        
    };
    
    private final SaveDataJob saveDataJob = new SaveDataJob();
    
    private AfpModelNew model;
    
    public AfpWizard() {
        super();        
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(WIZARD_TITLE);
    }

    @Override
    public boolean performFinish() {
        return false;
    }
    
    public AfpModelNew getModel() {
        return model;
    }
    
    public void setModel(AfpModelNew model) {
        this.model = model;
    }

    @Override
    public void addPages(){
        addPage(new Step0SelectScenarioPage(this));
        addPage(new Step1OptimizationGoalsPage(this));
        addPage(new Step2AvailableResourcesPage(this));
    }
    
    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof Step0SelectScenarioPage) {
            Step0SelectScenarioPage firstPage = (Step0SelectScenarioPage)page;
            
            AfpModelNew scenario = firstPage.getScenario();
            
            if (scenario == null) {
                try {
                    Job createJob = new CreateScenarioJob(firstPage.getScenarioName(), firstPage.getNetworkModel());
                    createJob.schedule();
                    createJob.join();
                }
                catch (InterruptedException e){ 
                    //do nothing - error on creating data already logged
                }
                scenario = getModel();
            }
            
            scenario.setSelectionModel(firstPage.getSelectionModel());
        }
        
        try {
            saveDataJob.schedule();
            saveDataJob.join();
        }
        catch (InterruptedException e) { 
            //do nothing - error on saving data already logged
        }
        
        AbstractAfpWizardPage afpPage = (AbstractAfpWizardPage)page;
        afpPage.refreshPage();
        
        return super.getNextPage(page);
    }
}
