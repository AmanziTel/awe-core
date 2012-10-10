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

package org.amanzi.awe.nem.ui.wizard.pages;

import java.util.List;

import org.amanzi.awe.nem.managers.structure.NetworkStructureManager;
import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.nem.ui.widgets.CRSSelectionWidget;
import org.amanzi.awe.nem.ui.widgets.CRSSelectionWidget.ICRSSelectedListener;
import org.amanzi.awe.nem.ui.widgets.NetworkNameWidget;
import org.amanzi.awe.nem.ui.widgets.NetworkNameWidget.INetworkNameChanged;
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget;
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget.ITableItemSelectionListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class InitialNetworkPage extends WizardPage
        implements
            ITableItemSelectionListener,
            INetworkNameChanged,
            ICRSSelectedListener {

    private static final GridLayout ONE_COLUMN_LAYOU = new GridLayout(1, false);

    private static final GridLayout TWO_COLUMN_LAYOU = new GridLayout(2, false);

    private Composite mainComposite;

    public boolean isCompleate;

    private TypeControlWidget typesSelector;

    private String networkName;

    /**
     * @param pageName
     */
    public InitialNetworkPage() {
        super(NemMessages.CREATE_NEW_NETWORK);
        setTitle(NemMessages.CREATE_NEW_NETWORK);
    }

    @Override
    public void createControl(Composite parent) {
        setMessage("Fill required fields");

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(ONE_COLUMN_LAYOU);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite networkAndCrsComposite = new Composite(mainComposite, SWT.NONE);
        networkAndCrsComposite.setLayout(TWO_COLUMN_LAYOU);
        networkAndCrsComposite.setLayoutData(getGridData());

        NetworkNameWidget networkNameWidget = new NetworkNameWidget(networkAndCrsComposite, this);
        networkNameWidget.initializeWidget();

        CRSSelectionWidget crsSelectionWidget = new CRSSelectionWidget(networkAndCrsComposite, SWT.NONE, this);
        crsSelectionWidget.initializeWidget();

        Composite typeComposite = new Composite(mainComposite, SWT.NONE);
        typeComposite.setLayout(ONE_COLUMN_LAYOU);
        typeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        typesSelector = new TypeControlWidget(typeComposite, SWT.NONE, this, NetworkStructureManager.getInstance()
                .getRequiredNetworkElements());
        typesSelector.initializeWidget();
        setControl(mainComposite);

    }

    public List<String> getNetworkStructure() {
        return typesSelector.getStructure();
    }

    @Override
    public boolean canFlipToNextPage() {
        return isCompleate;
    }

    /**
     * @return
     */
    private Object getGridData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, true);
    }

    @Override
    public boolean isPageComplete() {
        return isCompleate;
    }

    @Override
    public void onNameChanged(String name) {
        networkName = name;
        if (name.isEmpty()) {
            isCompleate = false;
        }
        isCompleate = true;
        setPageComplete(isCompleate);
    }

    @Override
    public void onCRSSelecte() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusUpdate(int code, String message) {
        switch (code) {
        case WARNING:
            setErrorMessage(message);
            break;
        case INFORMATION:
            setErrorMessage(null);
        default:
            break;
        }

    }

    /**
     * @return
     */
    public String getNetworkName() {
        return networkName;
    }
}