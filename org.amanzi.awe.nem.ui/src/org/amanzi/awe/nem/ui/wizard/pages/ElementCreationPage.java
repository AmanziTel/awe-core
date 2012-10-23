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

import org.amanzi.awe.nem.internal.NemPlugin;
import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ElementCreationPage extends PropertyEditorPage {

    private final INetworkModel model;

    private final INetworkNodeProperties networkNodeProperties;

    private final IGeneralNodeProperties generalNodeProperties;

    /**
     * @param pageName
     */
    public ElementCreationPage(final INodeType type, final INetworkModel model) {
        super(type);
        this.model = model;
        setTitle(MessageFormat.format(NEMMessages.ELEMENT_CREATION_PAGE_TITLE, type.getId()));
        this.networkNodeProperties = NemPlugin.getDefault().getNetworkNodeProperties();
        this.generalNodeProperties = NemPlugin.getDefault().getGeneralNodeProperties();
    }

    @Override
    protected List<PropertyContainer> getTypedProperties() {
        final List<PropertyContainer> defaultContainers = super.getTypedProperties();
        final IPropertyStatisticsModel propertyModel = model.getPropertyStatistics();
        final Set<String> properties = propertyModel.getPropertyNames(getType());
        final List<PropertyContainer> containers = new ArrayList<PropertyContainer>();

        for (final String property : properties) {
            final Object value = propertyModel.getDefaultValues(getType(), property);
            final Class< ? > clazz = propertyModel.getPropertyClass(getType(), property);
            final KnownTypes type = KnownTypes.getTypeByClass(clazz);
            final PropertyContainer container = new PropertyContainer(property, type);
            container.setValue(value == null ? type.getDefaultValue() : value);
            containers.add(container);
        }
        for (final PropertyContainer container : defaultContainers) {
            if (!containers.contains(container)) {
                containers.add(container);
            }
        }
        return containers;
    }

    /**
     * @return Returns the networkNodeProperties.
     */
    protected INetworkNodeProperties getNetworkNodeProperties() {
        return networkNodeProperties;
    }

    /**
     * @return Returns the generalNodeProperties.
     */
    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }
}
