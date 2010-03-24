package org.amanzi.awe.report;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.views.kpi.KPIPlugin;
import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * The activator class controls the plug-in life cycle
 */
public class ReportPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.report";

    // The shared instance
    private static ReportPlugin plugin;

    public static String TEMPLATES_FOLDER="templates";

    public static String REPORTS_FOLDER="reports";

    protected static String INIT_FILE="ruby/init.rb";

    private Ruby runtime;

    private final String JRUBY_PATH_RUBY_NAME = "jrubyPath";

    /**
     * The constructor
     */
    public ReportPlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        //force to start udig.project.ui plugin
        Map noMap = ApplicationGIS.NO_MAP;
        System.out.println("[DEBUG]ReportPlugin started");
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ReportPlugin getDefault() {
        return plugin;
    }

    public synchronized Ruby getRubyRuntime() {
        if (runtime == null) {
            try {
                initializeRubyRuntime();
            } catch (Exception e) {
                // TODO Handle IOException
                e.printStackTrace();
//                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        return runtime;
    }

    private void initializeRubyRuntime() throws IOException {
        System.out.println("initializeRubyRuntime");
        RubyInstanceConfig config = new RubyInstanceConfig();
        config.setJRubyHome(ScriptUtils.getJRubyHome());
        config.setLoader(this.getClass().getClassLoader());
        config.setError(System.err);
        config.setOutput(System.out);
        runtime = Ruby.newInstance(config);
        IRubyObject jrubyHome = JavaEmbedUtils.javaToRuby(runtime, ScriptUtils.getJRubyHome());
        runtime.getGlobalVariables().define("$" + JRUBY_PATH_RUBY_NAME, new ValueAccessor(jrubyHome));
        // runtime.getGlobalVariables().define("$" + PRINTING_ENGINE, new
        // ValueAccessor(rubyObject));

        runtime.getLoadService().init(ScriptUtils.makeLoadPath(getLoadPaths()));
        Job job=new Job("Initializing Report Engine"){

            @Override
            protected IStatus run(IProgressMonitor monitor) {
//        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
                try {
                    URL scriptURL = FileLocator.toFileURL(ReportPlugin.getDefault().getBundle().getEntry(INIT_FILE));
                    
                    final IRubyObject res = runtime.evalScriptlet(scriptURL.getPath());
                } catch (Exception e) {
                    // TODO Handle IOException
                    e.printStackTrace();
//                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
//                tx.finish();
                return null;
            }
            
        };
        try {
            job.schedule();
            job.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getLoadPaths() {
        String[] loadPaths = new String[5];
        try {
            final String RUBY = "ruby";
            String aweProjectName = AWEProjectManager.getActiveProjectName();
            IRubyProject rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, aweProjectName);
            
            // path to 'ruby' folder of KPI plugin
            URL entry = Platform.getBundle(KPIPlugin.PLUGIN_ID).getEntry(RUBY);
            loadPaths[0] = FileLocator.resolve(entry).getFile();
            System.out.println("load paths:"+ loadPaths[0]);
           
            // path to project 'kpi' folder
            final IProject project = rubyProject.getProject();
            final IFolder kpiFolder = project.getFolder(new Path(KPIPlugin.KPI_FOLDER));
            loadPaths[1]= kpiFolder.getLocation().toOSString();
            System.out.println("load paths:"+ loadPaths[1]);
            
            //path to Report plugin folders
            entry=Platform.getBundle(ReportPlugin.PLUGIN_ID).getEntry(RUBY);
            loadPaths[2] = FileLocator.resolve(entry).getFile();
            System.out.println("load paths:"+ loadPaths[2]);
            
            // path to project 'templates' folder
            final IFolder templatesFolder = project.getFolder(new Path(ReportPlugin.TEMPLATES_FOLDER));
            loadPaths[3]= templatesFolder.getLocation().toOSString();
            System.out.println("load paths:"+ loadPaths[3]);
            
            // path to project 'reports' folder
            final IFolder reportsFolder = project.getFolder(new Path(ReportPlugin.REPORTS_FOLDER));
            loadPaths[4]= reportsFolder.getLocation().toOSString();
            System.out.println("load paths:"+ loadPaths[4]);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (CoreException e) {
            // TODO Handle CoreException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
        return loadPaths;
    }
}
