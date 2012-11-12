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

import java.util.Map;
import java.util.TreeMap;

import org.amanzi.awe.nem.export.ExportedDataItems;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.models.network.INetworkModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ExportedDataSetupPage extends WizardPage implements INetworkExportPage, SelectionListener {

    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);
    private final Map<Integer, ExportedDataItems> sourcePages;
    private INetworkModel model;

    /**
     * @param pageName
     */
    public ExportedDataSetupPage() {
        super(NEMMessages.SELECT_NETWORK_DATA_TO_EXPORT);
        setTitle(NEMMessages.SELECT_NETWORK_DATA_TO_EXPORT);
        sourcePages = new TreeMap<Integer, ExportedDataItems>();
    }

    /**
     * @param mainComposite
     * @param item
     */
    private void createCombobox(final Composite mainComposite, final ExportedDataItems item) {
        Button bCheck = new Button(mainComposite, SWT.CHECK);
        bCheck.setText(item.getName());
        bCheck.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bCheck.addSelectionListener(this);
        if (item.getIndex() == 0) {
            sourcePages.put(item.getIndex(), item);
            bCheck.setSelection(true);
            bCheck.setEnabled(false);
        }
    }

    @Override
    public void createControl(final Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(ONE_ROW_GRID_LAYOUT);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (ExportedDataItems item : ExportedDataItems.values()) {
            createCombobox(mainComposite, item);
        }
        setControl(mainComposite);
    }

    public Map<Integer, ExportedDataItems> getSelectedPages() {
        return sourcePages;
    }

    @Override
    public void isValid() {
        if (sourcePages.isEmpty()) {
            setPageComplete(false);
            setErrorMessage(NEMMessages.SELECTED_EXPORTED_DATA_MESSAGE);
            return;
        }
        setPageComplete(true);
        setErrorMessage(null);
    }

    @Override
    public void setUpNetwork(final INetworkModel model) {
        if (model != null && !model.equals(this.model)) {
            this.model = model;
        }

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        Button b = (Button)e.getSource();
        ExportedDataItems item = ExportedDataItems.findByName(b.getText());
        if (b.getSelection()) {
            if (!sourcePages.containsValue(b.getText())) {
                sourcePages.put(item.getIndex(), item);
            }
        } else if (!b.getSelection()) {
            sourcePages.remove(item.getIndex());
        }
        isValid();
    }
}
