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

package org.amanzi.neo.core.service.listener;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * TODO Purpose of
 * <p>
 * abstract implementation of INeoServiceProviderListener
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public abstract class NeoServiceProviderListener implements INeoServiceProviderListener {
    protected GraphDatabaseService graphDatabaseService = NeoServiceProvider.getProvider().getService();

    @Override
    public void onNeoStop(Object source) {
        graphDatabaseService = null;
    }

    @Override
    public void onNeoStart(Object source) {
        graphDatabaseService = NeoServiceProvider.getProvider().getService();
    }

    @Override
    public void onNeoCommit(Object source) {
    }

    @Override
    public void onNeoRollback(Object source) {
    }

}
