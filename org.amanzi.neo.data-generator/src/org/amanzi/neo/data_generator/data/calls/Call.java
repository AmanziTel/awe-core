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

import java.util.HashMap;

/**
 * <p>
 * Saving common call information.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class Call {
    
    private Long startTime;
    private HashMap<String, Object> params;
    private Integer priority;
    
    /**
     * Constructor.
     * @param start Long (time of call start)
     * @param priorityValue Integer (call priority)
     */
    public Call(Long start, Integer priorityValue) {
        startTime = start;
        priority = priorityValue;
        params = new HashMap<String, Object>();
    }
    
    /**
     * @return Returns the startTime.
     */
    public Long getStartTime() {
        return startTime;
    }
    
    public Object getParameter(String key){
        return params.get(key);
    }
    
    public void addParameter(String key, Object value){
        params.put(key, value);
    }
    
    /**
     * @return Returns the params.
     */
    public HashMap<String, Object> getParameters() {
        return params;
    }
    
    /**
     * @return Returns the priority.
     */
    public Integer getPriority() {
        return priority;
    }

}
