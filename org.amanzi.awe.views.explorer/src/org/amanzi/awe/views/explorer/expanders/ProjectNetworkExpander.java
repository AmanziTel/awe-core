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

package org.amanzi.awe.views.explorer.expanders;

import java.util.Iterator;

import org.amanzi.awe.ui.tree.expanders.impl.AbstractModelExpander;
import org.amanzi.awe.views.explorer.ProjectExplorerPlugin;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.INetworkModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectNetworkExpander extends AbstractModelExpander<IProjectModel, IProjectModel, INetworkModel> {

    private final INetworkModelProvider networkModelProvider;

    public ProjectNetworkExpander() {
        networkModelProvider = ProjectExplorerPlugin.getDefault().getNetworkModelProvider();
    }

    @Override
    protected IProjectModel getParent(final IProjectModel child) {
        return null;
    }

    @Override
    protected Class<IProjectModel> getSupportedParentClass() {
        return IProjectModel.class;
    }

    @Override
    protected Class<IProjectModel> getSupportedChildClass() {
        return IProjectModel.class;
    }

    @Override
    protected Iterator<INetworkModel> getChildren(final IProjectModel model, final IProjectModel child) {
        try {
            return networkModelProvider.findAll(child).iterator();
        } catch (final ModelException e) {
            // TODO: LN: 10.10.2012, handle exception
        }
        return null;
    }
}
