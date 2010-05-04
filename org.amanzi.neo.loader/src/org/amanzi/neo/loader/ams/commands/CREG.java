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
import java.util.StringTokenizer;

import org.amanzi.neo.loader.ams.parameters.AMSCommandParameters;

/**
 * <p>
 * AT+CREG command.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CREG extends AbstractAMSCommand {

    /*
     * Name of command
     */
    private static final String COMMAND_NAME = "CREG";
    
    @Override
    public String getName() {
        return COMMAND_PREFIX+COMMAND_NAME;
    }

    @Override
    protected void initializeParameters() {
        parameters.add(AMSCommandParameters.REGISTERED_STATUS);
        parameters.add(AMSCommandParameters.LA);
        parameters.add(AMSCommandParameters.MNI);
    }
    
    @Override
    protected HashMap<String, Object> parseResults(StringTokenizer tokenizer) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        
        String nextToken = tokenizer.nextToken("|");
        if(nextToken.startsWith("~")){
            nextToken = tokenizer.nextToken("|");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(nextToken,getResultDelimiter());
        String paramString = stringTokenizer.nextToken();
        paramString = stringTokenizer.nextToken();
        StringTokenizer parametersTokenizer = new StringTokenizer(paramString, getParamterDelimiter());
        
        for (AMSCommandParameters singleParameter : parameters) {
            if (!parametersTokenizer.hasMoreTokens()) {
                break;
            }
            result.put(singleParameter.getName(), singleParameter.parseString(parametersTokenizer.nextToken().trim()));
        }
        
        return result;
    }
    
    @Override
    public HashMap<String, Object> getResults(CommandSyntax syntax, StringTokenizer tokenizer) {
        return parseResults(tokenizer);
    }

    @Override
    public boolean isCallCommand() {
        return true;
    }
}
