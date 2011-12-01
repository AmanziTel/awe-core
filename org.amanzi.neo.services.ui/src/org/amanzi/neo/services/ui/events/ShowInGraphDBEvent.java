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

package org.amanzi.neo.services.ui.events;

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * SHOW_IND_GRAPH_DB event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowInGraphDBEvent extends AbstractEvent {

    /**
     * element to show
     */
    private IDataElement dataElement;

    /**
     * create instance
     */
    public ShowInGraphDBEvent() {
        type = EventsType.SHOW_IND_GRAPH_DB;
    }

    /**
     * Constructor with initialization required fields
     * 
     * @param dataElement
     */
    public ShowInGraphDBEvent(IDataElement dataElement) {
        this.dataElement = dataElement;
        type = EventsType.SHOW_IND_GRAPH_DB;
    }

    public IDataElement getDataElement() {
        return dataElement;
    }
}
