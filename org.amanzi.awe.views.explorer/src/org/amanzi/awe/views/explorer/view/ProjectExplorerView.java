/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.awe.views.explorer.providers.ProjectTreeLabelProvider;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.NewEventManager;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends ViewPart {

    /*
     * ID of this View
     */
    public static final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";

    public static final String SHOW_PROPERTIES = "Show properties";
    public static final String DISTRIBUTION_ANALYSE = "Distribution analyse";

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    /**
     * The constructor.
     */
    public ProjectExplorerView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        setProviders();
        viewer.setInput(getSite());
        hookContextMenu();
        getSite().setSelectionProvider(viewer);
        setLayout(parent);
        addListeners();
    }

    /**
     * add required Listener
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        NewEventManager.getInstance().addListener(new UpdateDataEvent(), new RefreshTreeListener());
    }

    /**
     * <p>
     * describe listener to refresh Project Explorer View
     * </p>
     * 
     * @author Kondratenko_Vladislav
     * @since 1.0.0
     */
    private class RefreshTreeListener implements IEventsListener<UpdateDataEvent> {
        @Override
        public void handleEvent(UpdateDataEvent data) {
            viewer.refresh();
            viewer.setAutoExpandLevel(3);
        }

    }

    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * @param parent
     */
    private void setLayout(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        viewer.getTree().setLayoutData(formData);
    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    protected void setProviders() {
        viewer.setContentProvider(new ProjectTreeContentProvider());
        viewer.setLabelProvider(new ProjectTreeLabelProvider(viewer));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Select node
     * 
     * @param dataElement - dataElement to select
     */
    public void selectDataElement(IModel dataElement) {
        viewer.refresh();
        viewer.reveal(dataElement);
        viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

}
