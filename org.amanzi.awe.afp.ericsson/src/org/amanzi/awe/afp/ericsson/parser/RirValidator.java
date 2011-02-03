package org.amanzi.awe.afp.ericsson.parser;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoaderInputValidator;
import org.amanzi.neo.loader.core.IValidateResult;
import org.amanzi.neo.loader.ui.validators.ValidatorUtils;

/**
 * <p>
 * Validator for RIR data
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RirValidator implements ILoaderInputValidator<CommonConfigData> {

    @Override
    public IValidateResult validate(CommonConfigData data) {
        return ValidatorUtils.checkRootExist(data);
    }

    @Override
    public void filter(CommonConfigData data) {
        File file = data.getRoot();
        if (file != null && !file.isDirectory()) {
            data.setRoot(null);
        }
        List<File> files = data.getFileToLoad();
        if (files != null) {
            List<File> newList = new LinkedList<File>();
            for (File singlFile : files) {
                if (singlFile.isDirectory()) {
                    newList.add(singlFile);
                }
            }
            if (files.size() != newList.size()) {
                data.setFileToLoad(newList);
            }
        }
    }


    public void filterRir(CommonConfigData configurationData) {
        Collection<File> files = (Collection<File>)configurationData.getAdditionalProperties().get("RIR_FILES");

        if (files != null) {
            List<File> newList = new LinkedList<File>();
            for (File singlFile : files) {
                if (singlFile.isDirectory()) {
                    newList.add(singlFile);
                }
            }
            if (files.size() != newList.size()) {
                configurationData.getAdditionalProperties().put("RIR_FILES", files);
            }
        }
    }

}
