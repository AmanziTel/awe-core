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

package org.amanzi.neo.loader.core.parser;

/**
 * <p>
 *Data contains information about one text line
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class LineTransferData extends HeaderTransferData {
    
    /** long serialVersionUID field */
    private static final long serialVersionUID = -1270324348309659750L;
    private String stringLine;

    /**
     * Gets the string line.
     *
     * @return the string line
     */
    public String getStringLine() {
        return stringLine;
    }

    /**
     * Sets the string line.
     *
     * @param stringLine the new string line
     */
    public void setStringLine(String stringLine) {
        this.stringLine = stringLine;
    }
    
}
