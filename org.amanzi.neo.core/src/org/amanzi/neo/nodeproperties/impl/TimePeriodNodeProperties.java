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

package org.amanzi.neo.nodeproperties.impl;

import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class TimePeriodNodeProperties implements ITimePeriodNodeProperties {

    private static final String TIMESTAMP_PROPERTY = "timestamp";

    private static final String MIN_TIMESTAMP_PROPERTY = "min_timestamp";

    private static final String MAX_TIMESTAMP_PROPERTY = "max_timestamp";

    private static final String START_DATE_PROPERTY = "start_date";

    private static final String END_DATE_PROPERTY = "end_date";

    private static final String START_DATE_TIMESTAMP_PROPERTY = "start_date_timestamp";

    private static final String END_DATE_TIMESTAMP_PROPERTY = "end_date_timestamp";

    @Override
    public String getTimestampProperty() {
        return TIMESTAMP_PROPERTY;
    }

    @Override
    public String getMinTimestampProperty() {
        return MIN_TIMESTAMP_PROPERTY;
    }

    @Override
    public String getMaxTimestampProperty() {
        return MAX_TIMESTAMP_PROPERTY;
    }

    @Override
    public String getStartDateProperty() {
        return START_DATE_PROPERTY;
    }

    @Override
    public String getEndDateProperty() {
        return END_DATE_PROPERTY;
    }

    @Override
    public String getStartDateTimestampProperty() {
        return START_DATE_TIMESTAMP_PROPERTY;
    }

    @Override
    public String getEndDateTimestampProperty() {
        return END_DATE_TIMESTAMP_PROPERTY;
    }

}
