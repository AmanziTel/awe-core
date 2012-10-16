/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.explorer.ui.view;

import org.amanzi.awe.explorer.ui.ProjectExplorerPlugin;
import org.amanzi.awe.explorer.ui.preferences.ExplorerLabelsInitialzer;
import org.amanzi.awe.explorer.ui.provider.ExplorerContentProvider;
import org.amanzi.awe.ui.tree.view.AbstractAWETreeView;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * project explorer view
 * 
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends AbstractAWETreeView {
    /*
     * ID of this View
     */
    private static final String PROJECT_EXPLORER_ID = "org.amanzi.trees.ProjectExplorer";

    public ProjectExplorerView() {
        super();
    }

    @Override
    public String getViewId() {
        return PROJECT_EXPLORER_ID;
    }

    @Override
    protected ITreeContentProvider createContentProvider() {
        return new ExplorerContentProvider(getFactories());
    }

    @Override
    protected IPreferenceStore getPreferenceStore() {
        return ProjectExplorerPlugin.getDefault().getPreferenceStore();
    }

    @Override
    protected String getLabelTemplateKey() {
        return ExplorerLabelsInitialzer.EXPLORER_LABEL_TEMPLATE;
    }

}
