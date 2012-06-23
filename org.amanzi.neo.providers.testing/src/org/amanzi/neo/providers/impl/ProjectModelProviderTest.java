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

import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.ProjectModel;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
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
public class ProjectModelProviderTest extends AbstractMockitoTest {

    private static final String PROJECT_NAME = "PROJECT_NAME";

    private static final String NOT_FOUND_PROJECT = "not_found_project";

    private static final String PROJECT_NAME_FOR_CACHE_CHECK = "cached project name";

    private ProjectModelProvider projectModelProvider;

    private INodeService nodeService;

    private ProjectModel projectModel;

    private final static GeneralNodeProperties generalNodeProperties = new GeneralNodeProperties();

    private Node node;

    private Node referencedNode;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        node = mock(Node.class);
        referencedNode = mock(Node.class);

        nodeService = mock(INodeService.class);
        when(nodeService.getReferencedNode()).thenReturn(referencedNode);

        projectModel = mock(ProjectModel.class);

        projectModelProvider = new ProjectModelProvider(nodeService, generalNodeProperties);
    }

    @Test
    public void testCreateNewProjectActivity() throws Exception {
        spyProjectProvider();

        projectModelProvider.createProjectModel(PROJECT_NAME);

        verify(projectModelProvider).findProjectByName(PROJECT_NAME);
        verify(projectModel).initialize(PROJECT_NAME);
    }

    @Test
    public void testCreateNewProjectResult() throws Exception {
        spyProjectProvider();

        IProjectModel result = projectModelProvider.createProjectModel(PROJECT_NAME);

        assertNotNull("ProjectModel cannot be null", result);
        assertEquals("Unexpected model", projectModel, result);
    }

    @Test(expected = FatalException.class)
    public void testCheckDataInconsistencyExceptionOnNewProject() throws Exception {
        spyProjectProvider();

        doThrow(new FatalException(new IllegalArgumentException())).when(projectModel).initialize(PROJECT_NAME);

        projectModelProvider.createProjectModel(PROJECT_NAME);
    }

    @Test(expected = DuplicatedModelException.class)
    public void testCheckDuplicateExceptionOnNewProject() throws Exception {
        spyProjectProvider();
        setProjectFound(true);

        projectModelProvider.createProjectModel(PROJECT_NAME);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreateNewProjectWithoutName() throws Exception {
        projectModelProvider.createProjectModel(null);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckCreateNewProjectWithEmptyName() throws Exception {
        projectModelProvider.createProjectModel(StringUtils.EMPTY);
    }

    @Test
    public void testCheckActivityOnFindByName() throws Exception {
        projectModelProvider.findProjectByName(PROJECT_NAME);

        verify(nodeService).getChildByName(referencedNode, PROJECT_NAME);
    }

    @Test
    public void testCheckCacheActivityWithEmptyCache() throws Exception {
        when(nodeService.getChildByName(referencedNode, PROJECT_NAME_FOR_CACHE_CHECK)).thenReturn(node);

        // without cache
        projectModelProvider.findProjectByName(PROJECT_NAME_FOR_CACHE_CHECK);
        verify(nodeService).getChildByName(referencedNode, PROJECT_NAME_FOR_CACHE_CHECK);

        // get model from cache
        projectModelProvider.findProjectByName(PROJECT_NAME_FOR_CACHE_CHECK);
        verify(nodeService).getChildByName((Node)any(), (String)any());
    }

    @Test
    public void testCheckNohingFoundByName() throws Exception {
        when(nodeService.getChildByName(referencedNode, NOT_FOUND_PROJECT)).thenReturn(null);

        IProjectModel result = projectModelProvider.findProjectByName(NOT_FOUND_PROJECT);

        assertNull("ProjectModel cannot exist", result);
    }

    @Test
    public void testCheckModelFoundByName() throws Exception {
        when(nodeService.getChildByName(referencedNode, PROJECT_NAME)).thenReturn(node);

        IProjectModel result = projectModelProvider.findProjectByName(PROJECT_NAME);

        assertNotNull("ProjectModel cannot be null", result);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindProjectWithoutName() throws Exception {
        projectModelProvider.findProjectByName(null);
    }

    @Test(expected = ParameterInconsistencyException.class)
    public void testCheckFindProjectWithEmptyName() throws Exception {
        projectModelProvider.findProjectByName(StringUtils.EMPTY);
    }

    @Test(expected = FatalException.class)
    public void testCheckFatalExceptionOnFindProject() throws Exception {
        doThrow(new DatabaseException(new IllegalArgumentException())).when(nodeService).getReferencedNode();

        projectModelProvider.findProjectByName(PROJECT_NAME);
    }

    private void spyProjectProvider() throws ModelException {
        projectModelProvider = spy(projectModelProvider);
        when(projectModelProvider.createInstance()).thenReturn(projectModel);

        setProjectFound(false);
    }

    private void setProjectFound(boolean isFound) throws ModelException {
        doReturn(isFound ? new ProjectModel(nodeService) : null).when(projectModelProvider).findProjectByName(PROJECT_NAME);
    }
}
