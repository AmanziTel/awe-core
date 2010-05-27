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
 * CTCR command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CTCR extends AbstractAMSCommand {
	
	/*
	 * Name of command
	 */
	private static final String COMMAND_NAME = "CTCR";
	
	@Override
	public String getName() {
		return COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
	    parameters.add(AMSCommandParameters.CC_INSTANCE);
	    parameters.add(AMSCommandParameters.DISCONNECT_CAUSE);
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
