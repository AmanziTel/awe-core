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

import org.amanzi.neo.loader.etsi.commands.AbstractETSICommand.CommandSyntax;

/**
 * Package of ETSI command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSICommandPackage {
	
	/*
	 * Map with commands
	 */
	private static HashMap<String, AbstractETSICommand> commandsMap = new HashMap<String, AbstractETSICommand>();
	
	static {
		registerReadSyntaxCommand(new CCI(CommandSyntax.READ));
		registerReadSyntaxCommand(new CBS(CommandSyntax.READ));
		registerReadSyntaxCommand(new CSQ(CommandSyntax.READ));
		registerReadSyntaxCommand(new CNUM(CommandSyntax.READ));
		
		registerSetSyntaxCommand(new CTSDC(CommandSyntax.SET));
	}
	
	/**
	 * Registers command
	 * 
	 * @param command ETSI command
	 */
	private static void registerReadSyntaxCommand(AbstractETSICommand command) {
		commandsMap.put(command.getName() + "?", command);
	}
	
	private static void registerSetSyntaxCommand(AbstractETSICommand command) {
		commandsMap.put(command.getName() + "=", command);
	}
	
	/**
	 * Returns a command by it's name
	 *
	 * @param commandName name of command
	 * @return command
	 */
	public static AbstractETSICommand getCommand(String commandName) {		
		return commandsMap.get(commandName);
	}
	
	/**
	 * Checks is it ETSI command
	 *
	 * @param commandName name of command
	 * @return is it ETSI command
	 */
	public static boolean isETSICommand(String commandName) {
		return commandName.startsWith("AT");
	}
	
	/**
	 * Returns clear name of Command
	 *
	 * @param commandName name of command
	 * @return clear name
	 */
	public static String getRealCommandName(String commandName) {
		//if it's get syntax than command name should contain ?
		int index = commandName.indexOf("?");
		if (index < 0) {
			//if it's set syntax than command name should contain =
			index = commandName.indexOf("=");
		}
		if (index > 0) {
			return commandName.substring(0, index);
		}
		else {
			return commandName;
		}
	}

}
