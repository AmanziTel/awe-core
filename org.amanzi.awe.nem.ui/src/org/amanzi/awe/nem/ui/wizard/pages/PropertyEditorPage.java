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

import org.amanzi.awe.nem.properties.manager.NetworkPropertiesManager;
import org.amanzi.awe.nem.properties.manager.NetworkProperty;
import org.amanzi.awe.nem.ui.messages.NemMessages;
import org.amanzi.awe.nem.ui.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget.ITableChangedWidget;
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
public class PropertyEditorPage extends WizardPage implements ITableChangedWidget {
    private static final GridLayout ONE_COLUMN_LAYOU = new GridLayout(1, false);

    private Composite mainComposite;

    private String type;

    private PropertyTableWidget propertyTablWidget;

    private INetworkModel model;

    /**
     * @param pageName
     */
    public PropertyEditorPage(String type, INetworkModel model) {
        this(type);
        this.model = model;
    }

    public PropertyEditorPage(String type) {
        super(type);
        setTitle(NemMessages.PROPERTY_EDITOR_PAGE_TITLE + type);
        this.type = type;
    }

    @Override
    public void createControl(Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(ONE_COLUMN_LAYOU);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Iterable<NetworkProperty> properties = getTypedProperties();
        propertyTablWidget = new PropertyTableWidget(mainComposite, this, type, properties);
        propertyTablWidget.initializeWidget();
        setControl(mainComposite);

    }

    /**
     * @return
     */
    private Iterable<NetworkProperty> getTypedProperties() {
        if (model == null) {
            return NetworkPropertiesManager.getInstance().getProperties(type);
        } else {
            // TODO KV: implement getting properties from model;
            return null;
        }
    }

    public List<PropertyContainer> getProperties() {
        return propertyTablWidget.getProperties();
    }

    @Override
    public void updateStatus(String message) {
        this.setErrorMessage(message);
        setPageComplete(StringUtils.isEmpty(message));
    }
}
