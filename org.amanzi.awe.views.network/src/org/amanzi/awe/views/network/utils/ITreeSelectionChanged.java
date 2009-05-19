package org.amanzi.awe.views.network.utils;

import java.util.List;
/**
 * Interface to be used in render to receive a list of names on selected items from network tree
 * view.
 * 
 * @author Milan Dinic
 */
public interface ITreeSelectionChanged {
    /**
     * This event is triggered from network tree view when context menu item is clicked.
     * 
     * @param selectedTreeItems list of selected items
     */
    void update( List<String> selectedTreeItems );

}
