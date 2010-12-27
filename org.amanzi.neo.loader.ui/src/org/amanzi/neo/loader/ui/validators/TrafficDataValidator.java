package org.amanzi.neo.loader.ui.validators;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.core.ValidateResultImpl;
import org.amanzi.neo.loader.core.IValidateResult.Result;

public class TrafficDataValidator implements ILoaderInputValidator<CommonConfigData> {

	@Override
	public IValidateResult validate(CommonConfigData data) {
		// TODO Auto-generated method stub
		return new ValidateResultImpl(Result.SUCCESS, "");
	}

	@Override
	public void filter(CommonConfigData data) {
		// TODO Auto-generated method stub
		
	}

}
