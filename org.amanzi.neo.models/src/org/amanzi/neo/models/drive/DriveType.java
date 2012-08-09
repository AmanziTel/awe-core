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

package org.amanzi.neo.models.drive;

import org.amanzi.neo.models.drive.IDriveModel.IDriveType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public enum DriveType implements IDriveType {

    GEOPTIMA;

    @Override
    public String getId() {
        return NodeTypeUtils.getTypeId(name());
    }

    public static DriveType findById(final String name) {
        for (DriveType value : values()) {
            if (NodeTypeUtils.getTypeId(value.name()).equals(name)) {
                return value;
            }
        }

        return null;
    }

}
