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

package org.amanzi.awe.nem.ui.wizard.pages.export;

import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class EditSynonymsPage extends WizardPage implements INetworkExportPage {

    /**
     * @param pageName
     */
    protected EditSynonymsPage() {
        super(NEMMessages.EDIT_PROPERTIES_PAGE);
    }

    @Override
    public void createControl(final Composite parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void isValid() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUpNetwork(final INetworkModel model) {
        // TODO Auto-generated method stub

    }

}
