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

package org.amanzi.awe.ui.events.impl;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.impl.internal.AbstractEvent;
import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ShowInViewEvent extends AbstractEvent {

    private String viewId;
    private IDataElement element;

    /**
     * @param status
     * @param isAsync
     */
    public ShowInViewEvent(String viewId, IDataElement elementName) {
        super(EventStatus.SHOW_IN_VIEW, true);
        this.viewId = viewId;
        this.element = elementName;
    }

    /**
     * @return Returns the viewId.
     */
    public String getViewId() {
        return viewId;
    }

    /**
     * @return Returns the element.
     */
    public IDataElement getElement() {
        return element;
    }

}
