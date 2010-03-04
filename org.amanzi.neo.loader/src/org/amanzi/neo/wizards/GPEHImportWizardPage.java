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

package org.amanzi.neo.wizards;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.LoaderUtils;
import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;

/**
 * <p>
 * Import gpeh wizard page
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHImportWizardPage extends WizardPage {

    private Combo dataset;
    private final NeoService service;
    protected String datasetName;
    private DirectoryFieldEditor editor;
    private String directory;
    private Combo cOssType;
    protected Pair<OssType, Exception> ossDirType;
    private HashMap<String,Node> ossMap;

    /**
     * Constructor
     * @param pageName
     */
    protected GPEHImportWizardPage(String pageName) {
        super(pageName);
        setTitle(NeoLoaderPluginMessages.GpehTitle);
        setDescription(NeoLoaderPluginMessages.GpehDescr);
        service = NeoServiceProvider.getProvider().getService();
        ossDirType=new Pair<OssType, Exception>(null, null);
        validateFinish();
    }

    @Override
    public void createControl(Composite parent) {
        Composite main = new Composite(parent, SWT.NULL);
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
                datasetName = dataset.getText();
                validateFinish();
            }
        });
        dataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                datasetName = dataset.getText();
                validateFinish();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        editor = new DirectoryEditor(NeoLoaderPluginMessages.GpehImportDirEditorTitle, NeoLoaderPluginMessages.ETSIImport_directory, main); // NON-NLS-1
        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (StringUtils.isEmpty(datasetName)){
                    datasetName=ETSIImportWizardPage.getDatasetDefaultName(editor.getStringValue());
                    dataset.setText(datasetName);
                }
                setFileName(editor.getStringValue());
            }
        });
        label = new Label(main, SWT.LEFT);
        label.setText(NeoLoaderPluginMessages.OSS);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        cOssType = new Combo(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        cOssType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        cOssType.setItems(getOssFileType());
        cOssType.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ossDirType = new Pair<OssType, Exception>(OssType.getEnumById(cOssType.getText()), null);
                validateFinish();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        setControl(main);

    }

    /**
     * 
     * @return oss types
     */
    private String[] getOssFileType() {
        OssType[] types = OssType.values();
        String[] result = new String[types.length];
        for (OssType type : types) {
            result[type.ordinal()] = type.getId();
        }
        return result;
    }

    /**
     * set File name
     * @param dirName - dir. name
     */
    protected void setFileName(String dirName) {
        directory = dirName;
        DriveDialog.setDefaultDirectory(dirName);
        autoDefineOSSType();
        validateFinish();

    }

    /**
     *
     */
    private void autoDefineOSSType() {
        if (StringUtils.isEmpty(directory)) {
            return;
        }
        File file = new File(directory);
        if (!(file.isDirectory() && file.isAbsolute() && file.exists())) {
            return;
        }   
        List<File> files = LoaderUtils.getAllFiles(directory, new FileFilter() {
            
            @Override
            public boolean accept(File arg0) {
                return true;
            }
        });
        if (files.isEmpty()){
            return;
        }
        File fileToAnalyse = files.get(0);
        if (Pattern.matches(".*\\.(xml|XML)$", fileToAnalyse.getName())){
            ossDirType=new Pair<OssType, Exception>(OssType.COUNTER, null);
        }else{
            ossDirType=new Pair<OssType, Exception>(OssType.GPEH, null);
        }
        cOssType.setText(ossDirType.getLeft().getId());
    }

    /**
     *check correct input
     */
    private void validateFinish() {
        setPageComplete(isValidPage());
    }

    /**
     * validate page
     * @return
     */
    protected boolean isValidPage() {
        try {
            if (StringUtils.isEmpty(directory) || StringUtils.isEmpty(datasetName) || ossDirType.left() == null) {
                return false;
            }
            File file = new File(directory);
            if (!(file.isDirectory() && file.isAbsolute() && file.exists())) {
                return false;
            }
            Node ossNode = ossMap.get(datasetName);
            if (ossNode != null) {
                return ossDirType.left() == OssType.getOssType(ossNode, service);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *get all OSS
     * 
     * @return
     */
    private String[] getOssData() {
        Collection<Node> allOss = NeoUtils.getAllOss(service);
        ossMap=new HashMap<String,Node>();
        for (Node node : allOss) {
            ossMap.put(NeoUtils.getNodeName(node, service), node);  
        }
        final String[] result = ossMap.keySet().toArray(new String[0]);
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
    
}
