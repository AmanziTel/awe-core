package org.amanzi.neo.loader;

import org.eclipse.swt.widgets.Display;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadNetwork extends AbstractActionTool {
	private static final String[] FILTER_NAMES = {
		"OpenOffice.org Spreadsheet Files (*.sxc)",
		"Microsoft Excel Spreadsheet Files (*.xls)",
		"Comma Separated Values Files (*.csv)", "All Files (*.*)" };
	private static final String[] FILTER_EXTS = { "*.sxc", "*.xls", "*.csv", "*.*" };

	public LoadNetwork() {
	}

	@Override
	public void run() {
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		this.getContext().updateUI(new Runnable(){
			@Override
			public void run() {
				(new ShowFileDialog("Network",FILTER_NAMES,FILTER_EXTS)).run(display);
			}
		});
	}

	@Override
	public void dispose() {
	}

}
