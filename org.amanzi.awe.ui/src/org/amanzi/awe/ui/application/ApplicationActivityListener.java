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

package org.amanzi.awe.ui.application;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ApplicationActivityListener implements IAWEEventListenter {

    /**
     * 
     */
    public ApplicationActivityListener() {
    }

    @Override
    public void onEvent(IEvent event) {
        switch (event.getStatus()) {
        case AWE_STOPPED:
            stopDatabase();
            break;
        default:
            break;
        }
    }

    private void stopDatabase() {
        DatabaseManagerFactory.getDatabaseManager().shutdown();
    }
}
