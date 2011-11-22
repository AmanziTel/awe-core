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

package org.amanzi.awe.views.property;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author kostyukovich_n
 * @since 1.0.0
 */
public enum PropertyNumberFilter {
    
    EMPTY ("EMPTY", true),
    NOT_EMPTY ("NOT EMPTY", false);
    
    private String caption;
    private boolean propertyNull;
    
    private PropertyNumberFilter(String caption, boolean propertyNull) {
        this.caption = caption;
        this.propertyNull = propertyNull;
    }

    public String getCaption() {
        return caption;
    }

    public boolean isPropertyNull() {
        return propertyNull;
    }    
}
