package org.amanzi.awe.neo.views.network.utils;

import java.util.List;

/**
 * Interface to be used in render to receive a list of names on selected items
 * from network tree view.
 * 
 * @author Dalibor
 */
public interface ITreeSelectionChanged {
	/**
	 * This event is triggered from network tree view when context menu item is
	 * clicked.
	 * 
	 * @param selectedTreeItems
	 *            list of selected items
	 */
	void update(List<String> selectedTreeItems);

}
