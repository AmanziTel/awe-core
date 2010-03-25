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
package org.amanzi.neo.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.amanzi.neo.core.database.listener.IUpdateViewListener;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.database.services.NeoDataService;
import org.amanzi.neo.core.database.services.UpdateViewManager;
import org.amanzi.neo.core.database.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.preferences.NeoPreferencesInitializer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.neoclipse.view.NeoGraphViewPart;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for org.amanzi.neo.core
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NeoCorePlugin extends Plugin implements IUpdateViewListener {

	/*
	 * Plugin's ID
	 */

	private static final String ID = "org.amanzi.neo.core";

	/*
	 * Plugin variable
	 */

	static private NeoCorePlugin plugin;

	/*
	 * Initializer for AWE-specific Neo Preferences
	 */

	private NeoPreferencesInitializer initializer = new NeoPreferencesInitializer();

	private AweProjectService aweProjectService;
	private UpdateViewManager updateBDManager;
	private NeoDataService neoDataService;
	private final Object neoDataMonitor=new Object();
    final List<UpdateViewEventType> eventList = Arrays.asList(UpdateViewEventType.values());


	/**
	 * Constructor for SplashPlugin.
	 */
	public NeoCorePlugin() {
		super();
		plugin = this;
	}

	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		updateBDManager = new UpdateViewManager();
        updateBDManager.addListener(this);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static NeoCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns initializer of NeoPreferences
	 * 
	 * @return initializer of Neo Preferences
	 */

	public NeoPreferencesInitializer getInitializer() {
		return initializer;
	}

	/**
	 * 
	 * @return awe project service
	 */
	public AweProjectService getProjectService() {
	    if (aweProjectService == null) {
	        aweProjectService = new AweProjectService();
	    }
	    return aweProjectService;
	}
	/**
	 * get service
	 * @return awe project service
	 */
	public NeoDataService getNeoDataService() {
		if (neoDataService == null) {
		    synchronized (neoDataMonitor) {
		        if (neoDataService == null) {
		            neoDataService = new NeoDataService();
		        }
            }
		}
		return neoDataService;
	}
	
	/**
	 * Initialize project service for tests.
	 * @param aNeo NeoService
	 * @author Shcharbatsevich_A
	 */
	public void initProjectService(GraphDatabaseService aNeo){
		aweProjectService = new AweProjectService(aNeo);
	}

	/**
	 * 
	 * @return UpdateBDManager
	 */
	public UpdateViewManager getUpdateViewManager() {
		return updateBDManager;
	}
	
	/**
	 * Sets initializer of NeoPreferences
	 * 
	 * @param initializer
	 *            new initializer for NeoPreferences
	 */

	public void setInitializer(NeoPreferencesInitializer initializer) {
		this.initializer = initializer;
	}

	/**
	 * Print a message and information about exception to Log
	 * 
	 * @param message
	 *            message
	 * @param e
	 *            exception
	 */

	public static void error(String message, Throwable e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, ID, 0,
						message == null ? "" : message, e)); //$NON-NLS-1$
	}
	
	public String getNeoPluginLocation() {
	    try {
	        return FileLocator.resolve(Platform.getBundle("org.neo4j").getEntry(".")).getFile();
	    }
	    catch (IOException e) {
	        error(null, e);
	        return null;
	    }
	}

    @Override
    public void updateView(UpdateViewEvent event) {
        UpdateViewEventType type = event.getType();
        // update NeoGraphViewPart
        if(type.equals(UpdateViewEventType.DRILL_DOWN)){
            UpdateDrillDownEvent ddEvent = (UpdateDrillDownEvent)event;
            if (!ddEvent.getSource().equals(NeoGraphViewPart.ID)) {
                Node node = ddEvent.getNodes().get(0);
                org.neo4j.neoclipse.Activator.getDefault().updateNeoGraphView(node);
            }
            
        }else if(type.equals(UpdateViewEventType.SHOW_PREPARED_VIEW)){
            ShowPreparedViewEvent spvEvent = (ShowPreparedViewEvent)event;
            if (spvEvent.isViewNeedUpdate(NeoGraphViewPart.ID)) {
                Node node = spvEvent.getNodes().get(0);
                org.neo4j.neoclipse.Activator.getDefault().updateNeoGraphView(node);
            }
        }else{            
            org.neo4j.neoclipse.Activator.getDefault().updateNeoGraphView();
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return eventList;
    }

}
