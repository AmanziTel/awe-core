package org.amanzi.rdt.console;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import net.refractions.udig.catalog.CatalogPlugin;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.rdt.internal.launching.AweLaunchingPlugin;
import org.amanzi.rdt.internal.launching.AweLaunchingPluginMessages;
import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.console.SpreadsheetManager;
import org.amanzi.splash.neo4j.console.NeoSplashManager;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleHyperlink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.LoadService;
import org.rubypeople.rdt.internal.launching.StandardVMRunner;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;


/**
 * Console for input/output of Ruby Script Launch
 * 
 * @author Lagutko_N
 *
 */

public class RubyConsole extends IOConsole implements IConsole {
	
    /*
     * Path to Start Script
     */
	private static final String START_SCRIPT = "ruby/startScript.rb";

	/*
	 * ID of AWEScript Console plugin
	 */
    private static final String ORG_AMANZI_AWE_SCRIPT_JIRB_PLUGIN = "org.amanzi.awe.script.jirb";

    /*
     * Name of variable for path to AWEScript Console plugin
     */
    private static final String AWE_CONSOLE_PATH_PARAM = "awe_console_path";

    /*
     * Name of variable for Active Project
     */
    private static final String ACTIVE_PROJECT_PARAM = "active_project";

    /*
     * Name of Variable for all projects
     */
    private static final String PROJECTS_PARAM = "projects";

    /*
     * Name of Variable for Spreadsheet Manager
     */
    private static final String SPREADSHEET_MANAGER_PARAM = "spreadsheet_manager";
    
    /*
     * Name of Variable for Neo Spreadsheet Manager
     */
    private static final String NEO_SPREADSHEET_MANAGER_PARAM = "splash_manager";

    /*
     * Name of Variable for Catalogs
     */
    private static final String CATALOGS_PARAM = "catalogs";

    /*
     * Name of Variable for active catalog
     */
    private static final String CATALOG_PARAM = "catalog";

    /*
     * Name of Variable for Feature Source class
     */
    private static final String FEATURE_SOURCE_CLASS_PARAM = "feature_source_class";

    /*
     * Name of NeoReader class
     */
    private static final String NEO_READER_CLASS = "org.amanzi.awe.catalog.neo.actions.NeoReader";

    /*
     * Name of JSON Reader class
     */
    private static final String JSONREADER_CLASS = "org.amanzi.awe.catalog.json.JSONReader";

    /*
	 * Name of Process
	 */
	private static final String PROCESS_NAME = "AWE";
	
	/*
	 * Monitor
	 */
	IProgressMonitor monitor;
	
	/*
	 * Launced configuration
	 */
	ILaunchConfiguration configuration;
	
	/*
	 * Launch preferences
	 */
	ILaunch launch;
	
	/*
	 * Launched project
	 */
	IProject project;
	
	/*
	 * Console output stream
	 */
	PrintStream outputStream = null;
	
	/*
	 * Ruby runtime
	 */
	Ruby runtime;
	
	/**
	 * Public constructor that creates console 
	 * 
	 * @param configuration launch configuration
	 * @param launch launch preferences
	 * @param monitor monitor
	 * @throws CoreException 
	 */

	public RubyConsole(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		this(configuration.getName(), configuration.getType().getName());
		
		this.monitor = monitor;
		this.configuration = configuration;
		this.launch = launch;
		
		if (ConsoleRuntime.getConsoleLineTrackers(getType()).length > 0) {
			addPatternMatchListener(new AweConsoleLineNotfier());
		}
		
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new RubyConsole[] {this});
	}
	
	/**
	 * Internal constructor that creates Label of Console by launchName and configrurationType
	 * 
	 * @param launchName name of launch
	 * @param configurationType name of launch configuration type
	 */
	
	protected RubyConsole(String launchName, String configurationType) {
		this(launchName + " [" + configurationType + "] " + StandardVMRunner.renderProcessLabel(new String[] {PROCESS_NAME}));
	}
	
	/**
	 * Internal constructor that creates Console with label
	 * 
	 * @param label label of Console
	 */
	
	protected RubyConsole(String label) {
		
		super(label, IRubyLaunchConfigurationConstants.ID_RUBY_PROCESS_TYPE, null, true);
	}
	
	/**
	 * Initializes RubyConsole
	 * 
	 * @param jRubyInstall Ruby VM
	 * @throws CoreException
	 */

	public void init(IVMInstall jRubyInstall) throws CoreException {		
		activate();
		
		initializeJRubyInterpreter(createRubyConfig(jRubyInstall));
		
		if (!initializeGlobalVariables()) {
			//TODO: throw exception
		}
		
		if (!runInitScript()) {
			//TODO: throw exception
		}
	}
	
	/**
	 * Initialized Ruby Runtime
	 * 
	 * @param rubyConfiguration configuration of RubyRuntime
	 */
	
	protected void initializeJRubyInterpreter(RubyInstanceConfig rubyConfiguration) {
		runtime = Ruby.newInstance(rubyConfiguration);		
		runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));
	}
	
	/**
	 * Creates Configuration of Ruby Environment
	 * 
	 * @param configuration launch configuration
	 * @param mode mode of launch
	 * @return created RubyInstanceConfig class
	 * @throws CoreException 
	 */
	
	private RubyInstanceConfig createRubyConfig(final IVMInstall jRubyInstall) throws CoreException {
		outputStream = new PrintStream(newOutputStream());
		
		return new RubyInstanceConfig() {{
			setJRubyHome(jRubyInstall.getInstallLocation().getAbsolutePath());
			setOutput(outputStream);
			setError(outputStream);			
			setInput(getInputStream());
            setLoadServiceCreator(new LoadServiceCreator() {
                public LoadService create(Ruby runtime) {
                    return new EclipseLoadService(runtime);
                }
            });
		}};
	}
	
	/**
	 * Initialized GlobalVariable of Launch
	 * 
	 * @return true if variable was initialized successfully
	 */
	
	protected boolean initializeGlobalVariables() {
		HashMap<String, Object> globals = new HashMap<String, Object>();
		ScriptUtils.makeGlobalsFromClassNames(globals,new String[]{JSONREADER_CLASS, NEO_READER_CLASS});
		globals.put(FEATURE_SOURCE_CLASS_PARAM, org.geotools.data.FeatureSource.class);
		globals.put(CATALOG_PARAM, CatalogPlugin.getDefault());
		globals.put(CATALOGS_PARAM, CatalogPlugin.getDefault().getCatalogs());		
		globals.put(SPREADSHEET_MANAGER_PARAM, SpreadsheetManager.getInstance());
		globals.put(NEO_SPREADSHEET_MANAGER_PARAM, NeoSplashManager.getInstance());
		globals.put(PROJECTS_PARAM, AWEProjectManager.getGISProjects());
		globals.put(ACTIVE_PROJECT_PARAM, AWEProjectManager.getActiveGISProject());
		
		try {			
			globals.put(AWE_CONSOLE_PATH_PARAM, FileLocator.toFileURL(Platform.getBundle(ORG_AMANZI_AWE_SCRIPT_JIRB_PLUGIN).getEntry("")).getPath());			
		}
		catch (IOException e) {
			AweLaunchingPlugin.log(null, e);
			return false;
		}
		
		for(Entry<String,Object> entry : globals.entrySet()){
            IRubyObject rubyObj = org.jruby.javasupport.JavaEmbedUtils.javaToRuby(runtime, entry.getValue());
            String key = entry.getKey();
            if(!key.startsWith("$")) key = "$"+key;
            runtime.getGlobalVariables().define(key, new ValueAccessor(rubyObj));            
        }
		
		return true;
	}

	private boolean runInitScript() {
		try {
			URL scriptUrl = FileLocator.toFileURL(AweLaunchingPlugin.getDefault().getBundle().getEntry(START_SCRIPT));
		
			String script = NeoSplashUtil.getScriptContent(scriptUrl.getPath());
		
			runtime.evalScriptlet(script);
		}
		catch (IOException e) {
			AweLaunchingPlugin.log(null, e);
			return false;
		}
		return true;
	}
	
	/**
	 * Runs script by path
	 * 
	 * @param filePath path to script
	 */
	
	public void run(String filePath) {
		try {
			runtime.runFromMain(new FileInputStream(filePath), filePath);
		}
		catch (Exception e) {			
			//pring stack trace of any exception to output stream
			e.printStackTrace(outputStream);
		}
		finally {
			runtime.tearDown();
		}
		
		setConsoleLabelTerminated();
	}
	
	/**
	 * Add prefix '<terminated>' to Console's label
	 * 
	 */
	
	private void setConsoleLabelTerminated() {
	    final String newName = AweLaunchingPluginMessages.getFormattedString(AweLaunchingPluginMessages.Console_Terminated, getName());
		
		
		Runnable r = new Runnable() {
			public void run() {
				setName(newName);
				ConsolePlugin.getDefault().getConsoleManager().warnOfContentChange(RubyConsole.this);
			}
		};
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(r);
	}
	
	public void addLink(IConsoleHyperlink arg0, int arg1, int arg2) {
		try {
			addHyperlink(arg0, arg1, arg2);
		}
		catch (BadLocationException e) {
			AweLaunchingPlugin.log(null, e);
		}
	}

	public void addLink(IHyperlink arg0, int arg1, int arg2) {
		try {
			addHyperlink(arg0, arg1, arg2);
		}
		catch (BadLocationException e) {
			//TODO: handle this exception
			e.printStackTrace();
		}
		
	}

	public void connect(IStreamsProxy arg0) {
		//do nothing
		
	}

	public void connect(IStreamMonitor arg0, String arg1) {
		// do nothing
		
	}

	public IProcess getProcess() {		
		return null;
	}
	
	public IProject getProject() {
		return project;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public ILaunch getLaunch() {
		return launch;
	}

	public IRegion getRegion(IConsoleHyperlink arg0) {
		return super.getRegion(arg0);
	}

	public IOConsoleOutputStream getStream(String arg0) { 
		return null;
	}
}
