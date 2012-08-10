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

package org.amanzi.neo.loader.ui.preference.dateformat.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.loader.ui.internal.LoaderUIPlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>
 * manager for work with dates format
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class DateFormatManager {

    private static final Logger LOGGER = Logger.getLogger(DateFormatManager.class);
    private static final IPreferenceStore PREFERENCE_STORE = LoaderUIPlugin.getDefault().getPreferenceStore();
    public static final String FORMATS_SIZE_KEY = "date_formats_size";
    public static final String DATE_KEY_PREFIX = "dateFormat_";
    private static final String DEFAULT_FORMAT_KEY = "default_format";

    private String defaultFormat;

    private static class InstanceHolder {
        private static final DateFormatManager INSTANCE = new DateFormatManager();
    }

    public static DateFormatManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private Map<String, String> formatsMaps = new HashMap<String, String>();

    private DateFormatManager() {
        initMapIfNecessary();
    }

    /**
     * load all possible
     */
    private void initMapIfNecessary() {
        if (!formatsMaps.isEmpty()) {
            return;
        }
        int size = PREFERENCE_STORE.getInt(FORMATS_SIZE_KEY);
        for (int i = 0; i < size; i++) {
            String key = DATE_KEY_PREFIX + i;
            formatsMaps.put(key, PREFERENCE_STORE.getString(key));
        }
        String defaultFormatId = PREFERENCE_STORE.getString(DEFAULT_FORMAT_KEY);
        if (defaultFormatId != null) {
            defaultFormat = formatsMaps.get(defaultFormatId);
        }

    }

    /**
     * get all dateFormtsValue
     * 
     * @return
     */
    public Collection<String> getAllDateFormats() {
        LOGGER.info("Founded : " + formatsMaps.size() + " formats ");
        return formatsMaps.values();
    }

    /**
     * add new format to preference store
     * 
     * @param format
     */
    public void addNewFormat(String format) {
        assert !StringUtils.isEmpty(format);

        String key = DATE_KEY_PREFIX + formatsMaps.size();
        formatsMaps.put(key, format);
        PREFERENCE_STORE.setValue(FORMATS_SIZE_KEY, formatsMaps.size());
        PREFERENCE_STORE.setValue(key, format);

        LOGGER.info("Format : " + format + " was successeful saved into preference store with id: " + key);
    }

    /**
     * try to parse string with date in required format
     * 
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public Date parseStringToFormat(String date, String format) throws ParseException {
        try {
            return parseString(date, format);
        } catch (ParseException e) {
            LOGGER.error("can't parse string" + date + " to format " + format, e);
            throw e;
        }
    }

    private Date parseString(String date, String format) throws ParseException {
        assert !StringUtils.isEmpty(format);
        assert !StringUtils.isEmpty(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(date);
    }

    /**
     * try to autoparse string with date
     * 
     * @param date
     * @return
     */
    public Date autoParseString(String date) {
        assert !StringUtils.isEmpty(date);

        Date result = null;
        for (String value : formatsMaps.values()) {
            try {
                result = parseString(date, value);
                break;
            } catch (ParseException e) {
                result = null;
            }
        }
        return result;
    }

    /**
     * parse date to String
     * 
     * @param date
     * @param format
     * @return
     */
    public String parseDateToString(Date date, String format) {
        assert !StringUtils.isEmpty(format);
        assert date != null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * @return Returns the defaultFormat.
     */
    public String getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * @param defaultFormat The defaultFormat to set.
     */
    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

}
