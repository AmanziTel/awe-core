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

import java.util.Arrays;
import java.util.List;

import org.amanzi.awe.nem.ui.wizard.pages.PropertyCreatorPage;
import org.amanzi.awe.nem.ui.wizard.pages.PropertyEditorPage;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyCreationWizard extends NetworkCreationWizard {

    private INetworkModel model;

    private String type;

    public PropertyCreationWizard(INetworkModel model, String type) {
        assert model != null;

        this.type = type;
        this.model = model;

        List<String> structure = Arrays.asList(model.getNetworkStructure());
        getDataContainer().setName(model.getName());
        getDataContainer().setStructure(structure);

        setForcePreviousAndNextButtons(false);
    }

    @Override
    protected void handleFirstPageOnFinish(IWizardPage page) {
        handlePropertyPage((PropertyEditorPage)page);
    }

    @Override
    public void addPages() {
        addPage(new PropertyCreatorPage(type, model));
    }

    // TODO KV: not implemented yet
    protected void handleModelRefreshing() {

    }
}
