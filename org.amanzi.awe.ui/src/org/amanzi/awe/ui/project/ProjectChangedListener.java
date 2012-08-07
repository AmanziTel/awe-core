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

package org.amanzi.awe.ui.project;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ProjectNameChangedEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectChangedListener implements IAWEEventListenter {

    private static final Logger LOGGER = Logger.getLogger(ProjectChangedListener.class);

    private IProjectModelProvider projectModelProvider;

    protected IProjectModelProvider getProjectModelProvider() {
        if (projectModelProvider == null) {
            projectModelProvider = AWEUIPlugin.getDefault().getProjectModelProvider();
        }
        return projectModelProvider;
    }

    protected ProjectChangedListener(final IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void onEvent(final IEvent event) {
        switch (event.getStatus()) {
        case PROJECT_CHANGED:
            if (event instanceof ProjectNameChangedEvent) {
                onProjectChangedEvent((ProjectNameChangedEvent)event);
            }
            break;
        default:
            // do nothing
            break;
        }
    }

    private void onProjectChangedEvent(final ProjectNameChangedEvent event) {
        String name = event.getNewProjectName();

        try {
            IProjectModel projectModel = getProjectModelProvider().findProjectByName(name);
            if (projectModel == null) {
                projectModel = getProjectModelProvider().createProjectModel(name);
                AWEEventManager.getManager().fireDataUpdatedEvent();
            }

            getProjectModelProvider().setActiveProjectModel(projectModel);
        } catch (ModelException e) {
            LOGGER.error("Error on switching active UDIG Proejct", e);
        }
    }

}
