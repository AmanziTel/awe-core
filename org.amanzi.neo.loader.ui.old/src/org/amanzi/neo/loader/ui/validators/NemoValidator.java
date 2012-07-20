/**
 * 
 */
package org.amanzi.neo.loader.ui.validators;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.config.NemoConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;
import org.amanzi.neo.services.DatasetService.DriveTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.apache.commons.lang3.StringUtils;

/**
 * Nemo validator
 * 
 * @author Bondoronok_P
 */
public class NemoValidator extends AbstractValidator<NemoConfiguration> {

	/**
	 * Extension
	 */
	private static final String NMF = ".nmf";

	/**
	 * Messages
	 */
	private static final String DRIVE_ALREADY_EXSIST = "Drive already exist in model";
	private static final String NO_CONTENT = "The file no contains Nemo data";
	private static final String ERROR = "Error while Nemo data validate";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.amanzi.neo.loader.ui.validators.IValidator#appropriate(java.util.
	 * List)
	 */
	@Override
	public Result appropriate(List<File> filesToLoad) {
		for (File file : filesToLoad) {
			if (!checkFileByExtension(file, NMF)) {
				return Result.FAIL;
			}
		}
		return Result.SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.amanzi.neo.loader.ui.validators.IValidator#validate(org.amanzi.neo
	 * .loader.core.config.IConfiguration)
	 */
	@Override
	public IValidateResult validate(NemoConfiguration filesToLoad) {
		if (filesToLoad.getDatasetName() == null) {
			return new ValidateResultImpl(Result.FAIL, NO_PROJECT);
		}
		try {
			IDriveModel driveModel = findDriveModel(filesToLoad,
					DriveTypes.NEMO_V2);
			if (driveModel != null) {
				return new ValidateResultImpl(Result.FAIL, DRIVE_ALREADY_EXSIST);
			}
			if (appropriate(filesToLoad.getFilesToLoad()) == Result.FAIL) {
				return new ValidateResultImpl(Result.FAIL, NO_CONTENT);
			}
		} catch (AWEException e) {
			return new ValidateResultImpl(Result.FAIL, ERROR);
		}

		return new ValidateResultImpl(Result.SUCCESS, StringUtils.EMPTY);
	}

}
