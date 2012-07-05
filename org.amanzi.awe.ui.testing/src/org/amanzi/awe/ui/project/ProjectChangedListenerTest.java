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

package org.amanzi.awe.ui.project;

import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.events.impl.AWEStartedEvent;
import org.amanzi.awe.ui.events.impl.ProjectNameChangedEvent;
import org.amanzi.neo.models.exceptions.FatalException;
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
public class ProjectChangedListenerTest extends AbstractMockitoTest {

    private static final String NEW_PROJECT_NAME = "some new name";

    private static final IEvent EVENT = new ProjectNameChangedEvent("some new name");

    private ProjectChangedListener listener;

    private IProjectModelProvider projectModelProvider;

    private IProjectModel projectModel;

    @Before
    public void setUp() {
        projectModelProvider = mock(IProjectModelProvider.class);
        projectModel = mock(IProjectModel.class);

        listener = new ProjectChangedListener(projectModelProvider);
    }

    @Test
    public void testCheckEventHandlingWhenModelNotExists() throws Exception {
        when(projectModelProvider.findProjectByName(NEW_PROJECT_NAME)).thenReturn(null);
        when(projectModelProvider.createProjectModel(NEW_PROJECT_NAME)).thenReturn(projectModel);

        listener.onEvent(EVENT);

        verify(projectModelProvider).findProjectByName(NEW_PROJECT_NAME);
        verify(projectModelProvider).createProjectModel(NEW_PROJECT_NAME);
        verify(projectModelProvider).setActiveProjectModel(projectModel);
    }

    @Test
    public void testCheckEventHandlingWhenModelExists() throws Exception {
        when(projectModelProvider.findProjectByName(NEW_PROJECT_NAME)).thenReturn(projectModel);

        listener.onEvent(EVENT);

        verify(projectModelProvider).findProjectByName(NEW_PROJECT_NAME);
        verify(projectModelProvider, never()).createProjectModel(NEW_PROJECT_NAME);
        verify(projectModelProvider).setActiveProjectModel(projectModel);
    }

    @Test
    public void testCheckNothingHappenedOnWrongEvent() {
        listener.onEvent(new AWEStartedEvent());

        verifyNoMoreInteractions(projectModelProvider);
    }

    @Test
    public void testCheckNoExceptionsOnEventHandling() throws Exception {
        doThrow(new FatalException(new IllegalArgumentException())).when(projectModelProvider).findProjectByName(NEW_PROJECT_NAME);

        listener.onEvent(EVENT);
    }

}
