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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Saving common probe information.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Probe {
    private List<String> sourceGroups = new ArrayList<String>();
    private List<String> resGroups = new ArrayList<String>();
    
    private String name;
    private String phoneNumber;
    private Integer localArea;
    private Double frequency;
    
    /**
     * Constructor.
     * @param aName String probe name
     * @param aPhoneNumber String phone number
     * @param aLocalArea Integer local area
     * @param aFrequency Double frequency
     */
    public Probe(String aName, String aPhoneNumber, Integer aLocalArea, Double aFrequency){
        name = aName;
        phoneNumber = aPhoneNumber;
        localArea = aLocalArea;
        frequency = aFrequency;
    }
    
    /**
     * Probe name.
     *
     * @return String
     */
    public String getName(){
        return name;
    }
    
    /**
     * Phone number.
     *
     * @return String
     */
    public String getPhoneNumber(){
        return phoneNumber;
    }
    
    /**
     * Local area.
     *
     * @return Integer.
     */
    public Integer getLocalAria(){
        return localArea;
    }
      
    /**
     * Frequency.
     *
     * @return Double.
     */
    public Double getFrequency(){
        return frequency;
    }
    
    /**
     * Set group to source groups
     *
     * @param number
     */
    public void addSourceGroup(String number){
        sourceGroups.add(number);
    }
    
    /**
     * @return Returns the source Gropes.
     */
    public List<String> getSourceGroups() {
        return sourceGroups;
    }
    
    /**
     * Set group to receiver groups
     *
     * @param number
     */
    public void addResGroup(String number){
        resGroups.add(number);
    }
    
    /**
     * @return Returns the receiver Gropes.
     */
    public List<String> getResGroups() {
        return resGroups;
    }
}
