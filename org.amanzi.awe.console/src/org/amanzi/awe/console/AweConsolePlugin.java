package org.amanzi.awe.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;
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
    
    private MessageConsole pluginConsole;
    
    /*
     * Console's output
     */
    
    private MessageConsoleStream consoleStream;

    
    /*
     * Logging properties
     */
    public static boolean debug = false;
    private static boolean verbose = true;  
    

	// The shared instance
	private static AweConsolePlugin plugin;
	


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

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
	        if(PlatformUI.isWorkbenchRunning()) {
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
        }
        catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
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
            LOGGER.debug(line);
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
            //LOGGER.info(line);
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
            //LOGGER.warn(line);
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
    public OutputStream getPrintStream(){
        if (loggingPossible){
            return consoleStream;
        }
        return System.out;
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

}
