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
 * PESQ command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class PESQ extends AbstractAMSCommand {
	
	/** String ESTIMATED_DELAY field */
    public static final String ESTIMATED_DELAY = "estimated delay";
    
    /** String PESQ_LISTENING_QUALITIY field */
    public static final String PESQ_LISTENING_QUALITIY = "PESQ Listening Qualitiy";
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
	    parameters.add(new AMSCommandParameter(PESQ_LISTENING_QUALITIY, ParamterType.FLOAT));
	    parameters.add(new AMSCommandParameter("PESQ Threshold", ParamterType.FLOAT));
	    parameters.add(new AMSCommandParameter("Average Symmetrical Disturbance", ParamterType.FLOAT));
	    parameters.add(new AMSCommandParameter("Average Asymmetrical Distrurbance", ParamterType.FLOAT));
	    parameters.add(new AMSCommandParameter(ESTIMATED_DELAY, ParamterType.FLOAT));
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
