/**
 * 
 */
package org.amanzi.awe.drive.ui.view;

import org.amanzi.awe.drive.ui.provider.DriveLavelProvider;
import org.amanzi.awe.ui.tree.view.AbstractAWETreeView;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * This View contains a tree of measurements found in the database.
 * 
 * @author Bondoronok_P
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 */
public class DriveTreeView extends AbstractAWETreeView {

    private static final String DRIVE_TREE_VIEW_ID = "org.amanzi.trees.DriveTree";

    public DriveTreeView() {
        super();
    }

    @Override
    public String getViewId() {
        return DRIVE_TREE_VIEW_ID;
    }

    @Override
    protected IBaseLabelProvider createLabelProvider() {
        return new DriveLavelProvider();
    }

}
