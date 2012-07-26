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

package org.amanzi.neo.providers.impl.internal;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.internal.IDatasetModel;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.providers.IGISModelProvider;
import org.amanzi.neo.providers.IIndexModelProvider;
import org.amanzi.neo.providers.IPropertyStatisticsModelProvider;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDatasetModelProvider<M extends IDatasetModel, P extends IModel, C extends AbstractDatasetModel>
        extends
            AbstractNamedModelProvider<M, P, C> {

    private final IIndexModelProvider indexModelProvider;

    private final IPropertyStatisticsModelProvider propertyStatisticsModelProvider;

    private final IGeoNodeProperties geoNodeProperties;

    private final IGISModelProvider gisModelProvider;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    protected AbstractDatasetModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IIndexModelProvider indexModelProvider, final IPropertyStatisticsModelProvider propertyStatisticsModelProvider,
            final IGeoNodeProperties geoNodeProperties, final IGISModelProvider gisModelProvider) {
        super(nodeService, generalNodeProperties);
        this.indexModelProvider = indexModelProvider;
        this.propertyStatisticsModelProvider = propertyStatisticsModelProvider;
        this.geoNodeProperties = geoNodeProperties;
        this.gisModelProvider = gisModelProvider;
    }

    @Override
    protected void postInitialize(final C model) throws ModelException {
        super.postInitialize(model);

        initializeIndexes(model);
        initializePropertyStatistics(model);
        initializeGIS(model);
    }

    private void initializeGIS(final C model) throws ModelException {
        // main GIS model
        IGISModel mainModel = gisModelProvider.findByName(model, model.getName());
        if (mainModel == null) {
            mainModel = gisModelProvider.create(model, model.getName());
        }
        model.setMainGISModel(mainModel);

        // all other models
        for (IGISModel gisModel : gisModelProvider.findAll(model)) {
            model.addGISModel(gisModel);
        }
    }

    private void initializePropertyStatistics(final C model) throws ModelException {
        IPropertyStatisticsModel statisticsModel = propertyStatisticsModelProvider.getPropertyStatistics(model);
        model.setPropertyStatisticsModel(statisticsModel);
    }

    private void initializeIndexes(final C model) throws ModelException {
        IIndexModel indexModel = indexModelProvider.getIndexModel(model);
        model.setIndexModel(indexModel);
    }

    protected IGeoNodeProperties getGeoNodeProperties() {
        return geoNodeProperties;
    }

}
