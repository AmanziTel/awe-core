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

package org.amanzi.neo.data_generator.generate.calls;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.data_generator.data.calls.CallData;
import org.amanzi.neo.data_generator.data.calls.CallGroup;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;

/**
 * <p>
 * Common class for all messages data.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public abstract class MessageDataGenerator extends AmsDataGenerator{

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public MessageDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected CallData buildCall(CallGroup group, Integer hour, Long duration) {
        return null;
    }
    
    @Override
    protected List<CallData> buildCalls(CallGroup group) {
        List<CallData> calls = new ArrayList<CallData>();
        int hours = getHours();
        int callsCount = getCalls();
        int callVariance = getCallVariance();
        Long[] borders = getDurationBorders();
        RandomValueGenerator generator = RandomValueGenerator.getGenerator();
        for(int i = 0; i<hours; i++){
            int currCallCount = callsCount + RandomValueGenerator.getGenerator().getIntegerValue(-callVariance, callVariance);
            for(int j = 0; j<currCallCount; j++){
                Long duration = generator.getLongValue(borders[0], borders[1]);
                CallData call = buildCall(group, i, duration);
                calls.add(call);
            }
        }
        return calls;
    }

    @Override
    protected List<CallGroup> initCallGroups() {
        List<CallGroup> result = new ArrayList<CallGroup>();
        Integer probesCount = getProbesCount();
        int sourceCount = probesCount/2+(probesCount%2==0?0:1);
        RandomValueGenerator generator = getRandomGenerator();
        while (result.isEmpty()) {
            for (int i = 1; i <= sourceCount; i++) {
                for (int j = sourceCount + 1; j <= probesCount; j++) {
                    boolean canBePair = generator.getBooleanValue();
                    if (canBePair) {
                        result.add(getCallGroup(i, j));
                        result.add(getCallGroup(j, i));
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Get borders for call duration.
     *
     * @return Long[] (start, end)
     */
    protected abstract Long[] getDurationBorders();
    
    /**
     * Get count of messages in one call.
     *
     * @return int
     */
    protected abstract int getMessagesCount();

}
