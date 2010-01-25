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

/**
 * Abstract class that describes ETSI command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractETSICommand {
	
	/*
	 * Delimiter of parameters in result
	 */
	protected static final String PARAMETER_DELIMITER = ",";
	
	/*
	 * Delimiter of results in output
	 */
	protected static final String RESULT_DELIMITER = "|:";
	
	/*
	 * Prefix of all commands
	 */
	protected static final String COMMAND_PREFIX = "AT+";
	
	/*
	 * List of parameters in command
	 */
	protected ArrayList<ETSICommandParameter> parameters = new ArrayList<ETSICommandParameter>();
	
	public AbstractETSICommand() {
		initializeParameters();
	}

	/**
	 * Initializes parameters of command
	 */
	protected abstract void initializeParameters();

	/**
	 * Returns name of command
	 *
	 * @return name of command
	 */
	public abstract String getName();
	
	/**
	 * Parses String and returns map of parameters
	 *
	 * @param tokenizer tokenizer of string to parse
	 * @return map that contains name of parameter and it's value
	 */
	protected abstract HashMap<String, Object> parseResults(StringTokenizer tokenizer);
	
	/**
	 * Returns map with results of command
	 *
	 * @param tokenizer string with command results
	 * @return map that contains name of parameter and it's value
	 */
	public HashMap<String, Object> getResults(CommandSyntax syntax, StringTokenizer tokenizer) {
		switch (syntax) {
		case READ:
			tokenizer.nextToken(":");		
			return parseResults(tokenizer);
		case SET:
		case EXECUTE:
			return parseResults(tokenizer);
		}
		
		return null;
	}

	public abstract boolean isCallCommand();
	
	
}
