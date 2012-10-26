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

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.ui.wizard.pages.ElementCreationPage;
import org.amanzi.awe.nem.ui.wizard.pages.PropertyEditorPage;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkElementCreationWizard extends NetworkCreationWizard {

    private final INetworkModel model;

    private final INodeType type;

    private final IDataElement element;

    public NetworkElementCreationWizard(final INetworkModel model, final IDataElement parent, final INodeType type) {
        assert model != null;

        this.type = type;
        this.model = model;
        this.element = parent == null ? getModel().asDataElement() : parent;

        getDataContainer().setName(model.getName());
        getDataContainer().setStructure(type);
        setForcePreviousAndNextButtons(false);
    }

    @Override
    public void addPages() {
        addPage(new ElementCreationPage(type, model));
    }

    /**
     * @return Returns the element.
     */
    protected IDataElement getElement() {
        return element;
    }

    /**
     * @return Returns the model.
     */
    protected INetworkModel getModel() {
        return model;
    }

    /**
     * @return Returns the type.
     */
    protected INodeType getType() {
        return type;
    }

    @Override
    protected void handleFirstPageOnFinish(final IWizardPage page) {
        handlePropertyPage((PropertyEditorPage)page);
    }

    @Override
    protected void handleModelRefreshing(final List<INodeType> types, final IProgressMonitor monitor) {
        NetworkElementManager.getInstance().createElement(model, element, type, getDataContainer().getTypeProperties().get(type),
                monitor);
    }

}
