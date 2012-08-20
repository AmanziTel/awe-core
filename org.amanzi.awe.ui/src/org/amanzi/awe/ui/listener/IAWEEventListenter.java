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

package org.amanzi.awe.ui.listener;

import java.util.Arrays;
import java.util.Comparator;

import org.amanzi.awe.ui.events.IEvent;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Comparators;

public interface IAWEEventListenter {

    public enum Priority {
        HIGH(2), NORMAL(1), LOW(0);

        private final int severity;

        private Priority(final int severity) {
            this.severity = severity;
        }

        public static Priority[] getSortedPriorities() {
            Priority[] result = ArrayUtils.clone(values());

            Arrays.sort(result, new Comparator<Priority>() {

                @Override
                public int compare(final Priority o1, final Priority o2) {
                    return Comparators.compare(o2.severity, o1.severity);
                }
            });

            return result;
        }

    }

    void onEvent(IEvent event);

    Priority getPriority();

}