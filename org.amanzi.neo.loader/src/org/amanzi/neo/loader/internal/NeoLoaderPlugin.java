package org.amanzi.neo.loader.internal;

import java.io.IOException;
import java.io.PrintStream;

import org.amanzi.neo.core.NeoCorePlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * Activator class for org.amanzi.net.loader plugin
 * 
 * @author Lagutko_N
 */

public class NeoLoaderPlugin extends Plugin {
    
    /*
	 * Name of console
	 */
	
	private static String CONSOLE_NAME = "NeoLoader Console";
	
	/*
	 * Plugin variable
	 */

	static private NeoLoaderPlugin plugin;
	
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
	
	private MessageConsole pluginConsole;
	
	/*
	 * Console's output
	 */
	
	private MessageConsoleStream consoleStream;

    private IPreferenceStore preferenceStore = null;
	
	/*
	 * Logging properties
	 */
	public static boolean debug = false;
	private static boolean verbose = true;	
	
	/**
	 * Constructor for SplashPlugin.
	 */
	public NeoLoaderPlugin() {
		super();
		plugin = this;	
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		initializeConsole();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		removeConsole();
		
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static NeoLoaderPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Initialize console for output from NeoLoaderPlugin
	 */
	
	private void initializeConsole() {
		pluginConsole = new MessageConsole(CONSOLE_NAME, null, true);
		pluginConsole.initialize();
		
		consoleStream = pluginConsole.newMessageStream();		
		
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {pluginConsole});
		
		loggingPossible = (plugin != null) && (pluginConsole != null);
	}
	
	/**
	 * Destroys console for output
	 */
	
	private void removeConsole() {
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {pluginConsole});
		
		try {
			consoleStream.close();
		}
		catch (IOException e) {
			NeoCorePlugin.error(NeoLoaderPluginMessages.Console_ErrorOnClose, e);
		}
		
		pluginConsole.destroy();
	}
	
	/**
	 * Print debug message
	 * 
	 * @param line
	 */
	
	public static void debug(String line) {
		if (loggingPossible) {
			if (debug) {
				getDefault().printToStream(line);
			}
		} else if(debug) {
		    System.out.println(line);
		}
	}
	
	/**
	 * Print info message
	 * 
	 * @param line
	 */
	
	public static void info(String line) {
		if (loggingPossible) {
			if (verbose || debug) {
				getDefault().printToStream(line);
			}
        } else {
            System.out.println(line);
		}
	}
	
	/**
	 * Print a notification message
	 * 
	 * @param line
	 */
	
	public static void notify(String line) {
		if (loggingPossible) {
			getDefault().printToStream(line);
        } else {
            System.out.println(line);
		}
	}
	
	/**
	 * Print an error message
	 * 
	 * @param line
	 */
	
	public static void error(String line) {
		if (loggingPossible) {
			getDefault().printToStream(line);
        } else {
            System.err.println(line);
		}
	}
	
	/**
	 * Print an exception
	 * 
	 * @param line
	 */
	
	public static void exception(Exception e) {
		if (loggingPossible) {
			getDefault().printException(e);
        } else {
            e.printStackTrace(System.out);
		}
	}
	
	/** Print a message to Console */
	private void printToStream(final String line) {
		if (!isVisible) {			
			pluginConsole.activate();			
			ConsolePlugin.getDefault().getConsoleManager().showConsoleView(pluginConsole);
			isVisible = true;
		}		
		consoleStream.println(line);
	}
	
	/** Print a exception to Console */	
	private void printException(Exception e) {
		if (!isVisible) {			
			pluginConsole.activate();			
			ConsolePlugin.getDefault().getConsoleManager().showConsoleView(pluginConsole);
			isVisible = true;
		}
				
		PrintStream stream = new PrintStream(consoleStream);		
		e.printStackTrace(stream);		
    }

    /**
     * Returns the preference store for this plugin
     * 
     * @return the preference store
     */
    public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(new InstanceScope(), getBundle().getSymbolicName());

        }
        return preferenceStore;
    }
}
