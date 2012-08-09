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
import org.amanzi.neo.impl.util.AbstractDataElementIterator;
import org.amanzi.neo.models.IDataModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
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

    protected final class DataElementIterator extends AbstractDataElementIterator<IDataElement> {

        /**
         * @param nodeIterator
         */
        public DataElementIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        @Override
        protected IDataElement createDataElement(final Node node) {
            String name = null;
            INodeType type = null;

            try {
                name = getNodeService().getNodeName(node);
                type = getNodeService().getNodeType(node);
            } catch (Exception e) {
                LOGGER.error("can't get required property from node " + node, e);
                return null;
            }

            return getDataElement(node, type, name);
        }
    }

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

        IDataElement result = null;

        try {
            Node childNode = ((DataElement)childElement).getNode();

            Node parentNode = getParent(childNode);

            result = new DataElement(parentNode);
        } catch (ServiceException e) {
            processException("An error occured on searching for a Parent Element", e);
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
        Node parentNode = ((DataElement)parentElement).getNode();
        Iterator<Node> childs = null;
        try {
            childs = getNodeService().getChildren(parentNode);
        } catch (ServiceException e) {
            processException("Service exception found", e);
        }
        return new DataElementIterator(childs).toIterable();
    }
}
