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

package org.amanzi.awe.neostyle;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.enums.INodeType;
import org.apache.commons.lang.ObjectUtils;


/**
 * <p>
 *Contain base information about NeoGeoResource styles
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class BaseNeoStyle implements Serializable {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 2850532621545533216L;
    private Map<String,IFilterWrapper> filters=new HashMap<String,IFilterWrapper>();
    
    //TODO check if this values is necessary
//    private GroupFilter filter;
//
//    public void addFilter(GroupFilter filter) {
//        this.filter = filter;
//    }
//
//    public GroupFilter getFilter() {
//        return filter;
//    }
    public Set<String> getFilterNames(){
        return Collections.unmodifiableSet(filters.keySet());
    }
    public void setFilterMap( Collection<String> filterNames){
        filters.clear();
        if (filterNames!=null){
            filters.putAll(NeoStylePlugin.getDefault().getFilterModel().formsMapByName(filterNames));
        }
    }

   public void addFilter(String filterName,IFilterWrapper filter) {
       filters.put(filterName, filter);
   }

  /**
   * Removes the filter.
   *
   * @param filterName the filter name
   */
  public void removeFilter(String filterName) {
      filters.remove(filterName);
  }
  public Collection<IFilterWrapper> getFilterWrByType(INodeType type){
      Collection<IFilterWrapper> result=new HashSet<IFilterWrapper>();
      for (IFilterWrapper wr:filters.values()){
          if (ObjectUtils.equals(type, wr.getFilter().getNodeType())){
              result.add(wr);
          }
      }
    return result;
  }
}
