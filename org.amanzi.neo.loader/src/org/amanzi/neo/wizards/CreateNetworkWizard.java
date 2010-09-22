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

package org.amanzi.neo.wizards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.enums.INodeType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CreateNetworkWizard extends Wizard implements INewWizard {

    private IWorkbench workbench;
    private IStructuredSelection selection;
    private Map<INodeType, IWizardPage> pages = new HashMap<INodeType, IWizardPage>();
    private CreateNetworkMainPage mainPage;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
        setWindowTitle("Create network");
        setForcePreviousAndNextButtons(true);
        pages.clear();
        initPages();
    }

    @Override
    public IWizardPage getPreviousPage(IWizardPage page) {
         List<INodeType> struct = mainPage.getStructure();
        if (page instanceof CreateNetworkMainPage) {
            return null;
        } else if (page instanceof CreateNetworkConfigPage) {
            INodeType type = ((CreateNetworkConfigPage)page).getType();
            for (int i = 2; i < struct.size(); i++) {
                if (struct.get(i).equals(type)) {
                    return pages.get(struct.get(i-1));
                }
            }
        }
        return null;
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        List<INodeType> struct = mainPage.getStructure();
        if (page instanceof CreateNetworkMainPage) {
            return pages.get(struct.get(1));
        } else if (page instanceof CreateNetworkConfigPage) {
            INodeType type = ((CreateNetworkConfigPage)page).getType();
            for (int i = 1; i < struct.size() - 1; i++) {
                if (struct.get(i).equals(type)) {
                    return pages.get(struct.get(i+1));
                }
            }
        }
        return null;
    }

    private void initPages() {
        mainPage = new CreateNetworkMainPage("mainPage");
        addPage(mainPage);
    }

    @Override
    public boolean canFinish() {
        if (super.canFinish()) {
            for (IWizardPage page : pages.values()) {
                if (!page.isPageComplete()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean performFinish() {
        return true;
    }

    /**
     * @param type
     */
    public void createPage(INodeType type) {
        if (pages.containsKey(type)) {
            return;
        }
        CreateNetworkConfigPage page = new CreateNetworkConfigPage(type.getId(), type);
        pages.put(type, page);
        page.setWizard(this);
    }

    public void removePage(INodeType type) {
        IWizardPage page = pages.remove(type);
        if (page != null) {
            page.dispose();
        }
    }

}
