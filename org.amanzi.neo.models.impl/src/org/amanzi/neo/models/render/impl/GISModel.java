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

package org.amanzi.neo.models.render.impl;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractNamedModel;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class GISModel extends AbstractNamedModel implements IGISModel {

    private double minLatitude;

    private double maxLatitude;

    private double minLongitude;

    private double maxLongitude;

    private final IGeoNodeProperties geoNodeProperties;

    private IRenderableModel sourceModel;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public GISModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperites) {
        super(nodeService, generalNodeProperties);

        geoNodeProperties = geoNodeProperites;
    }

    @Override
    public void finishUp() throws ModelException {
        try {
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMaxLatitudeProperty(), maxLatitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMaxLongitudeProperty(), maxLongitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMinLatitudeProperty(), minLatitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMinLongitudeProperty(), minLongitude);
        } catch (ServiceException e) {
            processException("Error on updating GIS Model", e);
        }
    }

    @Override
    public void updateBounds(final double latitude, final double longitude) {
        minLatitude = Math.min(latitude, minLatitude);
        maxLatitude = Math.max(latitude, maxLatitude);
        minLongitude = Math.min(longitude, minLongitude);
        maxLongitude = Math.max(longitude, maxLongitude);
    }

    @Override
    protected RelationshipType getRelationTypeToParent() {
        return GISRelationType.GIS;
    }

    @Override
    protected INodeType getModelType() {
        return GISNodeType.GIS;
    }

    @Override
    public void setSourceModel(final IRenderableModel sourceModel) {
        this.sourceModel = sourceModel;

    }

}
