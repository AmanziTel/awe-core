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

package org.amanzi.neo.nodetypes;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class NodeTypeUtils {

    private NodeTypeUtils() {
        // hide consturctor
    }

    public static String getTypeId(final Enum< ? extends INodeType> enumItem) {
        assert enumItem != null;

        return enumItem.name().toLowerCase(Locale.getDefault());
    }

    public static String getTypeId(final String enumName) {
        assert enumName != null;

        return enumName.toLowerCase(Locale.getDefault());
    }

    public static String getTypeName(final String id) {
        assert !StringUtils.isEmpty(id);

        return id.trim().toUpperCase(Locale.getDefault());
    }

}
