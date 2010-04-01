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

package org.amanzi.neo.data_generator.data.calls;

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
public class CallGroup {
    
    private Integer sourceProbe;
    private String sourceProbeName;
    private List<Integer> receiverProbes;
    private List<String> receiverProbeNames;
    
    private List<CallData> data;
    
    /**
     * Constructor.
     * @param probeData HashMap<Integer, String> (probe numbers and names)
     */
    public CallGroup(Integer source, String sourceName, List<Integer> receivers, List<String> receiverNames){
        sourceProbe = source;
        sourceProbeName = sourceName;
        receiverProbes = receivers;
        receiverProbeNames = receiverNames;
    }
    
    /**
     * Getter for source number
     *
     * @return Integer.
     */
    public Integer getSourceProbe() {
        return sourceProbe;
    }
    
    /**
     * Getter for receiver number
     *
     * @return Integer.
     */
    public List<Integer> getReceiverProbes() {
        return receiverProbes;
    }
    
    /**
     * Getter for source name
     *
     * @return String.
     */
    public String getFirstName() {
        return sourceProbeName;
    }
    
    /**
     * Getter for receiver name
     *
     * @return String.
     */
    public List<String> getReceiverNames() {
        return receiverProbeNames;
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
