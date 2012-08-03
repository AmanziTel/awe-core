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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.scripting.testing.TestActivator;
import org.amanzi.log4j.LogStarter;
import org.amanzi.testing.AbstractAWEDBTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.core.runtime.FileLocator;
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
public class AbstractScriptingPluginTests extends AbstractAWEDBTest {
    private static List<File> allFiles;
    private static List<File> modules;
    private static final String SCRIPT_ROOT = "/ruby";
    private static final String SCRIPT_ID_SEPARATOR = ":";
    private static final String WORKSPACE_FOLDER = Platform.getInstanceLocation().getURL().getPath();
    private static final String PROJECT_FOLDER = "awe-scripts";
    private static final String TEST_SCRIPT_NAME = "testScript.t";
    private static final double EXPECTED_NUMBER_RESULT = 5.0;
    private static final String TEST1_MODULE_NAME = "test2:";
    private static final FileFilter TEMPLATES_FILTER = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            final String name = pathname.getName();
            return name.endsWith(".t");
        }
    };

    @BeforeClass
    public static void init() throws IOException {
        clearDb();
        initializeDb();
        new LogStarter().earlyStartup();
        Enumeration<URL> projectScripts = Platform.getBundle(TestActivator.ID).findEntries(SCRIPT_ROOT, "*", false);
        allFiles = new ArrayList<File>();
        modules = new ArrayList<File>();
        while (projectScripts.hasMoreElements()) {
            URL path = FileLocator.resolve(projectScripts.nextElement());
            File file = new File(path.getFile());
            modules.add(file);
            allFiles.addAll(Arrays.asList(file.listFiles(TEMPLATES_FILTER)));
        }
    }

    public void clearWS() throws IOException {
        File ws = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        FileUtils.deleteDirectory(ws);
    }

    @Test
    public void testProjectScriptFolderCreated() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        boolean isExist = false;
        for (File module : modules) {
            isExist = false;
            for (File file : projectFolder.listFiles()) {
                if (file.getName().equals(module.getName())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                Assert.fail("some modules not exists in workspace");
            }
        }
        Assert.assertTrue("Destination folder and source folder have different structure",
                projectFolder.listFiles().length >= modules.size());
    }

    @Test
    public void testProjectScriptFolderContainsAllScripts() {
        File projectFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        List<File> destinationRbFiles = new ArrayList<File>();
        for (File destProject : projectFolder.listFiles()) {
            destinationRbFiles.addAll(Arrays.asList(destProject.listFiles()));
        }

        for (File source : allFiles) {
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
    public void testSimpleScriptExecution() throws FileNotFoundException, ScriptingException {
        Object value = TestActivator.getDefault().getRuntimeWrapper().executeScriptByName(TEST1_MODULE_NAME + TEST_SCRIPT_NAME);
        Assert.assertNotNull("Not null value excepted", value);
        Assert.assertEquals("5.0 value expected", EXPECTED_NUMBER_RESULT, value);
    }

    @Test
    public void testGetScriptsForProjectifNotExist() throws IOException {
        clearWS();
        Assert.assertNull("Null expected", TestActivator.getDefault().getScriptsForProject(SCRIPT_ROOT));
        restoreWS();
    }

    @Test
    public void testGetScriptsForProjectifExist() throws IOException {
        String projectName = TEST1_MODULE_NAME.split(SCRIPT_ID_SEPARATOR)[NumberUtils.INTEGER_ZERO];
        File requiredModule = null;
        for (File module : modules) {
            if (module.getName().equals(projectName)) {
                requiredModule = module;
            }
        }
        Assert.assertEquals("Not expected count of files", TestActivator.getDefault().getScriptsForProject(projectName).size(),
                requiredModule.listFiles().length);

    }

    @Test
    public void testGetAllScripts() throws IOException {
        restoreWS();
        Map<String, File> scripts = TestActivator.getDefault().getAllScripts();
        Assert.assertEquals(scripts.size(), allFiles.size());
    }

    /**
     * @throws IOException
     */
    private void restoreWS() throws IOException {
        URL scriptFolderUrl = Platform.getBundle(TestActivator.ID).getEntry(SCRIPT_ROOT);
        File targetFolder = new File(WORKSPACE_FOLDER + File.separator + PROJECT_FOLDER);
        File rubyFolder = new File(FileLocator.resolve(scriptFolderUrl).getPath());
        for (File file : rubyFolder.listFiles()) {
            FileUtils.forceMkdir(targetFolder);
            FileUtils.copyDirectoryToDirectory(file, targetFolder);
        }
    }
}
