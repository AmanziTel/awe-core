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

package org.amanzi.neo.core.database.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Base entity
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class Base {
    // TODO add synchronize ?

    /** The node. */
    Node node = null;

    /** The property map. */
    protected HashMap<String, Object> propertyMap = new HashMap<String, Object>();

    /**
     * Instantiates a new base.
     */
    Base() {
        //do nothing
    }

    /**
     * Constructor.
     * 
     * @param node the node
     * @param service the service
     */
    Base(Node node, NeoDataService service) {
        this();
        this.node = node;
        for (String key : node.getPropertyKeys()) {
            propertyMap.put(key, node.getProperty(key));
        }
    }

    /**
     * Gets the property value.
     * 
     * @param key the key
     * @return the property value
     */
    public Object getPropertyValue(String key) {
        return propertyMap.get(key);
    }

    /**
     * Gets the property value.
     * 
     * @param key the key
     * @return the property value
     */
    public String getStringPropertyValue(String key) {
        return (String)getPropertyValue(key);
    }

    /**
     * Gets the property keys.
     * 
     * @return the property keys
     */
    public Set<String> getPropertyKeys() {
        return new HashSet<String>(propertyMap.keySet());
    }

    /**
     * Sets the property value.
     * 
     * @param key the key
     * @param value the value
     */
    public void setPropertyValue(String key, Object value) {
        if (value == null) {
            propertyMap.remove(key);
            return;
        }
        propertyMap.put(key, value);
    }

    /**
     * Gets the name.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return (String)propertyMap.get(INeoConstants.PROPERTY_NAME_NAME);
    }

    /**
     * Sets the name.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        propertyMap.put(INeoConstants.PROPERTY_NAME_NAME, name);
    }

    /**
     * Save in database. transaction do not create in this method!
     */
    void save() {
        assert node != null;
        List<String> keys = new LinkedList<String>();
        for (String key : node.getPropertyKeys()) {
            Object value = propertyMap.get(key);
            if (value == null) {
                node.removeProperty(key);
            } else {
                node.setProperty(key, value);
            }
            keys.add(key);
            propertyMap.put(key, node.getProperty(key));
        }
        for (String key : propertyMap.keySet()) {
            if (keys.contains(key)) {
                continue;
            }
            Object value = propertyMap.get(key);
            if (value != null) {
                node.setProperty(key, value);
            }
        }
    }



    /**
     * Creates in database. transaction do not create in this method!
     * 
     * @param service the service
     */
    void create(NeoDataService service) {
        assert node == null;
        node = service.createNode();
        save();
    }
}
