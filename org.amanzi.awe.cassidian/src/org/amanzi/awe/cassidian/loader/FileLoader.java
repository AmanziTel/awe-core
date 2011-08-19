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

package org.amanzi.awe.cassidian.loader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class FileLoader {
    private File root;
    private String dbRootPath;
    private static List<File> filelist = new LinkedList<File>();

    public FileLoader(String rootPath) {
        root = new File(rootPath);
        if(!root.exists()){
            root.mkdir();
        }
    }

    /**
     * @return Returns the root.
     */

    public File getRoot() {
        return root;
    }

    /**
     * @param root The root to set.
     */
    public void setRoot(File root) {
        this.root = root;
    }

    /**
     * @return Returns the dbRootPath.
     */
    public String getDbRootPath() {
        return dbRootPath;
    }

    /**
     * @param dbRootPath The dbRootPath to set.
     */
    public void setDbRootPath(String dbRootPath) {
        root = new File(dbRootPath);
        this.dbRootPath = dbRootPath;
    }

    public List<File> getRootsFile(File root) {
        File[] rootList = root.listFiles();
        if (rootList != null) {
            for (int i = 0; i < rootList.length; i++) {
                if (rootList[i].isFile()) {
                    if (rootList[i].getName().lastIndexOf(".xml") != -1) {
                        filelist.add(rootList[i]);
                    }

                } else if (rootList[i].isDirectory()) {
                    getRootsFile(rootList[i]);
                }
            }
        }
        return filelist;
    }
}
