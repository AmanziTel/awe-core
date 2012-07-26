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

package org.amanzi.neo.providers.impl.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.impl.internal.AbstractNamedModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.internal.INamedModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.apache.commons.lang3.StringUtils;
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
public abstract class AbstractNamedModelProvider<M extends IModel, P extends IModel, C extends AbstractNamedModel>
        extends
            AbstractModelProvider<C, M> implements INamedModelProvider<M, P> {

    private static final Logger LOGGER = Logger.getLogger(AbstractNamedModelProvider.class);

    private final INodeService nodeService;

    private final IGeneralNodeProperties generalNodeProperties;

    protected AbstractNamedModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<M> findAll(final P parent) throws ModelException {
        assert parent != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findAll", parent));
        }

        List<M> result = new ArrayList<M>();

        if (parent instanceof AbstractModel) {
            AbstractModel parentModel = (AbstractModel)parent;

            try {
                Iterator<Node> modelNodes = getNodeIterator(parentModel.getRootNode(), getModelType());

                while (modelNodes.hasNext()) {
                    C model = createInstance();
                    model.initialize(modelNodes.next());

                    postInitialize(model);

                    result.add((M)model);
                }
            } catch (ServiceException e) {
                processException("Error on search for all models by type <" + getModelType() + ">", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findByName"));
        }

        return result;
    }

    protected Iterator<Node> getNodeIterator(Node parent, INodeType nodeType) throws ServiceException {
        return nodeService.getChildren(parent, getModelType(), NodeServiceRelationshipType.CHILD);
    }

    protected Node getNodeByName(Node rootNode, String name, INodeType modelType) throws ServiceException {
        return nodeService.getChildByName(rootNode, name, getModelType(), NodeServiceRelationshipType.CHILD);
    }

    @SuppressWarnings("unchecked")
    @Override
    public M findByName(final P parent, final String name) throws ModelException {
        assert parent != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findByName", parent, name));
        }

        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException(generalNodeProperties.getNodeNameProperty(), name);
        }

        IKey key = new NameKey(name);

        C result = getFromCache(key);

        if ((result == null) && (parent instanceof AbstractModel)) {
            AbstractModel parentModel = (AbstractModel)parent;

            try {
                Node modelNode = getNodeByName(parentModel.getRootNode(), name, getModelType());

                if (modelNode != null) {
                    result = createInstance();
                    result.initialize(modelNode);

                    postInitialize(result, parent);

                    addToCache(result, key);
                }
            } catch (ServiceException e) {
                processException("Error on searching for a model <" + getModelType() + "> by name <" + name + ">", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findByName"));
        }

        return (M)result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public M create(final P parent, final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("create", parent, name));
        }
        // verify input parameters
        assert parent != null;

        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException(generalNodeProperties.getNodeNameProperty(), name);
        }

        if (findByName(parent, name) != null) {
            throw new DuplicatedModelException(getModelClass(), generalNodeProperties.getNodeNameProperty(), name);
        }

        M result = null;

        if (parent instanceof AbstractModel) {
            AbstractModel parentModel = (AbstractModel)parent;

            C resultModel = createInstance();
            resultModel.initialize(parentModel.getRootNode(), name);

            postInitialize(resultModel, parent);

            result = (M)resultModel;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("create"));
        }

        return result;
    }

    protected abstract INodeType getModelType();

    protected INodeService getNodeService() {
        return nodeService;
    }

    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    protected void postInitialize(final C model, final P parent) throws ModelException {
        postInitialize(model);
    }

}
