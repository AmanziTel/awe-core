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

package org.amanzi.awe.nem.managers.network;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.apache.log4j.Logger;
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
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CopyNetworkJob extends Job {

    private static final Logger LOGGER = Logger.getLogger(NetworkElementManager.class);

    private INetworkModel sourceModel;

    private INetworkModel targetModel;

    private final GraphDatabaseService service = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();

    private Integer txCounter = 0;

    /**
     * @param name
     */
    public CopyNetworkJob(String name, INetworkModel sourceModel, INetworkModel targetModel) {
        super(name);
        this.sourceModel = sourceModel;
        this.targetModel = targetModel;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        Transaction tx = service.beginTx();
        try {
            for (IDataElement sourceElement : sourceModel.getChildren(sourceModel.asDataElement())) {
                IDataElement newElement = targetModel.createElement((INetworkElementType)sourceElement.getNodeType(),
                        targetModel.asDataElement(), sourceElement.getName(), sourceElement.asMap());
                txCounter++;
                buildInnerTree(tx, sourceElement, newElement, sourceModel, targetModel);
                commitTx(tx);
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
            LOGGER.error("can't get children from node" + sourceModel.getName(), e);
            return new Status(Status.ERROR, "org.amanzi.awe.nem.ui", "can't get children from node" + sourceModel.getName(), e);
        } finally {
            tx.finish();
            try {
                targetModel.finishUp();
                fireUpdate();
            } catch (ModelException e) {
                LOGGER.error("can't finishup");
            }

        }

        return Status.OK_STATUS;
    }

    private void fireUpdate() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                AWEEventManager.getManager().fireDataUpdatedEvent(null);
            }
        }, true);
    }

    protected void buildInnerTree(Transaction tx, IDataElement sourceElement, IDataElement newElement, INetworkModel model,
            INetworkModel newModel) throws ModelException {
        for (IDataElement children : model.getChildren(sourceElement)) {
            IDataElement newInner = newModel.createElement((INetworkElementType)children.getNodeType(), newElement,
                    children.getName(), children.asMap());
            txCounter++;
            commitTx(tx);
            buildInnerTree(tx, children, newInner, model, newModel);
        }

    }

    /**
     * @param txCounter
     * @param tx
     */
    private void commitTx(Transaction tx) {
        if (txCounter > 1000) {
            tx.success();
            txCounter = 0;
            tx = service.beginTx();
        }
    }

}
