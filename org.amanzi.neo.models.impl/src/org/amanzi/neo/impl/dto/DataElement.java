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

package org.amanzi.neo.impl.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.nodetypes.INodeType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElement implements IDataElement {

    private Node node;

    private final Map<String, Object> properties = new HashMap<String, Object>();

    private INodeType nodeType;

    private String name;

    public DataElement() {

    }

    public DataElement(final Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return node == null ? properties.toString() : node.toString();
    }

    @Override
    public boolean equals(final Object anotherObject) {
        if (anotherObject instanceof DataElement) {
            DataElement anotherElement = (DataElement)anotherObject;
            if (node == null) {
                return properties.equals(anotherElement.properties);
            } else {
                return node.equals(anotherElement.getNode());
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return node == null ? super.hashCode() : node.hashCode();
    }

    @Override
    public Object get(final String header) {
        if ((node != null) && properties.isEmpty()) {
            fillProperties();
        }
        return properties.get(header);
    }

    protected void fillProperties() {
        for (String propertyKey : node.getPropertyKeys()) {
            Object value = node.getProperty(propertyKey);
            properties.put(propertyKey, value);
        }
    }

    @Override
    public Object put(final String key, final Object value) {
        return properties.put(key, value);
    }

    @Override
    public Set<String> keySet() {
        return properties.keySet();
    }

    public void setNodeType(final INodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public INodeType getNodeType() {
        return nodeType;
    }

    @Override
    public long getId() {
        return node == null ? -1 : node.getId();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
