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
    public PropertyCellEditor(final Composite parent, final int border) {
        super(parent, border);
    }

    private Class< ? > valueClass;

    @Override
    protected void doSetValue(final Object value) {
        valueClass = value.getClass();
        text.setText(value.toString());
    }

    @Override
    protected Object doGetValue() {
        final String newValue = (String)super.doGetValue();
        try {
            if (valueClass.isArray()) {
                // TODO: LN: 19.10.2012, is this code handle array of integers (or any non-string
                // type)
                return newValue.split(",");
            } else {
                // TODO: LN: 19.10.2012, how it will handle char properties?
                return valueClass.getConstructor(newValue.getClass()).newInstance(newValue);
            }
        } catch (final Exception e) {
            // TODO: LN: 19.10.2012, handle exception
            return newValue;
        }
    }
}
