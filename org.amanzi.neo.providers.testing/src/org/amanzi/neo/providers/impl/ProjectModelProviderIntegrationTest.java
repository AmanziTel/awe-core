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

package org.amanzi.neo.providers.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.impl.ProjectModelNodeType;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.neo.providers.util.AbstractProviderIntegrationTest;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectModelProviderIntegrationTest extends AbstractProviderIntegrationTest {

    /**
     * TODO Purpose of
     * <p>
     * </p>
     * 
     * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
     * @since 1.0.0
     */
    private static final class TEST_NODE_TYPE implements INodeType {
        @Override
        public String getId() {
            return "something";
        }
    }

    private static final String[] PROJECT_NAMES = new String[] {"project1", "project2", "project3"};

    private IProjectModelProvider provider;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        provider = getProviderPlugin().getProjectModelProvider();
    }

    @Test
    public void testCheckSearchOnEmptyDatabase() throws Exception {
        for (String name : PROJECT_NAMES) {
            assertNull("no project should be fount", provider.findProjectByName(name));
        }
    }

    @Test
    public void testCheckSearchForProject() throws Exception {
        initializeProjects();

        for (String name : PROJECT_NAMES) {
            IProjectModel model = provider.findProjectByName(name);

            assertNotNull("Project should be found", model);
            assertEquals("Unexpected name of Project", name, model.getName());
        }
    }

    @Test
    public void testCheckSearchForUnkonwnProject() throws Exception {
        initializeProjects();

        assertNull("Project should not be found", provider.findProjectByName("some other name"));
    }

    @Test
    public void testCheckSearchWithMixedNodeTypes() throws Exception {
        initializeProjects();
        String name = PROJECT_NAMES[0];

        createProject(name, new TEST_NODE_TYPE());

        IProjectModel model = provider.findProjectByName(name);

        assertNotNull("Project should be found", model);
        assertEquals("Unexpected name of Project", name, model.getName());
    }

    private List<Node> initializeProjects() {
        List<Node> projectNodes = new ArrayList<Node>();

        for (String name : PROJECT_NAMES) {
            projectNodes.add(createProject(name));
        }

        return projectNodes;
    }

    private Node createProject(String name, INodeType type) {
        Node projectNode = createNode(getGeneralNodeProperties().getNodeTypeProperty(), type.getId());

        projectNode.setProperty(getGeneralNodeProperties().getNodeNameProperty(), name);

        getGraphDatabaseService().getReferenceNode().createRelationshipTo(projectNode, NodeServiceRelationshipType.CHILD);

        return projectNode;
    }

    private Node createProject(String name) {
        return createProject(name, ProjectModelNodeType.PROJECT);
    }
}
