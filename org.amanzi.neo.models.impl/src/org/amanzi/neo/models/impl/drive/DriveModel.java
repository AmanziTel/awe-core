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

package org.amanzi.neo.models.impl.drive;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.impl.dto.LocationElement;
import org.amanzi.neo.models.drive.DriveNodeType;
import org.amanzi.neo.models.drive.IDriveModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractDatasetModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.neo4j.graphdb.Node;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveModel extends AbstractDatasetModel implements IDriveModel {

    private int locationCount;

    /**
     * @param nodeService
     * @param generalNodeProperties
     * @param geoNodeProperties
     */
    public DriveModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperties) {
        super(nodeService, generalNodeProperties, geoNodeProperties);
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound) throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<ILocationElement> getElements(final Envelope bound, final IFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRenderableElementCount() {
        return locationCount;
    }

    @Override
    protected ILocationElement getLocationElement(final Node node) {
        LocationElement location = new LocationElement(node);

        location.setNodeType(DriveNodeType.MP);

        return location;
    }

    @Override
    protected INodeType getModelType() {
        return DriveNodeType.DRIVE;
    }

}
