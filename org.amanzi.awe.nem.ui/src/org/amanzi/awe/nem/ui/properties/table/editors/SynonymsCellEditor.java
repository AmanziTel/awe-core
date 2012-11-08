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

package org.amanzi.awe.nem.ui.properties.table.editors;

import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SynonymsCellEditor extends EditingSupport {

    /**
     * @param viewer
     */
    public SynonymsCellEditor(final TableViewer viewer) {
        super(viewer);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean canEdit(final Object element) {
        return true;
    }

    @Override
    protected CellEditor getCellEditor(final Object element) {
        return new TextCellEditor(((TableViewer)getViewer()).getTable());
    }

    @Override
    protected Object getValue(final Object element) {
        SynonymsWrapper wrapper = (SynonymsWrapper)element;
        return wrapper.getHeader();
    }

    @Override
    protected void setValue(final Object element, final Object value) {
        SynonymsWrapper wrapper = (SynonymsWrapper)element;
        wrapper.setHeader((String)value);
        getViewer().refresh();
    }

}
