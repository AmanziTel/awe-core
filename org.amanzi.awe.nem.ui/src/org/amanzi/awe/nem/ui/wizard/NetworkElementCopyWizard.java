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
import org.amanzi.awe.nem.ui.wizard.pages.ElementCopyPage;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkElementCopyWizard extends NetworkElementCreationWizard {

    public NetworkElementCopyWizard(final INetworkModel model, final IDataElement parent, final INodeType type) {
        super(model, parent, type);
    }

    @Override
    public void addPages() {
        addPage(new ElementCopyPage(getType(), getModel(), getElement()));
    }

    @Override
    protected void handleModelRefreshing(final List<INodeType> types, final IProgressMonitor monitor) {
        NetworkElementManager.getInstance().copyElement(getModel(), getElement(), getType(),
                getDataContainer().getTypeProperties().get(getType()), monitor);
    }

}
