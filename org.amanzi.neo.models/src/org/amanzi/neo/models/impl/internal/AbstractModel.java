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
public abstract class AbstractModel implements IModel {

    private static final Logger LOGGER = Logger.getLogger(AbstractModel.class);

    protected String name;
    protected Node rootNode;
    protected INodeType nodeType;

    protected INodeService nodeService;

    public AbstractModel(INodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void initialize(long rootNodeId) throws ModelException {

    }

    public void initialize(Node rootNode) throws ModelException {
        assert rootNode == null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start initialize(<" + rootNode + ">)");
        }

        try {
            name = nodeService.getNodeName(rootNode);
            nodeType = nodeService.getNodeType(rootNode);
        } catch (ServiceException e) {
            LOGGER.error("An error occured on Model Initialization", e);

            ModelException exception = processInitializeException(e);
            if (exception != null) {
                throw exception;
            }
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
        return getName();
    }

    public Node getRootNode() {
        return rootNode;
    }

    private ModelException processInitializeException(ServiceException e) {
        switch (e.getReason()) {
        case DATABASE_EXCEPTION:
            return new FatalException(e);
        case PROPERTY_NOT_FOUND:
            return new DataInconsistencyException(e);
        }

        return null;
    }

}
