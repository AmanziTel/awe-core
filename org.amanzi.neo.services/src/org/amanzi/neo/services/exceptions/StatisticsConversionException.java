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

package org.amanzi.neo.services.exceptions;

import org.amanzi.neo.services.exceptions.enums.ServiceExceptionReason;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsConversionException extends ServiceException {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -8216293644683556959L;

    private final Class< ? > fromClass;

    private final Class< ? > toClass;

    private final Object value;

    private final String propertyName;

    /**
     * @param reason
     * @param fromClass
     * @param toClass
     * @param value
     * @param propertyName
     */
    public StatisticsConversionException(Class< ? > fromClass, Class< ? > toClass, Object value, String propertyName) {
        super(ServiceExceptionReason.STATISTICS_CONVERSION_EXCEPTION);
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.value = value;
        this.propertyName = propertyName;
    }

    /**
     * @return Returns the fromClass.
     */
    public Class< ? > getFromClass() {
        return fromClass;
    }

    /**
     * @return Returns the toClass.
     */
    public Class< ? > getToClass() {
        return toClass;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

}
