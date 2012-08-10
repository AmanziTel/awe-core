package org.amanzi.neo.loader.ui.preference.dateformat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class IsDefaultEditor extends EditingSupport {

    private final FormatTableViewer viewer;

    public IsDefaultEditor(FormatTableViewer viewer) {
        super(viewer);
        this.viewer = viewer;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);

    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        return element.equals(viewer.getDefaultFormat());

    }

    @Override
    protected void setValue(Object element, Object value) {
        viewer.setDefaultFormat((String)element);
        viewer.refresh();
    }
}
