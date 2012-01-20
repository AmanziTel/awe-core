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
package org.amanzi.awe.console;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AweConsolePlugin extends AbstractUIPlugin {

    private static final Logger LOGGER = Logger.getLogger(AweConsolePlugin.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.console";

    /** String DEFAULT_CHARSET field */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /*
     * Name of console
     */
    private static String CONSOLE_NAME = "AWE Console";

    /*
     * Is logging possible
     */

    private static boolean loggingPossible = false;

    /*
     * Is console visible
     */
    private static boolean isVisible = false;

    /*
     * Console for NeoLoaderPlugin
     */
    private static MessageConsole pluginConsole;

    /*
     * Console's output
     */
    private static MessageConsoleStream consoleStream;

    /*
     * Logging properties
     */
    private static boolean debug = false;
    private static boolean verbose = true;

    // The shared instance
    private static AweConsolePlugin plugin;

    private static Display device = Display.getCurrent();

    /**
     * Some useful colors.
     */
    private static final Color RED;
    private static final Color BLUE;
    private static final Color BLACK;

    static {
        RED = new Color(device, 255, 0, 0);
        BLUE = new Color(device, 0, 0, 128);
        BLACK = new Color(device, 0, 0, 0);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        device = getWorkbench().getDisplay();
        initializeConsole();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        removeConsole();
        isVisible = false;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static AweConsolePlugin getDefault() {
        return plugin;
    }

    /**
     * Initialize console for output from NeoLoaderPlugin
     */

    private void initializeConsole() {
        // Adding a workaound to ensure that testing can continue o Hudson build
        // need to find a better solution to this try catch block.
        try {
            pluginConsole = new MessageConsole(CONSOLE_NAME, null, true);
            pluginConsole.initialize();

            consoleStream = pluginConsole.newMessageStream();

            ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {pluginConsole});
            if (PlatformUI.isWorkbenchRunning()) {
                loggingPossible = (plugin != null) && (pluginConsole != null) && Display.getDefault() != null;
            } else {
                loggingPossible = false;
            }
        } catch (Exception e) {
            loggingPossible = false;
        }
    }

    /**
     * Destroys console for output
     */

    private void removeConsole() {
        ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {pluginConsole});

        try {
            consoleStream.close();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }

        pluginConsole.destroy();
    }

    /**
     * Print debug message
     * 
     * @param line
     */

    public void debug(String line) {
        if (loggingPossible) {
            if (debug) {
                getDefault().printToStream(line);
            }
        } else if (debug) {
            LOGGER.debug(line);
        }
    }

    /**
     * Print info message
     * 
     * @param line
     */
    public static void info(final String line) {
        if (loggingPossible) {
            if (verbose || debug) {
                consoleStream = pluginConsole.newMessageStream();
                setColor(BLACK);
                getDefault().printToStream(line);
            }
        } else {
            LOGGER.info(line);
        }
    }

    /**
     * Print a notification message
     * 
     * @param line
     */
    public static void notify(String line) {
        if (loggingPossible) {
            consoleStream = pluginConsole.newMessageStream();
            setColor(BLUE);
            getDefault().printToStream(line);
        } else {
            LOGGER.warn(line);
        }

    }

    /**
     * Print an error message
     * 
     * @param line
     */
    public static void error(String line) {
        if (loggingPossible) {
            consoleStream = pluginConsole.newMessageStream();
            setColor(RED);
            getDefault().printToStream(line);
        } else {
            LOGGER.error(line);
        }

    }

    /**
     * Print an exception
     * 
     * @param line
     */

    public static void exception(Exception e) {

        if (loggingPossible) {
            consoleStream = pluginConsole.newMessageStream();
            setColor(RED);
            getDefault().printException(e);
        } else {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /** Print a message to Console */
    private boolean printToStream(final String line) {
        if (loggingPossible) {
            if (!isVisible) {
                pluginConsole.activate();
                ConsolePlugin.getDefault().getConsoleManager().showConsoleView(pluginConsole);
                isVisible = true;
            }
            consoleStream.println(line);
        }
        return loggingPossible;
    }

    /** Print a exception to Console */
    private boolean printException(Exception e) {
        if (loggingPossible) {
            if (!isVisible) {
                pluginConsole.activate();
                ConsolePlugin.getDefault().getConsoleManager().showConsoleView(pluginConsole);
                isVisible = true;
            }

            PrintStream stream = new PrintStream(consoleStream);
            e.printStackTrace(stream);
        }
        return loggingPossible;
    }

    /**
     * set message color
     *
     * @param color message color
     */
    private static void setColor(final Color color) {
        device.syncExec(new Runnable() {

            @Override
            public void run() {
                consoleStream.setColor(color);
            }
        });
    }

}
