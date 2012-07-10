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
import org.amanzi.neo.loader.core.internal.IConfiguration;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.FatalException;
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

        @Override
        public void save(IData dataElement) {
        }

    }

    private TestSaver saver;

    private IConfiguration configuration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = mock(IConfiguration.class);

        saver = spy(new TestSaver());
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
    public void testCheckInitialization() {
        saver.init(configuration);

        assertEquals("unexpected configuration", configuration, saver.getConfiguration());
    }
}
