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

public class NetworkDirectoryLoaderPage extends AbstractLoaderPage<NetworkConfiguration> implements ModifyListener {

    private DirectoryEditor directoryEditor;

    private DatasetCombo datasetCombo;

    private LoaderCombo loaderCombo;

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);

        datasetCombo = new DatasetCombo();

        directoryEditor = new DirectoryEditor("lalala", "Select directory to load", getMainComposite());
        directoryEditor.getTextControl(getMainComposite()).addModifyListener(this);

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

        if (!StringUtils.isEmpty(directoryEditor.getStringValue())) {
            /*
             * String path = "."; String files; File folder = new File(path); File[] listOfFiles =
             * folder.listFiles(); for (int i = 0; i < listOfFiles.length; i++) { if
             * (listOfFiles[i].isFile()) { files = listOfFiles[i].getName();
             * System.out.println(files); } }
             */
            File folder = new File(directoryEditor.getStringValue());
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {

                configuration.setFile(listOfFiles[i]);                              
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
        // intialize network names
        try {
            List<String> networkNames = new ArrayList<String>();
            for (INetworkModel networkModel : ProjectModel.getCurrentProjectModel().findAllNetworkModels()) {
                networkNames.add(networkModel.getName());
            }

            datasetCombo.setDatasetNames(networkNames);
        } catch (AWEException e) {
            // TODO: handle
        }
    }

}
