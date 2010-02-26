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

package org.amanzi.awe.tool.star;

import org.amanzi.neo.core.enums.NetworkSiteType;

/**
 * <p>
 * sets 3g filter
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class FilterToolOnly3G extends FilterToolRemove {
   @Override
protected String getKey() {
    return NetworkSiteType.SITE_3G.getId();
}

}
