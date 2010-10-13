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

import org.amanzi.neo.core.INeoConstants;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public class IDENService extends AbstractDatasetService {

    public IDENService(GraphDatabaseService service, Node dataset) {
        super(service, dataset);
    }

    private PrimaryTypeTraverser primaryTraverser;

    @Override
    public Collection<Node> getAllNodes() {
        return getPrimaryTypeTraverser().getAllDataNodes();
    }

    @Override
    public String getKeyProperty(Node node) {
        return null;
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
        return null;
    }

    @Override
    public boolean isDatasetCorrelated() {
        return false;
    }

}
