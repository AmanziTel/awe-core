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


/**
 * Package of AMS command
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class AMSCommandPackage {
	
	/*
	 * Map with commands
	 */
	private static HashMap<String, AbstractAMSCommand> commandsMap = new HashMap<String, AbstractAMSCommand>();
	
	static {
		registerCommand(new CCI());
		registerCommand(new CBC());
		registerCommand(new CSQ());
		registerCommand(new CNUM());
		registerCommand(new CTSDC());
		registerCommand(new ATD());
		registerCommand(new ATH());
		registerCommand(new ATA());
		registerCommand(new UNSOLICITED());
		registerCommand(new CTOCP());
		registerCommand(new CTCR());
		registerCommand(new CTCC());
		registerCommand(new CME());
		registerCommand(new PESQ());
		registerCommand(new CTICN());
		registerCommand(new ATCMGS());
		registerCommand(new CDTXC());
		registerCommand(new CGPS());
		registerCommand(new CNCI());
		registerCommand(new CTSDS());
		registerCommand(new CTSDSR());
		registerCommand(new CTSG());
		registerCommand(new CTXG());
		registerCommand(new CTXI());
		registerCommand(new CMGS());
		registerCommand(new ATE());
		registerCommand(new ATI());
		registerCommand(new ATO());
		registerCommand(new ATQ());
		registerCommand(new ATS0());
		registerCommand(new ATS2());
		registerCommand(new ATS3());
		registerCommand(new ATS4());
		registerCommand(new ATS5());
		registerCommand(new ATV());
		registerCommand(new ATZ());
		registerCommand(new CLVL());
		registerCommand(new CREG());
		registerCommand(new CSPDCS());
		registerCommand(new CSPICPN());
	}
	
	/**
	 * Registers command
	 * 
	 * @param command AMS command
	 */
	private static void registerCommand(AbstractAMSCommand command) {
		commandsMap.put(command.getName(), command);
	}
	
	/**
	 * Returns a command by it's name
	 *
	 * @param commandName name of command
	 * @return command
	 */
	public static AbstractAMSCommand getCommand(String commandName, CommandSyntax syntax) {
		if (syntax == CommandSyntax.EXECUTE) {
			for (String singleCommandName : commandsMap.keySet()) {
				if (commandName.startsWith(singleCommandName)) {
					return commandsMap.get(singleCommandName);
				}
			}
			return null;
		}
		else {
			return commandsMap.get(getRealCommandName(commandName));
		}
	}
	
	public static CommandSyntax getCommandSyntax(String commandName) {
		if (commandName.contains("?")) {
			return CommandSyntax.READ;
		}
		if (commandName.contains("=")) {
			return CommandSyntax.SET;
		}
		return CommandSyntax.EXECUTE;
	}
	
	/**
	 * Checks is it AMS command
	 *
	 * @param commandName name of command
	 * @return is it AMS command
	 */
	public static boolean isAMSCommand(String commandName) {
		return commandName.toUpperCase().startsWith("AT");
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
		if (index < 0) {
		    //try PESQ.run
		    index = commandName.indexOf(".run");
		}
		if (index > 0) {
			return commandName.substring(0, index);
		}
		else {
			return commandName;
		}
	}

}
