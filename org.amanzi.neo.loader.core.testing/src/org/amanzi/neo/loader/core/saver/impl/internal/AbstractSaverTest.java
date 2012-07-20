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

package org.amanzi.neo.loader.core.saver.impl.internal;

import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.loader.core.IData;
import org.amanzi.neo.loader.core.exception.impl.UnderlyingModelException;
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.FatalException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.amanzi.testing.AbstractMockitoTest;
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
public class AbstractSaverTest extends AbstractMockitoTest {

    public static class TestSaver extends AbstractSaver<IConfiguration, IData> {

        protected TestSaver(IProjectModelProvider projectModelProvider) {
            super(projectModelProvider);
        }

        @Override
        protected void saveInModel(IData data) throws ModelException {
        }

    }

    private TestSaver saver;

    private IConfiguration configuration;

    private IProjectModelProvider projectModelProvider;

    private IProjectModel currentProject;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = mock(IConfiguration.class);

        projectModelProvider = mock(IProjectModelProvider.class);
        when(projectModelProvider.getActiveProjectModel()).thenReturn(currentProject);

        saver = spy(new TestSaver(projectModelProvider));
    }

    @Test
    public void testCheckFinishUpActivity() throws Exception {
        List<IModel> modelList = Arrays.asList(mock(IModel.class), mock(IModel.class), mock(IModel.class));

        for (IModel model : modelList) {
            saver.addProcessedModel(model);
        }

        saver.finishUp();

        for (IModel model : modelList) {
            verify(model).finishUp();
        }
    }

    @Test
    public void testCheckFinishUpActivityWithException() throws Exception {
        List<IModel> modelList = Arrays.asList(mock(IModel.class), mock(IModel.class), mock(IModel.class));

        doThrow(new FatalException(new IllegalArgumentException())).when(modelList.get(0)).finishUp();

        for (IModel model : modelList) {
            saver.addProcessedModel(model);
        }

        saver.finishUp();

        for (IModel model : modelList) {
            verify(model).finishUp();
        }
    }

    @Test
    public void testCheckInitialization() throws Exception {
        saver.init(configuration);

        assertEquals("unexpected configuration", configuration, saver.getConfiguration());
    }

    @Test
    public void testCheckCurrentProjectModelInitialization() throws Exception {
        saver.init(configuration);

        verify(projectModelProvider).getActiveProjectModel();
    }

    @Test
    public void testCheckCurrentProjectModelInitializationResult() throws Exception {
        saver.init(configuration);

        assertEquals("unexpected current project", currentProject, saver.getCurrentProject());
    }

    @Test(expected = UnderlyingModelException.class)
    public void testCheckLoaderExceptionOnSaver() throws Exception {
        doThrow(new FatalException(new IllegalArgumentException())).when(saver).saveInModel(any(IData.class));

        IData data = mock(IData.class);
        saver.save(data);
    }
}
