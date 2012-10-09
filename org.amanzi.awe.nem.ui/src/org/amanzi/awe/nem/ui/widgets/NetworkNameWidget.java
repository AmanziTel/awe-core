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

package org.amanzi.awe.nem.ui.widgets;

import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.nem.ui.widgets.NetworkNameWidget.INetworkNameChanged;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

;
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkNameWidget extends AbstractLabeledWidget<Text, INetworkNameChanged> implements ModifyListener {

    public interface INetworkNameChanged extends IAWEWidgetListener {
        void onNameChanged(String name);
    }

    private Text tNetworkNameField;

    /**
     * @param parent
     * @param listener
     * @param label
     */
    public NetworkNameWidget(Composite parent, INetworkNameChanged listener) {
        super(parent, listener, NemMessages.NETWORK_NAME_LABEL);
    }

    @Override
    protected Text createControl(Composite parent) {
        parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        tNetworkNameField = new Text(parent, SWT.BORDER);
        tNetworkNameField.addModifyListener(this);
        return tNetworkNameField;
    }

    protected GridData getElementLayoutData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, true);
    }

    @Override
    public void modifyText(ModifyEvent e) {
        for (INetworkNameChanged listener : getListeners()) {
            listener.onNameChanged(tNetworkNameField.getText());
        }

    }
}
