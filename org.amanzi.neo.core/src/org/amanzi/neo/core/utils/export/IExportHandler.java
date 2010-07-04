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
 * Export handler mechanism
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IExportHandler {

    /**
     * Inits
     */
    void init();

    /**
     * Handle data.
     * 
     * @param parameter the parameter
     */
    void handleHeaders(IExportProvider provider);

    void handleData(List<Object> data);

    /**
     * Finish.
     */
    void finish();
}
