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

package org.amanzi.neo.data_generator.data;

/**
 * Data saver for one call.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallData {
    
    private Long key;
    private ProbeData firstProbe;
    private ProbeData secondProbe;
    
    /**
     * Constructor.
     * @param aKey Long
     * @param first ProbeData source data
     * @param second ProbeData receiver data
     */
    public CallData(Long aKey,ProbeData first,ProbeData second) {
        key = aKey;
        firstProbe = first;
        secondProbe = second;
    }
    
    /**
     * Getter for call key.
     *
     * @return Long
     */
    public Long getKey(){
        return key;
    }
    
    /**
     * Getter for source data.
     *
     * @return ProbeData
     */
    public ProbeData getFirstProbe(){
        return firstProbe;
    }
    
    /**
     * Getter for receiver data.
     *
     * @return ProbeData
     */
    public ProbeData getSecondProbe(){
        return secondProbe;
    }
    
}
