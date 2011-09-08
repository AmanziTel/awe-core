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

import org.amanzi.neo.services.model.IDataElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class DataElement extends HashMap<String, Object> implements IDataElement {
    /** long serialVersionUID field */
    private static final long serialVersionUID = -5368114834697417654L;
    private Node node;

    public DataElement(Node node) {
        this.node = node;
    }

    public DataElement(Map< ? extends String, ? extends Object> params) {
        this.putAll(params);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public Object get(String header) {
        Object result = super.get(header);
        if (result == null) {
            result = node != null ? node.getProperty(header, null) : null;
            this.put(header, result);
        }
        return result;
    }

}
