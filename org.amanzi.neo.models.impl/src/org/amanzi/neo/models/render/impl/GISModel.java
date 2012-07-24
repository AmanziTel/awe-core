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
import org.amanzi.neo.models.impl.internal.util.CRSWrapper;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class GISModel extends AbstractNamedModel implements IGISModel {

    private static final Logger LOGGER = Logger.getLogger(GISModel.class);

    private double minLatitude;

    private double maxLatitude;

    private double minLongitude;

    private double maxLongitude;

    private final IGeoNodeProperties geoNodeProperties;

    private IRenderableModel sourceModel;

    private CoordinateReferenceSystem crs;

    private String crsCode;

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
    public void initialize(final Node rootNode) throws ModelException {
        super.initialize(rootNode);

        try {
            maxLatitude = getNodeService().getNodeProperty(rootNode, geoNodeProperties.getMaxLatitudeProperty(), null, true);
            minLatitude = getNodeService().getNodeProperty(rootNode, geoNodeProperties.getMinLatitudeProperty(), null, true);
            maxLongitude = getNodeService().getNodeProperty(rootNode, geoNodeProperties.getMaxLongitudeProperty(), null, true);
            minLongitude = getNodeService().getNodeProperty(rootNode, geoNodeProperties.getMinLongitudeProperty(), null, true);

            String crsCodeValue = getNodeService().getNodeProperty(rootNode, geoNodeProperties.getCRSProperty(), null, true);
            setCRS(crsCodeValue);
        } catch (ServiceException e) {
            processException("Cannot get GIS-related properties from Node", e);
        }
    }

    @Override
    public void finishUp() throws ModelException {
        try {
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMaxLatitudeProperty(), maxLatitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMaxLongitudeProperty(), maxLongitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMinLatitudeProperty(), minLatitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getMinLongitudeProperty(), minLongitude);
            getNodeService().updateProperty(getRootNode(), geoNodeProperties.getCRSProperty(), getCRSCode());
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

        if (getCRS() == null) {
            CRSWrapper wrapper = CRSWrapper.fromLocation(latitude, longitude, getName());
            setCRS(wrapper.getEpsg());
        }
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

    @Override
    public CoordinateReferenceSystem getCRS() {
        return crs;
    }

    protected String getCRSCode() {
        return crsCode;
    }

    protected void setCRS(final String crsCode) {
        try {
            this.crsCode = crsCode;
            crs = CRS.decode(crsCode);
        } catch (FactoryException e) {
            LOGGER.error("Cannot determinate CRS", e);
        }
    }

    @Override
    public IRenderableModel getSourceModel() {
        return sourceModel;
    }

    /**
     * @return Returns the minLatitude.
     */
    @Override
    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * @return Returns the maxLatitude.
     */
    @Override
    public double getMaxLatitude() {
        return maxLatitude;
    }

    /**
     * @return Returns the minLongitude.
     */
    @Override
    public double getMinLongitude() {
        return minLongitude;
    }

    /**
     * @return Returns the maxLongitude.
     */
    @Override
    public double getMaxLongitude() {
        return maxLongitude;
    }

}
