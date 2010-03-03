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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.neo4j.api.core.Transaction;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * The activator class controls the plug-in life cycle
 */
public class KPIPlugin extends AbstractUIPlugin {
    /** AbstractLoader DEFAULT_DIRRECTORY_LOADER field */
    public static final String DEFAULT_DIRRECTORY_LOADER = "DEFAULT_DIRRECTORY_LOADER";
	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.views.kpi";
    private static String CONSOLE_NAME = "NeoLoader Console";
    private static final int BUFFER = 0;
    private Object synch = new Object();
    private PrintStream output = null;
    private PrintStream error = null;
	// The shared instance
	private static KPIPlugin plugin;

    private Ruby runtime;
    private Long networkId;
    private Long driveId;
    public static final String KPI_FOLDER="kpi";
    private static final String JRUBY_PATH_RUBY_NAME = "jrubyPath";	
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
        NeoServiceProvider.getProvider().getService();
	}

    /**
     * @throws IOException
     * @throws InterruptedException
     */
    private void initializeRuby() throws IOException, InterruptedException {
        RubyInstanceConfig config = new RubyInstanceConfig();
        config.setJRubyHome(ScriptUtils.getJRubyHome());
        config.setLoader(this.getClass().getClassLoader());
        config.setError(getErrorOutputStream());
        config.setOutput(getOutputStream());
        runtime = Ruby.newInstance(config);
        String[] loadPaths=new String[2];
        try {
            String aweProjectName = AWEProjectManager.getActiveProjectName();
            IRubyProject rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, aweProjectName);
            String location = rubyProject.getResource().getLocation().toOSString();
            System.out.println("[DEBUG] rubyProjectlocation " + location);
            loadPaths[0]=location;
            loadPaths[1]=location;
//            Platform.getBundle("").getE
        } catch (CoreException e1) {
            // TODO Handle CoreException
            throw (RuntimeException) new RuntimeException( ).initCause( e1 );
        }
        IRubyObject rubyObject = JavaEmbedUtils.javaToRuby(runtime, ScriptUtils.getJRubyHome());
        runtime.getGlobalVariables().define("$" + JRUBY_PATH_RUBY_NAME, new ValueAccessor(rubyObject));
        
        runtime.getLoadService().init(ScriptUtils.makeLoadPath(loadPaths));
//        runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));
        Job job = new Job("Initialize KPI builder") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                URL scriptURL;
                Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
                NeoUtils.addTransactionLog(tx, Thread.currentThread(), "1111");
                tx.finish();
                try {
                    scriptURL = FileLocator.toFileURL(KPIPlugin.getDefault().getBundle().getEntry("initKpi.rb"));
                } catch (IOException e) {
                    scriptURL = null;
                }
                String script = NeoSplashUtil.getScriptContent(scriptURL.getPath());
                tx = NeoServiceProvider.getProvider().getService().beginTx();
                NeoUtils.addTransactionLog(tx, Thread.currentThread(), "2222");
                tx.finish();
                try {
                    runtime.evalScriptlet(script);
                    System.out.println("INIT OK!");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
        job.join();

    }

    /**
     * @return
     */
    private PrintStream getOutputStream() {
        if (output != null) {
            return output;
        }
        output = System.out;
        // IConsole console = getConsole();
        // if (console != null) {
        // output = new PrintStream(((MessageConsole)console).newOutputStream());
        // } else {
        // output = System.out;
        // }
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
        error = System.err;
        // IConsole console = getConsole();
        // if (console != null) {
        // error = new PrintStream(((MessageConsole)console).newOutputStream());
        // } else {
        // error = System.err;
        // }
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
        if (runtime == null) {
            synchronized (synch) {
                if (runtime == null) {
                    try {
                        initializeRuby();
                    } catch (Exception e) {
                        // TODO Handle IOException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                }
            }
        }
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

    /**
     * @param networkId
     */
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    /**
     * @param driveId
     */
    public void setDriveId(Long driveId) {
        this.driveId = driveId;
    }

    /**
     * @return Returns the networkId.
     */
    public Long getNetworkId() {
        return networkId;
    }

    /**
     * @return Returns the driveId.
     */
    public Long getDriveId() {
        return driveId;
    }

    public String getDirectory() {
        return getPluginPreferences().getString(DEFAULT_DIRRECTORY_LOADER);
    }

    /**
     * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
     * 
     * @param newDirectory new default directory
     * @author Lagutko_N
     */

    public void setDirectory(String newDirectory) {
        getPluginPreferences().setValue(DEFAULT_DIRRECTORY_LOADER, newDirectory);
    }
}
