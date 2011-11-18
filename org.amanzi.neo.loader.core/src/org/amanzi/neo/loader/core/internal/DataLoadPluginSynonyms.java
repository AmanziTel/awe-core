package org.amanzi.neo.loader.core.internal;

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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class DataLoadPluginSynonyms extends NLS {

    private static final String BUNDLE_NAME = DataLoadPluginSynonyms.class.getName();

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String NH_CITY;
    public static String NH_MSC;
    public static String NH_BSC;
    public static String NH_SITE;
    public static String NH_SECTOR;
    public static String NH_SECTOR_CI;
    public static String NH_SECTOR_LAC;
    public static String NH_LATITUDE;
    public static String NH_LONGITUDE;

    public static String DR_LATITUDE;
    public static String DR_LONGITUDE;
    public static String DR_BCCH;
    public static String DR_CI;
    public static String DR_EcIo;
    public static String DR_PN;
    public static String DR_RSSI;
    public static String DR_SC;
    public static String DR_TCH;

    public static String DR_MS;
    public static String DR_EVENT;
    public static String DR_SECTOR_ID;
    public static String DR_ALL_RXLEV_FULL;
    public static String DR_ALL_RXLEV_SUB;
    public static String DR_ALL_RXQUAL_FULL;
    public static String DR_ALL_RXQUAL_SUB;
    public static String DR_ALL_PILOT_SET_COUNT;
    public static String DR_ALL_SQI;
    public static String DR_ALL_SQI_MOS;
    public static String DR_TIME;
    public static String DR_MESSAGE_TYPE;

    public static String DR_ALL_PILOT_SET_EC_IO0;
    public static String DR_ALL_PILOT_SET_EC_IO1;
    public static String DR_ALL_PILOT_SET_EC_IO2;
    public static String DR_ALL_PILOT_SET_EC_IO3;
    public static String DR_ALL_PILOT_SET_EC_IO4;
    public static String DR_ALL_PILOT_SET_EC_IO5;
    public static String DR_ALL_PILOT_SET_EC_IO6;
    public static String DR_ALL_PILOT_SET_EC_IO7;
    public static String DR_ALL_PILOT_SET_EC_IO8;
    public static String DR_ALL_PILOT_SET_EC_IO9;
    public static String DR_ALL_PILOT_SET_EC_IO10;
    public static String DR_ALL_PILOT_SET_EC_IO11;
    public static String DR_ALL_PILOT_SET_EC_IO12;

    public static String DR_ALL_PILOT_SET_CHANNEL0;
    public static String DR_ALL_PILOT_SET_CHANNEL1;
    public static String DR_ALL_PILOT_SET_CHANNEL2;
    public static String DR_ALL_PILOT_SET_CHANNEL3;
    public static String DR_ALL_PILOT_SET_CHANNEL4;
    public static String DR_ALL_PILOT_SET_CHANNEL5;
    public static String DR_ALL_PILOT_SET_CHANNEL6;
    public static String DR_ALL_PILOT_SET_CHANNEL7;
    public static String DR_ALL_PILOT_SET_CHANNEL8;
    public static String DR_ALL_PILOT_SET_CHANNEL9;
    public static String DR_ALL_PILOT_SET_CHANNEL10;
    public static String DR_ALL_PILOT_SET_CHANNEL11;
    public static String DR_ALL_PILOT_SET_CHANNEL12;

    public static String DR_ALL_PILOT_SET_PN0;
    public static String DR_ALL_PILOT_SET_PN1;
    public static String DR_ALL_PILOT_SET_PN2;
    public static String DR_ALL_PILOT_SET_PN3;
    public static String DR_ALL_PILOT_SET_PN4;
    public static String DR_ALL_PILOT_SET_PN5;
    public static String DR_ALL_PILOT_SET_PN6;
    public static String DR_ALL_PILOT_SET_PN7;
    public static String DR_ALL_PILOT_SET_PN8;
    public static String DR_ALL_PILOT_SET_PN9;
    public static String DR_ALL_PILOT_SET_PN10;
    public static String DR_ALL_PILOT_SET_PN11;
    public static String DR_ALL_PILOT_SET_PN12;

    public static String PR_NAME;
    public static String PR_TYPE;
    public static String PR_LATITUDE;
    public static String PR_LONGITUDE;

    public static String NH_AZIMUTH;
    public static String NH_BEAMWIDTH;

    public static String NE_SRV_CI;
    public static String NE_SRV_NAME;
    public static String NE_SRV_LAC;
    public static String NE_SRV_CO;
    public static String NE_SRV_ADJ;

    public static String NE_NBR_CI;
    public static String NE_NBR_NAME;
    public static String NE_NBR_LAC;

    public static String TR_SITE_ID_SERV;
    public static String TR_SITE_NO_SERV;
    public static String TR_ITEM_NAME_SERV;
    public static String TR_SITE_ID_NEIB;
    public static String TR_SITE_NO_NEIB;

    public static String RADIO;
    public static String X_CEIVER;
    public static String ADMINISTRATION;

    public static String MO;
    public static String CHGR;
    public static String FHOP;

    private DataLoadPluginSynonyms() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getFormattedString(String key, Object... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, DataLoadPluginSynonyms.class);
    }

}
