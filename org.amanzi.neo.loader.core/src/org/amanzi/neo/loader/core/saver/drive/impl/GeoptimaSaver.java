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

package org.amanzi.neo.loader.core.saver.drive.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.internal.LoaderCorePlugin;
import org.amanzi.neo.loader.core.saver.impl.AbstractDriveSaver;
import org.amanzi.neo.loader.core.synonyms.SynonymsManager;
import org.amanzi.neo.models.drive.DriveType;
import org.amanzi.neo.models.drive.IDriveModel.IDriveType;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IDriveModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class GeoptimaSaver extends AbstractDriveSaver {

    /** int DOMINANT_TIMEOUT field */
    private static final int DOMINANT_TIMEOUT = 5000;

    /** String UNKNOWN_VALUE field */
    private static final String UNKNOWN_VALUE = "unknown";

    private static final Logger LOGGER = Logger.getLogger(GeoptimaSaver.class);

    private static final String NEIGHBOUR_SIGNAL_EVENT_NAME = "neighbor_signal";

    private static final String SIGNAL_EVENT_NAME = "signal";

    private static final String SIGNAL_STRENGTH = "signal_strength";

    private static final String NEIGHBOUR_SIGNAL_STRENGTH = "neighbour_signal_strength";

    private static final String DOMINANT_PROPERTY = "dominant";

    private static final String DELTA_RSSI_PROPERTY = "delta_rssi";

    private final IMeasurementNodeProperties measurementNodeProperties;

    private final Map<String, Map<String, Object>> signalCache = new HashMap<String, Map<String, Object>>();

    public GeoptimaSaver() {
        this(LoaderCorePlugin.getInstance().getTimePeriodNodeProperties(), LoaderCorePlugin.getInstance().getGeoNodeProperties(),
                LoaderCorePlugin.getInstance().getDriveModelProvider(), LoaderCorePlugin.getInstance().getProjectModelProvider(),
                LoaderCorePlugin.getInstance().getMeasurementNodeProperties(), SynonymsManager.getInstance());
    }

    /**
     * @param projectModelProvider
     * @param synonymsManager
     */
    protected GeoptimaSaver(final ITimePeriodNodeProperties timePeriodNodeProperties, final IGeoNodeProperties geoNodeProperties,
            final IDriveModelProvider driveModelProvider, final IProjectModelProvider projectModelProvider,
            final IMeasurementNodeProperties measurementNodeProperties, final SynonymsManager synonymsManager) {
        super(timePeriodNodeProperties, geoNodeProperties, driveModelProvider, projectModelProvider, synonymsManager);
        this.measurementNodeProperties = measurementNodeProperties;
    }

    @Override
    protected IDriveType getDriveType() {
        return DriveType.GEOPTIMA;
    }

    @Override
    protected Map<String, Object> getElementProperties(final INodeType nodeType, final IMappedStringData data,
            final boolean addNonMappedHeaders) {
        Map<String, Object> result = super.getElementProperties(nodeType, data, addNonMappedHeaders);

        String event = (String)result.get(measurementNodeProperties.getEventProperty());
        if (!StringUtils.isEmpty(event)) {
            Object imeiObject = result.get(measurementNodeProperties.getIMEIProperty());
            String imei = imeiObject == null ? StringUtils.EMPTY : imeiObject.toString();

            if (!StringUtils.isEmpty(imei)) {
                if (event.equals(SIGNAL_EVENT_NAME)) {
                    updateSignalCache(result, imei);
                } else if (event.equals(NEIGHBOUR_SIGNAL_EVENT_NAME)) {
                    updateProperties(result, imei);
                }
            } else {
                LOGGER.warn("No IMEI was found in line <" + data + ">");
            }
        } else {
            LOGGER.warn("No event was found in line <" + data + ">");
        }

        return result;
    }

    private void updateSignalCache(final Map<String, Object> data, final String imei) {
        Map<String, Object> cacheData = new HashMap<String, Object>();

        cacheData.put(getTimePeriodNodeProperties().getTimestampProperty(),
                data.get(getTimePeriodNodeProperties().getTimestampProperty()));
        cacheData.put(SIGNAL_STRENGTH, data.get(SIGNAL_STRENGTH));

        signalCache.put(imei, cacheData);
    }

    private void updateProperties(final Map<String, Object> data, final String imei) {
        Map<String, Object> cacheData = signalCache.get(imei);

        Boolean dominantValue = null;

        if (cacheData != null) {
            long previousTimestamp = (Long)cacheData.get(getTimePeriodNodeProperties().getTimestampProperty());
            long currentTimestamp = (Long)data.get(getTimePeriodNodeProperties().getTimestampProperty());

            if ((currentTimestamp - previousTimestamp) < DOMINANT_TIMEOUT) {
                Integer signalStrength = (Integer)cacheData.get(SIGNAL_STRENGTH);
                Integer neighbourStrength = (Integer)data.get(NEIGHBOUR_SIGNAL_STRENGTH);

                if ((signalStrength != null) && (neighbourStrength != null)) {
                    Integer deltaRssi = neighbourStrength - signalStrength;

                    dominantValue = deltaRssi > 0;

                    if (dominantValue) {
                        data.put(DELTA_RSSI_PROPERTY, deltaRssi);
                    }
                }
            }

        } else {
            LOGGER.warn("No cached data for IMEI <" + imei + ">.");
        }

        data.put(DOMINANT_PROPERTY, dominantValue == null ? UNKNOWN_VALUE : dominantValue);
    }

}
