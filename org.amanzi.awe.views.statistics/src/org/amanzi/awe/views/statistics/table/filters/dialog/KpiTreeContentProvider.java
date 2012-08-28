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

package org.amanzi.awe.views.statistics.table.filters.dialog;

import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class KpiTreeContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        Set<String> input = (Set<String>)inputElement;
        int size = input.size();
        Object[] elements = new Object[size + 1];
        System.arraycopy(input.toArray(), 0, elements, 1, size);
        elements[0] = KpiComboViewerWidget.SELECT_ALL_ITEM;
        return elements;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return false;
    }

}
