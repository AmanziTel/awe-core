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
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.util.AbstractLoggable;
import org.amanzi.neo.models.render.IRenderableModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DuplicatedNodeException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * <p>
 * Implements the basic methods of all the models.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractModel extends AbstractLoggable implements IModel {

    private static final Logger LOGGER = Logger.getLogger(AbstractModel.class);

    /** String INITIALIZE_METHOD_NAME field */
    protected static final String INITIALIZE_METHOD_NAME = "initialize";

    protected final class DataElementIterator extends AbstractDataElementIterator<IDataElement> {
        String defaultProperty = getGeneralNodeProperties().getNodeNameProperty();

        /**
         * @param nodeIterator
         */
        public DataElementIterator(final Iterator<Node> nodeIterator) {
            super(nodeIterator);
        }

        public DataElementIterator(final Iterator<Node> nodeIterator, final String propertyName) {
            this(nodeIterator);
            this.defaultProperty = propertyName;
        }

        @Override
        protected IDataElement createDataElement(final Node node) {
            String name = null;
            INodeType type = null;

            try {
                name = getNodeService().getNodeProperty(node, defaultProperty, null, false);
                type = getNodeService().getNodeType(node);
            } catch (final Exception e) {
                LOGGER.info("can't get required property from node " + node);
                return null;
            }

            return getDataElement(node, type, name);
        }
    }

    private String name;
    private Node rootNode;
    private INodeType nodeType;
    private Node parentNode;

    private final INodeService nodeService;
    private final IGeneralNodeProperties generalNodeProperties;

    private IDataElement dataElement;

    protected AbstractModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
    }

    protected void initialize(final Node parentNode, final String name, final INodeType nodeType) throws ModelException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(name);
        assert nodeType != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, parentNode, name, nodeType));
        }

        this.name = name;
        this.nodeType = nodeType;
        this.parentNode = parentNode;

        try {
            rootNode = createNode(parentNode, nodeType, name);
        } catch (final ServiceException e) {
            processException("Error on initializing new node for Model", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
    }

    protected Node createNode(final Node parentNode, final INodeType nodeType, final String name) throws ServiceException {
        return nodeService.createNode(parentNode, nodeType, getRelationToParent(), name);
    }

    public void initialize(final Node rootNode) throws ModelException {
        assert rootNode != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, rootNode));
        }

        try {
            this.rootNode = rootNode;
            name = nodeService.getNodeName(rootNode);
            nodeType = nodeService.getNodeType(rootNode);
            parentNode = getParent(rootNode);
        } catch (final Exception e) {
            processException("An error occured on Model Initialization", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
    }

    protected Node getParent(final Node rootNode) throws ServiceException {
        return nodeService.getParent(rootNode, getRelationToParent());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public INodeType getType() {
        return nodeType;
    }

    @Override
    public String toString() {
        return "<" + getClass().getSimpleName() + "> " + getName();
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Node getParentNode() {
        return parentNode;
    }

    protected void processException(final String logMessage, final Exception e) throws ModelException {
        LOGGER.error(logMessage, e);

        if (e instanceof ServiceException) {
            final ServiceException serviceException = (ServiceException)e;
            switch (serviceException.getReason()) {
            case DATABASE_EXCEPTION:
                throw new FatalException(serviceException);
            case PROPERTY_NOT_FOUND:
            case INCORRECT_PARENT:
            case INCORRECT_PROPERTY:
                throw new DataInconsistencyException(serviceException);
            case DUPLICATED_NODE:
                final DuplicatedNodeException error = (DuplicatedNodeException)e;
                throw new DuplicatedModelException(getClass(), error.getPropertyName(), error.getDuplicatedValue());
            default:
                // do nothing
            }
        } else if (e instanceof NodeTypeNotExistsException) {
            throw new FatalException(e);
        } else {
            throw new RuntimeException(e);
        }
    }

    protected INodeService getNodeService() {
        return nodeService;
    }

    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    @Override
    public IDataElement asDataElement() {
        dataElement = dataElement == null ? rootNode == null ? null : toDataElement() : dataElement;

        return dataElement;
    }

    protected IDataElement toDataElement() {
        final DataElement result = new DataElement(rootNode);

        result.setNodeType(getType());
        result.setName(name);

        return result;
    }

    @Override
    public boolean isRenderable() {
        return this instanceof IRenderableModel;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof IModel) {
            final IModel model = (IModel)o;

            return model.asDataElement().equals(asDataElement());
        }

        return false;
    }

    protected IDataElement getDataElement(final Node node, final INodeType nodeType, final String name) {
        final DataElement result = new DataElement(node);

        result.setNodeType(nodeType);
        result.setName(name);

        return result;
    }

    protected RelationshipType getRelationToParent() {
        return NodeServiceRelationshipType.CHILD;
    }

    public void setRootNode(final Node rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public void flush() throws ModelException {
        // do nothing
    }

    @Override
    public void delete() throws ModelException {
        try {
            nodeService.deleteChain(rootNode);
        } catch (final ServiceException e) {
            processException("Can't delete node" + rootNode, e);
        }
    }
}
