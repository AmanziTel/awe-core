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

package org.amanzi.awe.correlation.ui.view.table;

import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationTableContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof ICorrelationModel) {
            ICorrelationModel[] models = new ICorrelationModel[1];
            models[0] = (ICorrelationModel)inputElement;
            return models;
        }
        return new String[0];
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // TODO Auto-generated method stub

    }

}
