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

package org.amanzi.awe.ui.manager;

import java.util.Map;
import java.util.Set;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.listener.IAWEEventListenter.Priority;
import org.amanzi.awe.ui.manager.internal.Listener1;
import org.amanzi.awe.ui.manager.internal.Listener2;
import org.amanzi.awe.ui.manager.internal.Listener3;
import org.amanzi.testing.AbstractIntegrationTest;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AWEEventManagerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCheckExtensionPointInitialization() {
        AWEEventManager manager = AWEEventManager.getManager();

        Class< ? >[] projectChangedClasses = new Class< ? >[] {Listener2.class, Listener3.class};

        Class< ? >[] aweStartedClasses = new Class< ? >[] {Listener1.class, Listener3.class};

        Map<EventStatus, Set<IAWEEventListenter>> listenerMap = manager.getListeners(Priority.NORMAL);

        for (EventStatus status : new EventStatus[] {EventStatus.AWE_STARTED, EventStatus.PROJECT_CHANGED}) {
            Class< ? >[] classes = null;
            switch (status) {
            case AWE_STARTED:
                classes = aweStartedClasses;
                break;
            case PROJECT_CHANGED:
                classes = projectChangedClasses;
                break;
            default:
                // do notning
                break;
            }

            for (IAWEEventListenter listener : listenerMap.get(status)) {
                if (ArrayUtils.contains(classes, listener.getClass())) {
                    classes = ArrayUtils.removeElement(classes, listener.getClass());
                }
            }

            assertEquals("Not all Listeners was correctly initialized", 0, classes.length);
        }
    }
}
