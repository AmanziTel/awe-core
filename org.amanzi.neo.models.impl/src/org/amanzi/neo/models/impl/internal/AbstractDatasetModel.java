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

package org.amanzi.neo.models.impl.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.impl.util.AbstractDataElementIterator;
import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDatasetModel extends AbstractNamedModel implements IPropertyStatisticalModel, IRenderableModel {

    protected final class LocationIterator extends AbstractDataElementIterator<ILocationElement> {

        /**
         * @param nodeIterator
         */
        public LocationIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected ILocationElement createDataElement(final Node node) {
            return getLocationElement(node);
        }

    }

    private IIndexModel indexModel;

    private IPropertyStatisticsModel propertyStatisticsModel;

    private final IGeoNodeProperties geoNodeProperties;

    private final List<IGISModel> gisModels = new ArrayList<IGISModel>();

    private IGISModel mainGISModel;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public AbstractDatasetModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperties) {
        super(nodeService, generalNodeProperties);
        this.geoNodeProperties = geoNodeProperties;
    }

    @Override
    public void finishUp() throws ModelException {
        assert indexModel != null;
        assert propertyStatisticsModel != null;

        indexModel.finishUp();
        propertyStatisticsModel.finishUp();

        for (IGISModel gisModel : gisModels) {
            gisModel.finishUp();
        }
    }

    /**
     * @param indexModel The indexModel to set.
     */
    public void setIndexModel(final IIndexModel indexModel) {
        this.indexModel = indexModel;
    }

    /**
     * @param propertyStatisticsModel The propertyStatisticsModel to set.
     */
    public void setPropertyStatisticsModel(final IPropertyStatisticsModel propertyStatisticsModel) {
        this.propertyStatisticsModel = propertyStatisticsModel;
    }

    protected IIndexModel getIndexModel() {
        return indexModel;
    }

    protected IPropertyStatisticsModel getPropertyStatisticsModel() {
        return propertyStatisticsModel;
    }

    protected IGeoNodeProperties getGeoNodeProperties() {
        return geoNodeProperties;
    }

    @Override
    public IGISModel getMainGIS() {
        return mainGISModel;
    }

    @Override
    public List<IGISModel> getAllGIS() {
        return gisModels;
    }

    protected void updateLocation(final double latitude, final double longitude) {
        for (IGISModel gis : getAllGIS()) {
            gis.updateBounds(latitude, longitude);
        }
    }

    @Override
    public void addGISModel(final IGISModel model) {
        if (!gisModels.contains(model)) {
            gisModels.add(model);
        }
    }

    public void setMainGISModel(final IGISModel model) {
        mainGISModel = model;
        addGISModel(mainGISModel);
    }

    @Override
    public IPropertyStatisticsModel getPropertyStatistics() {
        return propertyStatisticsModel;
    }

    protected abstract ILocationElement getLocationElement(Node node);

}
