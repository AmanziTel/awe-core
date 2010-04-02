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

package org.amanzi.neo.data_generator.utils.nokia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Tag for save in file.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class SavedTag {

    private boolean empty;
    private String name;
    private String data = "";
    private HashMap<String, String> attributes;
    private List<SavedTag> innerTags;
    
    public SavedTag(String aName, boolean isEmpty) {
        name = aName;
        empty = isEmpty;
        if (!empty) {
            innerTags = new ArrayList<SavedTag>();
        }
        attributes = new HashMap<String, String>();
    }
    
    /**
     * @return Returns the innerTags.
     */
    public List<SavedTag> getInnerTags() {
        return innerTags;
    }
    
    /**
     * @return Returns the empty.
     */
    public boolean isEmpty() {
        return empty;
    }
    
    /**
     * @return Returns the data.
     */
    public String getData() {
        return data;
    }
    
    /**
     * @param data The data to set.
     */
    public void setData(String data) {
        this.data = data;
    }
    
    /**
     * Add tag to inner tags.
     *
     * @param inner SavedTag (inner tag)
     */
    public void addInnerTag(SavedTag inner){
        innerTags.add(inner);
    }
    
    /**
     * Add tag attribute.
     *
     * @param key String
     * @param value String
     */
    public void addAttribute(String key,String value) {
        attributes.put(key, value);
    }
    
    /**
     * Returns tag attribute.
     *
     * @param key String 
     * @return String
     */
    public String getAttribute(String key){
        return attributes.get(key);
    }
    
    /**
     * Returns string for open tag.
     *
     * @return String
     */
    public String getTagOpenString(){
        StringBuffer result = new StringBuffer("<").append(name);
        for(String key : attributes.keySet()){
            result.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
        }
        if(empty){
            result.append("/");
        }
        result.append(">");
        return result.toString();
    }
    
    /**
     * Returns string for close tag.
     *
     * @return String
     */
    public String getTagCloseString(){
        return data+"</"+name+">";
    }
}
