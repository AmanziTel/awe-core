package org.amanzi.awe.afp.testing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.scripting.jruby.EclipseLoadService;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.RubyInstanceConfig.LoadServiceCreator;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.load.LoadService;
import org.neo4j.graphdb.GraphDatabaseService;

public class CucumberRunner implements IApplication {
	

    public Object start(IApplicationContext context) throws Exception {

		RubyInstanceConfig config = new RubyInstanceConfig(){{
            setInput(System.in);
            setOutput(System.out);
            setError(System.err);
            setLoader(this.getClass().getClassLoader());
            setLoadServiceCreator(new LoadServiceCreator() {
        		public LoadService create(Ruby runtime) {
                    return new EclipseLoadService(runtime);
                }
        	});
        }};
        Ruby ruby = JavaEmbedUtils.initialize(new ArrayList<String>(), config);
        
        
		try {
			URL path = this.getClass().getResource("cucumber.template.rb");
			String file = FileLocator.resolve(path).toString().substring(5);
			
			ruby.executeScript(substitute(readScript(file)), "/cucumber.template.rb");

	    	System.out.println("Hello Cuucmber");
			return EXIT_OK;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			JavaEmbedUtils.terminate(ruby);
		}

    	
    }    
    private String substitute(String string) throws IOException {
		return string.replaceAll("@plugin_lib_dir", libDir()).replaceAll("@plugin_features_dir", featuresDir());
	}
    
    private String readScript(String script) throws IOException {
    	InputStream inputStream = new FileInputStream(script);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringWriter writer = new StringWriter();
		while (reader.ready())
			new PrintWriter(writer).println(reader.readLine());
		return writer.toString();
	}
    
	private String featuresDir() {
		return resolve(getClass().getResource("/features/"));
	}

	private String libDir() {
		return resolve(getClass().getResource("/lib/"));
	}
	
	private String resolve(URL url) {
		try {
			return FileLocator.resolve(url).getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    public void stop() {
        // do nothing
    }
}

