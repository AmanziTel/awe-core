package org.amanzi.splash.views.importbuilder;

public interface IImportBuilderFilterListViewer {
	
	/**
	 * Update the view to reflect the fact that a task was added 
	 * to the task list
	 * 
	 * @param task
	 */
	public void addFilter(ImportBuilderFilter filter);
	
	/**
	 * Update the view to reflect the fact that a filter was removed 
	 * from the filter list
	 * 
	 * @param filter
	 */
	public void removeFilter(ImportBuilderFilter filter);
	
	/**
	 * Update the view to reflect the fact that one of the filters
	 * was modified 
	 * 
	 * @param filter
	 */
	public void updateFilter(ImportBuilderFilter filter);
}
