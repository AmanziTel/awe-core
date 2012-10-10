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
import org.amanzi.neo.models.network.INetworkModel;
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
    private INetworkModel model;

    public NetworkCreationWizard(INetworkModel model) {
        this.model = model;
        setForcePreviousAndNextButtons(false);
    }

    public NetworkCreationWizard() {
        setForcePreviousAndNextButtons(true);
    }

    @Override
    public boolean performFinish() {
        if (model == null) {
            createModelFromContainer();
        } else {
            createSingleElement();
        }
        return true;
    }

    private void createModelFromContainer() {
        // TODO Auto-generated method stub

    }

    private void createSingleElement() {
        // TODO Auto-generated method stub

    }

    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof InitialNetworkPage) {
            initContainer((InitialNetworkPage)page);
            initializeNewPages((InitialNetworkPage)page);
        } else {
            PropertyEditorPage editor = (PropertyEditorPage)page;
            container.putToTypeProperties(page.getName(), editor.getProperties());
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
