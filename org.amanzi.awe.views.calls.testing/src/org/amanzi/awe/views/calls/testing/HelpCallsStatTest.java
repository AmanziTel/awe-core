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

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.amanzi.awe.views.calls.enums.IStatisticsHeader;
import org.amanzi.awe.views.calls.enums.StatisticsCallType;
import org.amanzi.neo.data_generator.DataGenerateManager;
import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;

/**
 * <p>
 * Tests for help calls statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class HelpCallsStatTest extends AmsStatisticsTest{
    
    @Override
    protected IDataGenerator getDataGenerator(Integer aHours, Integer aDrift, Integer aCallsPerHour, Integer aCallPerHourVariance,
            Integer aProbes, String dataDir) {
        return DataGenerateManager.getHelpAmsGenerator(dataDir, aHours, aDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected Date getCallStartTime(CallData call) {
        return null;
    }

    @Override
    protected StatisticsCallType getCallType() {
        return null;
    }

    @Override
    protected HashMap<IStatisticsHeader, Number> getStatValuesFromCall(CallData call) throws ParseException {
        return null;
    }

}
