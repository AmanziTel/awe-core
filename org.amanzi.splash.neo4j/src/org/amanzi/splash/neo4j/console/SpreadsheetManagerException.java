package org.amanzi.splash.neo4j.console;

public class SpreadsheetManagerException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8677852394029809009L;
	private String rdtName;
	private String udigName;
	private String sheetName;
	
	public SpreadsheetManagerException(String rdtName, String udigName) {
		this.rdtName = rdtName;
		this.udigName = udigName;
	}
	
	public SpreadsheetManagerException(String rdtName, String udigName, String sheetName) {
        this.rdtName = rdtName;
        this.udigName = udigName;
        this.sheetName = sheetName;
    }
	
	public SpreadsheetManagerException(String udigName) {
		this.udigName = udigName;
		this.rdtName = "";
	}
	
	public String getMessage() {
		if (rdtName == null) {
		    String message = SpreadsheetManagerExceptionMessages.getFormattedString(SpreadsheetManagerExceptionMessages.No_Ruby_Projects, udigName);
			return message;
		}
		if (rdtName.equals("")) {
		    String message = SpreadsheetManagerExceptionMessages.getFormattedString(SpreadsheetManagerExceptionMessages.No_AWE_Project, udigName);
            return message;
		}		
		if (sheetName != null) {
		    String message = SpreadsheetManagerExceptionMessages.getFormattedString(SpreadsheetManagerExceptionMessages.No_Spreadsheet, sheetName, udigName, rdtName);
            return message;
		}
		
		String message = SpreadsheetManagerExceptionMessages.getFormattedString(SpreadsheetManagerExceptionMessages.No_Ruby_Project_in_AWE, rdtName, udigName);
        return message;
	}

}
