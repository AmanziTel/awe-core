package org.amanzi.neo.core.database.services;

/**
 * A event which indicates that cells in bd was updates
 * 
 * @author Cinkel_A
 * 
 */
public class UpdateBdEvent {

	private final String rubyProjectName;
	private final String spreadSheetName;
	private final String fullCellID;

	/**
	 * Constructor
	 * 
	 * @param rubyProjectName
	 *            ruby project name
	 * @param spreadSheetName
	 *            spreadSheet Name
	 * @param fullCellID
	 *            full cell id
	 */
	public UpdateBdEvent(String rubyProjectName, String spreadSheetName,
			String fullCellID) {
		this.rubyProjectName = rubyProjectName;
		this.spreadSheetName = spreadSheetName;
		this.fullCellID = fullCellID;
	}

	/**
	 * @return the rubyProjectName
	 */
	public String getRubyProjectName() {
		return rubyProjectName;
	}

	/**
	 * @return the spreadSheetName
	 */
	public String getSpreadSheetName() {
		return spreadSheetName;
	}

	/**
	 * @return the fullCellID
	 */
	public String getFullCellID() {
		return fullCellID;
	}

}
