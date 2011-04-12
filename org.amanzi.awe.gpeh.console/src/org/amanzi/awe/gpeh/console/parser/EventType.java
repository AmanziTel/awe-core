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

package org.amanzi.awe.gpeh.console.parser;

/**
 * <p>
 * GPEH Event type
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum EventType {
    RNC {
        @Override
        public boolean isInternal() {
            return true;
        }
    },
    RRC, NBAP, RANAP, RNSAP,PCAP,SBAP;
    /***
     * is internal event
     * 
     * @return true/false
     */
    public boolean isInternal() {
        return false;
    }
}
