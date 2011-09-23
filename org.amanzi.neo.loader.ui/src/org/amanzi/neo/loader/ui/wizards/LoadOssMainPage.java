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
import java.util.Collection;
import java.util.HashMap;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.db.manager.DatabaseManager.DatabaseAccessType;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.amanzi.neo.loader.ui.utils.LoaderUiUtils;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Load OSS main page
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LoadOssMainPage extends LoaderPage<CommonConfigData>{
    private boolean manualDatasetEdit;
    private Combo dataset;
    // private final Object mon=new Object();
    private final GraphDatabaseService service;
    protected String datasetName;
    private DirectoryFieldEditor editorDir;
    private String directory;
    private Combo cOssType;
    private HashMap<String, Node> ossMap;
    private FileFieldEditorExt editorFile;

    public LoadOssMainPage() {
        super("ossMainPage");
        setTitle(NeoLoaderPluginMessages.GpehTitle);
        setDescription(NeoLoaderPluginMessages.GpehDescr);
        service = NeoServiceProviderUi.getProvider().getService();
    }


    @Override
    public void createControl(Composite parent) {
        manualDatasetEdit = false;
        final Composite main = new Composite(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        Label label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.GpehLbOSS);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        dataset = new Combo(main, SWT.DROP_DOWN);
        dataset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        dataset.setItems(getOssData());
        dataset.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!dataset.isEnabled()) {
                    return;
                }
                datasetName = dataset.getText();
                manualDatasetEdit = !StringUtils.isEmpty(datasetName);
                update();
            }
        });
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!dataset.isEnabled()) {
                    return;
                }
                datasetName = dataset.getText();
                manualDatasetEdit = true;
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        editorDir = new DirectoryEditor("editor", NeoLoaderPluginMessages.AMSImport_directory, main); // NON-NLS-1
        editorDir.setEmptyStringAllowed(true);
        editorDir.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!editorDir.getTextControl(main).isEnabled()) {
                    return;
                }
                editorFile.setEnabled(false, main);
                dataset.setEnabled(false);
                if (!manualDatasetEdit) {
                    datasetName = getDatasetDefaultName(editorDir.getTextControl(main).getText());
                    dataset.setText(datasetName);
                }
                String stringValue = editorDir.getStringValue();
                if (StringUtils.isEmpty(stringValue)) {
                    if (StringUtils.isEmpty(editorFile.getStringValue())) {
                        setFileName(stringValue);
                    }
                } else {
                    editorFile.setStringValue(" ");
                    setFileName(stringValue);
                }
                dataset.setEnabled(true);
                editorFile.setEnabled(true, main);
            }
        });
        editorFile = new FileFieldEditorExt(NeoLoaderPluginMessages.GpehImportDirEditorTitle, "Load single file: ", main);
        editorFile.setEmptyStringAllowed(true);
        editorFile.setDefaulDirectory(LoaderUiUtils.getDefaultDirectory());
        editorFile.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (!editorFile.getTextControl(main).isEnabled()) {
                    return;
                }
                editorDir.setEnabled(false, main);
                dataset.setEnabled(false);
                if (!manualDatasetEdit) {
                    datasetName = getDatasetDefaultName(editorFile.getStringValue());
                    dataset.setText(datasetName);
                }
                String stringValue = editorFile.getStringValue();
                if (StringUtils.isEmpty(stringValue)) {
                    if (StringUtils.isEmpty(editorDir.getStringValue())) {
                        setFileName(stringValue);
                    }
                } else {
                    editorDir.setStringValue("");
                    setFileName(stringValue);
                }
                dataset.setEnabled(true);
                editorDir.setEnabled(true, main);
            }
        });
        label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.OSS);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        cOssType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        cOssType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        cOssType.setItems(getLoadersDescriptions());
        cOssType.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectLoader(cOssType.getSelectionIndex());
                update();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        new Label(main,SWT.NONE);
        final Button batchMode = new Button(main,SWT.CHECK);
        batchMode.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setAccessType(batchMode.getSelection());
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        batchMode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        batchMode.setText("batch mode");
        setAccessType(false);
        batchMode.setSelection(false);
        setControl(main);
        update();
    }




    /**
     * set File name
     * 
     * @param dirName - dir. name
     */
    protected void setFileName(String dirName) {
        directory = dirName;
        LoaderUiUtils.setDefaultDirectory(dirName);
        autoDefineOSSType();
        update();
    }

    /**
     *auto defined type of oss datas
     */
    private void autoDefineOSSType() {
        // TODO implement!
    }


    /**
     *get all OSS
     * 
     * @return
     */
    private String[] getOssData() {
        Collection<Node> allOss = NeoUtils.getAllOss(service);
        ossMap = new HashMap<String, Node>();
        for (Node node : allOss) {
            ossMap.put(NeoUtils.getNodeName(node), node);
        }
        final String[] result = ossMap.keySet().toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }

    /**
     * @return Returns the datasetName.
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * @return Returns the directory.
     */
    public String getDirectory() {
        return directory;
    }

    @Override
    protected boolean validateConfigData(CommonConfigData configurationData) {
        try {
            if (StringUtils.isEmpty(directory) || StringUtils.isEmpty(datasetName) || getSelectedLoader() == null) {
                return false;
            }
            File file = new File(directory);
            if (!(file.isAbsolute() && file.exists())) {
                return false;
            }
            configurationData.setProjectName(LoaderUiUtils.getAweProjectName());
            configurationData.setDbRootName(datasetName);
            configurationData.setRoot(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     *
     * @param batchMode
     */
    protected void setAccessType(final boolean batchMode) {
        ((AbstractLoaderWizard<?>)getWizard()).setAccessType(batchMode?DatabaseManager.DatabaseAccessType.BATCH:DatabaseAccessType.EMBEDDED);
    }
    public static String getDatasetDefaultName(String aFileName) {
        if(aFileName == null){
            return "";
        }
        String result = aFileName;
        int index = result.lastIndexOf(File.separator);
        if (index > 0) {
            return result.substring(index + 1);
        }
        return result;      
    }
}
