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

import java.util.Iterator;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.impl.dto.DataElement;
import org.amanzi.neo.models.IDataModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDataModel extends AbstractModel implements IDataModel {

    private static final Logger LOGGER = Logger.getLogger(AbstractDataModel.class);

    /**
     * @param nodeService
     */
    public AbstractDataModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    @Override
    public IDataElement getParentElement(final IDataElement childElement) throws ModelException {
        assert childElement != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getParentElement", childElement));
        }

        DataElement result = null;

        try {
            final Node childNode = ((DataElement)childElement).getNode();

            final Node parentNode = getParent(childNode);

            if (parentNode != null) {
                final INodeType nodeType = getNodeService().getNodeType(parentNode);

                if (nodeType.equals(getType()) && parentNode.equals(getRootNode())) {
                    result = new DataElement(parentNode);

                    result.setName(getNodeService().getNodeName(parentNode));
                    result.setNodeType(nodeType);
                }
            }
        } catch (final ServiceException e) {
            processException("An error occured on searching for a Parent Element", e);
        } catch (final NodeTypeNotExistsException e) {
            processException("An error occured on initializing child element", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getParentElement"));
        }

        return result;
    }

    @Override
    public Iterable<IDataElement> getChildren(final IDataElement parentElement) throws ModelException {
        assert parentElement != null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getChildren", parentElement));
        }
        final Node parentNode = ((DataElement)parentElement).getNode();
        Iterator<Node> childs = null;
        try {
            childs = getNodeService().getChildren(parentNode);
        } catch (final ServiceException e) {
            processException("Service exception found", e);
        }
        return new DataElementIterator(childs).toIterable();
    }

    @Override
    public void deleteElement(final IDataElement element) throws ModelException {
        assert element != null;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("deleteElement", element));
        }

        final Node parentNode = ((DataElement)element).getNode();
        try {
            getNodeService().deleteChain(parentNode);
        } catch (final ServiceException e) {
            processException("Can't delete element" + e, e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("deleteElement"));
        }
    }
}
