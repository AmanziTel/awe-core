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

import java.util.Iterator;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * The class manages access to project nodes
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class ProjectService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(ProjectService.class);

    private Transaction tx;

    /**
     * <p>
     * This enum describes types of projects.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    protected enum ProjectNodeType implements INodeType {
        PROJECT {
            @Override
            public String getId() {
                return "project";
            }
        }
    }

    /**
     * <p>
     * This enum describes types of relationships used with project nodes.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    protected enum ProjectRelationshipType implements RelationshipType {
        PROJECT;
    }

    /**
     * Constructs a <code>ProjectService</code> instance, that uses the default
     * <code>GraphDatabaseService</code> instance of the running project
     */
    public ProjectService() {
        super();
    }

    /**
     * Constructs a <code>ProjectService</code> instance, that uses the defined
     * <code>GraphDatabaseService</code>
     * 
     * @param graphDb - <code>GraphDatabaseService</code> to use
     */
    public ProjectService(GraphDatabaseService graphDb) {
        super(graphDb);
    }

    /**
     * Creates a <i>project</i> node with the defined name, sets project type to
     * <code>ProjectNodeType.PROJECT</code> and creates <code>ProjectRelationshipType.PROJECT</code>
     * relationship from reference node to the project node
     * 
     * @param name - the name of the new project.
     * @return the newly created project node
     * @throws IllegalNodeDataException is thrown when <code>name</code> is null or empty
     * @throws DuplicateNodeNameException is thrown if a <i>project</i> node with the same name
     *         already exists
     */
    public Node createProject(String name) throws IllegalNodeDataException, DuplicateNodeNameException {
        // validate parameters
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Project name cannot be empty.");
        }
        if (findProject(name) != null) {
            throw new DuplicateNodeNameException(name, ProjectNodeType.PROJECT);
        }

        // create new project
        Node result = null;
        tx = graphDb.beginTx();
        try {
            result = createNode(ProjectNodeType.PROJECT);
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
            graphDb.getReferenceNode().createRelationshipTo(result, ProjectRelationshipType.PROJECT);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create project '" + name + "'.", e);
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * Finds a project with the defined name
     * 
     * @param name - the name of a project node to find
     * @return a project node with the defined name, or <code>null</code>, if nothing was found
     * @throws IllegalNodeDataException is thrown when <code>name</code> is null or empty
     * @throws DuplicateNodeNameException is thrown if more than one projects with the defined name
     *         is found
     */
    public Node findProject(String name) throws IllegalNodeDataException, DuplicateNodeNameException {
        // validate parameters
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Project name cannot be empty.");
        }

        Node result = null;
        Iterator<Node> it = getProjectTraversalDescription().evaluator(new NameTypeEvaluator(name, ProjectNodeType.PROJECT))
                .traverse(graphDb.getReferenceNode()).nodes().iterator();
        if (it.hasNext()) {
            result = it.next();
        }

        if (it.hasNext()) {
            throw new DuplicateNodeNameException(name, ProjectNodeType.PROJECT);
        }
        return result;
    }

    /**
     * Traverses database to find all project nodes
     * 
     * @return an <code>Iterable</code> over project nodes
     */
    public Iterable<Node> findAllProjects() {
        return getProjectTraversalDescription().traverse(graphDb.getReferenceNode()).nodes();
    }

    public Node getProject(String name) throws IllegalNodeDataException, DuplicateNodeNameException {
        Node result = findProject(name);
        if (result == null) {
            result = createProject(name);
        }
        return result;
    }

    /**
     * Generates a <code>TraversalDescription</code> to fetch all project nodes. Assumed that you
     * would start traversing from DB reference node
     * 
     * @return
     */
    public TraversalDescription getProjectTraversalDescription() {
        return Traversal.description().depthFirst().evaluator(Evaluators.excludeStartPosition()).evaluator(Evaluators.atDepth(1))
                .relationships(ProjectRelationshipType.PROJECT, Direction.OUTGOING);
    }
}
