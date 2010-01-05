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

import java.util.HashMap;
import java.util.StringTokenizer;

import org.amanzi.neo.loader.etsi.commands.ETSICommandParameter.ParamterType;

/**
 * AT+CBS command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CBS extends AbstractETSICommand {
	
	/**
	 * @param syntax
	 */
	public CBS(CommandSyntax syntax) {
		super(syntax);
	}

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
		parameters.add(new ETSICommandParameter("BCS", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("BCL", ParamterType.INTEGER));
	}

	@Override
	protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		StringTokenizer parametersTokenizer = new StringTokenizer(tokenizer.nextToken(RESULT_DELIMITER), PARAMETER_DELIMITER);
		
		for (ETSICommandParameter singleParameter : parameters) {
			if (!parametersTokenizer.hasMoreTokens()) {
				break;
			}
			result.put(singleParameter.getName(), singleParameter.parseString(parametersTokenizer.nextToken().trim()));
		}
		
		return result;
	}

}
