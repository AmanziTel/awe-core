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

import java.util.List;

import org.amanzi.awe.nem.exceptions.NemManagerOperationException;
import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.wizard.pages.InitialNetworkPage;
import org.amanzi.awe.nem.ui.wizard.pages.PropertyEditorPage;
import org.amanzi.neo.nodetypes.INodeType;
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
    public void addPages() {
        addPage(new InitialNetworkPage());
    }

    @Override
    public boolean performFinish() {
        if (getPages().length == 1) {
            handleFirstPageOnFinish(getPages()[0]);
        }

        List<INodeType> types = NetworkElementManager.getInstance().updateNodeTypes(
                getDataContainer().getStructure().toArray(new INodeType[getDataContainer().getStructure().size()]));;
        handleModelRefreshing(types);
        return true;
    }

    protected void handleModelRefreshing(List<INodeType> types) {
        try {
            NetworkElementManager.getInstance().createModel(container.getName(), types, container.getTypeProperties());
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
            initializeNewPages((InitialNetworkPage)getPages()[0], true);
        }
    }

    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            initContainerFromStartPage((InitialNetworkPage)page);
            initializeNewPages((InitialNetworkPage)page, false);
        } else {
            handlePropertyPage((PropertyEditorPage)page);

        }
        return super.getNextPage(page);
    }

    /**
     * @param page
     */
    protected void handlePropertyPage(PropertyEditorPage page) {
        container.putToTypeProperties(page.getType(), page.getProperties());
    }

    /**
     * @param page
     * @param b
     */
    private void initializeNewPages(InitialNetworkPage page, boolean isFinished) {
        for (int i = 1; i < page.getNetworkStructure().size(); i++) {

            INodeType type = page.getNetworkStructure().get(i);
            if (getPage(type.getId()) == null) {
                PropertyEditorPage propertyPage = new PropertyEditorPage(type);
                propertyPage.initializeTypes();
                if (isFinished) {
                    handlePropertyPage(propertyPage);
                } else {
                    addPage(propertyPage);
                }
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
