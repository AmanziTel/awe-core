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

package org.amanzi.neo.core.enums;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.amanzi.neo.core.NeoCorePlugin;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NetworkFileType {
    SITE, SECTOR, PROBE, NEIGHBOUR, TRANSMISSION;

    public static NetworkFileType getType(File networkFile) {
        // TODO implement!
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(networkFile)));
            String line = reader.readLine();
            reader.close();
            if (line.toLowerCase().contains("probe")) {
                return PROBE;
            } else {
                return SECTOR;
            }
        } catch (Exception e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            return null;
        }

    }

}
