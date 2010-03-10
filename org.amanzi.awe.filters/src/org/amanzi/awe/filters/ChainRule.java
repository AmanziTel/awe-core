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

package org.amanzi.awe.filters;


/**
 * <p>
 *Chain rule
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum ChainRule {
 AND("and"),OR("or");
 private final String id;

private ChainRule(String id){
    this.id = id;
 }
/**
 * Returns enum by its ID
 *
 * @param enumId id of Node Type
 * @return NodeTypes or null
 */
public static ChainRule getEnumById(String enumId) {
    if (enumId == null) {
        return null;
    }
    for (ChainRule call : ChainRule.values()) {
        if (call.getId().equals(enumId)) {
            return call;
        }
    }
    return null;
}
/**
 * @return Returns the id.
 */
public String getId() {
    return id;
}
}
