package org.amanzi.rdt.console;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import net.refractions.udig.catalog.CatalogPlugin;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.rdt.internal.launching.AweLaunchingPlugin;
import org.amanzi.scripting.jruby.ScriptUtils;
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
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.runtime.builtin.IRubyObject;
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
	 * Pulic constructor that creates console 
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
		}};
	}
	
	/**
	 * Initialized GlobalVariable of Launch
	 * 
	 * @return true if variable was initialized successfully
	 */
	
	protected boolean initializeGlobalVariables() {
		HashMap<String, Object> globals = new HashMap<String, Object>();
		
		for(String className:new String[]{"org.amanzi.awe.catalog.json.JSONReader", "org.amanzi.awe.catalog.neo.actions.NeoReader"}){
			try {
				String[] fds = className.split("\\.");
				String var = fds[fds.length-1].toLowerCase().replace("reader", "_reader_class");
				globals.put(var, Class.forName(className));			
			}
			catch (ClassNotFoundException e) {
				System.err.println("Error setting global Ruby variable for class '"+className+"': "+e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		
		globals.put("feature_source_class", org.geotools.data.FeatureSource.class);
		globals.put("catalog", CatalogPlugin.getDefault());
		globals.put("catalogs", CatalogPlugin.getDefault().getCatalogs());		
		//globals.put("spreadsheet_manager", SpreadsheetManager.getInstance());
		globals.put("projects", AWEProjectManager.getGISProjects());
		globals.put("active_project", AWEProjectManager.getActiveGISProject());
		
		try {			
			globals.put("awe_console_path", FileLocator.toFileURL(Platform.getBundle("org.amanzi.awe.script.jirb").getEntry("")).getPath());			
		}
		catch (IOException e) {
			//TODO: handle this exception
			e.printStackTrace();
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
			URL scriptUrl = FileLocator.toFileURL(AweLaunchingPlugin.getDefault().getBundle().getEntry("ruby/startScript.rb"));
		
			String script = loadScript(scriptUrl.getPath());
		
			runtime.evalScriptlet(script);
		}
		catch (IOException e) {
			//TODO: handle this exception
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Utility function that load script from file
	 * 
	 * @param scriptPath path to file
	 * @return text of script
	 */
	
	private String loadScript(String scriptPath){
        StringWriter sw = new StringWriter();
        BufferedReader br = null;
	    try{
            br = new BufferedReader(new FileReader(scriptPath));            
            String line;
            while((line=br.readLine())!=null) {sw.write(line);sw.append('\n');}
	    }catch(Exception e){
	        System.err.println("Failed to load script '"+scriptPath+"': "+e);
	        e.printStackTrace(System.err);
	    }finally{
	        try{
	            if(br!=null) br.close();
	        }catch(IOException ee){}
	    }
	    return sw.toString();
	}
	
	/**
	 * Runs script by path
	 * 
	 * @param filePath path to script
	 */
	
	public void run(String filePath) {
		try {
			runtime.runFromMain(new FileInputStream(filePath), filePath);
		
			runtime.tearDown();
		}
		catch (Exception e) {
			//pring stack trace of any exception to output stream
			e.printStackTrace(outputStream);
		}
		
		
		setName("<terminated> " + getName());
		
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new RubyConsole[] {this});
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new RubyConsole[] {this});
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
	}
	
	public void addLink(IConsoleHyperlink arg0, int arg1, int arg2) {
		try {
			addHyperlink(arg0, arg1, arg2);
		}
		catch (BadLocationException e) {
			//TODO: handle this exception
			e.printStackTrace();
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
