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
import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widgets.NetworkComboWidget;
import org.amanzi.awe.ui.view.widgets.NetworkComboWidget.INetworkSelectionListener;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget;
import org.amanzi.awe.ui.view.widgets.ResourceSelectorWidget.IResourceSelectorListener;
import org.amanzi.neo.models.network.INetworkModel;
import org.apache.commons.lang3.StringUtils;
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
public class SelectDestinationFolderPage extends WizardPage
        implements
            INetworkExportPage,
            INetworkSelectionListener,
            IResourceSelectorListener {

    private static final int MINIMAL_LABEL_WIDTH = 0;

    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);

    private INetworkModel model;

    private NetworkComboWidget cNetwork;

    private ResourceSelectorWidget selector;

    private String file;

    /**
     * @param pageName
     */
    public SelectDestinationFolderPage() {
        super(NEMMessages.DESTINATION_FOLDER_PAGE_TITLE);
        setTitle(NEMMessages.DESTINATION_FOLDER_PAGE_TITLE);
    }

    @Override
    public void createControl(final Composite parent) {
        Composite main = new Composite(parent, SWT.BORDER);
        main.setLayout(ONE_ROW_GRID_LAYOUT);
        main.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cNetwork = AWEWidgetFactory.getFactory().addNetworkComboWidget(this, NEMMessages.NETWORK_NAME_LABEL, main,
                MINIMAL_LABEL_WIDTH);
        cNetwork.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite selectorComposite = new Composite(main, SWT.FILL);
        selectorComposite.setLayout(new GridLayout(1, false));
        selectorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.selector = AWEWidgetFactory.getFactory().addDirectorySelector(selectorComposite, this);
        if (model != null) {
            int i = cNetwork.getControl().indexOf(model.getName());
            cNetwork.getControl().select(i);
        }
        setControl(main);
    }

    /**
     * @return
     */
    public INetworkModel getNetworkModel() {
        return model;
    }

    @Override
    public void isValid() {
        if (model == null) {
            setPageComplete(false);
            setErrorMessage(NEMMessages.SELECT_NETWORK_FOR_EXPORT);
            return;
        }
        if (StringUtils.isEmpty(file)) {
            setPageComplete(false);
            setErrorMessage(NEMMessages.FILE_NAME_IS_INCORRECT);
            return;
        }
        setPageComplete(true);
        setErrorMessage(null);
    }

    @Override
    public void onNetworkModelSelected(final INetworkModel model) {
        this.model = model;
        isValid();
    }

    @Override
    public void onResourceChanged() {
        this.file = selector.getFileName();
        isValid();
    }

    @Override
    public void setUpNetwork(final INetworkModel model) {
        this.model = model;
    }
}
