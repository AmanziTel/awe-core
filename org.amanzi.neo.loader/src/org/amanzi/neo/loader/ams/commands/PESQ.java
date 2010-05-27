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
 * PESQ command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class PESQ extends AbstractAMSCommand {
	
	/*
	 * Name of command
	 */
	private static final String COMMAND_NAME = "PESQ";
	
	@Override
	public String getName() {
		return COMMAND_NAME;
	}
	
	@Override
	protected String getParamterDelimiter() {
	    return "|";
	}
	
	@Override
	protected String getResultDelimiter() {
	    return "\n";
	}

	@Override
	protected void initializeParameters() {
	    parameters.add(AMSCommandParameters.PESQ_LISTENING_QUALITIY);
	    parameters.add(AMSCommandParameters.PESQ_THRESHOLD);
	    parameters.add(AMSCommandParameters.AVERAGE_SYMMETRICAL_DISTURBANCE);
	    parameters.add(AMSCommandParameters.AVERAGE_ASYMMETRICAL_DISTURBANCE);
	    parameters.add(AMSCommandParameters.ESTIMATED_DELAY);
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
