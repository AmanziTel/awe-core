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

package org.amanzi.awe.wizards.geoptima.export;

import java.util.List;

/**
 * <p>
 * Abstract exporter - provide work with Export models
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class AbstractExporter {

    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public abstract boolean isValid();

    /**
     * Checks for next line.
     * 
     * @return true, if successful
     */
    public abstract boolean hasNextLine();

    public abstract String getDataName();

    /**
     * Gets the next line.
     * 
     * @return the next line or null if no line exist
     */
    public abstract List<Object> getNextLine();

    /**
     * Gets the headers.
     * 
     * @return the headers
     */
    public abstract List<String> getHeaders();
}

