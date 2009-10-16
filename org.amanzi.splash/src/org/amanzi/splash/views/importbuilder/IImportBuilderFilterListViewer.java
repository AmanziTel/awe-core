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
