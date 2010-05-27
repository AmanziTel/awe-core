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

package org.amanzi.neo.loader.ams.commands;

import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CTSDS extends AbstractAMSCommand {
    
    /*
     * Command name
     */
    public static final String COMMAND_NAME = "CTSDS";

    @Override
    public String getName() {
        return COMMAND_PREFIX + COMMAND_NAME;
    }

    @Override
    protected void initializeParameters() {
        parameters.add(AMSCommandParameters.AI_SERVICE);
        parameters.add(AMSCommandParameters.CALLING_PARTY_IDENT_TYPE);
        parameters.add(AMSCommandParameters.AREA);
        parameters.add(AMSCommandParameters.ACCESS_PRIORITY);
        parameters.add(AMSCommandParameters.END_TO_END_ENCRYPTION);
    }

    @Override
    public boolean isCallCommand() {
        return true;
    }
    
    @Override
    public String getMMName() {
        return "";
    }

}
