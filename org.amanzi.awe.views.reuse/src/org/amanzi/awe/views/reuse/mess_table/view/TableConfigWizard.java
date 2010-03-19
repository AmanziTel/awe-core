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

package org.amanzi.awe.views.reuse.mess_table.view;

import org.amanzi.awe.views.reuse.Messages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for configure table columns visibility.  
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TableConfigWizard extends Wizard implements IImportWizard {

    private TableConfigWizardPage mainPage;
    private String[] visible;
    private String[] invisible;
    private String dataset;
    
    /**
     * Constructor.
     * @param datasetName
     * @param visibleArr
     * @param invisibleArr
     */
    public TableConfigWizard(String datasetName,String[] visibleArr, String[] invisibleArr) {
        visible = visibleArr;
        invisible = invisibleArr;
        dataset = datasetName;
    }

    @Override
    public boolean performFinish() {
        visible = mainPage.getVisibleProperties();
        invisible = mainPage.getInvisibleProperties();
        return true;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new TableConfigWizardPage(Messages.TableConfigWizard_title,
                Messages.TableConfigWizard_description + dataset,visible,invisible);
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }  
    
    /**
     * @return Returns the visible.
     */
    public String[] getVisible() {
        return visible;
    }
    
    /**
     * @return Returns the invisible.
     */
    public String[] getInvisible() {
        return invisible;
    }

}
