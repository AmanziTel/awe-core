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

import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * Data for one call in XML file.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallXmlData extends CallData{
    
    private Probe source;
    private List<Probe> receivers;
    private SavedTag root;

    /**
     * Constructor.
     * @param aKey
     * @param sourceProbe
     * @param receiverProbes
     */
    public CallXmlData(Long aKey,Probe sourceProbe,Probe... receiverProbes) {
        super(aKey);
        source = sourceProbe;
        receivers = Arrays.asList(receiverProbes); 
    }
    
    /**
     * Getter for source data.
     *
     * @return Probe
     */
    public Probe getSource(){
        return source;
    }
    
    /**
     * Getter for receiver data.
     *
     * @return List of Probes
     */
    public List<Probe> getReceivers(){
        return receivers;
    }
    
    /**
     * @return Returns the root.
     */
    public SavedTag getRoot() {
        return root;
    }
    
    /**
     * @param root The root to set.
     */
    public void setRoot(SavedTag root) {
        this.root = root;
    }

}
