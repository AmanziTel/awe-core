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

package org.amanzi.neo.loader.ui.wizard.impl.internal;

import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.ui.page.ILoaderPage;
import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLoaderWizard<T extends IConfiguration> extends Wizard implements ILoaderWizard<T> {

    private class LoadJob extends Job {

        /**
         * @param name
         */
        public LoadJob() {
            super("Loading data");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            for (IWizardPage page : getPages()) {
                if (page instanceof ILoaderPage) {
                    try {
                        runLoader(page, monitor);
                    } catch (ModelException e) {

                    }
                }
            }

            return null;
        }

        @SuppressWarnings("unchecked")
        private <C extends IConfiguration> void runLoader(IWizardPage page, IProgressMonitor monitor) throws ModelException {
            ILoaderPage<C> loaderPage = (ILoaderPage<C>)page;

            ILoader<C, ? > loader = loaderPage.getCurrentLoader();

            loader.init(getConfiguration(loaderPage));
            loader.run(monitor);
        }

    }

    @Override
    public void init(final IWorkbench workbench, final IStructuredSelection selection) {

    }

    @Override
    public void addLoaderPage(final ILoaderPage<T> loaderPage) {
        addPage(loaderPage);
    }

    @Override
    public boolean performFinish() {
        Job loadDataJob = new LoadJob();
        loadDataJob.schedule();
        return true;
    }

}
