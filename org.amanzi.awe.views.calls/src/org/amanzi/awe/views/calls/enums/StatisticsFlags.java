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

package org.amanzi.awe.views.calls.enums;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * Flag for statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum StatisticsFlags {

    RED("red",5, new Color(Display.getCurrent(), 255, 0, 0)),
    YELLOW("yellow",0, new Color(Display.getCurrent(), 255, 255, 0)),
    NONE("none",-1, null);
    private String id;
    private Color color;
    private int order;
    
    /**
     * Constructor.
     * @param id
     * @param color
     */
    private StatisticsFlags(String id, int order, Color color) {
        this.id = id;
        this.color = color;
        this.order = order;
    }
    
    /**
     * @return Returns the color.
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return Returns the order.
     */
    public int getOrder() {
        return order;
    }
    
    /**
     * Returns flag by id.
     *
     * @param id String
     * @return StatisticsFlags
     */
    public static StatisticsFlags getFlagById(String id){
        for(StatisticsFlags flag : values()){
            if(flag.id.equals(id)){
                return flag;
            }
        }
        return null;
    }
}
