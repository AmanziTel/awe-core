/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.models.IModel;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * project explorer view
 * 
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends AbstractTreeView {
    /*
     * ID of this View
     */
    public static final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";

    private static final IElementComparer TREE_ITEMS_COMPARATOR = new IElementComparer() {

        @Override
        public int hashCode(final Object element) {
            return HashCodeBuilder.reflectionHashCode(element, false);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object a, final Object b) {
            if ((a instanceof ITreeItem< ? >) && (b instanceof ITreeItem< ? >)) {
                ITreeItem<IModel> aM = (ITreeItem<IModel>)a;
                ITreeItem<IModel> bM = (ITreeItem<IModel>)b;
                return aM.equals(bM);
            }
            return a == null ? b == null : a.equals(b);
        }
    };

    /**
     * The constructor.
     */
    public ProjectExplorerView() {
        this(new ProjectTreeContentProvider());
    }

    protected ProjectExplorerView(ProjectTreeContentProvider projectTreeContentProvider) {
        super(projectTreeContentProvider);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(getTreeViewer().getControl());
        getTreeViewer().getControl().setMenu(menu);
        getTreeViewer().setComparer(TREE_ITEMS_COMPARATOR);
        getSite().registerContextMenu(menuManager, getTreeViewer());
    }

}
