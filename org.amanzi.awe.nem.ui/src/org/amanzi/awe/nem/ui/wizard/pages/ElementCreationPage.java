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

import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
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

    private INetworkModel model;

    /**
     * @param pageName
     */
    public ElementCreationPage(INodeType type, INetworkModel model) {
        super(type);
        this.model = model;
        setTitle(MessageFormat.format(NEMMessages.ELEMENT_CREATION_PAGE_TITLE, type.getId()));
    }

    @Override
    protected List<PropertyContainer> getTypedProperties() {
        List<PropertyContainer> defaultContainers = super.getTypedProperties();
        IPropertyStatisticsModel propertyModel = model.getPropertyStatistics();
        Set<String> properties = propertyModel.getPropertyNames(getType());
        List<PropertyContainer> containers = new ArrayList<PropertyContainer>();

        for (String property : properties) {
            Object value = propertyModel.getDefaultValues(getType(), property);
            Class< ? > clazz = propertyModel.getPropertyClass(getType(), property);
            KnownTypes type = KnownTypes.getTypeByClass(clazz);
            PropertyContainer container = new PropertyContainer(property, type);
            container.setValue(value == null ? type.getDefaultValue() : value);
            containers.add(container);
        }
        for (PropertyContainer container : defaultContainers) {
            if (!containers.contains(container)) {
                containers.add(container);
            }
        }
        return containers;
    }
}
