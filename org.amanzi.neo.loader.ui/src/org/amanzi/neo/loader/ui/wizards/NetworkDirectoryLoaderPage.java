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
import java.util.List;

import org.amanzi.neo.loader.core.config.AntennaConfiguration;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * TODO Purpose of 
 * <p>
 * Loading directory of antenna patterns files
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class NetworkDirectoryLoaderPage extends AbstractLoaderPage<AntennaConfiguration> implements ModifyListener {

    //field for directory
    private DirectoryEditor directoryEditor;

    //combo for dataset
    private DatasetCombo datasetCombo;

    //combo for loaders
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
            AntennaConfiguration configuration = getConfiguration();

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
        AntennaConfiguration configuration = getConfiguration();

        configuration.setDatasetName(datasetCombo.getDatasetName());

        super.updateState();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        AntennaConfiguration configuration = getConfiguration();
        configuration.getFilesToLoad().clear();

        if (!StringUtils.isEmpty(directoryEditor.getStringValue())) {
            File folder = new File(directoryEditor.getStringValue());
            File[] files = folder.listFiles();

            List<File> listOfFiles = new ArrayList<File>(Arrays.asList(files));
            configuration.setFiles(listOfFiles);

            loaderCombo.autodefineLoader();
        }

        updateState();
    }

    @Override
    protected AntennaConfiguration createConfiguration() {
        return new AntennaConfiguration();
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
