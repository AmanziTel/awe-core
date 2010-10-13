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

package org.amanzi.neo.loader.core;

/**
 * <p>
 *Utilits methods for loaders
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LoaderUtils {
    /**
     * Convert dBm values to milliwatts
     * 
     * @param dbm
     * @return milliwatts
     */
    public static final double dbm2mw(int dbm) {
        return Math.pow(10.0, ((dbm) / 10.0));
    }
    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    public static final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }
}
