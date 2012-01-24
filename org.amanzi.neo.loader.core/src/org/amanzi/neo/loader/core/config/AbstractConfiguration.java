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

package org.amanzi.neo.loader.core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public abstract class AbstractConfiguration implements IConfiguration {
    
    private List<File> filesToLoad = new ArrayList<File>();
    
    private String datasetName = StringUtils.EMPTY;

    @Override
    public List<File> getFilesToLoad() {
        return filesToLoad;
    }

    @Override
    public String getDatasetName() {
        return datasetName;
    }

    @Override
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    /**
     * @param filesToLoad The filesToLoad to set.
     */
    protected void setFilesToLoad(List<File> filesToLoad) {
        this.filesToLoad = filesToLoad;
    }
    
    protected void addFileToLoad(File fileToLoad) {
        this.filesToLoad.add(fileToLoad);
    }
    
    protected List<File> getSubFiles(File rootFile) {
        ArrayList<File> result = new ArrayList<File>();
        
        if (rootFile.isFile()) {
            result.add(rootFile);
        } else {
            for (File subFile : rootFile.listFiles()) {
                result.addAll(getSubFiles(subFile));
            }
        }
        
        return result;
    }

    @Override
    public void computeSourceFiles() {
        ArrayList<File> result = new ArrayList<File>();
        for (File sourceFile : getFilesToLoad()) {
            result.addAll(getSubFiles(sourceFile));
        }
        
        setFilesToLoad(result);
    }

}
