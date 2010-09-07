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

package org.amanzi.neo.loader.core;

import org.amanzi.neo.loader.core.parser.IConfigurationData;

/**
 * <p>
 * Provide interface for validate input configuration data for loaders success
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface ILoaderInputValidator<T extends IConfigurationData> {


    /**
     * Validate. Check if input data of ConfigurationData can be handled by loader. This method do
     * not fully check ConfigurationData, but only input part (for example files list).
     * 
     * @param data the data
     * @return the validate result
     */
    IValidateResult validate(T data);

    /**
     * Filter. Filtered data to remove information about input data, which can't be handled by
     * loader
     * 
     * @param data the data
     */
    void filter(T data);
}
