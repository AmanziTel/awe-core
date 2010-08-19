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

package org.amanzi.awe.catalog.neo.upd_layers.events;

import java.util.Map;

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class RefreshPropertiesEvent extends UpdateLayerEvent {
    
    private Node aggrNode;
    private Node propertyNode;
    private Node minSelNode;
    private Node maxSelNode;
    private Map<String, String[]> values;

    /**
     * @param aGisNode
     * @param newValues
     * @param needClear
     */
    public RefreshPropertiesEvent(Node aGisNode, Map<String, String[]> newValues, Node... aNodes) {
        super(UpdateLayerEventTypes.PROPERTY_REFRESH,aGisNode);
        values = newValues;
        aggrNode = aNodes[0];
        propertyNode = aNodes[1];
        minSelNode = aNodes[2];
        maxSelNode = aNodes[3];
    }
    
    /**
     * @return Returns the values.
     */
    public Map<String, String[]> getValues() {
        return values;
    }

    /**
     * @return Returns the aggrNode.
     */
    public Node getAggrNode() {
        return aggrNode;
    }
    
    /**
     * @return Returns the propertyNode.
     */
    public Node getPropertyNode() {
        return propertyNode;
    }
    
    /**
     * @return Returns the minSelNode.
     */
    public Node getMinSelNode() {
        return minSelNode;
    }
    
    /**
     * @return Returns the maxSelNode.
     */
    public Node getMaxSelNode() {
        return maxSelNode;
    }
}
