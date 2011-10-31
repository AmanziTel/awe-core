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

package org.amanzi.neo.services.events;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.model.IDataElement;

/**
 * Event for Drill Down (update all views where used concrete dataElements) 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NewUpdateDrillDownEvent extends UpdateViewEvent {
    
    private List<IDataElement> dataElements;
    private String source;
    

    /**
     * Constructor.
     * @param aDataElements
     */
    public NewUpdateDrillDownEvent(List<IDataElement> aDataElements, String aSource) {
        super(UpdateViewEventType.DRILL_DOWN);
        dataElements = aDataElements;
        source = aSource;
    }
    
    /**
     * Constructor.
     * @param aDataElement
     */
    public NewUpdateDrillDownEvent(IDataElement aDataElement, String aSource) {
        super(UpdateViewEventType.DRILL_DOWN);
        dataElements = new ArrayList<IDataElement>(1);
        dataElements.add(aDataElement);
        source = aSource;
    }
    
    /**
     * @return Returns dataElements.
     */
    public List<IDataElement> getDataElements() {
        return dataElements;
    }
    
    /**
     * @return Returns the source.
     */
    public String getSource() {
        return source;
    }

}
