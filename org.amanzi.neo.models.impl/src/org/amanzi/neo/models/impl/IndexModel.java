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

package org.amanzi.neo.models.impl;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IIndexService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexModel extends AbstractModel implements IIndexModel {

    private static final Logger LOGGER = Logger.getLogger(IndexModel.class);

    private final IIndexService indexService;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public IndexModel(final IGeneralNodeProperties generalNodeProperties, final IIndexService indexService) {
        super(null, generalNodeProperties);
        this.indexService = indexService;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        setRootNode(rootNode);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    @Override
    public void finishUp() throws ModelException {

    }

    @Override
    public Node getSingleNode(final INodeType nodeType, final String propertyName, final Object value) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getSingleNode", nodeType, propertyName, value));
        }

        try {
            Index<Node> index = indexService.getIndex(getRootNode(), nodeType);

            return index.get(propertyName, value).getSingle();
        } catch (ServiceException e) {
            processException("Exception on searching for a Node in Index", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getSingleNode"));
        }

        return null;
    }

    @Override
    public void index(final INodeType nodeType, final Node node, final String propertyName, final Object value)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("index", nodeType, node, propertyName, value));
        }

        try {
            indexService.addToIndex(getRootNode(), nodeType, node, propertyName, value);
        } catch (ServiceException e) {
            processException("Exception on indexing Node", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("index"));
        }
    }

    @Override
    public void indexInMultiProperty(final INodeType nodeType, final Node node) {
        // TODO Auto-generated method stub

    }

}
