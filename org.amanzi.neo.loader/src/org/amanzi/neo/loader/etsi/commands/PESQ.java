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
 * PESQ command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class PESQ extends AbstractETSICommand {
	
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
	    parameters.add(new ETSICommandParameter(PESQ_LISTENING_QUALITIY, ParamterType.FLOAT));
	    parameters.add(new ETSICommandParameter("PESQ Threshold", ParamterType.FLOAT));
	    parameters.add(new ETSICommandParameter("Average Symmetrical Disturbance", ParamterType.FLOAT));
	    parameters.add(new ETSICommandParameter("Average Asymmetrical Distrurbance", ParamterType.FLOAT));
	    parameters.add(new ETSICommandParameter(ESTIMATED_DELAY, ParamterType.FLOAT));
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
