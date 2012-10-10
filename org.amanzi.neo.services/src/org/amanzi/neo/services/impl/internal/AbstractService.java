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

package org.amanzi.neo.services.impl.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.db.manager.events.DatabaseEvent;
import org.amanzi.neo.db.manager.events.IDatabaseEventListener;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractService implements IDatabaseEventListener, IService {

    public static class PropertyEvaluator implements Evaluator {

        private final String propertyName;

        private final Object propertyValue;

        public PropertyEvaluator(String propertyName, Object propertyValue) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
        }

        @Override
        public Evaluation evaluate(Path arg0) {
            Node node = arg0.endNode();

            Object value = node.getProperty(propertyName, null);

            if ((value != null) && (value.equals(propertyValue))) {
                return Evaluation.INCLUDE_AND_CONTINUE;
            }

            return Evaluation.EXCLUDE_AND_CONTINUE;
        }
    }

    private GraphDatabaseService graphDb;

    private final IGeneralNodeProperties generalNodeProperties;

    private final Map<INodeType, PropertyEvaluator> nodeTypeEvaluatorCache = new HashMap<INodeType, PropertyEvaluator>();

    protected AbstractService(GraphDatabaseService graphDb, IGeneralNodeProperties generalNodeProperties) {
        this.graphDb = graphDb;
        this.generalNodeProperties = generalNodeProperties;

        DatabaseManagerFactory.getDatabaseManager().addDatabaseEventListener(this);
    }

    protected GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    protected IGeneralNodeProperties getGeneralNodeProperties() {
        return generalNodeProperties;
    }

    @Override
    public void onDatabaseEvent(DatabaseEvent event) {
        switch (event.getEventType()) {
        case BEFORE_SHUTDOWN:
            graphDb = null;
            break;
        case AFTER_STARTUP:
            graphDb = DatabaseManagerFactory.getDatabaseManager().getDatabaseService();
            break;
        default:
            // do nothing
            break;
        }
    }

    protected PropertyEvaluator getPropertyEvaluatorForType(INodeType nodeType) {
        PropertyEvaluator result = nodeTypeEvaluatorCache.get(nodeType);

        if (result == null) {
            result = new PropertyEvaluator(generalNodeProperties.getNodeTypeProperty(), nodeType.getId());

            nodeTypeEvaluatorCache.put(nodeType, result);
        }

        return result;
    }
}
