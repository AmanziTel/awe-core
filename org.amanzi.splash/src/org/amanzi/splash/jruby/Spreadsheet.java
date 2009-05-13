package org.amanzi.splash.jruby;

import org.amanzi.splash.utilities.Util;

public class Spreadsheet {
	public JRubyJavaInterface cells;
	public String test;
	
	public Spreadsheet() {
		super();
		cells = new JRubyJavaInterface();
		test = "Hello";
	}
	
	public static void print_somthing()
	{
		Util.log("Hello, I'm java");
	}

	public JRubyJavaInterface cells()
	{
		return cells;
	}
}
