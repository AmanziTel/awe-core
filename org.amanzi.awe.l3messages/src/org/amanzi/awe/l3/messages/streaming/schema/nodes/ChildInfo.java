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

package org.amanzi.awe.l3.messages.streaming.schema.nodes;

public class ChildInfo {
    
    private String childName;
    
    private boolean isOptional;
    
    /**
     * @param childName
     * @param isOptional
     */
    public ChildInfo(String childName, boolean isOptional) {
        super();
        this.childName = childName;
        this.isOptional = isOptional;
    }
    
    /**
     * @param childName
     */
    public ChildInfo(String childName) {
        this(childName, false);
    }

    /**
     * @return Returns the childName.
     */
    public String getChildName() {
        return childName;
    }

    /**
     * @return Returns the isOptional.
     */
    public boolean isOptional() {
        return isOptional;
    }
 
    @Override
    public String toString() {
        return childName;
    }
}