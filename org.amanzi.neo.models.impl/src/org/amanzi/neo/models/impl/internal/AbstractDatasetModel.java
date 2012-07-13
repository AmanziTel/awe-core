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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDatasetModel extends AbstractNamedModel implements IPropertyStatisticalModel {

    private IIndexModel indexModel;

    private IPropertyStatisticsModel propertyStatisticsModel;

    private final IGeoNodeProperties geoNodeProperties;

    private final Map<INodeType, MultiPropertyIndex< ? >> indexMap = new HashMap<INodeType, MultiPropertyIndex< ? >>();

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

        for (MultiPropertyIndex< ? > index : indexMap.values()) {
            index.finishUp();
        }

        indexModel.finishUp();
        propertyStatisticsModel.finishUp();
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

    protected void registerMultiPropertyIndexes(final INodeType nodeType, final String... propertyNames) {
        indexMap.put(nodeType, indexModel.getMultiPropertyIndex(nodeType, getRootNode(), propertyNames));
    }

    protected void index(final INodeType nodeType, final Node node) {
        MultiPropertyIndex< ? > index = indexMap.get(nodeType);

        if (index != null) {
            index.add(node);
        }
    }

    protected IGeoNodeProperties getGeoNodeProperties() {
        return geoNodeProperties;
    }

    public abstract void initializeIndexes();

}
