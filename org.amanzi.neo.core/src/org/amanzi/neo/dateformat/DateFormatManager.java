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

package org.amanzi.neo.dateformat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.core.internal.NeoCorePlugin;
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
    private static final IPreferenceStore PREFERENCE_STORE = NeoCorePlugin.getDefault().getPreferenceStore();
    public static final String DEFAULT_FORMAT_KEY = "default_format";
    public static final String FORMATS_SIZE_KEY = "date_formats_size";
    public static final String DATE_KEY_PREFIX = "dateFormat_";

    private static class InstanceHolder {
        private static final DateFormatManager INSTANCE = new DateFormatManager();
    }

    public static DateFormatManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private DateFormat defaultFormat;
    private final Map<String, String> formatMapping = new HashMap<String, String>();
    private final Map<String, String> reverseFormatMapping = new HashMap<String, String>();

    private String defaultFormatId;

    private DateFormatManager() {
        initMapIfNecessary();
    }

    /**
     * load all possible
     */
    private void initMapIfNecessary() {
        if (!formatMapping.isEmpty()) {
            return;
        }
        int size = PREFERENCE_STORE.getInt(FORMATS_SIZE_KEY);
        for (int i = 0; i < size; i++) {
            String key = DATE_KEY_PREFIX + i;
            formatMapping.put(key, PREFERENCE_STORE.getString(key));
            reverseFormatMapping.put(PREFERENCE_STORE.getString(key), key);
        }
        defaultFormatId = PREFERENCE_STORE.getString(DEFAULT_FORMAT_KEY);
        if (defaultFormatId != null) {
            setDefaultFormat(formatMapping.get(defaultFormatId));
        }

    }

    /**
     * get all dateFormtsValue
     * 
     * @return
     */
    public Collection<String> getAllDateFormats() {
        LOGGER.info("Founded : " + formatMapping.size() + " formats ");
        return formatMapping.values();
    }

    /**
     * add new format to preference store
     * 
     * @param format
     */
    private void addNewFormat(final String format) {
        assert !StringUtils.isEmpty(format);

        String key = DATE_KEY_PREFIX + formatMapping.size();
        formatMapping.put(key, format);
        reverseFormatMapping.put(format, key);
        PREFERENCE_STORE.setValue(FORMATS_SIZE_KEY, formatMapping.size());
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
    public Date parseStringToFormat(final String date, final String format) throws ParseException {
        try {
            return parseString(date, format);
        } catch (ParseException e) {
            LOGGER.error("can't parse string" + date + " to format " + format, e);
            throw e;
        }
    }

    private Date parseString(final String date, final String format) throws ParseException {
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
    public DateFormat autoParseStringToDate(final String date) {
        assert !StringUtils.isEmpty(date);

        for (String value : formatMapping.values()) {
            try {
                parseString(date, value);
                return new SimpleDateFormat(value);
            } catch (ParseException e) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * parse long to default string format
     * 
     * @param timestamp
     * @return
     */
    public String parseLongToStringDate(Long timestamp) {
        assert timestamp != null;
        return defaultFormat.format(new Date(timestamp));
    }

    /**
     * parse date to String
     * 
     * @param date
     * @param format
     * @return
     */
    public String parseDateToString(final Date date, final String format) {
        assert !StringUtils.isEmpty(format);
        assert date != null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * @return Returns the defaultFormat.
     */
    public String getDefaultFormatPattern() {
        return formatMapping.get(defaultFormatId);
    }

    public Date longToDate(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * @return Returns the defaultFormat.
     */
    public DateFormat getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * @param defaultFormat The defaultFormat to set.
     */
    private void setDefaultFormat(final String defaultFormat) {
        PREFERENCE_STORE.setValue(DEFAULT_FORMAT_KEY, reverseFormatMapping.get(defaultFormat));
        this.defaultFormat = new SimpleDateFormat(defaultFormat);
    }

    /**
     * added new formats and set default value
     * 
     * @param formats
     * @param defaultValue
     */
    public void addNewFormats(final Collection<String> formats, final String defaultValue) {
        if (!StringUtils.isEmpty(defaultValue)) {
            setDefaultFormat(defaultValue);
        }
        if (formats == null) {
            LOGGER.warn(" new formats list is null ");
            return;
        }
        for (String newFormat : formats) {
            String existedFormat = reverseFormatMapping.get(newFormat);
            if (existedFormat != null) {
                continue;
            }
            addNewFormat(newFormat);
        }
    }
}
