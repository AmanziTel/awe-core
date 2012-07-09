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

import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.project.ProjectModel;
import org.amanzi.neo.models.impl.project.ProjectModelNodeType;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
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
public class ProjectModelProvider extends AbstractModelProvider<ProjectModel, IProjectModel> implements IProjectModelProvider {

    private static final Logger LOGGER = Logger.getLogger(ProjectModelProvider.class);

    private final INodeService nodeService;

    private final IGeneralNodeProperties generalNodeProperties;

    private IProjectModel activeProject;

    public ProjectModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
    }

    @Override
    public IProjectModel findProjectByName(final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("findProjectByName", name));
        }

        // validate arguments
        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException(generalNodeProperties.getNodeNameProperty(), name);
        }

        IKey cacheKey = new NameKey(name);

        // check cache
        IProjectModel projectModel = getFromCache(cacheKey);

        if (projectModel == null) {
            Node modelNode = null;

            try {
                Node referencedNode = nodeService.getReferencedNode();
                modelNode = nodeService.getChildByName(referencedNode, name, ProjectModelNodeType.PROJECT);
            } catch (ServiceException e) {
                processException("Error on Searching for a Project Model", e);
            }

            if (modelNode != null) {
                ProjectModel model = createInstance();
                model.initialize(modelNode);

                addToCache(model, cacheKey);

                projectModel = model;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("findProjectByName"));
        }

        return projectModel;
    }

    @Override
    public IProjectModel createProjectModel(final String name) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createProjectModel", name));
        }

        // validate arguments
        if (StringUtils.isEmpty(name)) {
            throw new ParameterInconsistencyException(generalNodeProperties.getNodeNameProperty(), name);
        }

        // validate uniqueness
        if (findProjectByName(name) != null) {
            throw new DuplicatedModelException(IProjectModel.class, generalNodeProperties.getNodeNameProperty(), name);
        }

        ProjectModel projectModel = createInstance();
        projectModel.initialize(name);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createProjectModel"));
        }

        return projectModel;
    }

    @Override
    public IProjectModel getActiveProjectModel() {
        return activeProject;
    }

    @Override
    public void setActiveProjectModel(final IProjectModel projectModel) {
        assert projectModel != null;

        activeProject = projectModel;
    }

    @Override
    protected ProjectModel createInstance() {
        return new ProjectModel(nodeService, generalNodeProperties);
    }

    @Override
    protected Class< ? extends IProjectModel> getModelClass() {
        return ProjectModel.class;
    }
}
