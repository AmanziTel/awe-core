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

import java.util.Collection;
import java.util.List;

import org.amanzi.awe.statistics.engine.IDatasetService;
import org.amanzi.neo.core.INeoConstants;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public abstract class DriveDatasetService extends AbstractDatasetService implements IDatasetService {

    private PrimaryTypeTraverser primaryTraverser;

    public DriveDatasetService(GraphDatabaseService service,Node dataset) {
        super(service,dataset);
    }

    @Override
    public Collection<Node> getAllNodes() {
        return getPrimaryTypeTraverser().getAllDataNodes();
    }

   

    @Override
    public PrimaryTypeTraverser getPrimaryTypeTraverser() {
        if (primaryTraverser == null) {
            primaryTraverser = new PrimaryTypeTraverser(dataset);
        }
        return primaryTraverser;
    }

    @Override
    public Long getTime(Node node) {
        return (Long)node.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME);
    }

}
