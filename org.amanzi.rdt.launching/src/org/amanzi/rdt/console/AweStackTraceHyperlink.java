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
package org.amanzi.rdt.console;

import org.amanzi.rdt.internal.launching.AweLaunchingPlugin;
import org.amanzi.rdt.internal.launching.AweLaunchingPluginMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
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
				// wrongly detected stack trace
				return;
			}
			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, rubySourceLocator.getEditorId(input, sourceElement));
			this.setEditorToLine(editorPart, input);
		} catch (CoreException e) {
		    AweLaunchingPlugin.log(AweLaunchingPluginMessages.Could_not_open_editor + filename, e);
		}
	}

}
