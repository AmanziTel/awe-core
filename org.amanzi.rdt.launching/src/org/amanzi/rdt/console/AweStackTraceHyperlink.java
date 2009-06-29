package org.amanzi.rdt.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.RubySourceLocator;
import org.rubypeople.rdt.internal.debug.ui.console.RubyStackTraceHyperlink;
import org.rubypeople.rdt.internal.ui.util.StackTraceLine;

public class AweStackTraceHyperlink extends RubyStackTraceHyperlink {
	
	public AweStackTraceHyperlink(IConsole console, StackTraceLine line) {
		super(console, line);
	}
	
	/**
	 * @see org.eclipse.debug.ui.console.IHyperlink#linkActivated()
	 */
	public void linkActivated() {
		RubySourceLocator rubySourceLocator = null;
		
		RubyConsole console = (RubyConsole)getConsole();
		
		ILaunch launch = console.getLaunch();
		if (launch == null) { return; }
		ISourceLocator sourceLocator = launch.getSourceLocator();
		if (!(sourceLocator instanceof RubySourceLocator)) { return; }
		rubySourceLocator = (RubySourceLocator) sourceLocator;
		String filename = this.getFilename();
		try {
			Object sourceElement = rubySourceLocator.getSourceElement(filename);
			IEditorInput input = rubySourceLocator.getEditorInput(sourceElement);
			if (input == null) {
				if (RdtDebugUiPlugin.getDefault().isDebugging()) {
					System.out.println("Could not create editor input for stack trace: " + filename);
				}
				// wrongly detected stack trace
				return;
			}
			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, rubySourceLocator.getEditorId(input, sourceElement));
			this.setEditorToLine(editorPart, input);
		} catch (CoreException e) {
			RdtDebugUiPlugin.log(new Status(IStatus.ERROR, RdtDebugUiPlugin.PLUGIN_ID, 0, "Could not open editor or set line in editor." + filename, e));
		}
	}

}
