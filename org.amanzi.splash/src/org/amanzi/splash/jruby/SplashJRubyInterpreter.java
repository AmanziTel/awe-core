package org.amanzi.splash.jruby;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.table.TableModel;

import org.amanzi.scripting.jruby.ScriptUtils;

import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.Util;


public class SplashJRubyInterpreter{
	private ScriptEngine engine;
	TableModel model;
	
	public SplashJRubyInterpreter()
	{
		startJRubyInterpreter();
	}
	
	public String Interpret(String v, TableModel m) {
		model = m;
		String s1 = interpretRule(v);
		//Util.log("Interpreted String Phase1:" + s1);
		String s2 = interpretByJRuby(s1);
		//Util.log("Interpreted String Phase2:" + s2);
		return s2;
	}
	
	public Object Interpret(String cellID, String v, TableModel m) {
		model = m;
		String s0 = cellID.toLowerCase()+v;
		
		List<String> list = Util.findComplexCellIDs(s0);
		
		for (int i=0;i<list.size();i++)
			s0 = s0.replace(list.get(i), "$" + list.get(i).toLowerCase());
		
		ScriptContext ctx = getEngine().getContext();
		Object s = null;
		try {
			s = getEngine().eval("$a1 = 3", ctx);
			Util.log("JRuby1: " + s);
			s = getEngine().eval("$a2 = $a1", ctx);
			Util.log("JRuby2: " + s);
		} catch (ScriptException e) {
			
			e.printStackTrace();
		}
		

		
		
		return s;
	}
	
	private String interpretByJRuby1(String formula) {
		String s = "";
		
		if (engine == null) startJRubyInterpreter();

		String path = ScriptUtils.getJRubyHome() + "/lib/ruby/1.8" + "/erb";
		try {
			String input = "require" + "'" + path + "'" + "\n" +
			"template = ERB.new <<-EOF" + "\n" +
			formula + "\n" +
			"EOF" + "\n" + 
			"return template.result(binding)";
			Util.log("interpretByJRuby: Formula String:" + input);
			ScriptContext ctx = engine.getContext();
			s = (String) engine.eval(formula, ctx);
			Util.log("s = " + s);

		} catch (ScriptException e) {
			s = "!ERROR";
			e.printStackTrace();
		}
		
		if (s == null) s = "!ERROR";
		
		//Util.log("interpretByJRuby: Interpreted String:" + s);
		
		return s;
	}

	

	public List<String> getComplexCellIDsBySourceCellID(String sourceCellID) {
		// first, get forumla string for such cell;
		String formula = getFormulaStringByCellID(sourceCellID);
		
		// second, parse the formula to find complex cell IDs
		List<String> list = Util.findComplexCellIDs(formula);
		
		return list;
	}

	public String getFormulaStringByCellID(String cellID) {
		if (!cellID.equals("") == true)
		{
			
	         int row = Util.getRowIndexFromCellID(cellID);
	        
	         int column = Util.getColumnIndexFromCellID(cellID);
	         
	         Cell ex = (Cell)model.getValueAt(row, column);
	         return ex.getValue().toString();// TODO: return getString(row, column);
		}
		else
			return cellID;
	}

	public String interpretRule(String v) {
		String s = "";
		
		if (v.startsWith("=") == true)
		{
			v = v.replace("=", "");
			List<String> list1 = Util.findComplexCellIDs(v);
			
			for (int i=0;i<list1.size();i++)
			{
				
				v = v.replace("#{"+list1.get(i)+"}", getFormulaStringByCellID(list1.get(i)));
			}
			s = "<%= " + v + "%>";
			s = s.replace("\n", "");
		}
		else
		{
			List<String> list1 = Util.findComplexCellIDs(v);
			
			for (int i=0;i<list1.size();i++)
			{
				
				v = v.replace("#{"+list1.get(i)+"}", getFormulaStringByCellID(list1.get(i)));
			}
			s = v;
			s = s.replace("\n", "");
		}
		return s;
	}


	public String interpretByJRuby(String formula) {
String s = "";
		
		if (engine == null) startJRubyInterpreter();

		String path = ScriptUtils.getJRubyHome() + "/lib/ruby/1.8" + "/erb";
		try {
			String input = "require" + "'" + path + "'" + "\n" +
			"template = ERB.new <<-EOF" + "\n" +
			formula + "\n" +
			"EOF" + "\n" + 
			"return template.result(binding)";
			//Util.log("interpretByJRuby: Formula String:" + input);
			ScriptContext ctx = engine.getContext();
			s = (String) engine.eval(input, ctx);
			//String result = (String) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("result");

			//Util.log("s = " + s);
			
					
		} catch (ScriptException e) {
			s = "!ERROR";
			e.printStackTrace();
		}
		
		if (s == null) s = "!ERROR";
		
		//Util.log("interpretByJRuby: Interpreted String:" + s);
		
		return s;
	}

	
	

	public void startJRubyInterpreter() {
		ScriptEngineManager manager = new ScriptEngineManager();
		 
		 // Override due to classpath troubles with OSGi ?
		 manager.registerEngineName("jruby", 
				 new com.sun.script.jruby.JRubyScriptEngineFactory());
		 
		 //listScriptingEngines();
		 engine = manager.getEngineByName("jruby");
		
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}

	


}
