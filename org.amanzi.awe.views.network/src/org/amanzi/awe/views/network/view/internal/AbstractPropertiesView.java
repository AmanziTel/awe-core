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

package org.amanzi.awe.views.network.view.internal;

import java.util.List;

import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractPropertiesView extends ViewPart {
    
    private static final String LINE_SEPARATOR_PROPERTY = "line.separator";
    private static final String TAB_STRING = "\t";
    
    /**
     * transform list at line
     * 
     * @param list
     * @return line
     */
    protected String parseToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String value : list) {
            builder.append(value).append(TAB_STRING);
        }
        builder.append(System.getProperty(LINE_SEPARATOR_PROPERTY));
        return builder.toString();
    }

}
