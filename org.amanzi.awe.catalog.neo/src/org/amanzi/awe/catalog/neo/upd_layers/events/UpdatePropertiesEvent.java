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

import java.util.HashMap;

import org.neo4j.graphdb.Node;

/**
 * Event for update properties.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdatePropertiesEvent extends UpdateLayerEvent {
    
    private HashMap<String, Object> values;
    private boolean needClearOther;

    /**
     * Constructor.
     * @param aGisNode
     * @param newValues
     * @param needClear
     */
    public UpdatePropertiesEvent(Node aGisNode, HashMap<String, Object> newValues, boolean needClear) {
        this(UpdateLayerEventTypes.PROPERTY_UPDATE,aGisNode,newValues,needClear);
    }
    
    /**
     * Constructor.
     * @param aType
     * @param aGisNode
     * @param newValues
     * @param needClear
     */
    protected UpdatePropertiesEvent(UpdateLayerEventTypes aType, Node aGisNode, HashMap<String, Object> newValues, boolean needClear){
        super(aType,aGisNode);
        values = newValues;
        needClearOther = needClear;
    }

    /**
     * @return Returns the values.
     */
    public HashMap<String, Object> getValues() {
        return values;
    }
    
    /**
     * @return Returns the clearOther.
     */
    public boolean isNeedClearOther() {
        return needClearOther;
    }
}
