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
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class Calculator3GPPdBm {

    private static final int minRSCP = -115;
    private static final int maxRSCP = 25;
    private static final int stepRSCP = 1;

    public String getRSCP(int val3GPP) {
        int curVal = val3GPP * stepRSCP + minRSCP;
        return null;
    }

    // public Integer getLeftRSCP(){
    //        
    // }
}
