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

/**
 * Parameter of AMS command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
class AMSCommandParameter {
	
	/**
	 * Type of parameter
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	public enum ParamterType {
		INTEGER,
		FLOAT,
		STRING;
	}
	
	/*
	 * Name of parameter
	 */
	private String name;
	
	/*
	 * Type of parameter
	 */
	private ParamterType type;
	
	public AMSCommandParameter(String name, ParamterType type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Parses string to get a value of this parameter
	 *
	 * @param parameter line with value
	 * @return parsed value
	 */
	public Object parseString(String parameter) {
		try {
			switch (type) {
			case INTEGER: 
				return Integer.parseInt(parameter);
			case FLOAT:
				return Float.parseFloat(parameter);
			case STRING:
				return parameter;
			}
		}
		catch (Exception e) {
			//do nothing
		}
		return parameter;
	}
	
	/**
	 * Returns name of parameter
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

}
