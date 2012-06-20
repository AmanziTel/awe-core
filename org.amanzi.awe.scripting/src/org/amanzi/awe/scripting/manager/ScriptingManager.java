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

package org.amanzi.awe.scripting.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ScriptingManager {
    private final static String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath().toString();
    private final static String PROJECT_FOLDER = "awe-scripts";
    private File source;
    private File destination;

    /**
     * @param rubyScriptingFolder
     */
    public ScriptingManager(URL rubyScriptingFolder) {
        initWorkspace(rubyScriptingFolder);
    }

    /**
     * initialise scripts workspace;
     * 
     * @param rubyScriptingFolder
     * @return false if workspace is already exist, true- if newly created
     */
    public boolean initWorkspace(URL rubyScriptingFolder) {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        source = new File(rubyScriptingFolder.getPath());
        String scriptFolderName = rubyScriptingFolder.getFile();
        scriptFolderName = scriptFolderName.substring(0, scriptFolderName.length() - 1);
        scriptFolderName = scriptFolderName.substring(scriptFolderName.lastIndexOf(File.separator) + 1, scriptFolderName.length());
        boolean isExist = false;
        for (File existingName : projectFolder.listFiles()) {
            if (existingName.getName().equals(scriptFolderName)) {
                destination = existingName;
                isExist = true;
                break;
            }
        }
        if (isExist) {
            return false;
        }
        String createScriptFolder = projectFolder.getAbsolutePath() + File.separator + scriptFolderName;
        destination = new File(createScriptFolder);
        destination.mkdir();
        return true;
    }

    /**
     * copy directory from source to
     */
    public void copyScripts() {
        FileFilter fileFilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                final String name = pathname.getName();
                return name.endsWith(".rb") || name.endsWith(".t");
            }
        };
        List<String> destinationContent = Arrays.asList(destination.list());
        byte[] buf = new byte[1024];
        for (File sourceFile : source.listFiles(fileFilter)) {
            File destFile = new File(destination.getPath() + File.separator + sourceFile.getName());
            if (!destinationContent.contains(sourceFile.getName())) {
                copyFile(sourceFile, destFile, buf);

            }
        }
    }

    /**
     * copy file content from @param sourceFile to @param destFile
     * 
     * @param sourceFile
     * @param destFile
     * @param buf bufferSize;
     */
    public void copyFile(File sourceFile, File destFile, byte[] buf) {
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO Handle FileNotFoundException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
}
