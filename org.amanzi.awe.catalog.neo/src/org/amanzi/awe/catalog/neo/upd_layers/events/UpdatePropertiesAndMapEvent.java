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

import java.util.Collection;
import java.util.HashMap;

import org.neo4j.graphdb.Node;

/**
 * Event for update layer property and map. 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class UpdatePropertiesAndMapEvent extends UpdatePropertiesEvent {
    
    private Collection<Node> selection;
    private boolean needCentered;
    private boolean autoZoom;
    private double[] coords;

    /**
     * Constructor.
     * @param aGisNode
     * @param newValues
     * @param needClear
     */
    public UpdatePropertiesAndMapEvent(Node aGisNode, HashMap<String, Object> newValues, boolean needClear) {
        super(UpdateLayerEventTypes.PROPERTY_AND_MAP_UPDATE, aGisNode, newValues, needClear);
    }

    /**
     * @return Returns the selection.
     */
    public Collection<Node> getSelection() {
        return selection;
    }
    
    /**
     * @param selection The selection to set.
     */
    public void setSelection(Collection<Node> selection) {
        this.selection = selection;
    }
    
    /**
     * @return Returns the needCentered.
     */
    public boolean isNeedCentered() {
        return needCentered;
    }
    
    /**
     * @param needCentered The needCentered to set.
     */
    public void setNeedCentered(boolean needCentered) {
        this.needCentered = needCentered;
    }
    
    /**
     * @return Returns the autoZoom.
     */
    public boolean isAutoZoom() {
        return autoZoom;
    }
    
    /**
     * @param autoZoom The autoZoom to set.
     */
    public void setAutoZoom(boolean autoZoom) {
        this.autoZoom = autoZoom;
    }
    
    /**
     * @return Returns the coords.
     */
    public double[] getCoords() {
        return coords;
    }
    
    /**
     * @param coords The coords to set.
     */
    public void setCoords(double[] coords) {
        this.coords = coords;
    }
}
