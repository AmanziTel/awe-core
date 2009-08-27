package org.amanzi.neo.core.database.services;

import org.amanzi.neo.core.database.listener.IUpdateBDListener;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;

/**
 * Manager updating cells from bd
 * 
 * @author Cinkel_A
 * 
 */
public class UpdateBDManager {

	private ListenerList listeners = new ListenerList();

	/**
	 * Adds new listener
	 * 
	 * @param listener
	 */
	public void addListener(IUpdateBDListener listener) {
		getListeners().add(listener);
	}

	/**
	 * Remove Listener
	 * 
	 * @param listener
	 */
	public void removeListener(IUpdateBDListener listener) {
		getListeners().remove(listener);
	}

	/**
	 * Create and send UpdateBdEvent
	 * 
	 * @param rubyProjectName
	 *            ruby project name
	 * @param spreadSheetName
	 *            spreadsheet name
	 * @param fullCellID
	 *            full cell id
	 */
	public void updateCell(String rubyProjectName, String spreadSheetName,
			String fullCellID) {
		UpdateDatabaseEvent event = new UpdateDatabaseEvent(rubyProjectName,
				spreadSheetName, fullCellID);
		fireUbdateDatabase(event);

	}

	/**
	 * Gets all listeners
	 * 
	 * @return all listeners
	 */
	protected ListenerList getListeners() {
		return listeners;
	}

	/**
	 * Fires <code>UpdateBdEvent</code> to listeners.
	 * 
	 * @param event
	 *            UpdateBdEvent
	 */
    public void fireUbdateDatabase(final UpdateDatabaseEvent event) {
		Object[] allListeners = getListeners().getListeners();
		for (Object listener : allListeners) {
			final IUpdateBDListener singleListener = (IUpdateBDListener) listener;
			SafeRunner.run(new ISafeRunnable() {
				@Override
                public void run() throws Exception {
                    if (singleListener.getType().contains(event.getType())) {
                        singleListener.databaseUpdated(event);
                    }
				}

				@Override
				public void handleException(Throwable exception) {
				}
			});
		}
	}

}
