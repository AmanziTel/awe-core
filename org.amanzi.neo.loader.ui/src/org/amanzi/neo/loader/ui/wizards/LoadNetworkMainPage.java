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
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.IValidateResult.Result;
import org.amanzi.neo.loader.core.saver.IData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class LoadNetworkMainPage extends AbstractNetworkLoaderMainPage<ConfigurationDataImpl> {
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
    private String fullFileName;
    private String fileWithoutExtension;

    private boolean tryForDefault = false;
    private FileFieldEditorExt castedEditor;

    /**
     * Instantiates a new load network main page.
     */
    public LoadNetworkMainPage() {
        super(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR);
        setTitle(NeoLoaderPluginMessages.NetworkSiteImportWizard_PAGE_DESCR);
        rootName = StringUtils.EMPTY;

    }

    @Override
    protected void update() {
        super.commonUpdate();
    }

    /**
     * Sets file name
     * 
     * @param fileName file name
     * @return configured loader or null if there was an error
     */
    protected ILoader< ? extends IData, ConfigurationDataImpl> setFileName(String fileName) {
        if (this.fullFileName != null && this.fullFileName.equals(fileName)) {
            return null;
        }
        this.fullFileName = fileName;
        rootName = new java.io.File(getFileName()).getName();
        rootName = rootName.substring(0, rootName.lastIndexOf('.'));
        fileWithoutExtension = rootName;
        List<File> files = new LinkedList<File>();
        files.add(new File(fileName));
        ConfigurationDataImpl config = getConfigurationData();
        config.setSourceFile(files);
        ILoader< ? extends IData, ConfigurationDataImpl> loader = autodefineNew(config);
        int id = setSelectedLoader(loader);
        if (id >= 0) {
            cbLoaders.select(id);
        }
        LoaderUiUtils.setDefaultDirectory(castedEditor.getDefaulDirectory());

        return loader;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fullFileName;
    }

    @Override
    protected boolean validateConfigData(ConfigurationDataImpl configurationData) {
        if (fullFileName == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        File file = new File(fullFileName);
        if (!(file.isAbsolute() && file.exists())) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_FILE, DialogPage.ERROR);
            return false;
        }
        if (StringUtils.isEmpty(rootName)) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_NETWORK, DialogPage.ERROR);
            return false;
        }
        if (getSelectedLoader() == null) {
            setMessage(NeoLoaderPluginMessages.NetworkSiteImportWizardPage_NO_TYPE, DialogPage.ERROR);
            return false;
        }
        IValidateResult.Result result = getSelectedLoader().getValidator().isValid(configurationData);
        if (result == Result.FAIL) {
            if (!tryForDefault && !members.containsKey(fileWithoutExtension) && cbNetwork.getItemCount() != 0) {
                String defaultNetwork = cbNetwork.getItem(0);
                getConfigurationData().getDatasetNames().put(ConfigurationDataImpl.NETWORK_PROPERTY_NAME, defaultNetwork);
                tryForDefault = true;
                int i = 0;
                for (String labels : cbNetwork.getItems()) {
                    if (labels.equals(defaultNetwork)) {
                        cbNetwork.select(i);
                        break;
                    }
                    i++;
                }
            } else if (!tryForDefault && members.containsKey(rootName)) {
                tryForDefault = true;
            } else if (tryForDefault && members.containsKey(fileWithoutExtension)
                    && !cbNetwork.getText().equals(fileWithoutExtension)) {
                int i = 0;
                for (String labels : cbNetwork.getItems()) {
                    if (labels.equals(fileWithoutExtension)) {
                        getConfigurationData().getDatasetNames().put(ConfigurationDataImpl.NETWORK_PROPERTY_NAME,
                                fileWithoutExtension);
                        cbNetwork.select(i);
                        break;
                    }

                    i++;
                }
            }
            if (getSelectedLoader().getValidator().isValid(configurationData) == Result.FAIL) {
                setMessage(String.format(getSelectedLoader().getValidator().getMessages(), getSelectedLoader().getLoaderInfo()
                        .getName()), DialogPage.ERROR);
                return false;
            }

        } else if (result == Result.UNKNOWN) {
            setMessage(
                    String.format(getSelectedLoader().getValidator().getMessages(), getSelectedLoader().getLoaderInfo().getName()),
                    DialogPage.ERROR);
            return false;
        } else {
            setMessage(""); //$NON-NLS-1$
        }
        getConfigurationData().getDatasetNames().put(ConfigurationDataImpl.NETWORK_PROPERTY_NAME, cbNetwork.getText());
        List<File> files = new LinkedList<File>();
        files.add(file);
        getConfigurationData().setSourceFile(files);
        LoaderUiUtils.setDefaultDirectory((files.get(0).getAbsolutePath()));
        return true;
    }

    @Override
    protected void createEditor() {
        editor = new FileFieldEditorExt("fileSelectNeighb", NeoLoaderPluginMessages.NetworkSiteImportWizard_FILE, main); // NON-NLS-1 //$NON-NLS-1$
        castedEditor = (FileFieldEditorExt)editor;
        castedEditor.setDefaulDirectory(LoaderUiUtils.getDefaultDirectory());
        castedEditor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                handleEditorModification(e);
            }
        });
        castedEditor.setFileExtensions(NETWORK_FILE_EXTENSIONS);
        castedEditor.setFileExtensionNames(NETWORK_FILE_NAMES);
        castedEditor.setFocus();
    }

    @Override
    protected void handleEditorModification(EventObject event) {
        setFileName(castedEditor.getStringValue());
        cbNetwork.setText(rootName);
        tryForDefault = false;
    }

    @Override
    protected Integer defineLoaders() {
        return -1;
    }

    @Override
    protected void handleLoaderSelection() {
        selectLoader(cbLoaders.getSelectionIndex());
        autodefineNew(getConfigurationData());
        update();
    }

}
