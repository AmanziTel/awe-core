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

package org.amanzi.neo.loader.ui.wizards;

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.loaders.ILoader;
import org.amanzi.neo.services.exceptions.AWEException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
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
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractLoaderWizard extends Wizard implements IImportWizard {
    
    private class LoadDataJob extends Job {

        /**
         * @param name
         */
        public LoadDataJob(String name) {
            super(name);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                monitor.beginTask(getName(), getPages().length);
                for (IWizardPage page : getPages()) {
                    if (page instanceof AbstractLoaderPage) {
                        AbstractLoaderPage<? extends IConfiguration> loaderPage = (AbstractLoaderPage<? extends IConfiguration>)page;
                    
                        ILoader loader = loaderPage.getLoader();
                        loader.init(loaderPage.getConfiguration());
                        loader.run(monitor);
                    }
                }
            } catch (AWEException e) {
                e.printStackTrace();
                return new Status(Status.ERROR, NeoLoaderPlugin.PLUGIN_ID, "", e);
            }
            
            return Status.OK_STATUS;
        }
        
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        
    }

    @Override
    public boolean performFinish() {
        new LoadDataJob(getWindowTitle()).schedule();
        
        return true;
    }
    
    public abstract void initAdditionPages();

}
