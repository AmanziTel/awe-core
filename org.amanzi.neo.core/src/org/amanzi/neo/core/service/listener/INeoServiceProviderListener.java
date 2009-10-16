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
