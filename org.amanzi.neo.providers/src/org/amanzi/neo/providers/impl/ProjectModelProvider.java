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

import java.util.Set;

import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.ProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.INodeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectModelProvider extends AbstractModelProvider<ProjectModel> implements IProjectModelProvider {

    private static final Logger LOGGER = Logger.getLogger(ProjectModelProvider.class);

    private final INodeService nodeService;

    private final IGeneralNodeProperties generalNodeProperties;

    public ProjectModelProvider(INodeService nodeService, IGeneralNodeProperties generalNodeProperties) {
        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
    }

    @Override
    public Set<IProjectModel> findAllProjectModels() {
        return null;
    }

    @Override
    public IProjectModel findProjectByName(String name) {
        return null;
    }

    @Override
    public IProjectModel createProjectModel(String name) throws ModelException {
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
        return null;
    }

    @Override
    public void setActiveProjectModel(IProjectModel projectModel) {
    }

    @Override
    protected ProjectModel createInstance() {
        return new ProjectModel(nodeService);
    }
}
