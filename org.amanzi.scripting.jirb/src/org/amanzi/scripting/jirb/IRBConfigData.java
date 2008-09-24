package org.amanzi.scripting.jirb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple container for configuration information relevant to starting
 * the IRB console. This is used to customize the startup with various
 * options:
 * <ul>
 *   <li>title - a String to use as the title of the IRB session</li>
 *   <li>extraLoadPath - a String[] of additional paths to add to $: for require to search</li>
 *   <li>extraRequire - a String[] of additional files to require after irb, but before IRB.start, allowing for customization of the IRB session</li>
 * </ul>
 * @author craig
 *
 */
public class IRBConfigData {
	private String title = "JRuby IRB Console";
	private String[] extraLoadPath = null;
    private String[] extraRequire = null;
    private ArrayList<URL> extraScripts = null;
	private HashMap<String,Object> extraGlobals = new HashMap<String,Object>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String[] getExtraLoadPath() {
		return extraLoadPath==null ? (new String[0]) : extraLoadPath;
	}
	public void setExtraLoadPath(String[] extraLoadPath) {
		this.extraLoadPath = extraLoadPath;
	}
    public String[] getExtraRequire() {
        return extraRequire==null ? (new String[0]) : extraRequire;
    }
    public void setExtraRequire(String[] extraRequire) {
        this.extraRequire = extraRequire;
    }
    public URL[] getExtraScripts() {
        return extraScripts==null ? (new URL[0]) : extraScripts.toArray(new URL[0]);
    }
    public void setExtraScripts(URL[] scriptURLs) {
        this.extraScripts = new ArrayList<URL>(Arrays.asList(scriptURLs));
    }
    public void addExtraScript(URL scriptURL){
        if(extraScripts==null) extraScripts=new ArrayList<URL>();
        extraScripts.add(scriptURL);
    }
	public String[] getStartScriptlets(){
		ArrayList<String> scriptlets = new ArrayList<String>();
		scriptlets.add("require 'irb'");
		scriptlets.add("require 'irb/completion'");
        for(String require:getExtraRequire()) scriptlets.add("require '"+require+"'");
        for(URL scriptURL:getExtraScripts()) scriptlets.add(loadScript(scriptURL));
        scriptlets.add("IRB.start");
		return scriptlets.toArray(new String[0]);
	}
	private static String loadScript(URL scriptURL){
        StringWriter sw = new StringWriter();
        BufferedReader br = null;
	    try{
            br = new BufferedReader(new FileReader(scriptURL.getPath()));
            String line;
            while((line=br.readLine())!=null) {sw.write(line);sw.append('\n');}
	    }catch(Exception e){
	        System.err.println("Failed to load script '"+scriptURL+"': "+e);
	        e.printStackTrace(System.err);
	    }finally{
	        try{
	            if(br!=null) br.close();
	        }catch(IOException ee){}
	    }
	    return sw.toString();
	}
    public HashMap<String, Object> getExtraGlobals() {
        return extraGlobals;
    }
    public void addExtraGlobal(String key, Object value) {
        this.extraGlobals.put(key,value);
    }
}