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

package org.amanzi.awe.neighbours.gpeh;

/**
 * <p>
 * GpehReport enum.
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public enum GpehReport {
    RF_PERFORMANCE_ANALYSIS("RF Performance Analysis"), INTERFERENCE_ANALYSIS("Interference Analysis");
    private final String string;

    private GpehReport(String string) {
        this.string = string;
    }

}
