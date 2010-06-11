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

package org.amanzi.neo.core.amscall;

/**
 * <p>
 * Message call interface
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public interface IMessageCall extends IAmsCall {

    /**
     * Gets the message receive time.
     * 
     * @return the message reseive time
     */
    Long getMessageReceiveTime();

    /**
     * Gets the message acknowledge time.
     * 
     * @return the message acknowledge time
     */
    Long getMessageAcknowledgeTime();
}
