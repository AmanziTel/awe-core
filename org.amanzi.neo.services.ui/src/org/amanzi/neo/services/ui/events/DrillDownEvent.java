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

import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IProjectModel;

/**
 * <p>
 * DRILL_DOWN event
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DrillDownEvent extends AbstractEvent {

    /**
     * current project model
     */
    private IProjectModel projectModel;
    /**
     * selected data element
     */
    private IDataElement selectedDataElement;

    /**
     * initialize event class with necessary parameters
     */
    public DrillDownEvent(IProjectModel projectModel, IDataElement selectedDataElement) {
        type = EventsType.DRILL_DOWN;
        this.projectModel = projectModel;
        this.selectedDataElement = selectedDataElement;
    }

    public IProjectModel getProjectModel() {
        return projectModel;
    }

    public IDataElement getSelectedDataElement() {
        return selectedDataElement;
    }

}
