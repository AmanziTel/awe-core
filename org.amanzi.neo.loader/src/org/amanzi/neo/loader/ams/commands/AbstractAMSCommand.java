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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;

/**
 * Abstract class that describes AMS command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractAMSCommand {
	
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
	protected ArrayList<AMSCommandParameters> parameters = new ArrayList<AMSCommandParameters>();
	
	public AbstractAMSCommand() {
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
     * Returns name of mm node
     *
     * @return name of mm node
     */
    public abstract String getMMName();
	
	/**
	 * Returns a delimiter for Parameter
	 *
	 * @return
	 */
	protected String getParamterDelimiter() {
	    return PARAMETER_DELIMITER;
	}
	
	/**
	 * Returns a delimiter for Results 
	 *
	 * @return result delimiter
	 */
	protected String getResultDelimiter() {
	    return RESULT_DELIMITER;
	}
	
	/**
	 * Parses String and returns map of parameters
	 *
	 * @param tokenizer tokenizer of string to parse
	 * @return map that contains name of parameter and it's value
	 */
	protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
	    HashMap<String, Object> result = new HashMap<String, Object>();
        
        StringTokenizer parametersTokenizer = new StringTokenizer(tokenizer.nextToken(getResultDelimiter()), getParamterDelimiter());
        
        for (AMSCommandParameters singleParameter : parameters) {
            if (!parametersTokenizer.hasMoreTokens()) {
                break;
            }
            result.put(singleParameter.getName(), singleParameter.parseString(parametersTokenizer.nextToken().trim()));
        }
        
        return result;
	}
	
	
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
