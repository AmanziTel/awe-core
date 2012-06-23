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

package org.amanzi.neo.providers;

import java.util.Set;

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.exceptions.ModelException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IProjectModelProvider extends IModelProvider<IProjectModel, IModel> {

    Set<IProjectModel> findAllProjectModels();

    IProjectModel findProjectByName(String name);

    IProjectModel createProjectModel(String name) throws ModelException;

    IProjectModel getActiveProjectModel();

    void setActiveProjectModel(IProjectModel projectModel);

}
