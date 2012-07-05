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

import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeTypeVault {

    private int count;

    private final INodeType nodeType;

    public NodeTypeVault(final INodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getCount() {
        return count;
    }

    public void indexProperty(final String propertyValue, final Object propetyValue) {

    }

    public Set<String> getPropertyNames() {
        return null;
    }

    public Set<Object> getValues(final String property) {
        return null;
    }

    public int getValueCount(final String property, final Object value) {
        return 0;
    }

}
