package org.amanzi.awe.script.jirb;

import java.util.ArrayList;

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
	public String[] getStartScriptlets(){
		ArrayList<String> scriptlets = new ArrayList<String>();
		scriptlets.add("require 'irb'");
		scriptlets.add("require 'irb/completion'");
        for(String require:getExtraRequire()) scriptlets.add("require '"+require+"'");
		scriptlets.add("IRB.start");
		return scriptlets.toArray(new String[0]);
	}
}