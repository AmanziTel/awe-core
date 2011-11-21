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

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang.StringUtils;
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
public class ProjectService extends NewAbstractService {
    
    private static Logger LOGGER = Logger.getLogger(ProjectService.class);

    private Transaction tx;
    /**
     * Generates a <code>TraversalDescription</code> to fetch all project nodes. Assumed that you
     * would start traversing from DB reference node
     * 
     */
    public final TraversalDescription projectTraversalDescription = Traversal.description().breadthFirst()
            .evaluator(Evaluators.excludeStartPosition()).evaluator(Evaluators.atDepth(1))
            .relationships(ProjectRelationshipType.PROJECT, Direction.OUTGOING);

    /**
     * <p>
     * This enum describes types of projects.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum ProjectNodeType implements INodeType {
        PROJECT;

        @Override
        public String getId() {
            return name().toLowerCase();
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
     * <code>GraphDatabaseService</code> instance of the running application
     */
    public ProjectService() {
        super();
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
     * @throws DatabaseException if errors occur in database
     */
    public Node createProject(String name) throws IllegalNodeDataException, DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("Started createProject '" + name + "'");
        // validate parameters
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
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
            result.setProperty(NewAbstractService.NAME, name);
            graphDb.getReferenceNode().createRelationshipTo(result, ProjectRelationshipType.PROJECT);
            tx.success();
        } catch (DatabaseException e) {
            LOGGER.error("Could not create project '" + name + "'.", e);
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
        // TODO: Fake
        DatabaseManagerFactory.getDatabaseManager().commit();
        LOGGER.debug("Finished createProject");
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
        LOGGER.debug("Started findProject '" + name + "'");
        // validate parameters
        if ((name == null) || (name.equals(StringUtils.EMPTY))) {
            throw new IllegalNodeDataException("Project name cannot be empty.");
        }

        Node result = null;
        Iterator<Node> it = projectTraversalDescription.evaluator(new NameTypeEvaluator(name, ProjectNodeType.PROJECT))
                .traverse(graphDb.getReferenceNode()).nodes().iterator();
        if (it.hasNext()) {
            result = it.next();
        }

        if (it.hasNext()) {
            throw new DuplicateNodeNameException(name, ProjectNodeType.PROJECT);
        }
        LOGGER.debug("Finished findProject");
        return result;
    }

    /**
     * Traverses database to find all project nodes
     * 
     * @return an <code>Iterable</code> over project nodes
     */
    public Iterable<Node> findAllProjects() {
        LOGGER.debug("Started findAllProjects");
        return projectTraversalDescription.traverse(graphDb.getReferenceNode()).nodes();
    }

    /**
     * Finds or creates a project node by name.
     * 
     * @param name
     * @return
     * @throws IllegalNodeDataException
     * @throws DuplicateNodeNameException if more than one project found
     * @throws DatabaseException if errors occur in database
     */
    public Node getProject(String name) throws IllegalNodeDataException, DuplicateNodeNameException, DatabaseException {
        LOGGER.debug("Started getProject '" + name + "'");
        Node result = findProject(name);
        if (result == null) {
            result = createProject(name);
        }
        LOGGER.debug("Finished getProject");
        return result;
    }

}
