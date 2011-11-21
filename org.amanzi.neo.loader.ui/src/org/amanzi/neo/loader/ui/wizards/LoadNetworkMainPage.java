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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.ILoaderNew;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INetworkType;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class LoadNetworkMainPage extends LoaderPageNew<ConfigurationDataImpl> {
    private static Logger LOGGER = Logger.getLogger(LoadNetworkMainPage.class);
    /*
     * Names of supported files for Network
     */
    public static final String[] NETWORK_FILE_NAMES = {"All supported (*.*)", "Comma Separated Values Files (*.csv)",
            "Plain Text Files (*.txt)", "OpenOffice.org Spreadsheet Files (*.sxc)", "Microsoft Excel Spreadsheet Files (*.xls)",
            "eXtensible Markup Language Files (*.xml)"};

    /*
     * Extensions of supported files for Network
     */
    public static final String[] NETWORK_FILE_EXTENSIONS = {"*.*", "*.csv", "*.txt", "*.sxc", "*.xls", "*.xml"};

    /** The Constant PAGE_DESCR. */
    private String fileName;
    private Composite main;
    protected Combo network;
    private FileFieldEditorExt editor;
    private HashMap<String, INetworkModel> members;
    
    protected INetworkModel networkModel;
    private Label labNetworkDescr;
    private Combo networkType;
    protected String networkName = ""; //$NON-NLS-1$

    /**
     * Instantiates a new load network main page.
     */
    public LoadNetworkMainPage() {
        super("mainNetworkPage");
        setTitle(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR);
        networkModel = null;
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_NETWORK);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        network = new Combo(main, SWT.DROP_DOWN);
        network.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        network.setItems(getRootItems());
        network.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                changeNetworkName();
            }
        });
        network.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeNetworkName();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        editor = new FileFieldEditorExt("fileSelectNeighb", NeoLoaderPluginMessages.NetworkSiteImportWizard_FILE, main); // NON-NLS-1 //$NON-NLS-1$
        editor.setDefaulDirectory(LoaderUiUtils.getDefaultDirectory());

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                network.setText(networkName);
                changeNetworkName();
                // updateCRS();
                update();
            }
        });

        editor.setFileExtensions(NETWORK_FILE_EXTENSIONS);
        editor.setFileExtensionNames(NETWORK_FILE_NAMES);
        label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.NetworkSiteImportWizard_DATA_TYPE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 3));
        networkType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        networkType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 3));
        networkType.setItems(getNewLoadersDescriptions());
        networkType.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectNewLoader(networkType.getSelectionIndex());
                // updateCRS();
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        new Label(main, SWT.NONE);
        // LN, 28.02.2011, batch mode removed
        labNetworkDescr = new Label(main, SWT.LEFT);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL, SWT.CENTER, true, false, 3, 1);
        layoutData.minimumWidth = 150;
        editor.setFocus();
        setControl(main);
        update();
    }

    @Override
    protected void update() {
        super.updateNew();
    }

    /**
     *
     */
    protected void updateLabelNetwDescr() {
        String text = ""; //$NON-NLS-1$
        if (networkModel != null) {
            INetworkType type = networkModel.getNetworkType();
            if (type != null) {
                text = "Network type: " + type.getId(); //$NON-NLS-1$
            }
        }
        labNetworkDescr.setText(text);
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     * @return configured loader or null if there was an error
     */
    protected ILoaderNew< ? extends IData, ConfigurationDataImpl> setFileName(String fileName) {
        if (this.fileName != null && this.fileName.equals(fileName)) {
            return null;
        }
        this.fileName = fileName;
        networkName = new java.io.File(getFileName()).getName();
        networkName = networkName.substring(0, networkName.lastIndexOf('.'));
        // CommonConfigData configurationData = getConfigurationData();
        List<File> files = new LinkedList<File>();
        files.add(new File(fileName));
        getNewConfigurationData().setSourceFile(files);

        // config.getFilesToLoad()
        // configurationData.setRoot(new File(fileName));
        ILoaderNew< ? extends IData, ConfigurationDataImpl> loader = autodefineNew(getNewConfigurationData());
        int id = setSelectedLoaderNew(loader);
        if (id >= 0) {
            networkType.select(id);
        }
        update();
        // editor.store();
        LoaderUiUtils.setDefaultDirectory(editor.getDefaulDirectory());

        return loader;
    }

    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getRootItems() {
        members = new HashMap<String, INetworkModel>();

        try {
            IProjectModel projectModel = ProjectModel.getCurrentProjectModel();

            for (INetworkModel model : projectModel.findAllNetworkModels()) {
                String id = model.getName();
                members.put(id, model);
                
            }
        } catch (AWEException e) {
            LOGGER.error("Error while getRootItems work", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        String[] result = members.keySet().toArray(new String[] {});
        Arrays.sort(result);
        
        DatabaseManagerFactory.getDatabaseManager().commit();
        
        return result;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Returns the networkName.
     */
    public String getNetworkName() {
        return networkName;
    }

    protected void changeNetworkName() {
        networkName = network.getText();
        if (members != null && !members.isEmpty()) {
            networkModel = members.get(networkName);
        }
        getNewConfigurationData().getDatasetNames().put("Network", networkName);
        updateLabelNetwDescr();
        update();
    }

    @Override
    protected boolean validateConfigData(ConfigurationDataImpl configurationData) {
        // TODO must be refactoring after change loaders
        if (fileName == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        File file = new File(fileName);
        if (!(file.isAbsolute() && file.exists())) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        if (StringUtils.isEmpty(networkName)) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_NETWORK, DialogPage.ERROR);
            return false;
        }
        if (getNewSelectedLoader() == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE, DialogPage.ERROR);
            return false;
        }
        IValidateResult.Result result = getNewSelectedLoader().getValidator().isValid(configurationData);
        if (result == Result.FAIL) {
            setMessage(String.format(getNewSelectedLoader().getValidator().getMessages(), getNewSelectedLoader().getLoaderInfo()
                    .getName()), DialogPage.ERROR);
            return false;
        } else if (result == Result.UNKNOWN) {
            setMessage(String.format(getNewSelectedLoader().getValidator().getMessages(), getNewSelectedLoader().getLoaderInfo()
                    .getName()), DialogPage.WARNING);
        } else {
            setMessage(""); //$NON-NLS-1$
        }
        getNewConfigurationData().getDatasetNames().put("Network", networkName);
        List<File> files = new LinkedList<File>();
        files.add(file);
        getNewConfigurationData().setSourceFile(files);
        // configurationData.setProjectName(LoaderUiUtils.getAweProjectName());
        // configurationData.setCrs(getSelectedCRS());
        // configurationData.setDbRootName(networkName);
        // configurationData.setRoot(file);

        return true;
    }

}
