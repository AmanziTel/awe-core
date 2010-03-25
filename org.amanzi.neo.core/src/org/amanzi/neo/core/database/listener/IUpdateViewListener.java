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
package org.amanzi.neo.core.database.listener;

import java.util.Collection;

import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;

/**
 * Listener - updating cells node from script
 * 
 * @author Cinkel_A
 * 
 */
public interface IUpdateViewListener {
	/**
	 * Handles coming event.
	 * 
	 * @param event
	 *            UpdateBdEvent
	 */
	void updateView(UpdateViewEvent event);

    /**
     * Gets all event type, that handles current listener
     * 
     * @return
     */
    Collection<UpdateViewEventType> getType();
}
