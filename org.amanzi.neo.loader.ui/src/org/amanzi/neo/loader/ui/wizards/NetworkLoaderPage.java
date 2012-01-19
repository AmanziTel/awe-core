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
import java.util.List;

import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class NetworkLoaderPage extends AbstractLoaderPage<NetworkConfiguration> implements ModifyListener {
    
    private FileFieldEditorExt fileEditor;
    
    private DatasetCombo datasetCombo;
    
    private LoaderCombo loaderCombo;
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        datasetCombo = new DatasetCombo();
        
        fileEditor = new FileFieldEditorExt("lalala", "Select file to load", getMainComposite());
        fileEditor.getTextControl(getMainComposite()).addModifyListener(this);
        
        loaderCombo = new LoaderCombo();
        
        updateState();
        
        initializeFields();
    }

    @Override
    protected boolean validateConfiguration() {
        boolean superResult = super.validateConfiguration();
        
        if (superResult) {
            NetworkConfiguration configuration = getConfiguration();
            
            if (StringUtils.isEmpty(configuration.getDatasetName())) {
                setErrorMessage("Network name is not set");
                
                return false;
            }
            
            return true;
        }
        
        return superResult;
    }
    
    @Override
    protected void updateState() {
        NetworkConfiguration configuration = getConfiguration();
        
        configuration.setDatasetName(datasetCombo.getDatasetName());
        
        super.updateState();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        NetworkConfiguration configuration = getConfiguration();
        configuration.getFilesToLoad().clear();
        
        if (!StringUtils.isEmpty(fileEditor.getStringValue())) {
            File dataFile = new File(fileEditor.getStringValue());
            
            configuration.setFile(dataFile);
            
            String datasetName = getNameFromFile(dataFile);
            
            if (isMain()) {
                datasetCombo.setCurrentDatasetName(datasetName);
            }
            
            loaderCombo.autodefineLoader();
        }
        
        updateState();
    }

    @Override
    protected NetworkConfiguration createConfiguration() {
        return new NetworkConfiguration();
    }

    @Override
    protected void updateValues() {
        datasetCombo.setCurrentDatasetName(getConfiguration().getDatasetName());
    }

    @Override
    protected void initializeFields() {
        //intialize network names
        try { 
            List<String> networkNames = new ArrayList<String>();
            for (INetworkModel networkModel : ProjectModel.getCurrentProjectModel().findAllNetworkModels()) {
                networkNames.add(networkModel.getName());
            }
            
            datasetCombo.setDatasetNames(networkNames);
        } catch (AWEException e) {
            //TODO: handle
        }
    }
}
