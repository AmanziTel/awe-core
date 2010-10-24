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

package org.amanzi.neo.loader.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * <p>
 * Main page of
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LoadDatasetMainPage extends LoaderPage<CommonConfigData> {
    /** String ASC_PAT_FILE field */
    private static final String ASC_PAT_FILE = ".*_(\\d{6})_.*";
    private static final String FMT_PAT_FILE = ".*(\\d{4}-\\d{2}-\\d{2}).*";
    private static final String CSV_PAT_FILE = ".*(\\d{2}/\\d{2}/\\d{4}).*";
    private final Set<String> restrictedNames=new HashSet<String>();
    /*
     * Minimum height of Shell
     */
    private static final int MINIMUM_HEIGHT = 400;

    /*
     * Minimum width of Shell
     */
    private static final int MINIMUM_WIDTH = 600;

    /*
     * Dataset field width
     */
    private static final int DATASET_WIDTH = 150;

    /*
     * Layout for One column and Fixed Width
     */
    private final static GridLayout layoutOneColumnNotFixedWidth = new GridLayout(1, false);

    private static final int MAX_NEMO_LINE_READ = 50;

    /*
     * Shell of this Dialog
     */
    private Shell dialogShell;

    /*
     * Button for FileDialog
     */
    private Button browseDialogButton;

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
     * Load button
     */
    private Button loadButton;
    /**
     * file data
     */
    private Calendar workData = null;
    private boolean applyToAll = false;
    /*
     * Maps for storing name of file and path to file
     */
    private final HashMap<String, String> folderFiles = new HashMap<String, String>();
    private final Map<String, String> loadedFiles = new LinkedHashMap<String, String>();

    private Button addAllFilesToLoaded;

    private Button removeAllFilesFromLoaded;

    private Combo cDataset;
    private String datasetName;
    /*
     * Default directory for file dialogs
     */
    private static String defaultDirectory = null;
    /**
     * wizard page if tems dialog was created from import wizard page
     */
    private WizardPage wizardPage = null;

    private final LinkedHashMap<String, Node> dataset = new LinkedHashMap<String, Node>();

    private Label ldataset;
    private boolean addToSelect = false;

    private Node rootNode;
    private Combo cLoaders;

    public LoadDatasetMainPage() {
        super("mainDatasetPage");
        setTitle(NeoLoaderPluginMessages.TemsImportWizard_PAGE_DESCR);
        rootNode = null;
    }

    @Override
    public void createControl(Composite parent) {
        createControlForDialog(parent);
        createActions(parent.getShell());
        setControl(parent);
        update();
    }

    public void createControlForDialog(Composite parent) {
        GridLayout layout = layoutOneColumnNotFixedWidth;
        parent.setLayout(layout);
        parent.setLayoutData(new GridData(SWT.FILL));
        createSelectFileGroup(parent);
    }

    /**
     * Creates group for selecting files to load
     * 
     * @param parent
     */

    private void createSelectFileGroup(Composite parent) {
        createDatasetRow(parent);
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createFolderSelectionComposite(group);
        createManipulationComposite(group);
        createFileToLoadComposite(group);
        Composite panel = new Composite(group, SWT.NONE);
        panel.setLayout(new FormLayout());
        GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        panel.setLayoutData(data);

        Label ldataset = new Label(panel, SWT.NONE);
        ldataset.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_DATA_TYPE);
        cLoaders = new Combo(panel, SWT.NONE);
        cLoaders.setItems(getLoadersDescriptions());
        cLoaders.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectLoader(cLoaders.getSelectionIndex());
                update();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(cDataset, 5, SWT.CENTER);
        ldataset.setLayoutData(dLabel);

        FormData dCombo = new FormData();
        dCombo.left = new FormAttachment(ldataset, 5);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.width = DATASET_WIDTH;
        cLoaders.setLayoutData(dCombo);
    }

    /**
     * Creates List for files to load
     * 
     * @param parent
     */

    private void createFileToLoadComposite(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(layoutOneColumnNotFixedWidth);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        filesToLoadList = createSelectionList(panel, NeoLoaderPluginMessages.DriveDialog_FilesToLoadListLabel);
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
     * Creates Buttons for manipulations
     * 
     * @param parent
     */

    private void createManipulationComposite(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(layoutOneColumnNotFixedWidth);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        Composite choosePanel = new Composite(panel, SWT.NONE);
        choosePanel.setLayout(layoutOneColumnNotFixedWidth);
        choosePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        browseDialogButton = createChooseButton(choosePanel, NeoLoaderPluginMessages.DriveDialog_BrowseButtonText, SWT.TOP);

        Composite actionPanel = new Composite(panel, SWT.NONE);
        actionPanel.setLayout(layoutOneColumnNotFixedWidth);
        actionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        addFilesToLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.DriveDialog_AddButtonText, SWT.CENTER);
        addAllFilesToLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.DriveDialog_AddAllButtonText, SWT.CENTER);
        removeFilesFromLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.DriveDialog_RemoveButtonText, SWT.CENTER);
        removeAllFilesFromLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.DriveDialog_RemoveAllButtonText, SWT.CENTER);
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
     * Creates group for selecting
     * 
     * @param parent
     */
    private void createDatasetRow(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new FormLayout());
        GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        panel.setLayoutData(data);

        ldataset = new Label(panel, SWT.NONE);
        ldataset.setText(NeoLoaderPluginMessages.DriveDialog_DatasetLabel);
        cDataset = new Combo(panel, SWT.NONE);
        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(cDataset, 5, SWT.CENTER);
        ldataset.setLayoutData(dLabel);

        FormData dCombo = new FormData();
        dCombo.left = new FormAttachment(ldataset, 5);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.width = DATASET_WIDTH;
        cDataset.setLayoutData(dCombo);
        cDataset.setItems(getRootItems());

        cDataset.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                changeDatasetSelection();
            }
        });
        cDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDatasetSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }


    /**
     * Gets the root items.
     *
     * @return the root items
     */
    private String[] getRootItems() {
        final String  projectName = LoaderUiUtils.getAweProjectName();
        TraversalDescription td = NeoUtils.getTDRootNodesOfProject(projectName, null);
        Node refNode = DatabaseManager.getInstance().getCurrentDatabaseService().getReferenceNode();
        restrictedNames.clear();
        dataset.clear();
        for (Node node : td.traverse(refNode).nodes()) {
            String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            if (NodeTypes.DATASET.checkNode(node)) { //$NON-NLS-1$
                dataset.put(id, node);
            } else {
                restrictedNames.add(id);
            }
        }

        String[] result = dataset.keySet().toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }

    /**
     * Creates List for choosing Files
     * 
     * @param parent
     */

    private void createFolderSelectionComposite(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(layoutOneColumnNotFixedWidth);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        folderFilesList = createSelectionList(panel, NeoLoaderPluginMessages.DriveDialog_FilesToChooseListLabel);
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        String rootName = configurationData.getDbRootName();
        if (StringUtils.isEmpty(rootName)){
            setMessage("Select dataset",DialogPage.ERROR); 
            return false;
        }
        java.util.List<File> files = configurationData.getFileToLoad();
        if (files==null||files.isEmpty()){
            setMessage("Select files for import",DialogPage.ERROR); 
            return false;            
        }
        if (getSelectedLoader() == null){
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE,DialogPage.ERROR); 
            return false;           
        }
        configurationData.setProjectName(LoaderUiUtils.getAweProjectName());
        IValidateResult result = getSelectedLoader().getValidator().validate(configurationData);
        if (result.getResult()==Result.FAIL){
            setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.ERROR); 
            return false;          
        }else if (result.getResult()==Result.UNKNOWN){
            setMessage(String.format(result.getMessages(), getSelectedLoader().getDescription()),DialogPage.WARNING); 
        }else{
            setMessage(""); //$NON-NLS-1$
        }
        return true;
    }

    /**
     * change dataset selection
     */
    protected void changeDatasetSelection() {
            datasetName=cDataset.getText();
            getConfigurationData().setDbRootName(datasetName);
            update();
    }
    /**
     * Creates actions for buttons
     * 
     * @param parentShell Dialog Shell
     */
    
    private void createActions(final Shell parentShell) {
        
        //opens Dialog for choosing files
        browseDialogButton.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                // User has selected to open a single file
                FileDialog dlg = new FileDialog(parentShell, SWT.OPEN | SWT.MULTI);
                dlg.setText(NeoLoaderPluginMessages.DriveDialog_FileDialogTitle);
                dlg.setFilterPath(LoaderUiUtils.getDefaultDirectory());
                
                String fn = dlg.open();
              
                if (fn != null) {
                    LoaderUiUtils.setDefaultDirectory(dlg.getFilterPath());
                    for (String name : dlg.getFileNames()) {
                            addFileToLoad(name, dlg.getFilterPath(), true);
                            if (cDataset.getText().isEmpty()){
                                cDataset.setText(name);
                                changeDatasetSelection();
                            }
                        }
                    }

                    File[] listFiles = new File(LoaderUiUtils.getDefaultDirectory()).listFiles();
                    for (File file : listFiles) {
                        addFileToChoose(file.getName(), LoaderUiUtils.getDefaultDirectory(), true);
                    }

                }

        });
        
        //adds selected files to files to load
        addFilesToLoaded.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (String fileName : folderFilesList.getSelection()) {
                    addFileToLoad(fileName);
                }
            }
            
        });
        
        addAllFilesToLoaded.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (String fileName : folderFilesList.getItems()) {
                    addFileToLoad(fileName);
                }
            }

        });

        //removes selected files from files to load
        removeFilesFromLoaded.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (String fileName : filesToLoadList.getSelection()) {
                    removeFileToLoad(fileName);
                }
            }
            
        });
        removeAllFilesFromLoaded.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                for (String fileName : filesToLoadList.getItems()) {
                    removeFileToLoad(fileName);
                }
            }

        });
        

        
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
        formListFilesToLoad();
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
        formListFilesToLoad();
    }
    
    /**
     *
     */
    private void formListFilesToLoad() {
        ArrayList<File>fileToLoad=new ArrayList<File>();
        if (loadedFiles!=null){
            for (String file:loadedFiles.values()){
                fileToLoad.add(new File(file));
            }
        }
        getConfigurationData().setFileToLoad(fileToLoad);
        update();
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
}
