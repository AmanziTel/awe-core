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
package org.amanzi.neo.core.service;

import java.util.ArrayList;

import org.amanzi.neo.core.service.listener.INeoServiceProviderListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.graphdb.GraphDbServiceEvent;
import org.neo4j.neoclipse.graphdb.GraphDbServiceEventListener;
import org.neo4j.neoclipse.graphdb.GraphDbServiceManager;
import org.neo4j.neoclipse.preference.DecoratorPreferences;
import org.neo4j.neoclipse.preference.Preferences;

/**
 * Provider that give access to NeoService
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NeoServiceProvider implements IPropertyChangeListener{
    
    /*
     * Instance of NeoServiceProvider
     */
    private static NeoServiceProvider provider;
    
    /*
     * NeoService
     */
    private GraphDatabaseService neoService;
    
    /*
     * NeoServiceManager
     */
    private GraphDbServiceManager neoManager;
    
    /*
     * Listener for NeoServiceManager events
     */
    private GraphDbServiceEventListener defaultListener = new DefaultServiceListener();
    
    /*
     * Listeners of NeoServiceProvider
     */
    private ArrayList<INeoServiceProviderListener> listeners = new ArrayList<INeoServiceProviderListener>(0);
    
    /*
     * Location of Neo-database
     */
    private String databaseLocation;

    /*
     * Current Display 
     */
    private Display display;
    
    private LuceneIndexService indexService;
    
    /**
     * Creates an instance of NeoServiceProvider
     *
     * @return instance of NeoServiceProvider
     */
    
    public static NeoServiceProvider getProvider() {
        if (provider == null) {
            provider = new NeoServiceProvider();
        }
        
        return provider;
    }
    
    /**
     * Creates an instance of NeoServiceProvider
     *
     * @return instance of NeoServiceProvider
     */
    
    public static void initProvider(GraphDatabaseService service) {
        provider = new NeoServiceProvider();
        provider.init(service);
        
    }
    
    /**
     * Protected constructor
     */
    
    protected NeoServiceProvider() {
        try {
            this.display = PlatformUI.getWorkbench().getDisplay();
        } catch (RuntimeException e) {
            //We are probably running unit tests, log and error and continue
            System.err.println("Failed to get display: "+e);
        }
    }
    
    /**
     * Returns NeoService
     *
     * @return
     */
    
    public GraphDatabaseService getService() {
        init();
        
        return neoService;
    }

    /**
     * Returns NeoService
     * 
     * @return
     */

    // public NeoService getReadOnlyService() {
    // if (neoServiceReadOnly == null) {
    // neoServiceReadOnly = new EmbeddedReadOnlyNeo(getDefaultDatabaseLocation());
    // }
    //
    // return neoServiceReadOnly;
    // }
    
    /**
     * Initializes NeoService and NeoServiceManager
     */
    
    private void init() {
        if (neoService == null) {
            neoService = Activator.getDefault().getGraphDbServiceSafely();
        }

        if (neoManager == null) {
            neoManager = Activator.getDefault().getGraphDbServiceManager();
            neoManager.addServiceEventListener(defaultListener);
        }
        if (indexService == null) {
        	indexService = new LuceneIndexService(neoService);
        }

    }
    
    /**
     * Initializes NeoService and NeoServiceManager
     */
    
    private void init(GraphDatabaseService service) {
        neoService = service;

        if (neoManager == null) {
            neoManager = Activator.getDefault().getGraphDbServiceManager();
            neoManager.addServiceEventListener(defaultListener);
        }
        if (indexService == null) {
            indexService = new LuceneIndexService(neoService);
        }

    }

    /**
     * Returns current location of Neo-database
     * 
     * @return
     */
    
    public String getDefaultDatabaseLocation() {
        if (databaseLocation == null) {
            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            databaseLocation = store.getString(Preferences.DATABASE_RESOURCE_URI);
            if ((databaseLocation == null) || (databaseLocation.trim().isEmpty())) {
                databaseLocation = store.getString(Preferences.DATABASE_LOCATION);
            }
        }
        
        return databaseLocation;
    }
    
    /**
     * Add listener to Provider 
     *
     * @param listener listener for NeoServiceProvider
     */
    
    public void addServiceProviderListener(INeoServiceProviderListener listener) {
        listeners.add(listener);       
        
    }
    
    /**
     * Removes listener from Provider 
     *
     * @param listener listener of NeoServiceProvider
     */
    
    public void removeServiceProviderListener(INeoServiceProviderListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Shutdown NeoServiceManager
     */
    
    public void shutdown() {        
        if (neoManager != null) {
            neoManager.commit();
            neoManager.removeServiceEventListener(defaultListener);            
            neoManager = null;            
        }
    }
    
    /**
     * Commits changes
     */
    public void commit() {
        //Lagutko, 12.10.2009, also check is Workbech closing?
        boolean currentThread = (display == null) ||
        						PlatformUI.getWorkbench().isClosing() ||                                 
                                Thread.currentThread().equals(display.getThread());
        if (currentThread) {
            if (neoManager!=null) {
                neoManager.commit();
            }
        } else {
            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    neoManager.commit();
                }
            });
        }
    }
    
    /**
     * Rollback changes
     */
    public void rollback() {
        //Lagutko, 12.10.2009, also check is Workbech closing?
        boolean currentThread = PlatformUI.getWorkbench().isClosing() || 
                                (display == null) || 
                                Thread.currentThread().equals(display.getThread());
        if (currentThread) {
            neoManager.rollback();
        } else {
            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    neoManager.rollback();
                }
            });
        }
    }
    
    /**
     * Listener that listens for event of NeoServiceManager and provides them
     * to listeners of NeoServiceProvides
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */
    
    private class DefaultServiceListener implements GraphDbServiceEventListener {

    	@SuppressWarnings("unchecked")
        public void serviceChanged(GraphDbServiceEvent event) {
            //Lagutko, 12.10.2009, copy list of listeners to local variable
            ArrayList<INeoServiceProviderListener> copiedListener = (ArrayList<INeoServiceProviderListener>)listeners.clone();
            for (INeoServiceProviderListener listener : copiedListener) {
                Object source = event.getSource();
                switch (event.getStatus()) {
                case STOPPED:                    
                    listener.onNeoStop(source);
                    shutdown();
                    break;
                case STARTED:
                    listener.onNeoStart(source);
                    break;
                case COMMIT:
                    listener.onNeoCommit(source);
                    break;
                case ROLLBACK:
                    listener.onNeoRollback(source);
                    break;
                }                    
            }
        }
        
    }

    /**
     * Listens for changing properties
     */

    public void propertyChange(PropertyChangeEvent event) {
        //if location of icons was changes than we must re-initialize UserIcons
        if (event.getProperty().equals(DecoratorPreferences.DATABASE_LOCATION) ||
            event.getProperty().equals(Preferences.DATABASE_RESOURCE_URI) ||
            event.getProperty().equals(Preferences.DATABASE_LOCATION)) {
            //if location of Database was changed than restart Neo
            Activator.getDefault().restartNeo();
            databaseLocation = null;
        }        
    }
    
    /**
     * Stops Neo Service 
     * 
     */
    
    public void stopNeo() {
    	if (neoManager != null) {
    	    neoManager.commit();
    		neoManager.stopGraphDbService();
    	}
    	if (indexService != null) {
    		indexService.shutdown();
    	}
    	if (neoService != null) {
    		neoService.shutdown();
    	}
    	indexService = null;
    	neoManager = null;
    	neoService = null;
    }
    
    public LuceneIndexService getIndexService() {
    	return indexService;
    }

}
