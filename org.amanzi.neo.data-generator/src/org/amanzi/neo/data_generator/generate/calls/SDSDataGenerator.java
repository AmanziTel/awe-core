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
 * Generate data for SDS messages.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class SDSDataGenerator extends MessageDataGenerator{
    
    private static final int AI_SERVICE = 12;
    private static final String PAIR_DIRECTORY_POSTFIX = "SDS";
    private static final String[] MESSAGES = new String[]{"Hello","Hello World! This is a SDS","Hello World! This is a full SDS-4 message containing 120 characters of user data. ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789"};
    private static final Long[] DURATION_BORDERS = new Long[]{1L,MILLISECONDS*30L};
    private static final Long[] ACKNOWLEDGE_BORDERS = new Long[]{1L,MILLISECONDS*5L};
    
    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public SDSDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected String getDirectoryPostfix() {
        return PAIR_DIRECTORY_POSTFIX;
    }

    @Override
    protected Long[] getDurationBorders() {
        return DURATION_BORDERS;
    }

    @Override
    protected int getMessagesCount() {
        return getRandomGenerator().getIntegerValue(1, 3);
    }

    @Override
    protected Long[] getAcknowledgeBorders() {
        return ACKNOWLEDGE_BORDERS;
    }

    @Override
    protected Integer getAiService() {
        return AI_SERVICE;
    }

    @Override
    protected String[] getAllMessages() {
        return MESSAGES;
    }

}
