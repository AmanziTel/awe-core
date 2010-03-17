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
 * AT+CCI command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CCI extends AbstractAMSCommand {
	
	/*
	 * Command name 
	 */
    public static final String COMMAND_NAME = "CCI";
	
	@Override
	public String getName() {
		return COMMAND_PREFIX + COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
		parameters.add(new AMSCommandParameter("MNI", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("RSSI", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("BER", ParamterType.FLOAT));
		parameters.add(new AMSCommandParameter("LA", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("F", ParamterType.FLOAT));
		parameters.add(new AMSCommandParameter("C1", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("BNC_LA", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("BNC_C2", ParamterType.INTEGER));
		parameters.add(new AMSCommandParameter("BNC_RSSI", ParamterType.INTEGER));
	}

	@Override
	public boolean isCallCommand() {
		return false;
	}

}
