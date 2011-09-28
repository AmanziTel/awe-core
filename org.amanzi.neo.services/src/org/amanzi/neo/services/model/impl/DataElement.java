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

package org.amanzi.neo.services.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Implementation of {@link IDataElement}.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class DataElement extends HashMap<String, Object> implements IDataElement {
    /** long serialVersionUID field */
    private static final long serialVersionUID = -5368114834697417654L;
    private Node node;

    /**
     * Creates a <code>DataElement</code> with an underlying <code>Node</code> object.
     * 
     * @param node the node to wrap
     */
    public DataElement(Node node) {
        this.node = node;
    }

    /**
     * Creates a <code>DataElement</code> without an underlying <code>Node</code> object, but with a
     * bunch of preset parameters.
     * 
     * @param params
     */
    public DataElement(Map< ? extends String, ? extends Object> params) {
        if (params != null) {
            this.putAll(params);
        }
    }

    /**
     * @return the underlying node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Searches for a property value first in a stored map, and then in the underlying node.
     */
    @Override
    public Object get(String header) {
        Object result = super.get(header);
        if (result == null) {
            result = node != null ? node.getProperty(header, null) : null;
            if (result != null) {
                this.put(header, result);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return node == null ? this.get(NewAbstractService.NAME).toString() : StringUtils.EMPTY + node.getId();
    }

}
