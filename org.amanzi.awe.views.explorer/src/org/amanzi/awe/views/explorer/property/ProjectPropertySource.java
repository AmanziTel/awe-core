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
package org.amanzi.awe.views.explorer.property;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.impl.DataElement;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.neo4j.neoclipse.property.NodePropertySource;
import org.neo4j.neoclipse.property.PropertyDescriptor;

/**
 * Class that creates a properties of given DataElement.
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class ProjectPropertySource extends NodePropertySource implements IPropertySource {

    /**
     * Instantiates a new network property source.
     * 
     * @param dataElement the dataElement
     */
    public ProjectPropertySource(IDataElement dataElement) {
        super(((DataElement)dataElement).getNode(), null);
    }

    /**
     * Returns the descriptors for the properties of the node.
     * 
     * @return the property descriptors
     */
    @SuppressWarnings({"unused"})
    public IPropertyDescriptor[] getPropertyDescriptors() {

        List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();
        for (IPropertyDescriptor descriptor : getHeadPropertyDescriptors()) {
            descs.add(descriptor);
        }

        for (String key : container.getPropertyKeys()) {
            Object value = container.getProperty(key);
            Class< ? > c = value.getClass();
            NodeTypes nt = NodeTypes.getNodeType(container, null);
            if (nt == null || nt.isPropertyEditable(key)) {
                descs.add(new PropertyDescriptor(key, key, PROPERTIES_CATEGORY));
            } else {
                descs.add(new PropertyDescriptor(key, key, NODE_CATEGORY));
            }
        }
        return descs.toArray(new IPropertyDescriptor[descs.size()]);
    }

    // private Node getParent(Node childNode) {
    // return
    // (childNode).getRelationships(Direction.INCOMING).iterator().next().getOtherNode(childNode);
    // }
}
