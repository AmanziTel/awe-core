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
import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodetypes.INodeType;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ElementCreationPage extends PropertyEditorPage {

    private static final Logger LOGGER = Logger.getLogger(ElementCreationPage.class);
    private final INetworkModel model;

    /**
     * @param pageName
     */
    public ElementCreationPage(final INodeType type, final INetworkModel model) {
        super(type);
        this.model = model;
        setTitle(MessageFormat.format(NEMMessages.ELEMENT_CREATION_PAGE_TITLE, type.getId()));
        setCheckForEmptyValues(true);
    }

    @Override
    protected String additionalChecking(final List<PropertyContainer> properties) {
        PropertyContainer nameContainer = properties.get(properties.indexOf(getRequireNameProperty()));
        IDataElement sector;
        try {
            if (getType().equals(NetworkElementType.SECTOR)) {
                PropertyContainer ciContainer = new PropertyContainer(getNetworkNodeProperties().getCIProperty(),
                        KnownTypes.INTEGER);
                PropertyContainer lacContainer = new PropertyContainer(getNetworkNodeProperties().getLACProperty(),
                        KnownTypes.INTEGER);
                int ciIndex = properties.indexOf(ciContainer);
                int lacIndex = properties.indexOf(lacContainer);
                ciContainer = properties.get(ciIndex);
                lacContainer = properties.get(lacIndex);

                sector = model.findSector((String)nameContainer.getValue(), (Integer)ciContainer.getValue(),
                        (Integer)lacContainer.getValue());

                if (sector != null) {
                    return "sector with name: " + nameContainer.getValue() + ", and(or) with ci: " + ciContainer.getValue()
                            + "  lac: " + lacContainer.getValue() + " has already exists in model " + model.getName();
                }
            } else {
                sector = model.findElement(NetworkElementManager.getInstance().getNetworkType(getType()),
                        (String)nameContainer.getValue());
                if (sector != null) {
                    return getType() + " with name " + nameContainer.getValue() + " has already exists";
                }
            }
            return null;
        } catch (ModelException e) {
            LOGGER.error("error while trying to find element", e);
            return "error while trying to find element";
        }
    }

    @Override
    protected List<PropertyContainer> getTypedProperties() {
        final IPropertyStatisticsModel propertyModel = model.getPropertyStatistics();
        final Set<String> properties = propertyModel.getPropertyNames(getType());
        final List<PropertyContainer> containers = new ArrayList<PropertyContainer>();

        for (final String property : properties) {
            if (property.equals(getGeneralNodeProperties().getNodeTypeProperty())) {
                continue;
            }
            Object value = propertyModel.getDefaultValues(getType(), property);
            final Class< ? > clazz = propertyModel.getPropertyClass(getType(), property);
            final KnownTypes type = KnownTypes.getTypeByClass(clazz);
            final PropertyContainer container = new PropertyContainer(property, type);
            container.setValue(value == null ? type.getDefaultValue() : value);
            containers.add(container);
        }
        return containers;
    }
}
