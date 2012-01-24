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
import java.io.FileFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.ui.loaders.ILoader;
import org.amanzi.neo.loader.ui.utils.FileSelection;
import org.amanzi.neo.loader.ui.validators.IValidateResult;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractLoaderPage<T extends IConfiguration> extends WizardPage implements IWizardPage, IExecutableExtension {
    
    private static final GridLayout STANDARD_LOADER_PAGE_LAYOUT = new GridLayout(3, false);
    
    private static final int EDITABLE_COMBO_STYLE = SWT.DROP_DOWN;
    
    private static final int NON_EDITABLE_COMBO_STYLE = SWT.DROP_DOWN | SWT.READ_ONLY;
    
    protected enum FileFieldType {
        DIRECTORY,
        FILE;
    }
    
    protected class FileSelectionComponent {
        
        private FileSelection viewer;
        
        public FileSelectionComponent(String labelText, boolean showFiles) {
            viewer = new FileSelection(showFiles, labelText);
            viewer.createPartControl(getMainComposite());
            
            viewer.getTreeViewer().getTree().setLayoutData(getFileSelectionLayout());
        }
        
        public void addSelectionChangedListener(final ISelectionChangedListener listener) {
            viewer.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
                
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    listener.selectionChanged(event);
                    viewer.storeDefSelection(null);
                }
            });
        }
        
        public List<File> getFiles() {
            return getFiles(null);
        }
        
        public List<File> getFiles(FileFilter filter) {
            return viewer.getSelectedFiles(filter);
        }
        
    }
    
    protected class DataChooserField {
        
        private StringButtonFieldEditor editor;
        
        public DataChooserField(FileFieldType fieldType, String labelText) {
            switch (fieldType) {
            case DIRECTORY:
                editor = new DirectoryEditor("directory", labelText, getMainComposite());
                break;
            case FILE:
                editor = new FileFieldEditorExt("file", labelText, getMainComposite());
                break;
            }
        }
        
        public void addModifyListener(ModifyListener listener) {
            editor.getTextControl(getMainComposite()).addModifyListener(listener);
        }
        
        public String getFileName() {
            return editor.getStringValue();
        }
        
        public File getFile() {
            return new File(editor.getStringValue());
        }
        
    }
    
    protected class UpdateStateListener implements SelectionListener, ModifyListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            updateState();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        @Override
        public void modifyText(ModifyEvent e) {
            updateState();
        }
        
    }
    
    protected class DatasetCombo {
        
        private Combo datasetCombo;
        
        public DatasetCombo() {
            createLabel(getMainComposite(), "Dataset");
            
            datasetCombo = createCombo(getMainComposite(), true, isMain());
            datasetCombo.setText(getConfiguration().getDatasetName());
            
            datasetCombo.addModifyListener(UPDATE_STATE_LISTENER);
            
        }
        
        public String getDatasetName() {
            return datasetCombo.getText();
        }
        
        public void setDatasetNames(List<String> datasetNames) {
            datasetCombo.setItems(datasetNames.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
        }
        
        public void setCurrentDatasetName(String datasetName) {
            datasetCombo.setText(datasetName);
        }
        
    }
    
    protected class LoaderCombo implements SelectionListener {
        
        private Combo loaderCombo;
        
        public LoaderCombo() {
            createLabel(getMainComposite(), "Data type");
            
            loaderCombo = createCombo(getMainComposite(), false, enableLoaderCombo());
            
            loaderCombo.setItems(pageLoaders.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
            
            if (!enableLoaderCombo()) {
                setCurrentLoader(pageLoaders.values().iterator().next());
            }
            
            loaderCombo.addSelectionListener(this);
        }
        
        public ILoader getCurrentLoader() {
            currentLoader = pageLoaders.get(loaderCombo.getText());
            return currentLoader;
        }
        
        public void setCurrentLoader(ILoader loader) {
            currentLoader = loader;
            loaderCombo.setText(currentLoader.getName());
        }
        
        public void autodefineLoader() {
            ILoader definedLoader = null;
            for (ILoader loader : pageLoaders.values()) {
                Result result = loader.getValidator().appropriate(getConfiguration().getFilesToLoad());
                
                if (result == Result.UNKNOWN && definedLoader == null) {
                    definedLoader = loader;
                    setMessage("Loader defined with warnings", WARNING);
                } 
                if (result == Result.SUCCESS) {
                    definedLoader = loader;
                    break;
                }
            }
            
            if (definedLoader != null) {
                setCurrentLoader(definedLoader);
            }
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            getCurrentLoader();
            
            updateState();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
        
    }
    
    protected final UpdateStateListener UPDATE_STATE_LISTENER = new UpdateStateListener();
    
    private boolean isMain = false;
    
    private int priority = 0;
    
    private Map<String, ILoader> pageLoaders = new HashMap<String, ILoader>();
    
    private ILoader currentLoader;
    
    private Composite mainComposite;
    
    private T configuration;
    
    /**
     * @param pageName
     */
    protected AbstractLoaderPage() {
        super(StringUtils.EMPTY);
        
        setPageComplete(false);
    }
    
    protected String getNameFromFile(File dataFile) {
        int pointIndex = dataFile.getName().lastIndexOf(".");
        
        if (pointIndex > 0) {
            return dataFile.getName().substring(0, pointIndex);
        } 
        return dataFile.getName();
    }

    
        
    @Override
    public void createControl(Composite parent) {
        mainComposite = new Group(parent, SWT.NONE);
        mainComposite.setLayout(STANDARD_LOADER_PAGE_LAYOUT);
        
        setControl(mainComposite);
    }
    
    protected Composite getMainComposite() {
        return mainComposite;
    }

    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
    }

    /**
     * @return Returns the isMain.
     */
    public boolean isMain() {
        return isMain;
    }

    /**
     * @param isMain The isMain to set.
     */
    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void addLoader(ILoader newLoader) {
        pageLoaders.put(newLoader.getName(), newLoader);
    }
    
    protected boolean enableLoaderCombo() {
        return pageLoaders.size() > 1;
    }
    
    protected boolean enableDatasetNameCombo() {
        return isMain;
    }
    
    public ILoader getLoader() {
        return currentLoader;
    }
    
    protected void updateState() {
        setPageComplete(checkPage());
    }
    
    protected boolean checkPage() {
        setErrorMessage(null);
        
        if (!validateConfiguration()) {
            return false;
        }
        
        if (currentLoader == null) {
            setErrorMessage("Data Type not selected");
            
            return false;
        }
        
        IValidateResult result = currentLoader.getValidator().validate(getConfiguration().getFilesToLoad());
        if (result.getResult() == Result.FAIL) {
            setErrorMessage(result.getMessages());
            
            return false;
        }
        
        return true;
    }
    
    protected boolean validateConfiguration() {
        T configuration = getConfiguration();
        
        if (configuration.getFilesToLoad().isEmpty()) {
            setErrorMessage("No files to load");
            
            return false;
        }
        
        for (File file : configuration.getFilesToLoad()) {
            if (file == null) {
                setErrorMessage("File cannot be null");
                
                return false;
            }
            
            if (!file.exists()) {
                setErrorMessage("File <" + file + "> doesn't exists");
                
                return false;
            }
        }
        
        return true;
    }
    
    protected void createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(getLabelLayout());
        label.setText(text);
    }
    
    protected Combo createCombo(Composite parent, boolean editable, boolean enabled) {
        int style = NON_EDITABLE_COMBO_STYLE;
        if (editable) {
            style = EDITABLE_COMBO_STYLE;
        }
        
        Combo result = new Combo(parent, style);
        result.setLayoutData(getComboLayout());
        result.setEnabled(enabled);
        
        return result;
    }
    
    private static GridData getLabelLayout() {
        return  new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }
    
    private static GridData getFileSelectionLayout() { 
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 3;
        gridData.minimumHeight = 500;
        gridData.heightHint = 300;
        
        return gridData;
    }
    
    private static GridData getComboLayout() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
    }
    
    public T getConfiguration() {
        if (configuration == null) {
            configuration = createConfiguration();
        }
        
        return configuration;
    }
    
    protected abstract T createConfiguration();
    
    protected abstract void updateValues();
    
    protected abstract void initializeFields();
}
