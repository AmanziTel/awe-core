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

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum NodeType {
    
    SEQUENCE,
    CHOICE,
    ENUMERATED,
    ROOT,
    BIT_STRING,
    INTEGER,
    BOOLEAN,
    SEQUENCE_OF,
    OCTET_STRING,
    NULL;

    public static NodeType getType(String nodeType) {
        try {
            return valueOf(nodeType);
        }
        catch (Exception e){
            for (NodeType value : values()) {
                if (nodeType.startsWith(value.toString())) {
                    return value;
                }
            }
            return null;
        }     
    }
}
