package org.amanzi.neo.core.database.listener;

import java.util.Collection;

import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;

/**
 * Listener - updating cells node from script
 * 
 * @author Cinkel_A
 * 
 */
public interface IUpdateBDListener {
	/**
	 * Handles coming event.
	 * 
	 * @param event
	 *            UpdateBdEvent
	 */
	void databaseUpdated(UpdateDatabaseEvent event);

    /**
     * Gets all event type, that handles current listener
     * 
     * @return
     */
    Collection<UpdateDatabaseEventType> getType();
}
