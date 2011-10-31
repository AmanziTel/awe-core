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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.amanzi.neo.services.model.IDataElement;

/**
 * Event for show view with prepare information.
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class NewShowPreparedViewEvent extends ShowViewEvent {
    
    private List<IDataElement> dataElements;
    
    /**
     * Constructor.
     * @param aView String
     * @param aNodes Collection of Nodes (information for prepare)
     */
    public NewShowPreparedViewEvent(String aView, Collection<IDataElement> aDataElements) {
        super(aView, UpdateViewEventType.SHOW_PREPARED_VIEW);
        dataElements = new ArrayList<IDataElement>(aDataElements);
    }
    
    /**
     * Constructor.
     * @param aView String
     * @param aNode Node (information for prepare)
     */
    public NewShowPreparedViewEvent(String aView, IDataElement aDataElement) {
        super(aView, UpdateViewEventType.SHOW_PREPARED_VIEW);
        dataElements = Collections.singletonList(aDataElement);
    }
    
    /**
     * @return Returns the dataElements.
     */
    public List<IDataElement> getDataElements() {
        return dataElements;
    }

}
