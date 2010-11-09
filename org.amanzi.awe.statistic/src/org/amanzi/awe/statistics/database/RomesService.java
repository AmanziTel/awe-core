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

package org.amanzi.awe.statistics.database;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class RomesService extends DriveDatasetService {

    /**
     * @param service
     * @param dataset
     */
    public RomesService(GraphDatabaseService service, Node dataset) {
        super(service, dataset);
    }

    @Override
    public String getKeyProperty(Node node) {
        if (node.hasProperty(INeoConstants.SECTOR_ID_PROPERTIES)){
            return node.getProperty(INeoConstants.SECTOR_ID_PROPERTIES).toString();
        }
        Relationship rel = node.getSingleRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING);
        Object keyProperty = rel.getEndNode().getProperty(INeoConstants.SECTOR_ID_PROPERTIES);
        return keyProperty == null ? null : keyProperty.toString();
    }

    @Override
    public boolean isDatasetCorrelated() {
        return false;
    }

}
