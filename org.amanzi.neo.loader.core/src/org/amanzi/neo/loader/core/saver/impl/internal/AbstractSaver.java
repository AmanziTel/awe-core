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

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractSaver<C extends IConfiguration, D extends IData> implements ISaver<C, D> {

    private static final Logger LOGGER = Logger.getLogger(AbstractSaver.class);

    private C configuration;

    private final List<IModel> processedModels = new ArrayList<IModel>();

    /**
     * 
     */
    public AbstractSaver() {
    }

    @Override
    public void init(C configuration) {
        this.configuration = configuration;
    }

    @Override
    public void finishUp() {
        for (IModel model : processedModels) {
            try {
                model.finishUp();
            } catch (ModelException e) {
                LOGGER.error("an exception occured on finishing up a saver", e);
            }
        }
    }

    protected C getConfiguration() {
        return configuration;
    }

    protected void addProcessedModel(IModel model) {
        this.processedModels.add(model);
    }

}
