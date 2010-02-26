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

import java.util.List;


/**
 * Data saver for all calls of probes pair.
 * Attention: pairs A-B and B-A is different!
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallPair {
    
    private Integer firstProbe;    
    private Integer secondProbe;
    private String firstName;    
    private String secondName;
    
    private List<CallData> data;
    
    /**
     * Constructor.
     * @param first Integer (source number)
     * @param second Integer (receiver number)
     * @param names String[] (probes names)
     */
    public CallPair(Integer first, Integer second, String[] names){
        firstProbe = first;
        secondProbe = second;
        firstName = names[0];
        secondName = names[1];
    }
    
    /**
     * Getter for source number
     *
     * @return Integer.
     */
    public Integer getFirstProbe() {
        return firstProbe;
    }
    
    /**
     * Getter for receiver number
     *
     * @return Integer.
     */
    public Integer getSecondProbe() {
        return secondProbe;
    }
    
    /**
     * Getter for source name
     *
     * @return String.
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Getter for receiver name
     *
     * @return String.
     */
    public String getSecondName() {
        return secondName;
    }
    
    /**
     * Getter for call data.
     *
     * @return List of CallData.
     */
    public List<CallData> getData() {
        return data;
    }
    
    /**
     * Setter for call data.
     *
     * @param aData list of CallData.
     */
    public void setData(List<CallData> aData) {
        data = aData;
    }

}
