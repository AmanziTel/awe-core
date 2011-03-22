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

package org.amanzi.awe.ui;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.AweProjectNode;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Node;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class UiService  {
    /**
     * Gets the active project name.
     *
     * @return the active project name
     */
    public String getActiveProjectName() {
        IMap map = ApplicationGIS.getActiveMap();
        return map == ApplicationGIS.NO_MAP ? ApplicationGIS.getActiveProject().getName() : map.getProject().getName();
    }
    /**
     * Gets the active project name.
     *
     * @return the active project name
     */

    public Node getActiveProjectNode() {
        AweProjectNode nodeWr = NeoServiceFactory.getInstance().getProjectService().findAweProject(getActiveProjectName());
        return nodeWr==null?null:nodeWr.getUnderlyingNode();
    }

    /**
     * Define CoordinateReferenceSystem.
     * 
     * @param lat the lattitude
     * @param lon the longitude
     * @param hint
     * @return the coordinate reference system
     */
    public CoordinateReferenceSystem defineCRS(Double lat, Double lon, String hint) {
        if (lat == 0 || lon == null) {
            return null;
        }
        String epsg = "EPSG:4326";
        if ((lat > 90 || lat < -90) && (lon > 180 || lon < -180)) {
            if ("germany".equals(hint)) {
                epsg = "EPSG:31467";
            } else {
                epsg = "EPSG:3021";
            }
        }
        try {
            return CRS.decode(epsg);
        } catch (NoSuchAuthorityCodeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
