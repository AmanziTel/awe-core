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

package org.amanzi.neo.services.listeners;

/**
 * <p>
 * Event fired when project was changed
 * </p>
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class UpdateProjectDataEvent extends AbstractUIEvent {
    
    private String projectName;
    
    public UpdateProjectDataEvent() {}
    
    public UpdateProjectDataEvent(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }    

    @Override
    public AbstractUIEventType getType() {
        return AbstractUIEventType.PROJECT_CHANGED;
    }
}