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

package org.amanzi.awe.afp.testing.engine;

import java.io.File;
import java.util.ArrayList;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.ILoader;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class LoadNetworkConfigDataAction extends AbstractLoadAction {
    
    private static final Logger LOGGER = Logger.getLogger(LoadNetworkConfigDataAction.class);
    
    /**
     * @param file
     * @param projectName
     * @param rootName
     */
    public LoadNetworkConfigDataAction(File file, String projectName, String rootName) {
        super(file, projectName, rootName);
    }

    @Override
    protected CommonConfigData getConfigData() {
        CommonConfigData configData = super.getConfigData();
        
        LOGGER.info(file.getAbsolutePath());
        LOGGER.info(file.isDirectory());
        LOGGER.info(file.listFiles());
        if (file.listFiles() != null) {
            LOGGER.info(file.listFiles().length);
        }
        

        ArrayList<File> cnaFiles = new ArrayList<File>();
        ArrayList<File> bsmFiles = new ArrayList<File>();
        ArrayList<File> currentList = null;
        for (File singleFile : file.listFiles()) {
            if (singleFile.isDirectory()) {
                if (singleFile.getName().contains("CNA")) {
                    currentList = cnaFiles;
                } else if (singleFile.getName().contains("BSM")) {
                    currentList = bsmFiles;
                }
                
                for (File subFile : singleFile.listFiles()) {
                    if (!subFile.isDirectory()) {
                        currentList.add(subFile);
                    }
                }
            }            
        }
        
        configData.setFileToLoad(cnaFiles);
        configData.getAdditionalProperties().put("BSM_FILES", bsmFiles);

        return configData;      
    }

    @Override
    protected ILoader< ? , CommonConfigData> getLoader() {
        return FakeLoaderFactory.getNetworkConfigLoader();
    }

}
