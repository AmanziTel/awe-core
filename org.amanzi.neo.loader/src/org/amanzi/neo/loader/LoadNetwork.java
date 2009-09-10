package org.amanzi.neo.loader;

import java.io.IOException;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.dialogs.TEMSDialog;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class LoadNetwork extends AbstractActionTool {
    /*
     * Names of supported files for Network
     */
    private static final String[] NETWORK_FILE_NAMES = {
        "Comma Separated Values Files (*.csv)",
        "Plain Text Files (*.txt)",
        "OpenOffice.org Spreadsheet Files (*.sxc)",
        "Microsoft Excel Spreadsheet Files (*.xls)",
        "All Files (*.*)" };
    
    /*
     * Extensions of supported files for Network
     */
    private static final String[] NETWORK_FILE_EXTENSIONS = {"*.csv", "*.txt", "*.sxc", "*.xls", "*.*"};

    private static String directory = null;

    private final Display display;

	public LoadNetwork() {
        display = null;
	}

    public LoadNetwork(Display display) {
        this.display = display;
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
				FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
				dlg.setText(NeoLoaderPluginMessages.NetworkDialog_DialogTitle);
				dlg.setFilterNames(NETWORK_FILE_NAMES);
				dlg.setFilterExtensions(NETWORK_FILE_EXTENSIONS);
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
								networkLoader.printStats(false);
							} catch (IOException e) {								
								NeoCorePlugin.error("Error loading Network file", e);
							}
						}
					});
				}

	}

    public void runOnAction() {
        FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
        dlg.setText(NeoLoaderPluginMessages.NetworkDialog_DialogTitle);
        dlg.setFilterNames(NETWORK_FILE_NAMES);
        dlg.setFilterExtensions(NETWORK_FILE_EXTENSIONS);
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
                        networkLoader.printStats(false);
                    } catch (IOException e) {
                        NeoCorePlugin.error("Error loading Network file", e);
                    }
                }
            });
        }
    }
	public void dispose() {
	}

}
