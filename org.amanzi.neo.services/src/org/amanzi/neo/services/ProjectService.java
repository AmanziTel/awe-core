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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of 
 * <p>
 *  The class manages access to project nodes 
 * </p>
 * @author grigoreva_a
 * @since 1.0.0
 */
public class ProjectService extends NewAbstractService {
    public static final String PROJECT_NODE_TYPE = "project";
    protected enum RelType implements RelationshipType{
        PROJECT;
    }
    public ProjectService(){
        super();
    }
    public ProjectService(GraphDatabaseService graphDb){
        super(graphDb);
    }
    public Node createProject(String name){
        return null;
    }

    public Node findProject(String name){
        return null;
    }
    
    public Iterable<Node> findAllProjects(){
        return null;
    }
    
    public Node getProject(String name){
        Node result = findProject(name);
        if (result == null){
            result = createProject(name);
        }
        return result;
    }
}
