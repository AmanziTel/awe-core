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

package org.amanzi.neo.providers.impl;

import java.util.Iterator;

import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.models.render.impl.GISModel;
import org.amanzi.neo.models.render.impl.GISNodeType;
import org.amanzi.neo.models.render.impl.GISRelationType;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IGISModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractNamedModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class GISModelProvider extends AbstractNamedModelProvider<IGISModel, IRenderableModel, GISModel>
        implements
            IGISModelProvider {

    private final IGeoNodeProperties geoNodeProperties;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public GISModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IGeoNodeProperties geoNodeProperties) {
        super(nodeService, generalNodeProperties);
        this.geoNodeProperties = geoNodeProperties;
    }

    @Override
    protected INodeType getModelType() {
        return GISNodeType.GIS;
    }

    @Override
    protected GISModel createInstance() {
        return new GISModel(getNodeService(), getGeneralNodeProperties(), geoNodeProperties);
    }

    @Override
    protected Class< ? extends IGISModel> getModelClass() {
        return GISModel.class;
    }

    @Override
    protected void postInitialize(final GISModel model, final IRenderableModel parent) {
        model.setSourceModel(parent);
    }

    @Override
    protected Iterator<Node> getNodeIterator(Node parent, INodeType nodeType) throws ServiceException {
        return getNodeService().getChildren(parent, getModelType(), GISRelationType.GIS);
    }

    @Override
    protected Node getNodeByName(Node rootNode, String name, INodeType modelType) throws ServiceException {
        return getNodeService().getChildByName(rootNode, name, getModelType(), GISRelationType.GIS);
    }

}
