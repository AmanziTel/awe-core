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
 * AT+CTSDC command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CTSDC extends AbstractAMSCommand {
	
    public static final String SIMPLEX = "simplex";
    public static final String HOOK = "hook";
    public static final String COMMS_TYPE = "comms_type";
    public static final String SLOTS = "slots/codec";
    
	/*
	 * Command name
	 */
	public static final String COMMAND_NAME = "CTSDC";

	@Override
	public String getName() {
		return COMMAND_PREFIX + COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
		parameters.add(new AMSCommandParameter("AI Service", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("called party ident type", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("area", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter(HOOK, ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter(SIMPLEX, ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("end-to-end encryption", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter(COMMS_TYPE, ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter(SLOTS, ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("RqTx", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("priority", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("CLIR control", ParamterType.INTEGER));
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
