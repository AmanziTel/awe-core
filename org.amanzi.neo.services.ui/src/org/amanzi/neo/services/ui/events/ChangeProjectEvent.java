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

package org.amanzi.neo.services.ui.events;

/**
 * <p>
 * CHANGE_PROJECT Event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChangeProjectEvent extends AbstractEvent {
    /**
     * changed projectName
     */
    private String projectName;

    /**
     * initialize event with required parameters
     * 
     * @param projectName
     */
    public ChangeProjectEvent(String projectName) {
        this.projectName = projectName;
        type = EventsType.CHANGE_PROJECT;
    }

    public String getProjectName() {
        return projectName;
    }

}
