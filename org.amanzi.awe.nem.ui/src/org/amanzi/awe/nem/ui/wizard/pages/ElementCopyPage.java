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
import java.util.Map.Entry;

import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ElementCopyPage extends ElementCreationPage {

    private final IDataElement parent;

    /**
     * @param type
     * @param model
     * @param parent
     */
    public ElementCopyPage(final INodeType type, final INetworkModel model, final IDataElement parent) {
        super(type, model);
        this.parent = parent;
        setTitle(MessageFormat.format(NEMMessages.ELEMENT_COPY_PAGE, parent.getName()));
        setCheckForEmptyValues(true);
    }

    @Override
    protected List<PropertyContainer> getTypedProperties() {
        List<PropertyContainer> containers = new ArrayList<PropertyContainer>();

        for (Entry<String, Object> property : parent.asMap().entrySet()) {
            if (property.getKey().equals(getGeneralNodeProperties().getNodeTypeProperty())
                    || property.getKey().equals(getNetworkNodeProperties().getStuctureProperty())) {
                continue;
            }
            KnownTypes definedType = KnownTypes.defineClass(property.getValue());
            PropertyContainer container = new PropertyContainer(property.getKey(), definedType);
            container.setValue(property.getValue());
            containers.add(container);
        }
        return containers;
    }
}
