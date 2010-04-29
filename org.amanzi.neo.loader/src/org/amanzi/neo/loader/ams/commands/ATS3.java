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

import java.util.HashMap;
import java.util.StringTokenizer;

import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;

/**
 * <p>
 * ARS3 commands.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ATS3 extends AbstractAMSCommand {

    /*
     * Name of command
     */
    private static final String COMMAND_NAME = "ATS3";
    
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    protected void initializeParameters() {
        //parameters.add(AMSCommandParameters.COMMAND_TERMINATION);
    }
    
    @Override
    protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
        return null;
    }

    @Override
    public boolean isCallCommand() {
        return false;
    }

}
