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

package org.amanzi.neo.nodetypes;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class DatabaseJob extends Job {

    private GraphDatabaseService graphDb;

    private Transaction tx;

    /**
     * @param name
     */
    public DatabaseJob(String name) {
        super(name);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        graphDb = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();

        tx = graphDb.beginTx();

        try {
            doJob(monitor);
            tx.success();
        } catch (Exception e) {
            tx.failure();

            return Status.CANCEL_STATUS;
        } finally {
            tx.finish();
        }

        return Status.OK_STATUS;
    }

    protected abstract void doJob(IProgressMonitor monitor) throws Exception;

}
