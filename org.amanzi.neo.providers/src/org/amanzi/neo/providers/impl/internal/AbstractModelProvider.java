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

package org.amanzi.neo.providers.impl.internal;

import java.util.Set;

import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.providers.IModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractModelProvider<T1 extends AbstractModel, T2 extends IModel> implements IModelProvider<T1, T2> {

    @Override
    public T1 findById(long id) {
        return null;
    }

    @Override
    public Set<T1> findByParent(T2 parent) {
        return null;
    }

    @Override
    public Set<T1> findByName(T2 parent, String modelName) {
        return null;
    }

    @Override
    public T1 create(T2 parent, String name) {
        return null;
    }

}
