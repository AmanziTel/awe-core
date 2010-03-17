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

import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * ATD command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class ATH extends AbstractAMSCommand {
	
	/*
	 * Name of command
	 */
	private static final String COMMAND_NAME = "ATH";
	
	@Override
	public String getName() {
		return COMMAND_NAME;
	}

	@Override
	protected void initializeParameters() {
	}

	@Override
	protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
		return null;
	}

	@Override
	public boolean isCallCommand() {
		return true;
	}

}
