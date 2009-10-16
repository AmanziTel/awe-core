/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.splash.console;

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
