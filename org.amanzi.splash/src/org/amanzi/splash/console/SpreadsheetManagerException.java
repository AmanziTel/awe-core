package org.amanzi.splash.console;

public class SpreadsheetManagerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8677852394029809009L;
	private String rdtName;
	private String udigName;
	
	public SpreadsheetManagerException(String rdtName, String udigName) {
		this.rdtName = rdtName;
		this.udigName = udigName;
	}
	
	public SpreadsheetManagerException(String udigName) {
		this.udigName = udigName;
		this.rdtName = "";
	}
	
	public String getMessage() {
		if (rdtName == null) {
			return "AWE Project <" + udigName + "> doesn't contain any Ruby Projects";
		}
		if (rdtName.equals("")) {
			return "AWE Project <" + udigName + "> cannot be found";
		}		
		
		return "AWE Project <" + udigName + "> doesn't contain Ruby Project <" + rdtName +">.";
	}

}
