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

package org.amanzi.neo.services.filters;

import org.apache.commons.lang.StringUtils;

public enum ExpressionType {
    OR, AND;
    
    public static ExpressionType getByName(String name) {
        if (StringUtils.isEmpty(name))
            return null;
        for (ExpressionType expType : ExpressionType.values()) {
            if (expType.name().toLowerCase().equals(name.toLowerCase())) {
                return expType;
            }
        }
        return null;
    }
}