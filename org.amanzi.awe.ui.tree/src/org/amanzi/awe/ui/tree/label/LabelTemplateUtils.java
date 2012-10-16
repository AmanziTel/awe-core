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

package org.amanzi.awe.ui.tree.label;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.amanzi.neo.dateformat.DateFormatManager;
import org.amanzi.neo.dto.IDataElement;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class LabelTemplateUtils {

    private static final String PROPERTY_PREFIX = "#";

    public static class LabelTemplate {

        private final List<String> propertyNames;

        private final String templateMessage;

        private LabelTemplate(final List<String> propertyNames, final String templateMessage) {
            this.propertyNames = propertyNames;
            this.templateMessage = templateMessage;
        }

        public String toString(final IDataElement element) {
            final Object[] values = new String[propertyNames.size()];

            for (int i = 0; i < propertyNames.size(); i++) {
                final String property = propertyNames.get(i);

                if (property.equals("timestamp")) {
                    values[i] = formatTimestamp(element.get(property));
                } else {
                    values[i] = element.contains(property) ? element.get(property) : StringUtils.EMPTY;
                }

            }

            return MessageFormat.format(templateMessage, values);
        }

        public int handleRange(final IDataElement element) {
            int i = 0;

            for (final String property : propertyNames) {
                if (element.contains(property)) {
                    i++;
                }
            }

            return i;
        }
    }

    private static String formatTimestamp(final Object timestamp) {
        return DateFormatManager.getInstance().getDefaultFormat().format(new Date((Long)timestamp));
    }

    public static LabelTemplate getTemplate(final String message) {
        assert message != null;

        final StringBuilder templateMessage = new StringBuilder();
        final List<String> propertyNames = new ArrayList<String>();

        final StringTokenizer tokenizer = new StringTokenizer(message, PROPERTY_PREFIX);

        boolean isProperty = message.indexOf(PROPERTY_PREFIX) == 0;

        int counter = 0;

        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();

            if (isProperty) {
                propertyNames.add(token);

                templateMessage.append("{").append(counter++).append("}");

                isProperty = false;
            } else {
                templateMessage.append(token);
                isProperty = true;
            }
        }

        if (templateMessage.length() > 0 && !propertyNames.isEmpty()) {
            return new LabelTemplate(propertyNames, templateMessage.toString());
        }

        return null;
    }
}
