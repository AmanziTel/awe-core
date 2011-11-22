/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.amanzi.awe.views.explorer.ProjectExplorerPlugin;
import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.awe.views.explorer.providers.ProjectTreeLabelProvider;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends ViewPart {

    private static final String RENAME_MSG = "Enter new Name";

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
        addListeners(viewer);
    }

    /**
     * @param viewer2
     */
    private void addListeners(final TreeViewer viewer) {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                System.out.println(selection.getFirstElement());
            }
        });
    }

    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ProjectExplorerView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        ShowPropertiesViewAction select = new ShowPropertiesViewAction((IStructuredSelection)viewer.getSelection());
        if (select.isEnabled()) {
            manager.add(select);
        }
        DistributionAction distributeAction = new DistributionAction((IStructuredSelection)viewer.getSelection());
        if (distributeAction.isEnabled()) {
            manager.add(distributeAction);
        }
        RenameAction renameAction = new RenameAction((IStructuredSelection)viewer.getSelection());
        manager.add(renameAction);

    }

    private class DistributionAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
        public DistributionAction(IStructuredSelection selection) {
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                Object elementObject = it.next();
                if (elementObject instanceof IProjectModel) {
                    continue;
                } else {
                    IDataElement element = (IDataElement)elementObject;
                    selectedDataElements.add(element);
                }
            }
            enabled = selectedDataElements.size() > 0;
            text = DISTRIBUTION_ANALYSE;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
            } catch (PartInitException e) {
                ProjectExplorerPlugin.error(null, e);
            }
        }
    }

    private class ShowPropertiesViewAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
        public ShowPropertiesViewAction(IStructuredSelection selection) {
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                Object elementObject = it.next();
                if (elementObject instanceof IProjectModel) {
                    continue;
                } else {
                    IDataElement element = (IDataElement)elementObject;
                    selectedDataElements.add(element);
                }
            }
            enabled = selectedDataElements.size() > 0;
            text = SHOW_PROPERTIES;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                // getPropertySheetPage();
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
            } catch (PartInitException e) {
                ProjectExplorerPlugin.error(null, e);
            }
        }
    }

    private class RenameAction extends Action {

        private boolean enabled;
        private final String text;
        private IDataElement dataElement;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public RenameAction(IStructuredSelection selection) {
            text = "Rename";
            enabled = selection.size() == 1 && selection.getFirstElement() instanceof IDataElement
                    && !(selection.getFirstElement() instanceof INetworkModel);
            if (enabled) {
                dataElement = (IDataElement)selection.getFirstElement();
                enabled = (dataElement.get(NewAbstractService.NAME) == null) ? false : true;
            }
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            
            viewer.refresh();
        }

        /**
         * Opens a dialog asking the user for a new name.
         * 
         * @return The new name of the element.
         */
        private String getNewName(String oldName) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), RENAME_MSG, "", oldName, null); //$NON-NLS-1$
            int result = dialog.open();
            if (result == Dialog.CANCEL)
                return oldName;
            return dialog.getValue();
        }
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
    public void selectDataElement(IDataElement dataElement) {
        viewer.refresh();
        viewer.reveal(dataElement);
        viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }    
}
