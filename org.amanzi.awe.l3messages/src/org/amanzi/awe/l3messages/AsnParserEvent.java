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

package org.amanzi.awe.l3messages;

import org.amanzi.awe.l3.messages.streaming.schema.nodes.NodeType;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AsnParserEvent {
    
    private String className;
    
    private String elementName;
    
    private NodeType classType;
    
    private Object value;
    
    /**
     * @param className
     * @param elementName
     * @param classType
     * @param value
     */
    public AsnParserEvent(String className, String elementName, NodeType classType, Object value) {
        super();
        this.className = className;
        this.elementName = elementName;
        this.classType = classType;
        this.value = value;
    }

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return Returns the elementName.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @return Returns the classType.
     */
    public NodeType getClassType() {
        return classType;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

}
