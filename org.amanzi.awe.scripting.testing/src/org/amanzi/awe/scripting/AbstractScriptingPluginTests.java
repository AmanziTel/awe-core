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

package org.amanzi.awe.scripting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.amanzi.awe.scripting.testing.TestActivator;
import org.amanzi.testing.AbstractAWETest;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for activator
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AbstractScriptingPluginTests extends AbstractAWETest {
    private static List<File> expectedFiles;
    private final static String SCRIPT_ROOT = "/ruby";
    private final static String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath().toString();
    private final static String PROJECT_FOLDER = "awe-scripts";
    private static final String TEST_SCRIPT_NAME = "testScript.rb";

    @BeforeClass
    public static void init() {
        Enumeration<String> projectScripts = Platform.getBundle(TestActivator.ID).getEntryPaths(SCRIPT_ROOT);
        expectedFiles = new ArrayList<File>();
        String name = TestActivator.SCRIPT_PATH;
        while (projectScripts.hasMoreElements()) {
            String path = projectScripts.nextElement();
            if (!path.equals(name)) {
                continue;
            }
            File file = new File(path);
            expectedFiles.addAll(Arrays.asList(file.listFiles()));
        }
    }

    @AfterClass
    public static void clearWS() throws IOException {
        File ws = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        FileUtils.deleteDirectory(ws);
    }

    @Test
    public void testProjectScriptFolderCreated() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        Assert.assertTrue("Destination folder and source folder have different structure", projectFolder.listFiles().length == 1);
    }

    @Test
    public void testProjectScriptFolderContainsAllScripts() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        Assert.assertTrue("Destination folder and source folder have different structure", projectFolder.listFiles().length == 1);
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
            }
            if (!isExist) {
                Assert.assertTrue("file" + source.getParentFile().getName() + File.separator + source.getName()
                        + "doesn't exist in " + projectFolder.getName() + File.separator + source.getParentFile().getName()
                        + " directory", isExist);
            }
        }
    }

    @Test
    public void testSimpleScriptExecution() {
        Object value = TestActivator.getRuntimeWrapper().executeScriptByName(TEST_SCRIPT_NAME);
        Assert.assertNotNull("Not null value excepted", value);
        Assert.assertEquals("5.0 value expected", 5.0, value);
    }

    @Test
    public void testGetScriptsForProjectifNotExist() throws IOException {
        clearWS();
        String projectName = TestActivator.SCRIPT_PATH.split("/")[1];
        Assert.assertNull("Null expected", TestActivator.getScriptsForProject(projectName));
    }
}
