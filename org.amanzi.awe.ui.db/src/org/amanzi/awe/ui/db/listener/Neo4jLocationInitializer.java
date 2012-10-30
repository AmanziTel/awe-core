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

package org.amanzi.awe.ui.db.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.amanzi.awe.ui.db.dialog.ChooseDatabaseLocationDialog;
import org.amanzi.awe.ui.events.IEvent;
import org.amanzi.awe.ui.listener.IAWEEventListenter;
import org.amanzi.neo.db.manager.DatabaseManagerFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
public class Neo4jLocationInitializer implements IAWEEventListenter {

    private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

    private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

    private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

    private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

    private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$

    private static final String CMD_DATA = "-data"; //$NON-NLS-1$

    private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

    private static final String NEW_LINE = "\n"; //$NON-NLS-1$

    /**
     * Create and return a string with command line options for eclipse.exe that will launch a new
     * workbench that is the same as the currently running one, but using the argument directory as
     * its workspace.
     * 
     * @param workspace the directory to use as the new workspace
     * @return a string of command line options or null on error
     */
    private String buildCommandLine(final String workspace) {
        String property = System.getProperty(PROP_VM);

        StringBuffer result = new StringBuffer(512);
        result.append(property);
        result.append(NEW_LINE);

        // append the vmargs and commands. Assume that these already end in \n
        String vmargs = System.getProperty(PROP_VMARGS);
        if (vmargs != null) {
            result.append(vmargs);
        }

        // append the rest of the args, replacing or adding -data as required
        property = System.getProperty(PROP_COMMANDS);
        if (property == null) {
            result.append(CMD_DATA);
            result.append(NEW_LINE);
            result.append(workspace);
            result.append(NEW_LINE);
        } else {
            // find the index of the arg to replace its value
            int cmd_data_pos = property.lastIndexOf(CMD_DATA);
            if (cmd_data_pos != -1) {
                cmd_data_pos += CMD_DATA.length() + 1;
                result.append(property.substring(0, cmd_data_pos));
                result.append(workspace);
                result.append(property.substring(property.indexOf('\n', cmd_data_pos)));
            } else {
                result.append(CMD_DATA);
                result.append(NEW_LINE);
                result.append(workspace);
                result.append(NEW_LINE);
                result.append(property);
            }
        }

        // put the vmargs back at the very end (the eclipse.commands property
        // already contains the -vm arg)
        if (vmargs != null) {
            result.append(CMD_VMARGS);
            result.append(NEW_LINE);
            result.append(vmargs);
        }

        return result.toString();
    }

    /**
     * @return
     */
    private Object chooseWorkspaceDialog() {
        Display display = PlatformUI.createDisplay();

        Location instanceLoc = Platform.getInstanceLocation();

        try {
            ChooseWorkspaceData data = new ChooseWorkspaceData(instanceLoc.getURL());

            ChooseWorkspaceDialog dialog = new ChooseWorkspaceDialog(display.getActiveShell(), data, true, true);

            dialog.prompt(true);

            String selection = data.getSelection();

            Platform.getInstanceLocation().createLocation(Platform.getInstanceLocation(), new URL("file", null, selection), false);

            data.writePersistedData();
            restart(selection);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

        return IApplication.EXIT_OK;
    }

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     * @return
     */
    private boolean getState() {
        Location location = Platform.getConfigurationLocation();
        File file = new File(location.getURL().getPath() + "state.inf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringWriter writer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            writer = new StringWriter();
            IOUtils.copy(fis, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.isEmpty(writer.toString()) ? true : Boolean.valueOf(writer.toString());
    }

    @Override
    public void onEvent(final IEvent event) {
        switch (event.getStatus()) {
        case INITIALISATION:
            relocateDatabase(null);
            break;
        default:
            break;
        }

    }

    /**
     * @param path
     */
    private URL openDBselectionDialog(final String path) {
        ChooseDatabaseLocationDialog dialog = new ChooseDatabaseLocationDialog(PlatformUI.getWorkbench().getDisplay()
                .getActiveShell(), path);
        int openResult = dialog.open();
        switch (openResult) {
        case Window.CANCEL:
            PlatformUI.getWorkbench().close();
            System.exit(0);
            break;
        case Window.OK:
            try {
                return new URL("file", null, dialog.getDatabaseLocation());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        default:
            break;
        }
        return null;
    }

    private void relocateDatabase(final String path) {
        Boolean isUsed = false;
        if (StringUtils.isEmpty(path)) {
            isUsed = DatabaseManagerFactory.getDatabaseManager().isAlreadyUsed();
        } else {
            isUsed = DatabaseManagerFactory.getDatabaseManager(path, true).isAlreadyUsed();
        }
        if (isUsed) {
            Boolean changeLocation = getState();
            if (Boolean.valueOf(changeLocation)
                    && !DatabaseManagerFactory.getDatabaseManager().getDefaultLocation().equalsIgnoreCase(path)) {
                chooseWorkspaceDialog();
                setState(false);
                PlatformUI.getWorkbench().restart();
                return;
            }
            setState(true);
            URL dbLocation = openDBselectionDialog(DatabaseManagerFactory.getDatabaseManager().getLocation());
            relocateDatabase(dbLocation.getPath());
        } else {
            setState(true);
        }
    }

    private void restart(final String path) {
        String command_line = buildCommandLine(path);
        if (command_line == null) {
            return;
        }

        System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
        System.setProperty(PROP_EXIT_DATA, command_line);
    }

    /**
     * @param prefs
     * @param b
     */
    private void setState(final Boolean b) {
        Location location = Platform.getConfigurationLocation();
        File file = new File(location.getURL().getPath() + "state.inf");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fos = new FileWriter(file);
            BufferedWriter bf = new BufferedWriter(fos);
            bf.write(b.toString());
            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
