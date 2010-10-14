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

package org.amanzi.neo.services.ui;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;

/**
 * <p>
 *Service for provide handling both gui and neo4j operations
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class UiService{
    private DatasetService commonService;

    UiService() {
        super();
        commonService=NeoServiceFactory.getInstance().getDatasetService();
    }
    
    /**
     * Gets the active project name.
     *
     * @return the active project name
     */
    public static String getActiveProjectName() {
        IMap map = ApplicationGIS.getActiveMap();
        return map == ApplicationGIS.NO_MAP ? ApplicationGIS.getActiveProject().getName() : map.getProject().getName();
    }
}
