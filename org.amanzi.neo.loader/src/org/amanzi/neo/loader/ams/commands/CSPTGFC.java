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
 * AT+CSPTGFC command.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CSPTGFC extends AbstractAMSCommand {

    /*
     * Name of command
     */
    private static final String COMMAND_NAME = "CSPTGFC";
    
    @Override
    public String getName() {
        return COMMAND_PREFIX+COMMAND_NAME;
    }

    @Override
    protected void initializeParameters() {
        parameters.add(AMSCommandParameters.ALWAYS_ATTACHED_FOLDER);
        parameters.add(AMSCommandParameters.FOLDER_NUMBER);
        parameters.add(AMSCommandParameters.FOLDER_NAME);
        parameters.add(AMSCommandParameters.TALKGROUP);
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
            AMSCommandParameters singleParameter = AMSCommandParameters.FOLDER_NUMBER;
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
            singleParameter = AMSCommandParameters.FOLDER_NAME;
            column = (List<Object>)result.get(singleParameter.getName());
            if(column == null){
                column = new ArrayList<Object>();
                result.put(singleParameter.getName(), column);
            }
            if (!parametersTokenizer.hasMoreTokens()) {
                column.add(null);
            } else{
                column.add(singleParameter.parseString(parametersTokenizer.nextToken().trim()));
            }
            singleParameter = AMSCommandParameters.TALKGROUP;
            List<Integer> nums = new ArrayList();
            while(parametersTokenizer.hasMoreTokens()){
                nums.add((Integer)singleParameter.parseString(parametersTokenizer.nextToken().trim()));
            }
            column = (List<Object>)result.get(singleParameter.getName());
            if(column == null){
                column = new ArrayList<Object>();
                result.put(singleParameter.getName(), column);
            }
            column.add(nums.toArray(new Integer[]{})); //TODO is it correct.
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
}
