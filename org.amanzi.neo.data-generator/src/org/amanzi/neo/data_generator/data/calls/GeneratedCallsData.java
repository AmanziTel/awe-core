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

package org.amanzi.neo.data_generator.data.calls;

import java.util.List;

import org.amanzi.neo.data_generator.data.IGeneratedData;

/**
 * <p>
 * Generated data for call analyzer.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class GeneratedCallsData implements IGeneratedData {
    
    private List<CallGroup> data;
    
    public GeneratedCallsData(List<CallGroup> aData) {
        data = aData;
    }

    /**
     * @return Returns the data.
     */
    public List<CallGroup> getData() {
        return data;
    }
}
