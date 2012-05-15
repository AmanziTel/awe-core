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

import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.amanzi.neo.services.ui.enums.EventsType;

/**
 * <p>
 * describe UPDATE_DATA event
 * </p>
 * 
 * @author Vladislav Kondratenko
 * @since 1.0.0
 */
public class UpdateDataEvent extends AbstractEvent implements IDatabaseEventListener {	
	
    /**
     * create instants
     */
    public UpdateDataEvent() {
        type = EventsType.UPDATE_DATA;
    }

    @Override
    public void onDatabaseEvent(DatabaseEvent event) {
    }

}
