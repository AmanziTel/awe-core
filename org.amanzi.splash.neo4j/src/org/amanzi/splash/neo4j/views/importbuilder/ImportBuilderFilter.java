package org.amanzi.splash.neo4j.views.importbuilder;
/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

/**
 * Class used as a trivial case of a Task 
 * Serves as the business object for the TableViewer Example.
 * 
 * A Task has the following properties: completed, filter_text,
 * filter_heading and percentComplete 
 * 
 * @author Laurent
 *
 * 
 */
public class ImportBuilderFilter {

	
	private String filter_text 	= "";
	private String filter_heading 		= "?";
	
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

	

}
