package org.amanzi.neo.core.database.services;

/**
 * A event which indicates that database was updates now support: update cells in spreadsheet and
 * update GIS nodes
 * 
 * @author Cinkel_A
 */
public class UpdateDatabaseEvent {

    private final UpdateDatabaseEventType type;
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
	public UpdateDatabaseEvent(String rubyProjectName, String spreadSheetName,
			String fullCellID) {
        this.type = UpdateDatabaseEventType.Spreadsheet;
		this.rubyProjectName = rubyProjectName;
		this.spreadSheetName = spreadSheetName;
		this.fullCellID = fullCellID;
	}

    public UpdateDatabaseEvent(UpdateDatabaseEventType type) {
        this.type = type;
        this.rubyProjectName = null;
        this.spreadSheetName = null;
        this.fullCellID = null;
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

    /**
     * @return Returns the type.
     */
    public UpdateDatabaseEventType getType() {
        return type;
    }

}
