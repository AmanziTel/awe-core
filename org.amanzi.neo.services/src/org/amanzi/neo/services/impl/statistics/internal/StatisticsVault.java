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

package org.amanzi.neo.services.impl.statistics.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsVault {

    private int count;

    private final Map<INodeType, NodeTypeVault> nodeTypeVaults = new HashMap<INodeType, NodeTypeVault>();

    private boolean isChanged;

    public StatisticsVault() {
        isChanged = false;
    }

    public void addNodeTypeVault(final NodeTypeVault nodeTypeVault) {
        nodeTypeVaults.put(nodeTypeVault.getNodeType(), nodeTypeVault);
    }

    /**
     * @param nodeType
     * @param asMap
     */
    public void deleteElement(final INodeType nodeType, final Map<String, Object> properties) {
        getNodeTypeVaule(nodeType).deleteElement(properties);
        count--;

    }

    public Collection<NodeTypeVault> getAllNodeTypeVaults() {
        return nodeTypeVaults.values();
    }

    public int getCount() {
        return count;
    }

    public int getCount(final INodeType nodeType) {
        return getNodeTypeVaule(nodeType).getCount();
    }

    /**
     * @param type
     * @param property
     * @return
     */
    public Object getDefaultValue(final INodeType type, final String property) {
        return getNodeTypeVaule(type).getDefaultValue(property);
    }

    public NodeTypeVault getNodeTypeVaule(final INodeType nodeType) {
        NodeTypeVault result = nodeTypeVaults.get(nodeType);

        if (result == null) {
            result = new NodeTypeVault(nodeType);
            nodeTypeVaults.put(nodeType, result);
        }

        return result;
    }

    public Class< ? > getPropertyClass(final INodeType nodeType, final String propertyName) {
        NodeTypeVault nodeTypeVault = getNodeTypeVaule(nodeType);

        if (nodeTypeVault != null) {
            return nodeTypeVault.getPropertyClass(propertyName);
        }

        return null;
    }

    public Set<String> getPropertyNames() {
        Set<String> result = new HashSet<String>();

        for (NodeTypeVault subVault : getAllNodeTypeVaults()) {
            result.addAll(subVault.getPropertyNames());
        }

        return result;
    }

    public Set<String> getPropertyNames(final INodeType nodeType) {
        return getNodeTypeVaule(nodeType).getPropertyNames();
    }

    public int getValueCount(final INodeType nodeType, final String property, final Object value) {
        return getNodeTypeVaule(nodeType).getValueCount(property, value);
    }

    public Set<Object> getValues(final INodeType nodeType, final String property) {
        return getNodeTypeVaule(nodeType).getValues(property);
    }

    public void indexElement(final INodeType nodeType, final Map<String, Object> properties) throws ServiceException {
        assert nodeType != null;
        assert properties != null;

        NodeTypeVault vault = getNodeTypeVaule(nodeType);
        vault.indexElement(properties);

        count++;
        isChanged = true;
    }

    public boolean isChanged() {
        return isChanged;
    }

    /**
     * @param nodeType
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void renameProperty(final INodeType nodeType, final String propertyName, final Object oldValue, final Object newValue) {
        getNodeTypeVaule(nodeType).renameProperty(propertyName, oldValue, newValue);

    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public void updateDefaultProperties(final INodeType nodeType, final Map<String, Object> properties) {
        NodeTypeVault result = getNodeTypeVaule(nodeType);
        result.updateDefaultValues(properties);
        isChanged = result.isChanged();
    }
}
