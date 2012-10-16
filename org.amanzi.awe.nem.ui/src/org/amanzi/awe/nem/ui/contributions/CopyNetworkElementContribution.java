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
import org.amanzi.awe.nem.ui.wizard.NetworkElementCopyWizard;
import org.amanzi.awe.ui.dto.IUIItemNew;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.log4j.Logger;
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
public class CopyNetworkElementContribution extends AbstractNetworkMenuContribution {
    private static final Logger LOGGER = Logger.getLogger(CopyNetworkElementContribution.class);

    @Override
    public void fill(final Menu menu, final int index) {
        final IUIItemNew item = getSelectedItem(LOGGER);

        if (item == null) {
            return;
        }

        final INetworkModel model = MenuUtils.getModelFromItem(item);
        final IDataElement element = MenuUtils.getElementFromItem(item);
        final INodeType type = MenuUtils.getType(model, element);
        String itemName = "copy ";
        if (element == null) {
            itemName += model.getName();
        } else {
            itemName += element.getName();
        }

        final MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
        menuItem.setText(itemName);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                openWizard(model, element, type);
            }
        });

    }

    @Override
    protected IWizard getWizard(final INetworkModel model, final IDataElement root, final INodeType type) {
        return new NetworkElementCopyWizard(model, root, type);
    }
}
