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
import java.util.Set;

import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

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
    
    private PropertyContainer propertyContainer;

    /**
     * Creates a <code>DataElement</code> with an underlying <code>PropertyContainer</code> object.
     * 
     * @param propertyContainer the node to wrap
     */
    public DataElement(PropertyContainer propertyContainer) {
        this.propertyContainer = propertyContainer;
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
        if (propertyContainer instanceof Node)
            return (Node)propertyContainer;
        return null;
    }

    /**
     * @return the underlying node
     */
    public Relationship getRelationship() {
        if (propertyContainer instanceof Relationship)
            return (Relationship)propertyContainer;
        return null;
    }
    
    /**
     * Searches for a property value first in a stored map, and then in the underlying node.
     */
    @Override
    public Object get(String header) {
        Object result = super.get(header);
        if (result == null) {
            result = propertyContainer != null ? propertyContainer.getProperty(header, null) : null;
            if (result != null) {
                this.put(header, result);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        if (this.containsKey(NewAbstractService.NAME)) {
            return this.get(NewAbstractService.NAME).toString();
        } else {
            if (getNode() != null) {
                return (getNode().hasProperty(NewAbstractService.NAME) ? 
                        getNode().getProperty(NewAbstractService.NAME).toString() : 
                        Long.toString(getNode().getId()));
            } else if (getRelationship() != null) {
                return Long.toString(getRelationship().getId());
            } else {
                return super.toString();
            }       
             
        }    
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataElement) {
            if (propertyContainer != null) { 
                return ((DataElement)o).propertyContainer.equals(propertyContainer);
            } else {
                return super.equals(o);
            }
        }
        
        return false;
    }

    @Override 
    public Set<String> keySet() {
        if (propertyContainer == null) {
            return super.keySet();
        }
        for (String property : propertyContainer.getPropertyKeys()) {
            get(property);
        }
        return super.keySet();
    }


}
