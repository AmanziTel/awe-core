package org.amanzi.neo.core.service.listener;

/**
 * Interface that listens for events of NeoServiceProvider
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public interface INeoServiceProviderListener {
    
    /**
     * NeoService was stopped
     *
     * @param source
     */
    
    public void onNeoStop(Object source);
    
    /**
     * NeoService was started
     *
     * @param source
     */
    
    public void onNeoStart(Object source);
    
    /**
     * Data was commited to Neo-database
     *
     * @param source
     */
    
    public void onNeoCommit(Object source);
    
    /**
     * Changes was rolled back from Neo-database
     *
     * @param source
     */
    
    public void onNeoRollback(Object source);

}
