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
package org.amanzi.neo.core.database.services;

import org.amanzi.neo.core.database.listener.IUpdateViewListener;
import org.amanzi.neo.core.database.listener.ShowViewListener;
import org.amanzi.neo.core.database.services.events.ShowViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Manager updating cells from bd
 * 
 * @author Cinkel_A
 * 
 */
// TODO create extension point?
public class UpdateViewManager {

	private ListenerList listeners = new ListenerList();
	private IUpdateViewListener showViewListener = new ShowViewListener();
	
	/**
	 * Adds new listener
	 * 
	 * @param listener
	 */
	public void addListener(IUpdateViewListener listener) {
		getListeners().add(listener);
	}

	/**
	 * Remove Listener
	 * 
	 * @param listener
	 */
	public void removeListener(IUpdateViewListener listener) {
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
		fireUpdateView(event);

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
     * Fires <code>UpdateDatabaseEvent</code> to listeners.
     * 
     * @param event UpdateDatabaseEvent
     */
    public void fireUpdateView(final UpdateViewEvent event) {
        Display display = PlatformUI.getWorkbench().getDisplay();
        boolean currentThread = (display == null) ||
                                PlatformUI.getWorkbench().isClosing() ||                                 
                                Thread.currentThread().equals(display.getThread());
        if (currentThread) {
            fireEvent(event);
        }else{
            ActionUtil.getInstance().runTask(new Runnable() {
                @Override
                public void run() {
                    fireEvent(event);
                }
            }, true);
        }
	}

    private void fireEvent(final UpdateViewEvent event) {
        if(event instanceof ShowViewEvent){
            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    showViewListener.updateView(event);
                }

                @Override
                public void handleException(Throwable exception) {
                }
            });
        }else{
    		Object[] allListeners = getListeners().getListeners();
    		for (Object listener : allListeners) {
    			final IUpdateViewListener singleListener = (IUpdateViewListener) listener;
    			SafeRunner.run(new ISafeRunnable() {
    				@Override
                    public void run() throws Exception {
    				    if(event instanceof ShowViewEvent){
    				        showViewListener.updateView(event);
    				    }
    				    else if (singleListener.getType().contains(event.getType())) {
                            singleListener.updateView(event);
                        }
    				}
    
    				@Override
    				public void handleException(Throwable exception) {
    				}
    			});
    		}
        }
    }
    
    /**
     * Fires <code>ShowPreparedViewEvent</code> to listeners.
     * 
     * @param event ShowPreparedViewEvent
     */
    public void fireShowPreparedView(final ShowViewEvent event) {
        Object[] allListeners = getListeners().getListeners();
        for (Object listener : allListeners) {
            final IUpdateViewListener singleListener = (IUpdateViewListener) listener;
            SafeRunner.run(new ISafeRunnable() {
                @Override
                public void run() throws Exception {
                    if (singleListener.getType().contains(event.getType())) {
                        singleListener.updateView(event);
                    }
                }

                @Override
                public void handleException(Throwable exception) {
                }
            });
        }
    }

}
