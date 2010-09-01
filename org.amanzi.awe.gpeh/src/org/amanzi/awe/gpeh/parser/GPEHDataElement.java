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

package org.amanzi.awe.gpeh.parser;

import java.util.HashMap;

import org.amanzi.awe.parser.core.IDataElementOldVersion;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class GPEHDataElement extends HashMap<String, Object>implements IDataElementOldVersion {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -6160645933742511478L;

    private Long timestamp;
    
    private String name;
    
    private Events type;
    
    private boolean isNewFile = false;
    
    private long size;
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(Events type) {
        this.type = type;
    }
    
    public Events getType() {
        return type;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getLatitude() {
        return 0l;
    }

    @Override
    public Long getLongitude() {
        return 0l;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setNewFile(boolean isNewFile) {
        this.isNewFile = isNewFile;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public long getSize() {
        return size;
    }
    
}
