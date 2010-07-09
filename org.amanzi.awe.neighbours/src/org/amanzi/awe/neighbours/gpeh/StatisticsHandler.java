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

package org.amanzi.awe.neighbours.gpeh;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *Contains information about statistics
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class StatisticsHandler {
    private Map<Integer,String>keys=new HashMap<Integer,String>();
    private Map<Integer,Integer>counters=new HashMap<Integer,Integer>();
    /**
     * 
     */
    public StatisticsHandler() {
    }
    public void clear(){
        keys.clear();
        counters.clear();
    }
    public void registerKey(int key,String reason){
        if (keys.containsKey(key)){
            throw new IllegalArgumentException(String.format("Key %s already exist",key));
        }
        keys.put(key, reason);
        counters.put(key,0);
    }
    public void increaseConter(int key){
        counters.put(key,counters.get(key)+1);
    }
}
