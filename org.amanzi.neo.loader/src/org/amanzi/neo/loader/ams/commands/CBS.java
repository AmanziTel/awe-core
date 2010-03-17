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

import org.amanzi.neo.loader.ams.commands.AMSCommandParameter.ParamterType;

/**
 * AT+CBS command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CBS extends AbstractAMSCommand {
	
	/*
	 * Name of command
	 */
	private static final String COMMAND_NAME = "CBS";
	
	@Override
	public String getName() {
		return COMMAND_PREFIX + COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
		parameters.add(new AMSCommandParameter("BCS", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("BCL", ParamterType.INTEGER));
	}

	@Override
	public boolean isCallCommand() {
		return false;
	}

}
