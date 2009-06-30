package org.amanzi.neo.loader;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadNetwork extends AbstractActionTool {
	private static final String[] FILTER_NAMES = {
		"Comma Separated Values Files (*.csv)",
		"OpenOffice.org Spreadsheet Files (*.sxc)",
		"Microsoft Excel Spreadsheet Files (*.xls)",
		"All Files (*.*)" };
	private static final String[] FILTER_EXTS = { "*.csv", "*.sxc", "*.xls", "*.*" };
	private String directory = null;

	public LoadNetwork() {
	}
	
	public String getDirectory(){
		return directory;
	}

	@Override
	public void run() {
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		this.getContext().updateUI(new Runnable(){
			@Override
			public void run() {
				FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
				dlg.setText("Select a file containing network information in CSV format");
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				dlg.setFilterPath(directory);
				final String filename = dlg.open();
				if (filename != null) {
					directory = dlg.getFilterPath();
					display.asyncExec(new Runnable() {
						public void run() {
							NetworkLoader networkLoader;
							try {
								networkLoader = new NetworkLoader(filename);
								networkLoader.run();
								networkLoader.printStats();
							} catch (IOException e) {
								System.err.println("Error loading file: " + e.getMessage());
								e.printStackTrace(System.err);
							}
						}
					});
				}
			}
		});
	}

	@Override
	public void dispose() {
	}

}
