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

package org.amanzi.neo.loader.core.validator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IValidationResult {

    public static final IValidationResult SUCCESS = new IValidationResult() {

        @Override
        public Result getResult() {
            return Result.SUCCESS;
        }

        @Override
        public String getMessages() {
            return "successed validation";
        }

    };

    public static final IValidationResult FAIL = new IValidationResult() {

        @Override
        public Result getResult() {
            return Result.FAIL;
        }

        @Override
        public String getMessages() {
            return "failed validation";
        }

    };

    public static final IValidationResult UNKNOWN = new IValidationResult() {

        @Override
        public Result getResult() {
            return Result.UNKNOWN;
        }

        @Override
        public String getMessages() {
            return "unknown validation";
        }

    };

    /**
     * <p>
     * Result of validation
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static enum Result {
        SUCCESS, FAIL, UNKNOWN;
    }

    /**
     * Gets the result.
     * 
     * @return the result
     */
    Result getResult();

    /**
     * Gets the messages.
     * 
     * @return the messages
     */
    String getMessages();
}
