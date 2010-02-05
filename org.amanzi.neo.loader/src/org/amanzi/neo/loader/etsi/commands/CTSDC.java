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

package org.amanzi.neo.loader.etsi.commands;

import org.amanzi.neo.loader.etsi.commands.ETSICommandParameter.ParamterType;


/**
 * AT+CTSDC command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CTSDC extends AbstractETSICommand {
	
	/*
	 * Command name
	 */
	private static final String COMMAND_NAME = "CTSDC";

	@Override
	public String getName() {
		return COMMAND_PREFIX + COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
		parameters.add(new ETSICommandParameter("AI Service", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("called party ident type", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("area", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("hook", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("simplex", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("end-to-end encryption", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("comms_type", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("slots/codec", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("RqTx", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("priority", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("CLIR control", ParamterType.INTEGER));
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
