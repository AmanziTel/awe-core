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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.managers.properties.DynamicNetworkType;
import org.amanzi.awe.nem.managers.structure.NetworkStructureManager;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget;
import org.amanzi.awe.nem.ui.widgets.TypeControlWidget.ITableItemSelectionListener;
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.CRSSelector.ICRSSelectorListener;
import org.amanzi.awe.ui.view.widgets.TextWidget.ITextChandedListener;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
            ICRSSelectorListener,
            ITextChandedListener {

    private static final GridLayout ONE_COLUMN_LAYOU = new GridLayout(1, false);

    private static final GridLayout TWO_COLUMN_LAYOU = new GridLayout(2, false);

    private Composite mainComposite;

    public boolean isCompleate;

    private TypeControlWidget typesSelector;

    private String networkName;
    private final Set<String> existedNetwork;
    private CoordinateReferenceSystem crs;

    /**
     * @param pageName
     */
    public InitialNetworkPage() {
        super(NEMMessages.CREATE_NEW_NETWORK);
        setTitle(NEMMessages.CREATE_NEW_NETWORK);
        existedNetwork = NetworkElementManager.getInstance().getExistedNetworkNames();
    }

    @Override
    public boolean canFlipToNextPage() {
        return isCompleate;
    }

    @Override
    public void createControl(final Composite parent) {
        setMessage("Fill required fields");

        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(ONE_COLUMN_LAYOU);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite networkAndCrsComposite = new Composite(mainComposite, SWT.NONE);
        networkAndCrsComposite.setLayout(TWO_COLUMN_LAYOU);
        networkAndCrsComposite.setLayoutData(getGridData());

        AWEWidgetFactory.getFactory().addStyledTextWidget(this, SWT.BORDER, NEMMessages.NETWORK_NAME_LABEL, networkAndCrsComposite);

        AWEWidgetFactory.getFactory().addCRSSelectorWidget(this, networkAndCrsComposite);

        Composite typeComposite = new Composite(mainComposite, SWT.NONE);
        typeComposite.setLayout(ONE_COLUMN_LAYOU);
        typeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        typesSelector = new TypeControlWidget(typeComposite, SWT.NONE, this, NetworkStructureManager.getInstance()
                .getRequiredNetworkElements());
        typesSelector.initializeWidget();
        setControl(mainComposite);
        onTextChanged(null);

    }

    /**
     * @return
     */
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    /**
     * @return
     */
    private Object getGridData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, true);
    }

    /**
     * @return
     */
    public String getNetworkName() {
        return networkName;
    }

    public List<INodeType> getNetworkStructure() {
        List<INodeType> types = new ArrayList<INodeType>();
        for (String type : typesSelector.getStructure()) {
            INodeType newType;
            try {
                newType = NodeTypeManager.getInstance().getType(type);
            } catch (NodeTypeNotExistsException e) {
                newType = new DynamicNetworkType(type);
            }
            types.add(newType);
        }
        return types;
    }

    @Override
    public void onCRSSelected(final CoordinateReferenceSystem crs) {
        this.crs = crs;

    }

    @Override
    public void onStatusUpdate(final int code, final String message) {
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

    @Override
    public void onTextChanged(final String name) {
        networkName = name;
        if (StringUtils.isEmpty(name)) {
            setErrorMessage(NEMMessages.ENTER_NETWORK_NAME);
            isCompleate = false;
        } else if (existedNetwork.contains(networkName)) {
            setErrorMessage(MessageFormat.format(NEMMessages.NETWORK_ALREADY_EXIST, networkName));
            isCompleate = false;
        } else {
            isCompleate = true;
            setErrorMessage(null);
        }
        setPageComplete(isCompleate);

    }

}