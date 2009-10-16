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
package org.amanzi.scripting.jirb.console;

import org.amanzi.scripting.jirb.SWTIRBConsole;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * This is the simplest possible implementation of an eclipse view embedding the JRuby IRB console from org.amanzi.scripting.jirb.
 * @see org.amanzi.scripting.jirb
 * @see org.amanzi.scripting.jirb.console.RubyConsole for a more complete example
 */
public class SimpleRubyConsole extends ViewPart {
	private SWTIRBConsole ex;
	public SimpleRubyConsole() {}

	/** Create the view in the passed parent composite. */
	public void createPartControl(Composite parent) {
		ex = new SWTIRBConsole(parent);
	}

	/** Passing the focus request to the embedded composite. */
	public void setFocus() {
		ex.setFocus();
	}
}
