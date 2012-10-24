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
import java.util.Collections;
import java.util.List;

import org.amanzi.awe.nem.internal.NemPlugin;
import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.NetworkPropertiesManager;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget;
import org.amanzi.awe.nem.ui.widgets.PropertyTableWidget.ITableChangedWidget;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.IWizardPage;
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

    private final INodeType type;

    private PropertyTableWidget propertyTablWidget;

    private final PropertyContainer requireNameProperty;

    private List<PropertyContainer> properties;

    private final INetworkNodeProperties networkNodeProperties;

    private final IGeneralNodeProperties generalNodeProperties;

    public PropertyEditorPage(final INodeType type) {
        super(type.getId());
        this.type = type;
        requireNameProperty = new PropertyContainer("name", KnownTypes.STRING, type.getId());
        setTitle(MessageFormat.format(NEMMessages.PROPERTY_EDITOR_PAGE_TITLE, type.getId()));

        this.networkNodeProperties = NemPlugin.getDefault().getNetworkNodeProperties();
        this.generalNodeProperties = NemPlugin.getDefault().getGeneralNodeProperties();
    }

    /**
     * should be override in other pages if necessary
     * 
     * @param properties2
     * @return
     */
    protected String additionalChecking(final List<PropertyContainer> properties2) {
        return null;
    }

    @Override
    public void createControl(final Composite parent) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(ONE_COLUMN_LAYOU);
        mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        initializeTypes();

        propertyTablWidget = new PropertyTableWidget(mainComposite, this, properties);

        propertyTablWidget.initializeWidget();
        setControl(mainComposite);
        updateStatus(null);

    }

    /**
     * @return Returns the generalNodeProperties.
     */
    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    /**
     * @return Returns the networkNodeProperties.
     */
    protected INetworkNodeProperties getNetworkNodeProperties() {
        return networkNodeProperties;
    }

    @Override
    public IWizardPage getPreviousPage() {
        if (super.getPreviousPage() != null && super.getPreviousPage() instanceof InitialNetworkPage) {
            return null;
        }
        return super.getPreviousPage();
    }

    public List<PropertyContainer> getProperties() {
        return properties;
    }

    /**
     * @return Returns the requireNameProperty.
     */
    protected PropertyContainer getRequireNameProperty() {
        return requireNameProperty;
    }

    public INodeType getType() {
        return type;
    }

    /**
     * should be override in children. if necessary
     * 
     * @return
     */
    protected List<PropertyContainer> getTypedProperties() {
        return new ArrayList<PropertyContainer>();
    }

    public void initializeTypes() {
        if (properties != null && !properties.isEmpty()) {
            return;
        }
        properties = NetworkPropertiesManager.getInstance().getProperties(type.getId());

        List<PropertyContainer> uniqProperties = getTypedProperties();

        Collections.sort(uniqProperties);
        for (final PropertyContainer container : properties) {
            int index = uniqProperties.indexOf(container);
            if (index >= 0) {
                container.setValue(uniqProperties.get(index).getValue());
                uniqProperties.remove(container);
            }
        }
        properties.addAll(uniqProperties);

        if (!properties.contains(requireNameProperty)) {
            properties.add(0, requireNameProperty);
        } else {
            int nameIndex = properties.indexOf(requireNameProperty);
            PropertyContainer container = properties.get(nameIndex);
            container.setValue(StringUtils.EMPTY);
            properties.add(0, container);
            properties.remove(++nameIndex);
        }
    }

    @Override
    public void updateStatus(String message) {
        for (PropertyContainer container : properties) {
            if (StringUtils.isEmpty(container.getValue().toString())) {
                message = "required property \"" + container.getName() + "\"  can't be empty";
                break;
            }
            message = additionalChecking(properties);
        }
        this.setErrorMessage(message);
        setPageComplete(StringUtils.isEmpty(message));
    }
}
