package org.amanzi.neo.loader.dialogs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;

import org.amanzi.neo.loader.LoadNetwork;
import org.amanzi.neo.loader.TEMSLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for Loading TEMS data
 * 
 * @author Lagutko_N
 */
public class TEMSDialog {
	
	private static final String LOAD_TEMS_DIALOG_TITLE = "Select a files containing information in TEMS format";

	// These filter names are displayed to the user in the file dialog. Note that
	// the inclusion of the actual extension in parentheses is optional, and
	// doesn't have any effect on which files are displayed.
	private static final String[] FILTER_NAMES = {
	      " (*.FMT)"
	};

	// These filter extensions are used to filter which files are displayed.
	private static final String[] FILTER_EXTS = { "*.FMT"};
	
	/*
	 * Shell of this Dialog
	 */
	private Shell temsShell;
	
	/*
	 * Button for FileDialog
	 */
	private Button chooseFileDialogButton;
	
	/*
	 * Button for adding to load files
	 */
	private Button addFilesToLoaded;
	
	/*
	 * Button for removing from load files
	 */
	private Button removeFilesFromLoaded;
	
	/*
	 * List for files to choose
	 */
	private List folderFilesList;
	
	/*
	 * List for files to load
	 */
	private List filesToLoadList;
	
	/*
	 * Cancel button
	 */
	private Button cancelButton;
	
	/*
	 * Load button
	 */
	private Button loadButton;
	
	/*
	 * Maps for storing name of file and path to files
	 */
	private HashMap<String, String> folderFiles = new HashMap<String, String>();
	private HashMap<String, String> loadedFiles = new HashMap<String, String>();
	
	/* 
	 * Default directory for file dialogs 
	 */
	
	private static String defaultDirectory = null;

	/**
	 * Creates a Shell and add GUI elements
	 * 
	 * @param shell shell
	 * @param createNewShell is true than create a child shell of given shell for dialog
	 */
	
	protected TEMSDialog(Shell shell, boolean createNewShell) {
		if (createNewShell) {
			temsShell = new Shell(shell);
		}
		else {
			temsShell = shell;
		}
		temsShell.setMinimumSize(600, 400);
		
		temsShell.setText(LOAD_TEMS_DIALOG_TITLE);
		
		createControl(temsShell);
		createActions(temsShell);
	}
	
	/**
	 * Creates a Dialog in parentShell
	 * 
	 * @param parentShell
	 */
	
	public TEMSDialog(Shell parentShell) {
		this(parentShell, true);
	}
	
	/**
	 * Create a Dialog in new Shell
	 * @param display
	 */
	
	public TEMSDialog(Display display) {
		this(new Shell(display), false);
	}
	
	/**
	 * Opens a Dialog
	 * 
	 */
	
	public void open() {		
		temsShell.pack();
		temsShell.open();
	}
	
	/**
	 * Creates controls in parent Composite
	 * 
	 * @param parent parent Composite
	 */
	
	private void createControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(SWT.FILL));
		
		createSelectFileGroup(parent);
		createFinishButtons(parent);
	}
	
	/**
	 * Creates group for selecting files to load
	 * 
	 * @param parent
	 */
	
	private void createSelectFileGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);		
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createFolderSelectionComposite(group);
		createManipulationComposite(group);
		createFileToLoadComposite(group);
	}
	
	/**
	 * Creates List for choosing Files
	 * 
	 * @param parent
	 */
	
	private void createFolderSelectionComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(1, false));		
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		folderFilesList = createSelectionList(panel, "Files to choose:");
	}
	
	/**
	 * Creates Buttons for manipulations
	 * 
	 * @param parent
	 */
	
	private void createManipulationComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(1, false));
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		
		Composite choosePanel = new Composite(panel, SWT.NONE);
		choosePanel.setLayout(new GridLayout(1, false));
		choosePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		chooseFileDialogButton = createChooseButton(choosePanel, "Browse", SWT.TOP);
		
		Composite actionPanel = new Composite(panel, SWT.NONE);
		actionPanel.setLayout(new GridLayout(1, false));
		actionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		addFilesToLoaded = createChooseButton(actionPanel, "Add", SWT.CENTER);
		removeFilesFromLoaded = createChooseButton(actionPanel, "Remove", SWT.CENTER);
	}
	
	/**
	 * Creates List for files to laod
	 * 
	 * @param parent
	 */
	
	private void createFileToLoadComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout(1, false));		
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		filesToLoadList = createSelectionList(panel, "Files to load:");
	}
	
	/**
	 * Creates 'Cancel' and 'Load' buttons
	 * 
	 * @param parent
	 */
	
	private void createFinishButtons(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());
		GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);	
		panel.setLayoutData(data);
		
		cancelButton = new Button(panel, SWT.CENTER);
		cancelButton.setText("Cancel");
		FormData formData = new FormData();
		formData.right = new FormAttachment(100, -10);
		formData.bottom = new FormAttachment(100, -10);
		formData.top = new FormAttachment(0, 10);
		formData.width = 100;
		cancelButton.setLayoutData(formData);
		
		loadButton = new Button(panel, SWT.CENTER);
		loadButton.setText("Load");
		loadButton.setEnabled(false);
		FormData formData2 = new FormData();
		formData2.right = new FormAttachment(cancelButton, -10);
		formData2.bottom = new FormAttachment(100, -10);
		formData2.top = new FormAttachment(0, 10);
		formData2.width = 100;
		loadButton.setLayoutData(formData2);
	}
	
	/**
	 * Create button for manipulation
	 * 
	 * @param parent parent Composite
	 * @param label label of Button
	 * @param position position of Button
	 * @return created Button
	 */
	
	private Button createChooseButton(Composite parent, String label, int position) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(label);
		button.setLayoutData(new GridData(SWT.FILL, position, true, true));
		
		return button;
	}
	
	/**
	 * Creates a List with Label
	 * 
	 * @param parent parent Composite
	 * @param label test of Label
	 * @return created List
	 */
	
	private List createSelectionList(Composite parent, String label) {
		Label listLabel = new Label(parent, SWT.NONE);
		listLabel.setText(label);
		listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		List list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 150;
		list.setLayoutData(gridData);		
		
		return list;
	}
	
	/**
	 * Creates actions for buttons
	 * 
	 * @param parentShell Dialog Shell
	 */
	
	private void createActions(final Shell parentShell) {
		
		//opens Dialog for choosing files
		chooseFileDialogButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				// User has selected to open a single file
		        FileDialog dlg = new FileDialog(parentShell, SWT.OPEN | SWT.MULTI);
				dlg.setText("Select a file containing TEMS data in FMT format");
		        dlg.setFilterNames(FILTER_NAMES);
		        dlg.setFilterExtensions(FILTER_EXTS);
		        dlg.setFilterPath(getDefaultDirectory());
				
		        String fn = dlg.open();
		      
		        if (fn != null) {
		        	setDefaultDirectory(dlg.getFilterPath());
		        	
		        	for (String name : dlg.getFileNames()) {		        		
		        		addFileToLoad(name, dlg.getFilterPath(), true);
		        	}
		        	
		        	for (File file : new File(getDefaultDirectory()).listFiles(new TEMFFileFilter())) {
		        		addFileToChoose(file.getName(), getDefaultDirectory(), true);
		        	}
		        }
			}
			
		});
		
		//adds selected files to files to load
		addFilesToLoaded.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for (String fileName : folderFilesList.getSelection()) {
					addFileToLoad(fileName);
				}
			}
			
		});
		
		//removes selected files from files to load
		removeFilesFromLoaded.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for (String fileName : filesToLoadList.getSelection()) {
					removeFileToLoad(fileName);
				}
			}
			
		});
		
		//closes dialog
		cancelButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				temsShell.close();
			}
			
		});
		
		loadButton.addListener(SWT.SELECTED, new Listener() {

			public void handleEvent(Event event) {
				System.out.println("Gera");
				
			}
			
		});
		
		//loads TEMS data from choosen files		
		loadButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				loadTemsData();
			}
			
		});
		
	}
	
	/**
	 * FileFilter for TEMS data files
	 * 
	 * @author Lagutko_N
	 *
	 */
	
	private class TEMFFileFilter implements FileFilter {

		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".FMT");
		}				
	}
	
	/**
	 * Loads TEMS data from files
	 * 
	 */
	
	private void loadTemsData() {		
		for (String filePath : loadedFiles.values()) {
			try {
				TEMSLoader temsLoader = new TEMSLoader(filePath);
				temsLoader.run();
				temsLoader.printStats();	// stats for this load						
			}
			catch (IOException e) {
				NeoLoaderPlugin.exception(e);
			}
		}
		loadButton.setEnabled(false);
	}
	
	/**
	 * Add file to 'Choose File' list 
	 * 
	 * @param name name of file
	 * @param path path to file
	 * @param isNeedToConvert if true than convert name to 'fileName (filePath)'
	 */
	
	private void addFileToChoose(String name, String path, boolean isNeedToConvert) {		
		if (isNeedToConvert) {
			path = path + File.separator + name;
			
			name = name + " (" + path + ")";
		}
		
		//if list doesn't contain this item than add it
		if (!folderFiles.containsKey(name) && !loadedFiles.containsKey(name)) {
			folderFiles.put(name, path);
			
			folderFilesList.add(name);
		}
	}
	
	/**
	 * Add file to 'Files To Load' list
	 * 
	 * @param name name of file
	 * @param path path to file
	 * @param isNeedToConvert if true than convert name to 'fileName (filePath)'
	 */
	
	private void addFileToLoad(String name, String path, boolean isNeedToConvert) {
		if (isNeedToConvert) {
			path = path + File.separator + name;
			name = name + " (" + path + ")";
		}
		
		//if list doesn't contain this item than add it
		if (!loadedFiles.containsKey(name)) {
			loadedFiles.put(name, path);
			
			filesToLoadList.add(name);
		}
		
		//if added file already contains in FolderFilesList than remove it from FolderFilesList
		if (folderFiles.containsKey(name)) {
			folderFiles.remove(name);
			folderFilesList.remove(name);
		}
		checkLoadButton();
	}
	
	/**
	 * Replaces file from 'Files to Choose' list to 'Files to Load' list
	 * 
	 * @param name name of file
	 */
	
	private void addFileToLoad(String name) {
		String path = folderFiles.get(name);
		folderFiles.remove(name);
		folderFilesList.remove(name);
		
		addFileToLoad(name, path, false);
	}
	
	/**
	 * Replaces file from 'File to Load' list to 'Files to Choose'
	 * 
	 * @param name name of file
	 */
	
	private void removeFileToLoad(String name) {
		String path = loadedFiles.get(name);
		loadedFiles.remove(name);
		filesToLoadList.remove(name);
		
		addFileToChoose(name, path, false);
		
		checkLoadButton();
	}
	
	/**
	 * Checks is it possible to load files. If possible (e.g. 'File to Load' list is not empty)
	 * than enable 'Load' button.
	 * 
	 */
	
	private void checkLoadButton() {
		loadButton.setEnabled(filesToLoadList.getItemCount() > 0);
	}
	
	/**
	 * Returns Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @return default directory
	 */
	
	public static String getDefaultDirectory() {
		if (defaultDirectory == null) {	
			if (LoadNetwork.hasDirectory()) {
				defaultDirectory = LoadNetwork.getDirectory();
			}
		}
		
		return defaultDirectory;
	}
	
	/**
	 * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @param newDirectory new default directory
	 */
	
	public static void setDefaultDirectory(String newDirectory) {
		if (!newDirectory.equals(defaultDirectory)) {
			defaultDirectory = newDirectory;
			LoadNetwork.setDirectory(newDirectory);
		}
	}
	
	public static boolean hasDefaultDirectory() {
		return defaultDirectory != null;
	}

}
