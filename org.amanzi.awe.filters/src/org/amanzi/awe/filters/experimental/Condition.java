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

package org.amanzi.awe.filters.experimental;

/**
 * Enumeration for conditions
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public enum Condition {
    LT("<"), LE("<="), GT(">"), GE(">="), NE("!="), EQ("=="), IN("in"), HAS_PROPERTY("has property");
    private String text;

    private Condition(String text) {
        this.text = text;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

}
