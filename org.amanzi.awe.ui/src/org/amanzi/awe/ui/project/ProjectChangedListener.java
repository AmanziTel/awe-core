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

import org.amanzi.awe.ui.Activator;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.ProjectNameChangedEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.exceptions.ModelException;
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

    private final IProjectModelProvider projectModelProvider;

    /**
     * 
     */
    public ProjectChangedListener() {
        this(Activator.getDefault().getProjectModelProvider());
    }

    protected ProjectChangedListener(IProjectModelProvider projectModelProvider) {
        this.projectModelProvider = projectModelProvider;
    }

    @Override
    public void onEvent(IEvent event) {
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

    private void onProjectChangedEvent(ProjectNameChangedEvent event) {
        String name = event.getNewProjectName();

        try {
            IProjectModel projectModel = projectModelProvider.findProjectByName(name);
            if (projectModel == null) {
                projectModel = projectModelProvider.createProjectModel(name);
            }

            projectModelProvider.setActiveProjectModel(projectModel);
        } catch (ModelException e) {
            LOGGER.error("Error on switching active UDIG Proejct", e);
        }
    }

}
