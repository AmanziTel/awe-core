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

package org.amanzi.neo.propertyFilter;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public enum OperationCase {
    INCLUDE("Include"), EXCLUDE("Exclude"), NEW("NEW"), REMOVE_CANDIDAT("remove");

    private final String stringValue;

    private OperationCase(String stringValue) {
        this.stringValue = stringValue;
    }

    public OperationCase nextCase() {
        switch (this) {
        case INCLUDE:
            return EXCLUDE;
        case EXCLUDE:
            return REMOVE_CANDIDAT;
        default:
            return INCLUDE;
        }
    }

    public String getStringValue() {
        return stringValue;
    }

    public static OperationCase getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (OperationCase operationCase : OperationCase.values()) {
            if (operationCase.getStringValue().equals(enumId)) {
                return operationCase;
            }
        }
        return null;
    }
}
