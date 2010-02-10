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
public class CTSDC extends AbstractETSICommand {
	
    public static final String SIMPLEX = "simplex";
    public static final String HOOK = "hook";
    public static final String COMMS_TYPE = "comms_type";
    public static final String SLOTS = "slots/codec";
    
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
		parameters.add(new ETSICommandParameter(HOOK, ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter(SIMPLEX, ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("end-to-end encryption", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter(COMMS_TYPE, ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter(SLOTS, ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("RqTx", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("priority", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("CLIR control", ParamterType.INTEGER));
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
