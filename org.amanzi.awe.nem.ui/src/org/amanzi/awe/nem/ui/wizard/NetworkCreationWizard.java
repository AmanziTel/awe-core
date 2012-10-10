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

import org.amanzi.awe.nem.NetworkElementManager;
import org.amanzi.awe.nem.exceptions.NemManagerOperationException;
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

    public NetworkCreationWizard() {
        setForcePreviousAndNextButtons(true);
    }

    @Override
    public boolean performFinish() {
        if (getPages().length == 1) {
            handleFirstPageOnFinish(getPages()[0]);
        }
        NetworkElementManager.getInstance().updateNodeTypes(
                getDataContainer().getStructure().toArray(new String[getDataContainer().getStructure().size()]));

        handleModelRefreshing();
        return true;
    }

    protected void handleModelRefreshing() {
        try {
            NetworkElementManager.getInstance().createModel(container.getName(), container.getStructure(),
                    container.getTypeProperties());
        } catch (NemManagerOperationException e) {
            return;
        }
    }

    /**
     * @param iWizardPage
     */
    protected void handleFirstPageOnFinish(IWizardPage iWizardPage) {
        if (getPages()[0] instanceof InitialNetworkPage) {
            initContainerFromStartPage((InitialNetworkPage)getPages()[0]);
        }
    }

    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            initContainerFromStartPage((InitialNetworkPage)page);
            initializeNewPages((InitialNetworkPage)page);
        } else {
            handlePropertyPage((PropertyEditorPage)page);

        }
        return super.getNextPage(page);
    }

    /**
     * @param page
     */
    protected void handlePropertyPage(PropertyEditorPage page) {
        container.putToTypeProperties(page.getName(), page.getProperties());
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

    protected NetworkDataContainer getDataContainer() {
        if (container == null) {
            container = new NetworkDataContainer();
        }
        return container;
    }

    /**
     * @param page
     */
    private void initContainerFromStartPage(InitialNetworkPage page) {
        getDataContainer().setName(page.getNetworkName());
        getDataContainer().setStructure(page.getNetworkStructure());

    }

}
