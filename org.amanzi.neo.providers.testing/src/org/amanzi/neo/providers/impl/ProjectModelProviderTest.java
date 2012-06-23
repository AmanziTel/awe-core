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
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.ProjectModel;
import org.amanzi.neo.nodeproperties.impl.GeneralNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.testing.AbstractMockitoTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

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

    private ProjectModelProvider projectModelProvider;

    private INodeService nodeService;

    private ProjectModel projectModel;

    private final static GeneralNodeProperties generalNodeProperties = new GeneralNodeProperties();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        nodeService = mock(INodeService.class);

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

    private void spyProjectProvider() {
        projectModelProvider = spy(projectModelProvider);
        when(projectModelProvider.createInstance()).thenReturn(projectModel);

        setProjectFound(false);
    }

    private void setProjectFound(boolean isFound) {
        when(projectModelProvider.findProjectByName(PROJECT_NAME)).thenReturn(isFound ? new ProjectModel(nodeService) : null);
    }
}
