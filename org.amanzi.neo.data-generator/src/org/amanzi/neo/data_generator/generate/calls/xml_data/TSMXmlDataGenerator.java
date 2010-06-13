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

package org.amanzi.neo.data_generator.generate.calls.xml_data;

import org.amanzi.neo.data_generator.utils.call.CallConstants;

/**
 * <p>
 * Generate XML data for TSM messages.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class TSMXmlDataGenerator extends MessageXmlDataGenerator{

    /**
     * @param aDirectory
     * @param aHours
     * @param aHourDrift
     * @param aCallsPerHour
     * @param aCallPerHourVariance
     * @param aProbes
     */
    public TSMXmlDataGenerator(String aDirectory, Integer aHours, Integer aHourDrift, Integer aCallsPerHour,
            Integer aCallPerHourVariance, Integer aProbes) {
        super(aDirectory, aHours, aHourDrift, aCallsPerHour, aCallPerHourVariance, aProbes);
    }

    @Override
    protected Long[] getDurationBorders() {
        return CallConstants.TSM_DURATION_BORDERS;
    }

    @Override
    protected Long[] getAcknowledgeBorders() {
        return CallConstants.TSM_ACKNOWLEDGE_BORDERS;
    }

    @Override
    protected Integer getAiService() {
        return CallConstants.TSM_AI_SERVICE;
    }

    @Override
    protected String getMessage() {
        return CallConstants.TSM_MESSAGE;
    }

    @Override
    protected String getTypeKey() {
        return "TSM";
    }

}
