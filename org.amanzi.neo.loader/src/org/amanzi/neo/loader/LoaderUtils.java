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
package org.amanzi.neo.loader;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

public class LoaderUtils {
    /**
     * return AWE project name of active map
     * 
     * @return
     */
    public static String getAweProjectName() {
        IMap map = ApplicationGIS.getActiveMap();
        return map == ApplicationGIS.NO_MAP ? ApplicationGIS.getActiveProject().getName() : map.getProject().getName();
    }

    /**
     * Convert dBm values to milliwatts
     *
     * @param dbm
     * @return milliwatts
     */
    public static final double dbm2mw(int dbm){
        return Math.pow(10.0, (((float)dbm)/10.0));
    }

    /**
     * Convert milliwatss values to dBm
     *
     * @param milliwatts
     * @return dBm
     */
    public static final float mw2dbm(double mw){
      return (float)(10.0*Math.log10(mw));
    }

}
