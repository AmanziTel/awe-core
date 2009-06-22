package org.amanzi.neo.loader;

import java.io.IOException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.neo4j.api.core.EmbeddedNeo;

/**
 * This class demonstrates FileDialog
 */
public class ShowFileDialog {
	// These filter names are displayed to the user in the file dialog. Note that
	// the inclusion of the actual extension in parentheses is optional, and
	// doesn't have any effect on which files are displayed.
	public static final String[] DEFAULT_FILTER_NAMES = {"Comma Separated Values Files (*.csv)", "All Files (*.*)" };

	// These filter extensions are used to filter which files are displayed.
	public static final String[] DEFAULT_FILTER_EXTS = { "*.csv", "*.*" };

	private String[] filterNames = DEFAULT_FILTER_NAMES;
	private String[] filterExts = DEFAULT_FILTER_EXTS;
	private String title = "file";

	/** Construct with specified title, file names and extensions
	 * @param title to show the user
	 * @param filterNames for specified file types
	 * @param filterExts for extensions to filter
	 */
	public ShowFileDialog(String title, String[] filterNames, String[] filterExts){
		this.title = title;
		this.filterNames = filterNames;
		this.filterExts = filterExts;
	}
	/**
	 * Construct with specified title, and default file name/ext filter
	 * @param title to show the user
	 */
	public ShowFileDialog(String title){
		this.title = title;
	}
	/**
	 * Construct with default title, and file name/ext filter
	 */
	public ShowFileDialog(){
	}
	
	/**
	 * Runs the application
	 */
	public void run(Display display) {
		Shell shell = new Shell(display);
		shell.setText("File Dialog");
		createContents(shell);
		shell.pack();
		shell.open();
	}

	/**
	 * Creates the contents for the window
	 * 
	 * @param shell the parent shell
	 */
	public void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(5, true));

		new Label(shell, SWT.NONE).setText("File Name:");

		final Text fileName = new Text(shell, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		fileName.setLayoutData(data);

		Button open = new Button(shell, SWT.PUSH);
		open.setText("Open ...");
		open.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// User has selected to open a single file
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setFilterNames(filterNames);
				dlg.setFilterExtensions(filterExts);
				String fn = dlg.open();
				if (fn != null) {
					fileName.setText(fn);
				}
			}
		});

		Button save = new Button(shell, SWT.PUSH);
		save.setText("Load "+title+"...");
		save.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Display.getDefault().asyncExec(new Runnable(){
				//	public void run() {
				EmbeddedNeo neo = new EmbeddedNeo("var/neo");
				NetworkLoader networkLoader;
				try {
					networkLoader = new NetworkLoader(neo, fileName.getText());
					networkLoader.printStats();
					neo.shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * The application entry point
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new ShowFileDialog().run(new Display());
	}

}
