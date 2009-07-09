package org.amanzi.neo.loader;

import java.io.IOException;

import org.amanzi.neo.loader.dialogs.TEMSDialog;
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
	private static String directory = null;

	public LoadNetwork() {
	}
	
	public static String getDirectory(){
		//LN, 9.07.2009, if directory in LoadNetwork is null than get DefaultDirectory from TEMSDialog
		if (directory == null) {
			if (TEMSDialog.hasDefaultDirectory()) {
				directory = TEMSDialog.getDefaultDirectory();
			}
		}
		return directory;
	}
	
	/**
	 * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @param newDirectory new default directory
	 * @author Lagutko_N
	 */
	
	public static void setDirectory(String newDirectory) {
		if (!newDirectory.equals(directory)) {
			directory = newDirectory;		
			TEMSDialog.setDefaultDirectory(newDirectory);
		}
	}
	
	/**
	 * Is DefaultDirectored set
	 * 
	 * @return 
	 * @author Lagutko_N
	 */
	
	public static boolean hasDirectory() {
		return directory != null;
	}

	public void run() {
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		this.getContext().updateUI(new Runnable(){
			
			public void run() {
				FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
				dlg.setText("Select a file containing network information in CSV format");
				dlg.setFilterNames(FILTER_NAMES);
				dlg.setFilterExtensions(FILTER_EXTS);
				dlg.setFilterPath(getDirectory());
				final String filename = dlg.open();
				if (filename != null) {
					setDirectory(dlg.getFilterPath());
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

	public void dispose() {
	}

}
