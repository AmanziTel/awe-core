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
import java.util.List;
import java.util.StringTokenizer;

import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;

/**
 * <p>
 * AT+CSPTGF command.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CSPTGF extends AbstractAMSCommand {

    /*
     * Name of command
     */
    private static final String COMMAND_NAME = "CSPTGF";
    
    @Override
    public String getName() {
        return COMMAND_PREFIX+COMMAND_NAME;
    }

    @Override
    protected void initializeParameters() {
        parameters.add(AMSCommandParameters.ALWAYS_ATTACHED_FOLDER);
        parameters.add(AMSCommandParameters.FOLDER_NUMBER);
        parameters.add(AMSCommandParameters.FOLDER_NAME);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        while (tokenizer.hasMoreTokens()) {
            String row = tokenizer.nextToken("|");
            if(row.contains("OK")){
                break;
            }
            if(row.startsWith("~")){
                continue;
            }
            if(row.startsWith("+")){
                StringTokenizer parametersTokenizer = new StringTokenizer(row, getResultDelimiter());
                parametersTokenizer.nextToken();
                AMSCommandParameters singleParameter = AMSCommandParameters.ALWAYS_ATTACHED_FOLDER;
                result.put(singleParameter.getName(), singleParameter.parseString(parametersTokenizer.nextToken().trim()));
            }
            StringTokenizer parametersTokenizer = new StringTokenizer(row, getParamterDelimiter());
            for (AMSCommandParameters singleParameter : parameters) {
                if(singleParameter.equals(AMSCommandParameters.ALWAYS_ATTACHED_FOLDER)){
                    continue;
                }
                List<Object> column = (List<Object>)result.get(singleParameter.getName());
                if(column == null){
                    column = new ArrayList<Object>();
                    result.put(singleParameter.getName(), column);
                }
                if (!parametersTokenizer.hasMoreTokens()) {
                    column.add(null);
                } else{
                    column.add(singleParameter.parseString(parametersTokenizer.nextToken().trim()));
                }
            }
        }
        return result;
    }
    
    @Override
    public HashMap<String, Object> getResults(CommandSyntax syntax, StringTokenizer tokenizer) {
        return parseResults(tokenizer);
    }
    
    @Override
    public boolean isCallCommand() {
        return false;
    }
    
    @Override
    public String getMMName() {
        return "folder group";
    }
}
