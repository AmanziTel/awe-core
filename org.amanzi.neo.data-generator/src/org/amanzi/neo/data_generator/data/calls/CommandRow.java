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

package org.amanzi.neo.data_generator.data.calls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.amanzi.neo.data_generator.data.nemo.PointData;
import org.amanzi.neo.data_generator.utils.call.CommandCreator;


/**
 * Data saver for command row in probe file.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CommandRow {
    
    protected static final String COMMAND_SEPARATOR = "|";
    protected static final String PARAMETERS_SEPARATOR = ",";
    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";
    
    protected static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat(TIMESTAMP_FORMAT);
    
    private Date time;
    private String prefix;
    private String command;
    private List<Object> params = new ArrayList<Object>();
    private List<Object> additional = new ArrayList<Object>();
    /** gps position for probe on this command */
    private PointData pointData;
    
    /**
    * Constructor.
    * @param name String (command name)
    */
    public CommandRow(String name){
        command = name;
    }
    
    /**
     * Getter for time of receive command.
     *
     * @return Date
     */
    public Date getTime(){
        return time;
    }
    
    /**
     * Setter for time of receive command.
     *
     * @param aTime Date
     */
    public void setTime(Date aTime){
        time = aTime;
    }
    
    /**
     * Getter for command prefix.
     *
     * @return String
     */
    public String getPrefix(){
        return prefix;
    }
    
    /**
     * Setter for command prefix.
     *
     * @param aPrefix String
     */
    public void setPrefix(String aPrefix){
        prefix = aPrefix;
    }
    
    /**
     * Getter for command name.
     *
     * @return String
     */
    public String getCommand(){
        return command;
    }
    
    /**
     * Getter for command parameters.
     *
     * @return List
     */
    public List<Object> getParams(){
        return params;
    }
    
    /**
     * Getter for command additional parameters.
     *
     * @return List
     */
    public List<Object> getAdditional(){
        return additional;
    }
    
    /**
     * Returns command string.
     *
     * @return String.
     */
    public String getCommandAsString(){
        StringBuilder result = new StringBuilder();
        if(time!=null){
            result.append(TIME_FORMATTER.format(time))
                  .append(COMMAND_SEPARATOR);
        }
        if(prefix!=null){
            result.append(prefix)
                  .append(COMMAND_SEPARATOR);
        }
        result.append(command)
              .append(getParamString())
              .append(getAdditionalString());
        return result.toString();
    }
    
    /**
     * Getter for parameters prefix.
     *
     * @return String
     */
    private String getParamPrefix() {
        if(command!=null&& command.equals(CommandCreator.CTSDC)){
            return "=";
        }
        return ": ";
    }
    
    /**
     * Form string of parameters.
     *
     * @return String
     */
    protected String getParamString() {
        if(params==null||params.isEmpty()){
            return "";
        }
        String separator = getParamPrefix();
        StringBuilder result = new StringBuilder();
        for(Object param : params){
            result.append(separator).append(param);
            separator = PARAMETERS_SEPARATOR;
        }
        return result.toString();
    }
    
    /**
     * Form string of additional parameters.
     *
     * @return String
     */
    protected String getAdditionalString() {
        if(additional==null||additional.isEmpty()){
            return "";
        }
        StringBuilder result = new StringBuilder();
        for(Object param : additional){
            result.append(COMMAND_SEPARATOR).append(param);
        }
        return result.toString();
    }
    
    /** 
     * Gets gps position for probe on this command
     * @return gps position for probe on this command
     */
    public PointData getPointData() {
        return pointData;
    }
    
    /** 
     * Sets gps position for probe on this command
     * @param pointData gps position for probe on this command
     */
    public void setPointData(PointData pointData) {
        this.pointData = pointData;
    }

}
