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

package org.amanzi.awe.explorer.ui.wrappers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.explorer.ui.ProjectExplorerPlugin;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.ITreeWrapperFactory;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectWrapperFactory implements ITreeWrapperFactory {

    private final IProjectModelProvider projectModelProvider;

    public ProjectWrapperFactory() {
        this.projectModelProvider = ProjectExplorerPlugin.getDefault().getProjectModelProvider();
    }

    @Override
    public Iterator<ITreeWrapper> getWrappers(final Object parent) {
        final List<ITreeWrapper> wrappers = new ArrayList<ITreeWrapper>();

        try {
            for (final IProjectModel model : projectModelProvider.findAll()) {
                wrappers.add(new ProjectModelWrapper(model));
            }
        } catch (final ModelException e) {
            // TODO: LN: 15.10.2012, handle exception
        }
        return wrappers.iterator();
    }
}
