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

package org.amanzi.awe.ui.tree.expanders;

import java.util.Collection;

import org.amanzi.awe.ui.dto.IUIItem;
import org.amanzi.neo.models.IModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IChildrenExpander<P extends IModel, C extends Object> {

    Collection<IUIItem< ? , ? >> getChildren(IUIItem<P, C> parent);

    IUIItem< ? , ? > getParent(IUIItem<P, C> child);

    boolean hasChildren(IUIItem<P, C> parent);

    boolean canHandle(Class< ? > parentClass, Class< ? > childClass);
}
