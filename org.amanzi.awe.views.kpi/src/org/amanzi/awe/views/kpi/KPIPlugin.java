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
package org.amanzi.awe.views.kpi;

import java.io.IOException;
import java.io.PrintStream;

import org.amanzi.scripting.jruby.ScriptUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class KPIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.views.kpi";
    private static String CONSOLE_NAME = "NeoLoader Console";
    private static final int BUFFER = 0;
    private PrintStream output = null;
    private PrintStream error = null;
	// The shared instance
	private static KPIPlugin plugin;

    private Ruby runtime;
	
	/**
	 * The constructor
	 */
	public KPIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        initializeRuby();
	}

    /**
     * @throws IOException
     */
    private void initializeRuby() throws IOException {
        RubyInstanceConfig config = new RubyInstanceConfig();
        config.setError(getErrorOutputStream());
        config.setOutput(getOutputStream());
        runtime = Ruby.newInstance(config);
        runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));
    }

    /**
     * @return
     */
    private PrintStream getOutputStream() {
        if (output != null) {
            return output;
        }
        IConsole console = getConsole();
        if (console != null) {
            output = new PrintStream(((MessageConsole)console).newOutputStream());
        } else {
            output = System.out;
        }
        return output;
    }

    /**
     * @return
     */
    private IConsole getConsole() {
        for (IConsole console:ConsolePlugin.getDefault().getConsoleManager().getConsoles()){
            if (CONSOLE_NAME.equals(console.getName())) {
                return console;
            }
        }
        return null;
    }

    /**
     * @return
     */
    private PrintStream getErrorOutputStream() {
        if (error != null) {
            return error;
        }
        IConsole console = getConsole();
        if (console != null) {
            error = new PrintStream(((MessageConsole)console).newOutputStream());
        } else {
            error = System.err;
        }
        return error;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

    /**
     * gets ruby runtime
     * 
     * @return runtime
     */
    public Ruby getRubyRuntime() {
        return runtime;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
	public static KPIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
