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

package org.amanzi.neo.loader.core.newsaver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kondratenko_Vladislav
 */
public class ConfigurationDataImpl implements IConfiguration {
    private List<File> filelist = new ArrayList<File>();
    private File sourceFile;
    private Map<Long, File> fileMap = new TreeMap<Long, File>();

    /**
     * init source
     */
    public ConfigurationDataImpl(String source) {
        sourceFile = new File(source);
    }

    @Override
    public boolean isMultiFile() {
        if (getFilesToLoad().length > 1) {
            return true;
        }
        return false;
    }

    @Override
    public File[] getFilesToLoad() {
        return (File[])getRootsFiles(sourceFile).toArray();
    }

    private Long extractTimeStampFromFileName(String fileName) {
        String longString = fileName.substring(fileName.lastIndexOf("#") + 1, fileName.lastIndexOf("."));
        return Long.parseLong(longString);

    }

    private List<File> getRootsFiles(File root) {
        File[] rootList = root.listFiles();
        if (rootList != null) {
            for (int i = 0; i < rootList.length; i++) {
                if (rootList[i].isFile()) {
                    if (rootList[i].getName().lastIndexOf(".xml") != -1 && rootList[i].getName().lastIndexOf("#") != -1) {
                        Long fileName = extractTimeStampFromFileName(rootList[i].getName());
                        fileMap.put(fileName, rootList[i]);
                    }

                } else if (rootList[i].isDirectory()) {
                    getRootsFiles(rootList[i]);
                }
            }
            for (Long key : fileMap.keySet()) {
                filelist.add(fileMap.get(key));
            }
        }
        return filelist;
    }

    @Override
    public Map<Object, String> getDatasetNames() {
        return null;
    }

}
