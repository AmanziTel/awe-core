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

/**
 * <p>
 * Wrapper of control file
 * </p>
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ControlFile {
    
    private static final int MAX_LINE = 100;

    /** The property pattern. */
    Pattern propertyPattern=Pattern.compile("^(\\S+)\\s(.*)$");
    
    /** The property map. */
    private Map<String,String> propertyMap=new HashMap<String, String>();

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
