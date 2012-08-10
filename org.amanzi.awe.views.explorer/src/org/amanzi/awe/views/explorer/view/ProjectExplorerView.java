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
import org.amanzi.neo.models.project.IProjectModel;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * project explorer view
 * 
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends AbstractTreeView implements MenuListener {
    /*
     * ID of this View
     */
    public static final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";
    private static final String SHOW_IN_VIEW_ITEM = "Show in View";
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

    private Menu menu;

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
        menu = menuManager.createContextMenu(getTreeViewer().getControl());
        menu.addMenuListener(this);
        getTreeViewer().getControl().setMenu(menu);
        getTreeViewer().setComparer(TREE_ITEMS_COMPARATOR);
        getSite().registerContextMenu(menuManager, getTreeViewer());
    }

    @Override
    public void menuHidden(MenuEvent e) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void menuShown(MenuEvent e) {
        IStructuredSelection selection = (IStructuredSelection)getTreeViewer().getSelection();
        ITreeItem<IProjectModel> item = (ITreeItem<IProjectModel>)selection.getFirstElement();
        if (item.getParent().asDataElement().equals(item.getDataElement())) {
            menu.getItems()[getMenuItemIndexByName(SHOW_IN_VIEW_ITEM)].setEnabled(false);
        } else {
            menu.getItems()[getMenuItemIndexByName(SHOW_IN_VIEW_ITEM)].setEnabled(true);
        }

    }

    /**
     * @param showInViewItem
     * @return
     */
    private int getMenuItemIndexByName(String showInViewItem) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            if (menu.getItem(i).getText().equalsIgnoreCase(showInViewItem)) {
                return i;
            }
        }
        return 0;
    }
}
