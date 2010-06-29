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

package org.amanzi.awe.afp.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.awe.console.AweConsolePlugin;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Wrapper of control file
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ControlFile {
    
    /** The Constant MAX_LINE. */
    private static final int MAX_LINE = 100;

    /** The property pattern. */
    Pattern propertyPattern=Pattern.compile("^(\\S+)\\s(.*)$");
    
    /** The property map. */
    private Map<String,String> propertyMap=new HashMap<String, String>();

    /** The neighbour file. */
    private File logFile;

    /** The cell file file. */
    private File cellFile;

    /** The neighbour file. */
    private File neighbourFile;

    /** The interference file. */
    private File interferenceFile;

    /** The output file. */
    private File outputFile;

    /** The cliques file. */
    private File cliquesFile;

    /** The forbidden file. */
    private File forbiddenFile;

    /** The exception file. */
    private File exceptionFile;

    /**
     * Gets the log file.
     * 
     * @return the log file
     */
    public File getLogFile() {
        return logFile;
    }

    /**
     * Sets the log file.
     * 
     * @param logFile the new log file
     */
    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    /**
     * Gets the cell file file.
     * 
     * @return the cell file file
     */
    public File getCellFile() {
        return cellFile;
    }

    /**
     * Sets the cell file file.
     * 
     * @param cellFile the new cell file file
     */
    public void setCellFile(File cellFile) {
        this.cellFile = cellFile;
    }

    /**
     * Gets the neighbour file.
     * 
     * @return the neighbour file
     */
    public File getNeighbourFile() {
        return neighbourFile;
    }

    /**
     * Sets the neighbour file.
     * 
     * @param neighbourFile the new neighbour file
     */
    public void setNeighbourFile(File neighbourFile) {
        this.neighbourFile = neighbourFile;
    }

    /**
     * Gets the interference file.
     * 
     * @return the interference file
     */
    public File getInterferenceFile() {
        return interferenceFile;
    }

    /**
     * Sets the interference file.
     * 
     * @param interferenceFile the new interference file
     */
    public void setInterferenceFile(File interferenceFile) {
        this.interferenceFile = interferenceFile;
    }

    /**
     * Gets the output file.
     * 
     * @return the output file
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Sets the output file.
     * 
     * @param outputFile the new output file
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Gets the cliques file.
     * 
     * @return the cliques file
     */
    public File getCliquesFile() {
        return cliquesFile;
    }

    /**
     * Sets the cliques file.
     * 
     * @param cliquesFile the new cliques file
     */
    public void setCliquesFile(File cliquesFile) {
        this.cliquesFile = cliquesFile;
    }

    /**
     * Gets the forbidden file.
     * 
     * @return the forbidden file
     */
    public File getForbiddenFile() {
        return forbiddenFile;
    }

    /**
     * Sets the forbidden file.
     * 
     * @param forbiddenFile the new forbidden file
     */
    public void setForbiddenFile(File forbiddenFile) {
        this.forbiddenFile = forbiddenFile;
    }

    /**
     * Gets the exception file.
     * 
     * @return the exception file
     */
    public File getExceptionFile() {
        return exceptionFile;
    }

    /**
     * Sets the exception file.
     * 
     * @param exceptionFile the new exception file
     */
    public void setExceptionFile(File exceptionFile) {
        this.exceptionFile = exceptionFile;
    }

    /**
     * Gets the property map.
     * 
     * @return the property map
     */
    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Instantiates a new control file.
     * 
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ControlFile(File file) throws IOException {
        loadPropertyFromFile(file);
        setParametersFromProperty();
    }

    /**
     * Sets the parameters from property.
     */
    private void setParametersFromProperty() {
        logFile = getFile("LogFile");
        cellFile = getFile("CellFile");
        neighbourFile = getFile("NeighboursFile");
        interferenceFile = getFile("InterferenceFile");
        outputFile = getFile("OutputFile");
        cliquesFile = getFile("CliquesFile");
        forbiddenFile = getFile("ForbiddenFile");
        exceptionFile = getFile("ExceptionFile");
    }


    /**
     * Gets the file.
     * 
     * @param propertyName the property name
     * @return the file
     */
    protected File getFile(String propertyName) {
        String fileName = normalizePath(propertyMap.get(propertyName));
        if (StringUtils.isNotEmpty(fileName)) {
            File result = new File(fileName);
            if (result.isFile() && result.exists()) {
                return result;
            }
        }
        return null;
    }


    /**
     * Normalize path.
     * 
     * @param path the path
     * @return the string
     */
    private String normalizePath(String path) {
        if (StringUtils.isEmpty(path)){
            return "";
        }
        if (path.startsWith("\"")){
            path=path.substring(1);
        }
        if (path.endsWith("\"")) {
            path = path.substring(0, path.length() - 1);
        }
        path = path.replace('\\', '/');
        return path;
    }

    /**
     * Load property from file.
     * 
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void loadPropertyFromFile(File file) throws IOException {
        propertyMap.clear();
        FileInputStream in=new FileInputStream(file);
        //TODO add charset
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try{
           String line;
           int count=0;
           while ((line = reader.readLine()) != null) {
               count++;
               if (count>MAX_LINE){
                   throw new IllegalArgumentException(String.format("Incorrect file '%s' for AFP Conrol file!",file));
               }
               Matcher matcher = propertyPattern.matcher(line);
               if (matcher.matches()){
                   String id=matcher.group(1).trim();
                   String value=matcher.group(2).trim();
                   propertyMap.put(id, value);
               }else{
                   AweConsolePlugin.error("Not parsed line: "+line);
               }
           }
        }finally{
            reader.close();
        }
    }
}
