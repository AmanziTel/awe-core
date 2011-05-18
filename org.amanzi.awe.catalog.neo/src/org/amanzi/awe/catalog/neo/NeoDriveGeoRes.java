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

package org.amanzi.awe.catalog.neo;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Wrapper for drive geo res
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class NeoDriveGeoRes extends NeoGeoResource {


    /**
     * Instantiates a new neo drive geo res.
     *
     * @param service the service
     * @param neo the neo
     * @param gisNode the gis node
     */
    public NeoDriveGeoRes(NeoService service, GraphDatabaseService neo, Node gisNode) {
        super(service, neo, gisNode);
    }

}
