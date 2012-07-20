/**
 * 
 */
package org.amanzi.neo.loader.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.config.NemoConfiguration;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Nemo Loader Page
 * 
 * @author Bondoronok_P
 */
public class NemoLoaderPage extends AbstractLoaderPage<NemoConfiguration>
		implements ModifyListener {

	private static final String SELECT_FILE_TITLE = "Select file to load:";

	private DatasetCombo datasetCombo;

	private DataChooserField chooserField;

	private LoaderCombo loaderCombo;

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		datasetCombo = new DatasetCombo();
		chooserField = new DataChooserField(FileFieldType.FILE,
				SELECT_FILE_TITLE);
		chooserField.addModifyListener(this);
				
		loaderCombo = new LoaderCombo();

		updateState();
		initializeFields();
	}

	@Override
	protected void updateState() {
		NemoConfiguration configuration = getConfiguration();
		configuration.setDatasetName(datasetCombo.getDatasetName());
		super.updateState();
	}

	@Override
	public void modifyText(ModifyEvent e) {
		NemoConfiguration configuration = getConfiguration();
		configuration.getFilesToLoad().clear();

		if (!StringUtils.isEmpty(chooserField.getFileName())) {
			File dataFile = chooserField.getFile();

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
	protected NemoConfiguration createConfiguration() {
		return new NemoConfiguration();
	}

	@Override
	protected void updateValues() {
		datasetCombo.setCurrentDatasetName(getConfiguration().getDatasetName());
	}

	/**
	 * Initialize Loaded Drive Model's
	 */
	protected void initializeFields() {
		try {
			List<String> driveNames = new ArrayList<String>();
			for (IDriveModel driveModel : ProjectModel.getCurrentProjectModel()
					.findAllDriveModels()) {
				driveNames.add(driveModel.getName());
			}

			datasetCombo.setDatasetNames(driveNames);
		} catch (AWEException e) {
			// TODO: handle
		}
	}
}
