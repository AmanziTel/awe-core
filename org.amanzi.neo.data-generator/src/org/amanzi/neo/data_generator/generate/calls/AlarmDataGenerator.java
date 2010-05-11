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

/**
 * <p>
 * Data for alarm messages.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class AlarmDataGenerator extends MessageDataGenerator{
    
    private static final String PAIR_DIRECTORY_POSTFIX = ""; //TODO real postfix

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public AlarmDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected String getDirectoryPostfix() {
        return PAIR_DIRECTORY_POSTFIX; 
    }

    @Override
    protected Long[] getDurationBorders() {
        return null;
    }

    @Override
    protected int getMessagesCount() {
        return 1;
    }

}
