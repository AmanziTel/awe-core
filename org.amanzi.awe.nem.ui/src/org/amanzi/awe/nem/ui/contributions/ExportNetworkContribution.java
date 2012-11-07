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

package org.amanzi.awe.nem.ui.contributions;

import org.amanzi.awe.nem.ui.utils.MenuUtils;
import org.amanzi.awe.nem.ui.wizard.NetworkExportWizard;
import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ExportNetworkContribution extends AbstractNetworkMenuContribution {

    @Override
    public void fill(final Menu menu, final int index) {
        final IUIItem item = getSelectedItem();

        if (item == null) {
            return;
        }

        final INetworkModel model = MenuUtils.getModelFromItem(item);
        String itemName = "Export network " + model.getName() + " to .txt";

        final MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);

        menuItem.setText(itemName);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                openWizard(model, null, null);
            }
        });

    }

    @Override
    protected IWizard getWizard(final INetworkModel model, final IDataElement root, final INodeType newType) {
        return new NetworkExportWizard(model);
    }

}
