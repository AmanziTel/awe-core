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
import org.amanzi.neo.services.impl.statistics.IPropertyStatistics;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsVault implements IPropertyStatistics {

    private int count;

    private final Map<INodeType, NodeTypeVault> nodeTypeVaults = new HashMap<INodeType, NodeTypeVault>();

    private boolean isChanged;

    public StatisticsVault() {
        isChanged = false;
    }

    @Override
    public void indexProperty(final INodeType nodeType, final String property, final Object value) throws ServiceException {
        assert nodeType != null;
        assert !StringUtils.isEmpty(property);
        assert value != null;

        NodeTypeVault vault = getNodeTypeVaule(nodeType);
        vault.indexProperty(property, value);

        count++;
        isChanged = true;
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> result = new HashSet<String>();

        for (NodeTypeVault subVault : getAllNodeTypeVaults()) {
            result.addAll(subVault.getPropertyNames());
        }

        return result;
    }

    @Override
    public Set<String> getPropertyNames(final INodeType nodeType) {
        return getNodeTypeVaule(nodeType).getPropertyNames();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getCount(final INodeType nodeType) {
        return getNodeTypeVaule(nodeType).getCount();
    }

    @Override
    public Set<Object> getValues(final INodeType nodeType, final String property) {
        return getNodeTypeVaule(nodeType).getValues(property);
    }

    @Override
    public int getValueCount(final INodeType nodeType, final String property, final Object value) {
        return getNodeTypeVaule(nodeType).getValueCount(property, value);
    }

    protected NodeTypeVault getNodeTypeVaule(final INodeType nodeType) {
        NodeTypeVault result = nodeTypeVaults.get(nodeType);

        if (result == null) {
            result = new NodeTypeVault(nodeType);
            nodeTypeVaults.put(nodeType, result);
        }

        return result;
    }

    public Collection<NodeTypeVault> getAllNodeTypeVaults() {
        return nodeTypeVaults.values();
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addNodeTypeVault(NodeTypeVault nodeTypeVault) {
        nodeTypeVaults.put(nodeTypeVault.getNodeType(), nodeTypeVault);
    }

}
