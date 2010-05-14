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

package org.amanzi.awe.views.calls.testing;

import org.amanzi.neo.data_generator.data.calls.Call;

/**
 * <p>
 * Common class for tests messages data statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class MessagesStatisticsTest extends AmsStatisticsTest{

    protected Float getTimeByKey(Call call, String key){
        return ((Long)call.getParameter(key)).floatValue()/MILLISECONDS;
    }
    
    @Override
    protected boolean hasSecondLevelStatistics() {
        return true;
    }
    
}
