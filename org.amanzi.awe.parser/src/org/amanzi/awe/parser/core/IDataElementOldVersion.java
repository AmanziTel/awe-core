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

package org.amanzi.awe.parser.core;

import java.util.Map;

/**
 * Interface that describes parsed data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public interface IDataElementOldVersion extends Map<String, Object>{
    
    public String getName();
    
    public Long getTimestamp();
    
    public Long getLatitude();
    
    public Long getLongitude();
    
    public long getSize();

}
