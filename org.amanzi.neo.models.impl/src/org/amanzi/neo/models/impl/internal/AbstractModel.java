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

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.DataInconsistencyException;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.util.AbstractLoggable;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DuplicatedNodeException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Implements the basic methods of all the models.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractModel extends AbstractLoggable implements IModel {
    /** String INITIALIZE_METHOD_NAME field */
    protected static final String INITIALIZE_METHOD_NAME = "initialize";

    private static final Logger LOGGER = Logger.getLogger(AbstractModel.class);

    private String name;
    private Node rootNode;
    private INodeType nodeType;
    private Node parentNode;

    private final INodeService nodeService;
    private final IGeneralNodeProperties generalNodeProperties;

    public AbstractModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
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
            rootNode = nodeService.createNode(parentNode, nodeType, NodeService.NodeServiceRelationshipType.CHILD, name);
        } catch (ServiceException e) {
            processException("Error on initializing new node for Model", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
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
            parentNode = nodeService.getParent(rootNode);
        } catch (ServiceException e) {
            processException("An error occured on Model Initialization", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
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

    public void setRootNode(final Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getParentNode() {
        return parentNode;
    }

    protected void processException(final String logMessage, final ServiceException e) throws ModelException {
        LOGGER.error(logMessage, e);

        switch (e.getReason()) {
        case DATABASE_EXCEPTION:
            throw new FatalException(e);
        case PROPERTY_NOT_FOUND:
        case INCORRECT_PARENT:
        case INCORRECT_PROPERTY:
            throw new DataInconsistencyException(e);
        case DUPLICATED_NODE:
            DuplicatedNodeException error = (DuplicatedNodeException)e;
            throw new DuplicatedModelException(getClass(), error.getPropertyName(), error.getDuplicatedValue());
        default:
            // do nothing
        }
    }

    protected INodeService getNodeService() {
        return nodeService;
    }

    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

}
