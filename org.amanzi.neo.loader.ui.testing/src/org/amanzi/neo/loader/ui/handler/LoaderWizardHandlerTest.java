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

package org.amanzi.neo.loader.ui.handler;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.ui.wizard.ILoaderWizard;
import org.amanzi.neo.loader.ui.wizard.impl.internal.LoaderContext;
import org.amanzi.testing.AbstractMockitoTest;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
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
public class LoaderWizardHandlerTest extends AbstractMockitoTest {

    private static final String WIZARD_ID = "some wizard id";

    private LoaderContext context;

    private ExecutionEvent event;

    private ILoaderWizard wizard;

    private LoaderWizardHandler handler;

    private IWorkbenchWindow window;

    private Shell shell;

    private IWorkbench workbench;

    private Dialog dialog;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        wizard = mock(ILoaderWizard.class);

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(LoaderWizardHandler.LOADER_WIZARD_ID, WIZARD_ID);

        event = new ExecutionEvent(null, parameters, null, null);

        context = mock(LoaderContext.class);

        window = mock(IWorkbenchWindow.class);

        workbench = mock(IWorkbench.class);
        when(window.getWorkbench()).thenReturn(workbench);

        shell = mock(Shell.class);
        when(window.getShell()).thenReturn(shell);

        handler = spy(new LoaderWizardHandler(context));
        doReturn(window).when(handler).getWorkbenchWindow(event);

        dialog = mock(Dialog.class);
        doReturn(dialog).when(handler).createDialog(shell, wizard);
    }

    @Test
    public void testCheckActivityOnCommandExecution() throws Exception {
        when(context.getLoaderWizard(WIZARD_ID)).thenReturn(wizard);

        handler.execute(event);

        verify(context).getLoaderWizard(WIZARD_ID);
        verify(wizard).init(workbench, null);

        verify(handler).createDialog(shell, wizard);
        verify(dialog).create();
        verify(dialog).open();
    }

    @Test(expected = ExecutionException.class)
    public void testCheckExceptionWhenNoWizardId() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(LoaderWizardHandler.LOADER_WIZARD_ID, null);

        event = new ExecutionEvent(null, parameters, null, null);

        handler.execute(event);
    }

    @Test(expected = ExecutionException.class)
    public void testCheckExceptionWhenNoWizardById() throws Exception {
        handler.execute(event);
    }
}
