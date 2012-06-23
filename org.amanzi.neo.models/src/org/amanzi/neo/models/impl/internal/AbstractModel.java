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
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.util.AbstractLoggable;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
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
    private static final Logger LOGGER = Logger.getLogger(AbstractModel.class);

    private String name;
    private Node rootNode;
    private INodeType nodeType;
    private Node parentNode;

    private final INodeService nodeService;

    public AbstractModel(INodeService nodeService) {
        this.nodeService = nodeService;
    }

    protected void initialize(Node parentNode, String name, INodeType nodeType) throws ModelException {

    }

    public void initialize(Node rootNode) throws ModelException {
        assert rootNode != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        try {
            this.rootNode = rootNode;
            this.name = nodeService.getNodeName(rootNode);
            this.nodeType = nodeService.getNodeType(rootNode);
            this.parentNode = nodeService.getParent(rootNode);
        } catch (ServiceException e) {
            processException("An error occured on Model Initialization", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
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

    public Node getParentNode() {
        return parentNode;
    }

    protected void processException(String logMessage, ServiceException e) throws ModelException {
        LOGGER.error(logMessage, e);

        switch (e.getReason()) {
        case DATABASE_EXCEPTION:
            throw new FatalException(e);
        case PROPERTY_NOT_FOUND:
            throw new DataInconsistencyException(e);
        }
    }

    protected INodeService getNodeService() {
        return nodeService;
    }

}
