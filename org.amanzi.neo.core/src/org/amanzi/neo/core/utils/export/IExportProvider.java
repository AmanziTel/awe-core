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

package org.amanzi.neo.core.utils.export;

import java.util.List;

/**
 * <p>
 * IExportProvider - provide interface for export provider
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IExportProvider {

    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    boolean isValid();

    /**
     * Checks for next line.
     * 
     * @return true, if successful
     */
    boolean hasNextLine();

    /**
     * Gets the data name.
     * 
     * @return the data name
     */
    String getDataName();

    /**
     * Gets the next line.
     * 
     * @return the next line or null if no line exist
     */
    List<Object> getNextLine();

    /**
     * Gets the headers.
     * 
     * @return the headers
     */
    List<String> getHeaders();

}