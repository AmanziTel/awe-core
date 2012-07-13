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

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;
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
public abstract class AbstractNamedModel extends AbstractDataModel {

    private static final Logger LOGGER = Logger.getLogger(AbstractNamedModel.class);

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public AbstractNamedModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    public void initialize(final Node parentNode, final String name) throws ModelException {
        assert name != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, name));
        }

        initialize(parentNode, name, getModelType());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
    }

    protected abstract INodeType getModelType();

}
