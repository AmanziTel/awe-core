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

package org.amanzi.awe.views.explorer.providers;

import java.util.List;

import org.amanzi.awe.ui.AWEUIPlugin;
import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.awe.views.treeview.provider.impl.AbstractContentProvider;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;

/**
 * content provider for project explorer
 * 
 * @author Vladislav_Kondratenko
 */
public class ProjectTreeContentProvider extends AbstractContentProvider<IProjectModel, IModel> {

    /**
     * @param projectModelProvider
     */
    public ProjectTreeContentProvider() {
        super(AWEUIPlugin.getDefault().getProjectModelProvider());
        // TODO Auto-generated constructor stub
    }

    @Override
    public ITreeItem<IProjectModel, IModel> createInnerItem(IProjectModel key, IModel value) {
        return new ProjectTreeItem(key, value);
    }

    @Override
    public List<IProjectModel> getRootList() throws ModelException {
        return getProjectModelProvider().findAll();
    }

    @Override
    public ITreeItem<IProjectModel, IModel> createInnerItem(IProjectModel key, IProjectModel value) {
        return new ProjectTreeItem(key, value);
    }

}
