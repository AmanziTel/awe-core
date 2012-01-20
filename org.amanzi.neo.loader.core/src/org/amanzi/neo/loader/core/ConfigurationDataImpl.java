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

package org.amanzi.neo.loader.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * common loader configuration
 * 
 * @author Kondratenko_Vladislav
 */
public class ConfigurationDataImpl implements IConfiguration {
	
	private final static Comparator<File> FILE_COMPARATOR = new Comparator<File>() {

		@Override
		public int compare(File o1, File o2) {
			return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
		}
	};
	
    /**
     * 
     */
    public ConfigurationDataImpl() {
        super();
    }

    /**
     * selected directory
     */
    private Collection<File> sourceFile;
    /**
     * dataset names collection
     */
    private Map<Object, String> datasetNames;
    
	private boolean filesChanged = false;
	
	private List<File> fileList;


    /**
     * init source with file or directory path
     */
    public ConfigurationDataImpl(String source) {
        sourceFile = new LinkedList<File>();
        sourceFile.add(new File(source));
    }

    @Override
    public boolean isMultiFile() {
        if (getFilesToLoad().size() > 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<File> getFilesToLoad() {
		if (fileList == null && filesChanged) {

			List<File> fileList = new ArrayList<File>();
			if (sourceFile != null) {
				for (File file : sourceFile) {
					fileList.addAll(getRootsFiles(file));
				}
			}

			Collections.sort(fileList, FILE_COMPARATOR);
			
			this.fileList = fileList;
			filesChanged = false;
		}
        
        return fileList;
    }

    /**
     * get all files from directory and subdirectories
     * 
     * @param sourceFile2
     * @return
     */
    private List<File> getRootsFiles(File sourceFile2) {
        List<File> filelist = new ArrayList<File>();
        File[] rootList;
        if (sourceFile2.isDirectory()) {
            rootList = sourceFile2.listFiles();
        } else {
            rootList = new File[1];
            rootList[0] = sourceFile2;
        }
        
        for (File singleFile : rootList) {
        	if (singleFile.isFile()) {
        		filelist.add(singleFile);
        	}
        }
        
        return filelist;
    }

    @Override
    public Map<Object, String> getDatasetNames() {
        if (datasetNames == null) {
            datasetNames = new HashMap<Object, String>();
        }
        return datasetNames;
    }

    @Override
    public void setSourceFile(Collection<File> sourceFile) {
        this.sourceFile = sourceFile;
        this.filesChanged = true;
    }

    @Override
    public void setDatasetNames(Map<Object, String> datasetNames) {
        this.datasetNames = datasetNames;
    }

}
