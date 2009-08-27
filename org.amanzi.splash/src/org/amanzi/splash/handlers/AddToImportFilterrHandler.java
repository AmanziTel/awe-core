package org.amanzi.splash.handlers;

import org.amanzi.splash.ui.AbstractSplashEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class AddToImportFilterrHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public AddToImportFilterrHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		
				AbstractSplashEditor editor = (AbstractSplashEditor) window.getActivePage().getActiveEditor();
//				editor.plotCellsBarChart();
		
		//editor.addNewFilter();
				
				editor.LoadHeadings();
		return null;
	}
}
