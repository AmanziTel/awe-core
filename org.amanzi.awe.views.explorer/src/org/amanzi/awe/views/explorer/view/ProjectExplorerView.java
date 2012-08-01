/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.awe.views.treeview.AbstractTreeView;
import org.amanzi.neo.models.IModel;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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

    /**
     * The constructor.
     */
    public ProjectExplorerView() {
        super();
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {

        treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        setProviders();
        treeViewer.setInput(getViewSite());
        treeViewer.setComparer(new IElementComparer() {

            @Override
            public int hashCode(final Object element) {
                return 0;
            }

            @Override
            public boolean equals(final Object a, final Object b) {
                if ((a instanceof IModel) && (b instanceof IModel)) {
                    IModel aM = (IModel)a;
                    IModel bM = (IModel)b;
                    return aM.getName().equals(bM.getName()) && aM.getClass().equals(bM.getClass());
                }
                return a == null ? b == null : a.equals(b);
            }
        });
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuManager, treeViewer);
        getSite().setSelectionProvider(treeViewer);
        setLayout(parent);

    }

    @Override
    public void dispose() {
        AWEEventManager.getManager().removeListener(this);
        super.dispose();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    @Override
    protected IContentProvider getContentProvider() {
        return new ProjectTreeContentProvider();
    }

    @Override
    protected void addEventListeners() {

    }

}
