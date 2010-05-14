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

package org.amanzi.neo.data_generator.utils.call;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CommandRow;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;

/**
 * Creates command rows.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CommandCreator {
    
    /** Command names */
    public static final String CCI = "+CCI";
    public static final String AT_CCI = "AT+CCI?";
    public static final String CTGS = "+CTGS";
    public static final String AT_CTGS = "AT+CTGS?";
    public static final String UNSOLICITED = "<UNSOLICITED>";
    public static final String CTCR = "+CTCR";
    public static final String ATH = "ATH";
    public static final String ATA = "ATA";
    public static final String CTICN = "<UNSOLICITED>|+CTICN";
    public static final String ATD = "atd";
    public static final String CTSDC = "AT+CTSDC";
    public static final String CTXG = "+CTXG";
    public static final String PESQ = "PESQ.run";
    public static final String CTCC = "+CTCC";
    public static final String CTOCP = "+CTOCP";
    public static final String CTSDS = "AT+CTSDS";
    public static final String AT_CMGS = "AT+CMGS";
    public static final String CMGS = "+CMGS";
    public static final String CTSDSR = "<UNSOLICITED>|+CTSDSR";
    public static final String CSPTR = "AT+CSPTR";
    public static final String AT_CREG = "AT+CREG?";
    public static final String CREG = "+CREG";
    
    private static final String ADD_COMAND_PREFIX = "~";
    private static final String OK_PARAMETER = "OK";
    private static final String DEFAULT_COMMAND_PREFIX_WRITE = "Port.writeAT";
    private static final String DEFAULT_COMMAND_PREFIX_READ = "Port.readAT";
    private static final String PROBE_NAME_COMMAND_PREFIX = "Probe Name: PROBE";
    private static final String PROBE_NAME_COMMAND = "Probe Phone Number: ";
    
    /**
     * Row with probe name and number.
     *
     * @param time Long.
     * @param name String
     * @param number String
     * @return CommandRow.
     */
    public static CommandRow getProbeNumberRow(Long time,String name, String number){
        CommandRow row = new CommandRow(PROBE_NAME_COMMAND+number);
        row.setTime(getDate(time));
        row.setPrefix(PROBE_NAME_COMMAND_PREFIX+name);
        return row;
    }
    
    /**
     * Row with 'CTSDC' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtsdcRow(Long time, Integer... params){
        CommandRow row = new CommandRow(CTSDC);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'CTSDC' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCtsdcRow(Long time, CommandRow source){
        CommandRow row = new CommandRow(CTSDC);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().addAll(source.getParams());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CTSDS' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtsdsRow(Long time, Integer... params){
        CommandRow row = new CommandRow(CTSDS);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'CTSDS' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCtsdsRow(Long time, CommandRow source){
        CommandRow row = new CommandRow(CTSDS);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().addAll(source.getParams());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'atd' command.
     *
     * @param time Long.
     * @param resNumber String
     * @return CommandRow.
     */
    public static CommandRow getAtdRow(Long time, String resNumber){
        CommandRow row = new CommandRow(ATD+resNumber);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'atd' command.
     *
     * @param time Long.
     * @param ctocp1 CommandRow (row of CTOCP command)
     * @param ctocp2 CommandRow (row of CTOCP command)
     * @param ctcc CommandRow (row of CTCC command)
     * @return CommandRow.
     */
    public static CommandRow getAtdRow(CommandRow source, CommandRow ctocp1, CommandRow ctocp2, CommandRow ctcc){
        CommandRow row = new CommandRow(source.getCommand());
        row.setTime(source.getTime());
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ADD_COMAND_PREFIX+ctocp1.getCommandAsString());
        row.getAdditional().add(ADD_COMAND_PREFIX+ctocp2.getCommandAsString());
        row.getAdditional().add(ctcc.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'atd' command.
     *
     * @param time Long.
     * @param ctocp1 CommandRow (row of CTOCP command)
     * @param ctocp2 CommandRow (row of CTOCP command)
     * @param ctcc CommandRow (row of CTCC command)
     * @return CommandRow.
     */
    public static CommandRow getAtdRow(Long time,CommandRow source, CommandRow ctcc, CommandRow ctxg){
        CommandRow row = new CommandRow(source.getCommand());
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ADD_COMAND_PREFIX+ctcc.getCommandAsString());
        row.getAdditional().add(ctxg.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CTOCP' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtocpRow(Long time){
        CommandRow row = new CommandRow(CTOCP);
        row.setTime(getDate(time));
        row.getParams().add(1); //CC instance
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(1);
        return row;
    }
    
    /**
     * Row with 'CTCC' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtccRow(Long time,Integer... params){
        CommandRow row = new CommandRow(CTCC);
        row.setTime(getDate(time));
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'CTXG' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtxgRow(Integer... params){
        CommandRow row = new CommandRow(CTXG);
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'CTXG' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtxgRow(String key,Integer... params){
        CommandRow row = new CommandRow(CTXG);
        for(Integer param : params){
            row.getParams().add(param);
        }
        row.getParams().add(key);
        return row;
    }

    /**
     * Row with 'CTICN' command.
     *
     * @param time Long.
     * @param sourceNumber String
     * @return CommandRow.
     */
    public static CommandRow getCticnRow(Long time, String sourceNumber){
        CommandRow row = new CommandRow(CTICN);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().add(1);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(sourceNumber);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(1);
        return row;
    }
    
    /**
     * Row with 'CTICN' command.
     *
     * @param time Long.
     * @param sourceNumber String
     * @return CommandRow.
     */
    public static CommandRow getCticnRow(Long time, String sourceNumber, CommandRow ctcc, CommandRow ctxg){
        CommandRow row = new CommandRow(CTICN);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().add(2);
        row.getParams().add(0);
        row.getParams().add(0);
        row.getParams().add(1);
        row.getParams().add(sourceNumber);
        row.getParams().add(1);
        row.getParams().add(1);
        row.getParams().add(0);
        row.getParams().add(1);
        row.getParams().add(1);
        row.getAdditional().add(ctcc.getCommandAsString());
        row.getAdditional().add(ctxg.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'ATA' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getAtaRow(Long time){
        CommandRow row = new CommandRow(ATA);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'ATA' command.
     *
     * @param source CommandRow (row of ATA command)
     * @param ctcc CommandRow (row of CTCC command)
     * @return CommandRow.
     */
    public static CommandRow getAtaRow(CommandRow source, CommandRow ctcc){
        CommandRow row = new CommandRow(source.getCommand());
        row.setTime(source.getTime());
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ADD_COMAND_PREFIX+ctcc.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'ATH' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getAthRow(Long time){
        CommandRow row = new CommandRow(ATH);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'ATH' command.
     *
     * @param time Long.
     * @param ctcr CommandRow (row of CTCR command)
     * @return CommandRow.
     */
    public static CommandRow getAthRow(Long time, CommandRow ctcr){
        CommandRow row = new CommandRow(ATH);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ADD_COMAND_PREFIX+ctcr.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CTCR' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtcrRow(Long time, Integer... params){
        CommandRow row = new CommandRow(CTCR);
        row.setTime(getDate(time));
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'UNSOLICITED|+CTCR' command.
     *
     * @param time Long.
     * @param ctcr CommandRow (row of CTCR command)
     * @return CommandRow.
     */
    public static CommandRow getUnsoCtcrRow(Long time, CommandRow ctcr){
        CommandRow row = new CommandRow(UNSOLICITED);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ctcr.getCommandAsString());
        return row;
    }
    
    /**
     * Row with 'AT+CCI' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getAtCciRow(Long time){
        CommandRow row = new CommandRow(AT_CCI);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'AT+CCI' command.
     *
     * @param time Long.
     * @param cci CommandRow (row of CCI command)
     * @return CommandRow.
     */
    public static CommandRow getAtCciRow(Long time, CommandRow cci){
        CommandRow row = new CommandRow(AT_CCI);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(cci.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CCI' command.
     *
     * @param networkIdentity Long.
     * @param localArea Integer.
     * @param frequency Integer.
     * @return CommandRow.
     */
    public static CommandRow getCciRow(Long networkIdentity, Integer localArea, Double frequency){
        CommandRow row = new CommandRow(CCI);
        row.getParams().add(networkIdentity);
        RandomValueGenerator generator = RandomValueGenerator.getGenerator();
        row.getParams().add(generator.getIntegerValue(-100, 100));
        row.getParams().add("99.9");
        row.getParams().add(localArea);
        row.getParams().add(formatDoubleValue(frequency,4));
        row.getParams().add(generator.getIntegerValue(0, 100));
        row.getParams().add(generator.getIntegerValue(0, 100));
        row.getParams().add(generator.getIntegerValue(0, 9999));
        row.getParams().add(generator.getIntegerValue(-100, 0));
        return row;
    }
    
    /**
     * Row with 'AT+CTGS' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getAtCtgsRow(Long time){
        CommandRow row = new CommandRow(AT_CTGS);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'AT+CTGS' command.
     *
     * @param time Long.
     * @param ctgs CommandRow (row of CCI command)
     * @return CommandRow.
     */
    public static CommandRow getAtCtgsRow(Long time, CommandRow ctgs){
        CommandRow row = new CommandRow(AT_CTGS);
        row.setTime(new Date(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(ctgs.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CTGS' command.
     *
     * @return CommandRow.
     */
    public static CommandRow getCtgsRow(List<String> sourceParams, List<String> resParams){
        CommandRow row = new CommandRow(CTGS);
        String prefix = "";
        for(String param : sourceParams){
            row.getParams().add(prefix+"1");
            row.getParams().add("0"+param);
            prefix = "|";
        }
        for(String param : resParams){
            row.getParams().add(prefix+"4");
            row.getParams().add("0"+param);
            prefix = "|";
        }
        return row;
    }
    
    public static CommandRow getPESQRow(Long time, Float audioQuality){
        CommandRow row = new CommandRow(PESQ);
        row.setTime(new Date(time));
        RandomValueGenerator generator = RandomValueGenerator.getGenerator();
        row.getAdditional().add(formatDoubleValue(audioQuality.doubleValue(), 7));
        row.getAdditional().add(formatDoubleValue(generator.getDoubleValue(100.0, 999.0), 7));
        row.getAdditional().add(formatDoubleValue(generator.getDoubleValue(100.0, 999.0), 7));
        row.getAdditional().add(formatDoubleValue(generator.getDoubleValue(100.0, 999.0), 7));
        row.getAdditional().add(formatDoubleValue(generator.getDoubleValue(0.0, 1.0), 7));
        return row;
    }
    
    /**
     * Row with 'CMGS' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCmgsRow(Integer... params){
        CommandRow row = new CommandRow(CMGS);
        for(Integer param : params){
            row.getParams().add(param);
        }
        return row;
    }
    
    /**
     * Row with 'UNSOLICITED|CMGS' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getUnsoCmgsRow(Long time,CommandRow cmgs){
        CommandRow row = new CommandRow(UNSOLICITED);
        row.setTime(new Date(time));
        row.getAdditional().add(cmgs.getCommandAsString());
        return row;
    }
    
    /**
     * Row with 'CMGS' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCmgsRow(Long time, String number, String message){
        CommandRow row = new CommandRow(AT_CMGS);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        row.getParams().add(number);
        String hex = convertAsciiToHex(message);
        row.getParams().add(hex.length()*4);
        row.getAdditional().add(hex);
        return row;
    }
    
    /**
     * Row with 'CMGS' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCmgsRow(Long time, CommandRow source, CommandRow cmgs){
        CommandRow row = new CommandRow(AT_CMGS);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().addAll(source.getParams());
        row.getAdditional().add(source.getAdditional().get(0));
        row.getAdditional().add(cmgs.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Row with 'CTSDSR' command.
     *
     * @param time Long.
     * @return CommandRow.
     */
    public static CommandRow getCtsdsrRow(Long time,Integer aiService, String source, String receiver, String message){
        CommandRow row = new CommandRow(CTSDSR);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getParams().add(aiService);
        row.getParams().add(source);
        row.getParams().add(1);
        row.getParams().add(receiver);
        row.getParams().add(1);
        String hex = convertAsciiToHex(message);
        row.getParams().add(hex.length()*4);
        row.getAdditional().add(hex);
        return row;
    }
    
    /**
     * Row with 'CSPTR' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCsprtRow(Long time, boolean isWrite){
        CommandRow row = new CommandRow(CSPTR);
        row.setTime(getDate(time));
        if (isWrite) {
            row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        }else{
            row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
            row.getAdditional().add(OK_PARAMETER);
        }
        return row;
    }
    
    /**
     * Row with 'CREG' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCregRow(Long time){
        CommandRow row = new CommandRow(AT_CREG);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_WRITE);
        return row;
    }
    
    /**
     * Row with 'CREG' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCregRow(Long time, String param){
        CommandRow row = new CommandRow(AT_CREG);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(param);
        return row;
    }
    
    /**
     * Row with 'CREG' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCregRow(String mni, Integer la){
        CommandRow row = new CommandRow(CREG);
        row.getParams().add(1);
        row.getParams().add(la);
        row.getParams().add(mni);
        return row;
    }
    
    /**
     * Row with 'CREG' command.
     *
     * @param time Long.
     * @param source CommamdRow.
     * @return CommandRow.
     */
    public static CommandRow getCregRow(Long time, CommandRow source){
        CommandRow row = new CommandRow(AT_CREG);
        row.setTime(getDate(time));
        row.setPrefix(DEFAULT_COMMAND_PREFIX_READ);
        row.getAdditional().add(source.getCommandAsString());
        row.getAdditional().add(OK_PARAMETER);
        return row;
    }
    
    /**
     * Format double value to string.
     *
     * @param value Double
     * @param after int (count of numbers after point)
     * @return String
     */
    private static String formatDoubleValue(Double value, int after){
        if (value == null) {
            return "";
        }
        if(after<0){
            return value.toString();
        }
        return new BigDecimal(value).setScale(after, RoundingMode.CEILING).toString();
    }
    
    /**
     * Get date by time.
     *
     * @param time Long 
     * @return Date.
     */
    private static Date getDate(Long time){
        if(time == null){
            return null;
        }
        return new Date(time);
    }
    
    /**
     * Convert ASCII string to hex string.
     *
     * @param ascii
     * @return
     */
    private static String convertAsciiToHex(String ascii){
        StringBuilder hex = new StringBuilder();
        
        for (int i=0; i < ascii.length(); i++) {
            hex.append(Integer.toHexString(ascii.charAt(i)));
        }
        
        return hex.toString();
    }

}
