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

package org.amanzi.neo.models.impl.internal.util;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractLoggable {

    /*
     * constants to create log statement
     */
    private static final String LOG_STATEMENT_FINISH_ARGS = ">)";
    private static final String LOG_STATEMENT_ARG_SEPARATOR = ">, <";
    private static final String LOG_STATEMENT_START_ARGS = "(<";
    private static final String START_LOG_STATEMENT_PREFIX = "start ";

    protected String getStartLogStatement(String methodName, Object... args) {
        StringBuilder builder = new StringBuilder(START_LOG_STATEMENT_PREFIX).append(methodName).append(LOG_STATEMENT_START_ARGS);

        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                builder.append(LOG_STATEMENT_ARG_SEPARATOR);
            }

            builder.append(args[i]);
        }

        builder.append(LOG_STATEMENT_FINISH_ARGS);

        return builder.toString();
    }

    protected String getFinishLogStatement(String methodName) {
        StringBuilder builder = new StringBuilder("finish ").append(methodName).append("()");

        return builder.toString();
    }

}
