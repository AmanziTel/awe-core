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
 * CTOCP command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CTOCP extends AbstractAMSCommand {
	
	/*
	 * Name of command
	 */
	private static final String COMMAND_NAME = "CTOCP";
	
	@Override
	public String getName() {
		return COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
	    parameters.add(AMSCommandParameters.CC_INSTANCE);
        parameters.add(AMSCommandParameters.CALL_STATUS);
        parameters.add(AMSCommandParameters.AI_SERVICE);
        parameters.add(AMSCommandParameters.HOOK);
        parameters.add(AMSCommandParameters.SIMPLEX);
        parameters.add(AMSCommandParameters.END_TO_END_ENCRYPTION);
        parameters.add(AMSCommandParameters.COMMS_TYPE);
        parameters.add(AMSCommandParameters.SLOTS_CODEC);
	}

	@Override
	public boolean isCallCommand() {
		return false;
	}

}
