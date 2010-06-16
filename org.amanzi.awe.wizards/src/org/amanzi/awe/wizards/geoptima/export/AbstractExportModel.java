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
 * Abstract Export model
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class AbstractExportModel {

    /**
     * Instantiates a new export model.
     */
    public AbstractExportModel() {
        super();
    }

    /**
     * Gets the headers.
     *
     * @return the headers list
     */
    public abstract List<String> getHeaders();

    /**
     * Gets the results.
     *
     * @param parameter the parameter
     * @return the results list from parameter
     */
    public abstract List<Object> getResults(IExportParameter parameter);
}
