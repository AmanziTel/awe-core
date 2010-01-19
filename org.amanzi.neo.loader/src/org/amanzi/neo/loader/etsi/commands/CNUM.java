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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.amanzi.neo.loader.etsi.commands.ETSICommandParameter.ParamterType;

/**
 * AT+CNUM command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class CNUM extends AbstractETSICommand {
	
	/*
	 * Command name
	 */
	private static final String COMMAND_NAME = "CNUM";
	
	@Override
	public String getName() {
		return COMMAND_PREFIX + COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
		parameters.add(new ETSICommandParameter("num type", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("Called Party Identity", ParamterType.INTEGER));
		parameters.add(new ETSICommandParameter("Alpha", ParamterType.STRING));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		while (tokenizer.hasMoreTokens()) {
			String parametersLine = tokenizer.nextToken(RESULT_DELIMITER).trim();
			
			StringTokenizer parametersTokenizer = new StringTokenizer(parametersLine, PARAMETER_DELIMITER);
			
			for (int i = 0; i < parameters.size(); i++) {
				Object value = null;
				ETSICommandParameter parameter = parameters.get(i);
				if (parametersTokenizer.hasMoreTokens()) {
					value = parameter.parseString(parametersTokenizer.nextToken());
				}
				
				ArrayList<Object> resultList = (ArrayList<Object>)result.get(parameter.getName());
				if (resultList == null) {
					resultList = new ArrayList<Object>();
				}
				resultList.add(value);
				
				result.put(parameter.getName(), resultList);
			}
		}	
		
		return result;
	}

	@Override
	public boolean isCallCommand() {
		return false;
	}

}
