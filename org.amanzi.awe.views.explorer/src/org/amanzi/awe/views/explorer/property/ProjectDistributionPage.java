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

package org.amanzi.awe.views.explorer.property;

import org.amanzi.awe.views.explorer.view.ProjectExplorerView;
import org.amanzi.awe.views.reuse.views.DistributionAnalyzerView;
import org.amanzi.neo.services.model.IDataElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Vladislav_Kondrate
 */
public class ProjectDistributionPage extends DistributionAnalyzerView implements ISelectionListener {

    private ProjectsPropertySourceProvider provider;

    /**
     * Constructor. Sets SourceProvider for this Page
     * 
     * @param viewer
     */

    public ProjectDistributionPage() {
        super();
        // provider = new ProjectsPropertySourceProvider();
        // setPropertySourceProvider(provider);
    }

    /**
     * Get last clicked element
     * 
     * @return
     */
    public IDataElement getLastClickedElement() {
        return provider.getLastRawObject();
    }

    /**
     * Creates a Control of this Page and adds a Listener for NetworkTreeView
     */

    public void createControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().addSelectionListener(ProjectExplorerView.PROJECT_EXPLORER_ID, this);
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        System.out.println("analyze");
    }
}
