/*
 * Licensed to "Neo Technology," Network Engine for Objects in Lund AB
 * (http://neotechnology.com) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at (http://www.apache.org/licenses/LICENSE-2.0). Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.neo4j.neoclipse.graphdb;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.preference.Preferences;
import org.neo4j.remote.RemoteGraphDatabase;

// TODO: Auto-generated Javadoc
/**
 * This manager controls the neo4j service.
 * 
 * @author Peter H&auml;nsgen
 * @author Anders Nawroth
 */
public class GraphDbServiceManager {
    /**
     * The service instance.
     */
    protected GraphDatabaseService graphDb;
    
    /** The service mode. */
    protected GraphDbServiceMode serviceMode;
    // protected GraphDatabaseLifecycle lifecycle; // TODO

    /** Current thread for shutdown. */
    protected Thread shutdownHook;

    /**
     * The registered service change listeners.
     */
    protected ListenerList listeners;
    
    /** The tx. */
    private Transaction tx;
    
    /** The preference store. */
    private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
    
    /** The user performance config. */
    private Map<String, String> userPerformanceConfig = new HashMap<String, String>();

    /**
     * The constructor.
     */
    public GraphDbServiceManager() {
        listeners = new ListenerList();
        serviceMode = GraphDbServiceMode.valueOf(preferenceStore.getString(Preferences.CONNECTION_MODE));
    }

    /**
     * Checks if is read only mode.
     *
     * @return true, if is read only mode
     */
    public boolean isReadOnlyMode() {
        return serviceMode == GraphDbServiceMode.READ_ONLY_EMBEDDED;
    }

    /**
     * Sets the graph service mode.
     *
     * @param gdbServiceMode the new graph service mode
     */
    public void setGraphServiceMode(final GraphDbServiceMode gdbServiceMode) {
        serviceMode = gdbServiceMode;
    }

    /**
     * Starts the neo4j service.
     *
     * @throws Exception the exception
     */
    public void startGraphDbService() throws Exception {
        if (graphDb == null) {
            System.out.println("trying to start/connect ...");
            String dbLocation;
            Map<String, String> config = new HashMap<String, String>();
            if(!userPerformanceConfig.isEmpty()){
                config.putAll(userPerformanceConfig);
            }
            switch (serviceMode) {
            case READ_WRITE_EMBEDDED:
                dbLocation = getDbLocation();
                config.put("string_block_size", "60");
                graphDb = new EmbeddedGraphDatabase(dbLocation, config);
                System.out.println("connected to embedded neo4j");
                break;
            case READ_ONLY_EMBEDDED:
                dbLocation = getDbLocation();
                graphDb = new EmbeddedReadOnlyGraphDatabase(dbLocation);
                System.out.println("connected to embedded read-only neo4j");
                break;
            case REMOTE:
                graphDb = new RemoteGraphDatabase(getResourceUri());
                System.out.println("connected to remote neo4j");
                break;
            }
            // :TODO: save thread and remove shutdown on shutdown ...
            registerShutdownHook(graphDb);
            tx = graphDb.beginTx();
            fireServiceChangedEvent(GraphDbServiceStatus.STARTED);
        }
    }

    /**
     * Register shutdown hook.
     *
     * @param graphDbInstance the graph db instance
     */
    private void registerShutdownHook(final GraphDatabaseService graphDbInstance) {
        shutdownHook = new Thread() {
            @Override
            public void run() {
                if (graphDbInstance == null) {
                    return;
                }
                graphDbInstance.shutdown();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    /**
     * Removes the shutdownhook.
     */
    private void removeShutdownhook() {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
    }

    // determine the neo4j directory from the preferences
    /**
     * Gets the db location.
     *
     * @return the db location
     */
    private String getDbLocation() {
        String location = preferenceStore.getString(Preferences.DATABASE_LOCATION);
        if ((location == null) || (location.trim().length() == 0)) {
            // if there's really no db dir, create one in the node space
            Location workspace = Platform.getInstanceLocation();
            if (workspace == null) {
                throw new IllegalArgumentException("The database location is not correctly set.");
            }
            try {
                File dbDir = new File(workspace.getURL().toURI().getPath() + "/neo4j-db");
                if (!dbDir.exists()) {
                    if (!dbDir.mkdir()) {
                        throw new IllegalArgumentException("Could not create a database directory.");
                    }
                    System.out.println("created: " + dbDir.getAbsolutePath());
                }
                location = dbDir.getAbsolutePath();
                preferenceStore.setValue(Preferences.DATABASE_LOCATION, location);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("The database location is not correctly set.");
            }
        }
        File dir = new File(location);
        if (!dir.exists()) {
            throw new IllegalArgumentException("The database location does not exist.");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("The database location is not a directory.");
        }
        if (!dir.canWrite()) {
            throw new IllegalAccessError("Writes are not allowed to the database location.");
        }
        System.out.println("using location: " + location);
        return location;
    }

    /**
     * Gets the resource uri.
     *
     * @return the resource uri
     */
    private String getResourceUri() {
        String resourceUri = preferenceStore.getString(Preferences.DATABASE_RESOURCE_URI);
        if (resourceUri == null || resourceUri.trim().length() == 0) {
            throw new IllegalArgumentException("There is no resource URI defined.");
        }
        return resourceUri;
    }

    /**
     * Returns the graphdb service or null, if it isn't started.
     *
     * @return the graph db service
     * @throws Exception the exception
     */
    public GraphDatabaseService getGraphDbService() throws Exception {
        if (graphDb == null) {
            startGraphDbService();
        }
        return graphDb;
    }

    /**
     * Stops the neo service.
     */
    public void stopGraphDbService() {
        if (graphDb != null) {
            System.out.println("trying to stop/disconnect ...");
            try {
                tx.failure();
                tx.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                graphDb.shutdown();
                // notify listeners
                fireServiceChangedEvent(GraphDbServiceStatus.STOPPED);
            } finally {
                graphDb = null;
            }
        }
        removeShutdownhook();
        System.out.println("stopped/disconnected");
    }

    /**
     * Commit transaction.
     */
    public void commit() {
        if (tx == null) {
            return;
        }
        if (serviceMode == GraphDbServiceMode.READ_WRITE_EMBEDDED) {
            tx.success();
        } else {
            System.out.println("Committing while not in write mode");
        }
        tx.finish();
        tx = graphDb.beginTx();
        fireServiceChangedEvent(GraphDbServiceStatus.COMMIT);
    }

    /**
     * Rollback transaction.
     */
    public void rollback() {
        tx.failure();
        tx.finish();
        tx = graphDb.beginTx();
        fireServiceChangedEvent(GraphDbServiceStatus.ROLLBACK);
    }

    /**
     * Registers a service listener.
     *
     * @param listener the listener
     */
    public void addServiceEventListener(final GraphDbServiceEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters a service listener.
     *
     * @param listener the listener
     */
    public void removeServiceEventListener(final GraphDbServiceEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners about the new service status.
     *
     * @param status the status
     */
    protected void fireServiceChangedEvent(final GraphDbServiceStatus status) {
        Object[] changeListeners = listeners.getListeners();
        if (changeListeners.length > 0) {
            final GraphDbServiceEvent e = new GraphDbServiceEvent(this, status);
            for (Object changeListener : changeListeners) {
                final GraphDbServiceEventListener l = (GraphDbServiceEventListener)changeListener;
                ISafeRunnable job = new ISafeRunnable() {
                    public void handleException(final Throwable exception) {
                        // do nothing
                    }

                    public void run() throws RuntimeException {
                        l.serviceChanged(e);
                    }
                };
                SafeRunner.run(job);
            }
        }
    }

    /**
     * Sets the user performance config.
     *
     * @param userPerformanceConfig the user performance config
     */
    public void setUserPerformanceConfig(Map<String, String> userPerformanceConfig) {
        this.userPerformanceConfig = userPerformanceConfig;
    }
}
