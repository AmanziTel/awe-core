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

package org.amanzi.neo.data_generator.data.nokia;

import java.util.HashMap;

/**
 * <p>
 * Abstract generated data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class AbstractTagData {

    private String id;
    private String className;
    private String distName;
    private HashMap<String, String> properties;
    
    /**
     * Constructor.
     * @param aClass String (value for tag attribute 'class') 
     * @param aDistName String (value for tag attribute 'distName')
     * @param anId String (value for tag attribute 'id')
     */
    public AbstractTagData(String aClass, String aDistName, String anId) {
        className = aClass;
        distName = aDistName;
        id = anId;
    }
    
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * @return Returns the distName.
     */
    public String getDistName() {
        return distName;
    }
    
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @return Returns the properties.
     */
    public HashMap<String, String> getProperties() {
        if(properties==null){
            properties = new HashMap<String, String>();
        }
        return properties;
    }
    
    /**
     * Add property to tag.
     *
     * @param key
     * @param value
     */
    public void addProperty(String key, String value){
        getProperties().put(key, value);
    }
    
}
