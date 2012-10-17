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

package org.amanzi.awe.views.properties.views.internal;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyCellEditor extends TextCellEditor {
    /**
     * @param parent
     * @param border
     */
    public PropertyCellEditor(Composite parent, int border) {
        super(parent, border);
    }

    private Class< ? > valueClass;

    @Override
    protected void doSetValue(Object value) {
        valueClass = value.getClass();
        text.setText(value.toString());
    }

    @Override
    protected Object doGetValue() {
        String newValue = (String)super.doGetValue();
        try {
            return valueClass.getConstructor(newValue.getClass()).newInstance(newValue);
        } catch (Exception e) {
            return newValue;
        }
    }
}
