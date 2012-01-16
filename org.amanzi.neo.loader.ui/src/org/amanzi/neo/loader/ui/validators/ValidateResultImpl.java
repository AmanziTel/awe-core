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

package org.amanzi.neo.loader.ui.validators;


/**
 * <p>
 *Implementation of validate result
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class ValidateResultImpl implements IValidateResult {

    private Result result;
    private String messages;

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return messages;
    }


    /**
     * Instantiates a new validate result impl.
     *
     * @param result the result
     * @param messages the messages
     */
    public ValidateResultImpl(Result result, String messages) {
        super();
        this.result = result;
        this.messages = messages;
    }

    
}
