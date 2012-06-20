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

package org.amanzi.awe.scripting.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for activator
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AbstractScriptingPluginTesting {
    private static List<File> expectedFiles;
    private final static String SCRIPT_ROOT = "/ruby";
    private final static String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath().toString();
    private final static String PROJECT_FOLDER = "awe-scripts";

    @BeforeClass
    public static void init() {
        Enumeration<String> projectScripts = Platform.getBundle(FakeActivator.ID).getEntryPaths(SCRIPT_ROOT);
        expectedFiles = new ArrayList<File>();
        String name = FakeActivator.SCRIPT_PATH;
        while (projectScripts.hasMoreElements()) {
            String path = projectScripts.nextElement();
            if (!path.equals(name)) {
                continue;
            }
            File file = new File(path);
            expectedFiles.addAll(Arrays.asList(file.listFiles()));
        }
    }

    @Test
    public void TestProjectScriptFolderCreated() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        Assert.assertTrue("Destination folder and source folder have different structure",
                projectFolder.listFiles().length == expectedFiles.size());
    }

    @Test
    public void TestProjectScriptFolderContainsAllScripts() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        Assert.assertTrue("Destination folder and source folder have different structure",
                projectFolder.listFiles().length == expectedFiles.size());
        List<File> destinationRbFiles = new ArrayList<File>();
        for (File destProject : projectFolder.listFiles()) {
            destinationRbFiles.addAll(Arrays.asList(destProject.listFiles()));
        }

        for (File source : expectedFiles) {
            boolean isExist = false;
            for (File deFile : destinationRbFiles) {
                if (deFile.getName().equals(source.getName())
                        && deFile.getParentFile().getName().equals(source.getParentFile().getName())) {
                    isExist = true;
                    break;
                }
                if (!isExist) {
                    Assert.assertTrue("file" + source.getParentFile().getName() + File.separator + source.getName()
                            + "doesn't exist in " + projectFolder.getName() + File.separator + source.getParentFile().getName()
                            + " directory", isExist);
                }
            }
        }
    }
}
