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

package org.amanzi.neo.data_generator.generate;

import org.amanzi.neo.data_generator.data.IGeneratedData;



/**
 * <p>
 * Common interface for all data generators.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public interface IDataGenerator {

    /**
     * Generate data.
     *
     * @return GeneratedData
     */
    public IGeneratedData generate();
}
