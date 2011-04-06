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

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class LoadNetworkMeasurementDataAction extends AbstractLoadAction {

    /**
     * @param file
     * @param projectName
     * @param rootName
     */
    public LoadNetworkMeasurementDataAction(File file, String projectName, String rootName) {
        super(file, projectName, rootName);
    }
    
    @Override
    protected CommonConfigData getConfigData() {
        CommonConfigData configData = super.getConfigData();

        ArrayList<File> barFiles = new ArrayList<File>();
        ArrayList<File> rirFiles = new ArrayList<File>();
        ArrayList<File> currentList = null;
        for (File singleFile : file.listFiles()) {
            if (singleFile.isDirectory()) {
                if (singleFile.getName().equals("BAR")) {
                    currentList = barFiles;
                } else if (singleFile.getName().equals("RIR")) {
                    currentList = rirFiles;
                }
                
                for (File subFile : singleFile.listFiles()) {
                    if (!subFile.isDirectory()) {
                        currentList.add(subFile);
                    }
                }
            }            
        }
        
        configData.setFileToLoad(barFiles);
        configData.getAdditionalProperties().put("RIR_FILES", rirFiles);

        return configData;
    }
    
    @Override
    protected ILoader< ? , CommonConfigData> getLoader() {
        return FakeLoaderFactory.getNetworkMeasurementsLoader();
    }

}
