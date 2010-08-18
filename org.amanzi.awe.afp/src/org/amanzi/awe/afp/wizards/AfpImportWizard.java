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

import org.amanzi.awe.afp.Activator;
import org.amanzi.awe.afp.executors.AfpProcessExecutor;
import org.amanzi.awe.afp.loaders.AfpLoader;
import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
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

    @Override
    public boolean performFinish() {
    	if (loadPage.getFileName() == null || loadPage.getFileName().trim().isEmpty()){
    		Job job2 = new AfpProcessExecutor("Execute Afp Process", loadPage.datasetNode, servise);
            job2.schedule();
    	}
    	
   
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("AFP import");
        servise = NeoServiceProvider.getProvider().getService();
        loadPage = new AfpLoadWizardPage("loadPage", servise);
        addPage(loadPage);
    }

}
