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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = Logger.getLogger(PropertyCellEditor.class);

    private Object oldValue;

    /**
     * @param parent
     * @param border
     */
    public PropertyCellEditor(final Composite parent, final int border) {
        super(parent, border);
    }

    /**
     * create an array of new values on base of class of first element in previous array
     * 
     * @param klass
     * @param length
     * @return
     */
    private Object[] createNewArray(final Class< ? extends Object> klass, final int length) {
        if (klass.equals(Integer.class)) {
            return new Integer[length];
        } else if (klass.equals(Long.class)) {
            return new Long[length];
        } else if (klass.equals(String.class)) {
            return new String[length];
        } else if (klass.equals(Double.class)) {
            return new Double[length];
        } else if (klass.equals(Float.class)) {
            return new Float[length];
        } else if (klass.equals(Character.class)) {
            return new Character[length];
        }
        return null;
    }

    @Override
    protected Object doGetValue() {
        String newValue = (String)super.doGetValue();
        try {
            if (oldValue.getClass().isArray()) {
                return performArrayCasting(newValue);
            } else {
                if (oldValue.getClass().equals(Character.class)) {
                    return oldValue.getClass().cast(newValue);
                }
                return oldValue.getClass().getConstructor(newValue.getClass()).newInstance(newValue);
            }
        } catch (Exception e) {
            LOGGER.error("Error on casting value ", e);
            return newValue;
        }
    }

    @Override
    protected void doSetValue(final Object value) {
        oldValue = value;
        text.setText(value.toString());
    }

    /**
     * @param newValue
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    private Object performArrayCasting(final String newValue) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String preparedString = prepareStringToConverting(newValue);
        String[] stringArray = preparedString.split(",");

        Object[] oldArray = (Object[])oldValue;
        Class< ? > expectedClass = oldArray[0].getClass();
        Object[] newArray = createNewArray(expectedClass, stringArray.length);

        if (newArray == null) {
            return null;
        }

        for (int i = 0; i < stringArray.length; i++) {
            String newStringValue = stringArray[i];
            if (expectedClass.equals(Character.class)) {
                newArray[i] = oldValue.getClass().cast(newValue);
                continue;
            }
            newArray[i] = expectedClass.getConstructor(newStringValue.getClass()).newInstance(newStringValue);

        }
        return newArray;
    }

    /**
     * @param newValue
     * @return
     */
    private String prepareStringToConverting(String newValue) {
        newValue = newValue.replace("[", StringUtils.EMPTY);
        newValue = newValue.replace("]", StringUtils.EMPTY);
        return newValue;
    }
}
