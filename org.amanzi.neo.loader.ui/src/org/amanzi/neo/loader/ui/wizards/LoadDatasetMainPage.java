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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.DialogPage;
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
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Main page of
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LoadDatasetMainPage extends LoaderPageNew<ConfigurationDataImpl> {

	private static final Logger LOGGER = Logger.getLogger(LoadDatasetMainPage.class);
    
    private Map<Object, String> names = new HashMap<Object, String>();

    /*
     * Layout for One column and Fixed Width
     */
    private final static GridLayout layoutOneColumnNotFixedWidth = new GridLayout(1, false);

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
     * Maps for storing name of file and path to file
     */
    private final HashMap<String, String> folderFiles = new HashMap<String, String>();
    private final Map<String, String> loadedFiles = new LinkedHashMap<String, String>();

    private Button addAllFilesToLoaded;

    private Button removeAllFilesFromLoaded;

    private Combo cDataset;
    private String datasetName;

    private final LinkedHashMap<String, IDriveModel> dataset = new LinkedHashMap<String, IDriveModel>();

    private Label ldataset;

    private Combo cLoaders;
    private DateTime date;
    private Button selectCRS;

    public LoadDatasetMainPage() {
        super("mainDatasetPage");
        setTitle(NeoLoaderPluginMessages.TemsImportWizard_PAGE_DESCR);
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
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new FormLayout());
        GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        panel.setLayoutData(data);

        Label ldataset = new Label(panel, SWT.NONE);
        ldataset.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_DATA_TYPE);
        cLoaders = new Combo(panel, SWT.NONE);
        cLoaders.setItems(getNewLoadersDescriptions());
        cLoaders.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectNewLoader(cLoaders.getSelectionIndex());
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
        dCombo.right = new FormAttachment(50, -2);
        cLoaders.setLayoutData(dCombo);

        Label labl = new Label(panel, SWT.NONE);
        labl.setText("Preffered date");
        dLabel = new FormData();
        dLabel.left = new FormAttachment(50, 5);
        dLabel.top = new FormAttachment(cDataset, 5, SWT.CENTER);
        labl.setLayoutData(dLabel);

        date = new DateTime(panel, SWT.FILL | SWT.BORDER | SWT.DATE | SWT.MEDIUM);
        dCombo = new FormData();
        dCombo.left = new FormAttachment(labl, 5);
        dCombo.top = new FormAttachment(0, 2);
        // dCombo.right = new FormAttachment(100, -2);
        date.setLayoutData(dCombo);
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
        removeAllFilesFromLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.DriveDialog_RemoveAllButtonText,
                SWT.CENTER);
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
        dCombo.right = new FormAttachment(50, -2);
        cDataset.setLayoutData(dCombo);
        cDataset.setItems(getRootItems());

        selectCRS = new Button(panel, SWT.FILL | SWT.PUSH);
        selectCRS.setAlignment(SWT.LEFT);
        dLabel = new FormData();
        dLabel.left = new FormAttachment(50, 5);
        dLabel.right = new FormAttachment(100, -5);
        dLabel.top = new FormAttachment(cDataset, 5, SWT.CENTER);
        selectCRS.setLayoutData(dLabel);
        selectCRS.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectCRS();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

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

    @Override
    protected void update() {
        // CoordinateReferenceSystem crs = getSelectedCRS();
        // selectCRS.setText(String.format("CRS: %s", crs.getName().toString()));
        super.updateNew();
    }

    /**
     * Gets the root items.
     * 
     * @return the root items
     */
    private String[] getRootItems() {
        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();

            for (IDriveModel model : projectModel.findAllDriveModels()) {
                String id = model.getName();
                dataset.put(id, model);
            }
        } catch (AWEException e) {
            LOGGER.error("Error while getRootItems work", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        String[] result = dataset.keySet().toArray(new String[] {});
        Arrays.sort(result);
        
        DatabaseManagerFactory.getDatabaseManager().commit();
        
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

    /**
     * change dataset selection
     */
    protected void changeDatasetSelection() {
        datasetName = cDataset.getText();
        names.put("Dataset", datasetName);
        getNewConfigurationData().setDatasetNames(names);
        update();
    }

    /**
     * Creates actions for buttons
     * 
     * @param parentShell Dialog Shell
     */

    private void createActions(final Shell parentShell) {

        // opens Dialog for choosing files
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
                        if (cDataset.getText().isEmpty()) {
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

        // adds selected files to files to load
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

        // removes selected files from files to load
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

        // if list doesn't contain this item than add it
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

        // if list doesn't contain this item than add it
        if (!loadedFiles.containsKey(name)) {
            loadedFiles.put(name, path);

            filesToLoadList.add(name);
        }

        // if added file already contains in FolderFilesList than remove it from FolderFilesList
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
        ArrayList<File> fileToLoad = new ArrayList<File>();
        if (loadedFiles != null) {
            for (String file : loadedFiles.values()) {
                fileToLoad.add(new File(file));
            }
        }
        getNewConfigurationData().setSourceFile(fileToLoad);
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

    @Override
    protected boolean validateConfigData(ConfigurationDataImpl configurationData) {
        String rootName = configurationData.getDatasetNames().get("Dataset");
        if (StringUtils.isEmpty(rootName)) {
            setMessage("Select dataset", DialogPage.ERROR);
            return false;
        }
        java.util.List<File> files = configurationData.getFilesToLoad();
        if (files == null || files.isEmpty()) {
            setMessage("Select files for import", DialogPage.ERROR);
            return false;
        }
        if (getNewSelectedLoader() == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE, DialogPage.ERROR);
            return false;
        }
        try {
            names.put("Project", ProjectModel.getCurrentProjectModel().getName());
        } catch (AWEException e) {
            // TODO Handle AWEException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        configurationData.setDatasetNames(names);
        // configurationData.setCrs(getSelectedCRS());
        // Calendar cl = configurationData.getDatasetNames().get("workdate");
        // if (cl == null) {
        // cl = Calendar.getInstance();
        // cl.set(Calendar.HOUR, 0);
        // cl.set(Calendar.MINUTE, 0);
        // cl.set(Calendar.SECOND, 0);
        // cl.set(Calendar.MILLISECOND, 0);
        // configurationData.getAdditionalProperties().put("workdate", cl);
        // }
        // cl.set(Calendar.YEAR, date.getYear());
        // cl.set(Calendar.MONTH, date.getMonth());
        // cl.set(Calendar.DAY_OF_MONTH, date.getDay());

        Result result = getNewSelectedLoader().getValidator().isValid(configurationData);
        String messaString = getNewSelectedLoader().getValidator().getMessages();
        if (result == Result.FAIL) {
            setMessage(String.format(messaString, getNewSelectedLoader().getLoaderInfo().getName()), DialogPage.ERROR);
            return false;
        } else if (result == Result.UNKNOWN) {
            setMessage(String.format(messaString, getNewSelectedLoader().getLoaderInfo().getName()), DialogPage.WARNING);
        } else {
            setMessage(""); //$NON-NLS-1$
        }
        return true;
    }
}
