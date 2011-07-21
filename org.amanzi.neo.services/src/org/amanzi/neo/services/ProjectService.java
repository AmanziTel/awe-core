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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * The class manages access to project nodes
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class ProjectService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(ProjectService.class);

    private Transaction tx;

    protected enum ProjectNodeType implements INodeType {
        PROJECT {
            @Override
            public String getId() {
                return "project";
            }
        }
    }

    protected enum ProjectRelationshipType implements RelationshipType {
        PROJECT;
    }

    public ProjectService() {
        super();
    }

    public ProjectService(GraphDatabaseService graphDb) {
        super(graphDb);
    }

    public Node createProject(String name) {
        // validate parameters
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Project name cannot be empty.");
        }
        if (findProject(name) != null) {
            throw new IllegalDBOperationException("A project with name '" + name + "' already exists.");
        }

        // create new project
        Node result = null;
        tx = graphDb.beginTx();
        try {
            result = createNode(graphDb.getReferenceNode(), ProjectRelationshipType.PROJECT);
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, ProjectNodeType.PROJECT.getId());
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create project '" + name + "'.", e);
        } finally {
            tx.finish();
        }
        return result;
    }

    public Node findProject(String name) {
        // validate parameters
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Project name cannot be empty.");
        }

        for (Node node : findAllProjects()) {
            if (name.equals(node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null))) {
                return node;
            }
        }
        return null;
    }

    public Iterable<Node> findAllProjects() {
        // TODO: check
        return graphDb.getReferenceNode().traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                ReturnableEvaluator.ALL_BUT_START_NODE, ProjectRelationshipType.PROJECT, Direction.OUTGOING);
    }

    public Node getProject(String name) {
        Node result = findProject(name);
        if (result == null) {
            result = createProject(name);
        }
        return result;
    }
}
