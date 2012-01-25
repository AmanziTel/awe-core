package org.amanzi.neo.loader.ui.validators;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.ui.validators.IValidateResult.Result;

public class AbstractNetworkValidator implements IValidator<NetworkConfiguration>{
    
    protected String[] possibleFieldSepRegexes = new String[] {"\t",",",";"};

    @Override
    public Result appropriate(List<File> filesToLoad) {
        return null;
    }

    @Override
    public IValidateResult validate(NetworkConfiguration configuration) {
        return null;
    }

}
