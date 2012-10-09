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

package org.amanzi.awe.nem.ui.wizard;

import org.amanzi.awe.nem.ui.wizard.pages.InitialNetworkPage;
import org.amanzi.awe.nem.ui.wizard.pages.PropertyEditorPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkCreationWizard extends Wizard {
    private NetworkDataContainer container;

    @Override
    public boolean performFinish() {
        for (IWizardPage page : getPages()) {
            if (!page.isPageComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     */
    public NetworkCreationWizard() {
        super();
        setForcePreviousAndNextButtons(true);
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            initContainer((InitialNetworkPage)page);
            initializeNewPages((InitialNetworkPage)page);
        } else {
            // TODO KV: handle PropertyEditor page handling
        }
        return super.getNextPage(page);
    }

    /**
     * @param page
     */
    private void initializeNewPages(InitialNetworkPage page) {
        for (int i = 1; i < page.getNetworkStructure().size(); i++) {
            IWizardPage newPage = new PropertyEditorPage(page.getNetworkStructure().get(i));
            if (getPage(newPage.getName()) == null) {
                addPage(new PropertyEditorPage(page.getNetworkStructure().get(i)));
            }
        }

    }

    /**
     * @param page
     */
    private void initContainer(InitialNetworkPage page) {
        if (container == null) {
            container = new NetworkDataContainer();
        }
        container.setName(page.getNetworkName());
        container.setStructure(page.getNetworkStructure());

    }

}
