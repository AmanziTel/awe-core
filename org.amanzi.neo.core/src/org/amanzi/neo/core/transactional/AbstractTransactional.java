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

package org.amanzi.neo.core.transactional;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.IDatabaseManager;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractTransactional {

    private static final int MAX_TX_COUNT = 2000;

    private int txCount;

    private final int maxTxCount;

    private final IDatabaseManager dbManager;

    /**
     * 
     */
    protected AbstractTransactional() {
        this(DatabaseManagerFactory.getDatabaseManager(), MAX_TX_COUNT);
    }

    protected AbstractTransactional(IDatabaseManager manager, int maxTxCount) {
        this.maxTxCount = maxTxCount;
        this.dbManager = manager;
    }

    protected void startTransaction() {
        dbManager.startThreadTransaction();
    }

    protected void updateTransaction() {
        txCount++;
        if (txCount > maxTxCount) {
            txCount = 0;
            saveTx(true, true);
        }
    }

    protected void saveTx(final boolean success, final boolean shouldContinue) {
        if (success) {
            dbManager.commitThreadTransaction();
        } else {
            dbManager.rollbackThreadTransaction();
        }

        dbManager.finishThreadTransaction();
        if (shouldContinue) {
            dbManager.startThreadTransaction();
        }
    }

}
