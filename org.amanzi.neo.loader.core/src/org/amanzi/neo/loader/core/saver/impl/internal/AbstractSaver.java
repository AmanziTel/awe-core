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

package org.amanzi.neo.loader.core.saver.impl.internal;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.ui.events.impl.ShowGISOnMap;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.manager.EventChain;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.LoaderException;
import org.amanzi.neo.loader.core.exception.impl.UnderlyingModelException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSaver<C extends IConfiguration, D extends IData> implements ISaver<C, D> {

    private static final int MAX_TX_COUNT = 2000;

    private static final Logger LOGGER = Logger.getLogger(AbstractSaver.class);

    private C configuration;

    private final List<IModel> processedModels = new ArrayList<IModel>();

    private final IProjectModelProvider projectModelProvider;

    private IProjectModel currentProject;

    private Transaction tx;

    private int txCount;

    protected AbstractSaver(final IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void init(final C configuration) throws ModelException {
        this.configuration = configuration;

        this.currentProject = projectModelProvider.getActiveProjectModel();

        DatabaseManagerFactory.getDatabaseManager().startThreadTransaction();
        tx = DatabaseManagerFactory.getDatabaseManager().getDatabaseService().beginTx();
    }

    @Override
    public void finishUp() {
        saveTx(true, true);

        EventChain eventChain = new EventChain(true);
        eventChain.addEvent(AWEEventManager.DATA_UPDATED_EVENT);

        for (IModel model : processedModels) {
            try {
                model.finishUp();

                if (model.isRenderable()) {
                    for (IGISModel gisModel : ((IRenderableModel)model).getAllGIS()) {
                        eventChain.addEvent(new ShowGISOnMap(gisModel));
                    }
                }
            } catch (ModelException e) {
                LOGGER.error("an exception occured on finishing up a saver", e);
            }
        }
        saveTx(true, false);

        AWEEventManager.getManager().fireEventChain(eventChain);
    }

    @Override
    public void save(final D data) throws LoaderException {
        try {
            saveInModel(data);
            updateTransaction();
        } catch (ParameterInconsistencyException e) {
            LOGGER.error(e.getParameterName());
        } catch (ModelException e) {
            saveTx(false, true);
            throw new UnderlyingModelException(e);
        }
    }

    protected void updateTransaction() {
        txCount++;
        if (txCount > MAX_TX_COUNT) {
            txCount = 0;
            saveTx(true, true);
        }
    }

    protected void saveTx(final boolean success, final boolean shouldContinue) {
        if (success) {
            tx.success();
        } else {
            tx.failure();
        }

        tx.finish();
        if (shouldContinue) {
            tx = DatabaseManagerFactory.getDatabaseManager().getDatabaseService().beginTx();
        }
    }

    protected abstract void saveInModel(D data) throws ModelException;

    protected C getConfiguration() {
        return configuration;
    }

    protected void addProcessedModel(final IModel model) {
        this.processedModels.add(model);
    }

    protected IProjectModel getCurrentProject() {
        return currentProject;
    }

}
