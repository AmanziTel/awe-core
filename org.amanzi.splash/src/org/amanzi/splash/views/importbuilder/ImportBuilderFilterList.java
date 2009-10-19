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


package org.amanzi.splash.views.importbuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that plays the role of the domain model in the ImportBuilderTableViewer
 * In real life, this class would access a persistent store of some kind.
 * 
 */

public class ImportBuilderFilterList {

	//private Vector filters = new Vector(COUNT);
	private Set<IImportBuilderFilterListViewer> changeListeners = new HashSet<IImportBuilderFilterListViewer>();

	ArrayList<ImportBuilderFilter> filters = new ArrayList<ImportBuilderFilter>();
	ArrayList<String> filter_headings = new ArrayList<String>();
	
	/**
	 * Constructor
	 */
	public ImportBuilderFilterList() {
		super();
	}
	
	/**
	 * Return the array of owners   
	 */
	public String[] getHeadingsList() {
		
		String[] ret = new String[filter_headings.size()];
		for (int i=0;i<filter_headings.size();i++) ret[i] = filter_headings.get(i);
		return ret;	    
		
	}
	
	public void addHeadingToList(String heading){
		filter_headings.add(heading);
	}
	
	public void setFilterHeading(int index, String heading){
		filter_headings.set(index, heading);
	}
	
	/**
	 * Return the collection of filters
	 */
	public ArrayList<ImportBuilderFilter> getFilters() {
		return filters;
	}
	
	/**
	 * Add a new filter to the collection of filters
	 */
	public void addFilter() {
		ImportBuilderFilter filter = new ImportBuilderFilter("Filter Heading", "Filter Text");
		filters.add(filters.size(), filter);
		Iterator<IImportBuilderFilterListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().addFilter(filter);
	}
	
	/**
	 * Add a new filter to the collection of filters
	 */
	public void addFilter(String heading, String text) {
		ImportBuilderFilter filter = new ImportBuilderFilter(heading, text);
		filters.add(filters.size(), filter);
		Iterator<IImportBuilderFilterListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().addFilter(filter);
		
		
	}

	/**
	 * @param filter
	 */
	public void removeFilter(ImportBuilderFilter filter) {
		filters.remove(filter);
		Iterator<IImportBuilderFilterListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().removeFilter(filter);
	}

	/**
	 * @param filter
	 */
	public void filterChanged(ImportBuilderFilter filter) {
		Iterator<IImportBuilderFilterListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().updateFilter(filter);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(IImportBuilderFilterListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IImportBuilderFilterListViewer viewer) {
		changeListeners.add(viewer);
	}
	
	public String getFilterRubyCode(){
		String code = "";
		code += "sheet_importer = SheetImporter.create.filter_and { \n";
		for (int i=0;i<filters.size();i++){
			code += "match '" + filters.get(i).getFilterHeading() + "', '"+filters.get(i).getFilterText()+"' \n";
		}
		code += "} \n";
		
		return code;
	}


}
