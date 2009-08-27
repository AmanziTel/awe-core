package org.amanzi.splash.views.importbuilder;

public class ImportBuilderFilter {

	
	private String filter_text 	= "";
	private String filter_heading 		= "?";
	private String filter_filename 		= "new_filter.rb";
	
	/**
	 * Create a task with an initial filter_text
	 * 
	 * @param string
	 */
	public ImportBuilderFilter(String string) {
		
		super();
		setFilterText(string);
	}

	public ImportBuilderFilter(String heading, String text) {
		
		super();
		setFilterHeading(heading);
		setFilterText(text);
	}
	
public ImportBuilderFilter(String heading, String text, String filename) {
		
		super();
		setFilterHeading(heading);
		setFilterText(text);
		setFilterFilename(filename);
	}

	/**
	 * @return String task filter_text
	 */
	public String getFilterText() {
		return filter_text;
	}

	/**
	 * @return String task filter_heading
	 */
	public String getFilterHeading() {
		return filter_heading;
	}

	

	
	/**
	 * Set the 'filter_text' property
	 * 
	 * @param string
	 */
	public void setFilterText(String string) {
		filter_text = string;
	}

	/**
	 * Set the 'filter_heading' property
	 * 
	 * @param string
	 */
	public void setFilterHeading(String string) {
		filter_heading = string;
	}

	public String getFilterFilename() {
		return filter_filename;
	}

	public void setFilterFilename(String filter_filename) {
		this.filter_filename = filter_filename;
	}

	

}
