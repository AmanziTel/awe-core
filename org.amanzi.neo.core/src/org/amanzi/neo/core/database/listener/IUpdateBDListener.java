package org.amanzi.neo.core.database.listener;

import org.amanzi.neo.core.database.services.UpdateBdEvent;

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
	void databaseUpdated(UpdateBdEvent event);
}
