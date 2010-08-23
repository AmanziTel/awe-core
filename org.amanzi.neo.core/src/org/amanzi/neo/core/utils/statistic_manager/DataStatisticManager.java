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

package org.amanzi.neo.core.utils.statistic_manager;

import java.util.HashMap;


/**
 * <p>
 * Statistic manager for working with stored statistic information of loading data
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DataStatisticManager {
    public HashMap<String,HeaderMaps> headerMap=new HashMap<String, HeaderMaps>();
    /**
     * add header
     *
     * @param key - key
     * @param header- header
     */
    public void addHeader(String index,String key,Header header){
        
    }
    public void indexValue(String index,String key, Object value){
        
    }
    public void parseAndIndex(String index,String key, String value){
        HeaderMaps map = getHeaderMap(index);
        if (map.headerAllowed(key)){
            map.getHeader(key);
        }
        
    }
    protected HeaderMaps getHeaderMap(String index){
        HeaderMaps result= headerMap.get(index);
        if (result==null){
            result=new HeaderMaps();
            headerMap.put(index, result);
        }
        return result;
    }
    
}
