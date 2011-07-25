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

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.services.enums.INodeType;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class NewAbstractService {
    protected final static String PROPERTY_TYPE_NAME = "type";
    protected final static String PROPERTY_NAME_NAME = "name";
    
    private static Logger LOGGER = Logger.getLogger(NewAbstractService.class);

    protected GraphDatabaseService graphDb;
    private Transaction tx;

    public NewAbstractService() {
        // TODO: get database service
        graphDb = NeoServiceProvider.getProvider().getService();
    }

    public NewAbstractService(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
    }

    protected Node createNode(INodeType nodeType) {
        Node result = null;
        tx = graphDb.beginTx();
        try {
            result = graphDb.createNode();
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, nodeType.getId());
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create node.", e);
            // TODO: throw exception further
        } finally {
            tx.finish();
        }
        return result;
    }

    public class NameTypeEvaluator implements Evaluator {
        private String name;
        private INodeType type;

        public NameTypeEvaluator(String name, INodeType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public Evaluation evaluate(Path path) {
            if (path.length() == 0) {
                return Evaluation.EXCLUDE_AND_CONTINUE;
            }
            Node node = path.endNode();
            if          ((node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(name))
                    &&   (node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(type.getId()))) {
                return Evaluation.INCLUDE_AND_CONTINUE;
            } else {
                return Evaluation.EXCLUDE_AND_CONTINUE;
            }
        }
    }
}
