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

package org.amanzi.neo.services;

import org.amanzi.neo.services.enums.INodeType;
import org.apache.log4j.Logger;
import org.neo4j.examples.server.Relationship;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class NewAbstractService {
    private static Logger LOGGER = Logger.getLogger(NewAbstractService.class);

    protected GraphDatabaseService graphDb;
    private Transaction tx;

    public NewAbstractService() {
        // TODO: get database service
    }

    public NewAbstractService(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }
    
    protected Node createNode(INodeType nodeType) {
        Node result = null;
        tx = graphDb.beginTx();
        try {
            result = graphDb.createNode();
            result.setProperty(DataService.TYPE, nodeType);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
        } finally {
            tx.finish();
        }
        return result;
    }

    protected Node createNode(Node startNode, RelationshipType relType) {
        Node result = null;
        tx = graphDb.beginTx();
        try {
            result = graphDb.createNode();
            startNode.createRelationshipTo(result, relType);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
        } finally {
            tx.finish();
        }
        return result;
    }

    // TODO: add some basic traversers
}
