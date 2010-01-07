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
package org.amanzi.neo.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.ZoomToLayer;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.NetworkLoader.CRS;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

public abstract class AbstractLoader {
    /** AbstractLoader DEFAULT_DIRRECTORY_LOADER field */
    public static final String DEFAULT_DIRRECTORY_LOADER = "DEFAULT_DIRRECTORY_LOADER";
    private String typeName = "CSV";
    protected NeoService neo;
    private NeoServiceProvider neoProvider;
    protected Node gis = null;
    private CRS crs = null;
    protected String filename = null;
    protected String basename = null;
    private Display display;
    private String fieldSepRegex;
    private String[] possibleFieldSepRegexes = new String[] {"\t", "\\,", "\\;"};
    protected int lineNumber = 0;
    private int limit = 0;
    private double[] bbox;
    private long savedData = 0;
    private long started = System.currentTimeMillis();
    private ArrayList<MultiPropertyIndex<?>> indexes = new ArrayList<MultiPropertyIndex<?>>();
    private ArrayList<Pattern> headerFilters = new ArrayList<Pattern>();
    private LinkedHashMap<String, List<String>> knownHeaders = new LinkedHashMap<String, List<String>>();
    private LinkedHashMap<String, MappedHeaderRule> mappedHeaders = new LinkedHashMap<String, MappedHeaderRule>();
    protected LinkedHashMap<String, Header> headers = new LinkedHashMap<String, Header>();
    private TreeSet<String> dropStatsHeaders = new TreeSet<String>();
    private TreeSet<String> nonDataHeaders = new TreeSet<String>();
    @SuppressWarnings("unchecked")
    public static final Class[] NUMERIC_PROPERTY_TYPES = new Class[] {Integer.class, Long.class, Float.class, Double.class};
    @SuppressWarnings("unchecked")
    public static final Class[] KNOWN_PROPERTY_TYPES = new Class[] {Integer.class, Long.class, Float.class, Double.class,
            String.class};
    private boolean indexesInitialized = false;

    protected class Header {
        private static final int MAX_PROPERTY_VALUE_COUNT = 100; // discard value sets if count
        // exceeds 100
        private static final float MAX_PROPERTY_VALUE_SPREAD = 0.5f; // discard value sets if
        // spread exceeds 50%
        private static final int MIN_PROPERTY_VALUE_SPREAD_COUNT = 50; // only calculate spread
        // after this number of data
        // points
        int index;
        String key;
        String name;
        HashMap<Class< ? extends Object>, Integer> parseTypes = new HashMap<Class< ? extends Object>, Integer>();
        HashMap<Object, Integer> values = new HashMap<Object, Integer>();
        int parseCount = 0;

        Header(String name, String key, int index) {
            this.index = index;
            this.name = name;
            this.key = key;
            for (Class< ? extends Object> klass : KNOWN_PROPERTY_TYPES) {
                parseTypes.put(klass, 0);
            }
        }

        Header(Header old) {
            this(old.name, old.key, old.index);
            this.parseCount = old.parseCount;
            this.values = old.values;
        }

        protected boolean invalid(String field) {
            return field == null || field.length() < 1 || field.equals("?");
        }

        Object parse(String field) {
            if (invalid(field))
                return null;
            parseCount++;            
            try {
            	int value = Integer.parseInt(field);
                incValue(value);
                incType(Integer.class);
                return value;
            } catch (Exception e) {
                try {
                    float value = Float.parseFloat(field);
                    incValue(value);
                    incType(Float.class);
                    return value;
                } catch (Exception e2) {
                    incValue(field);
                    incType(String.class);
                    return field;
                }
            }
        }

        protected void incType(Class< ? extends Object> klass) {
            parseTypes.put(klass, parseTypes.get(klass) + 1);
        }

        protected Object incValue(Object value) {
            if (values != null) {
                Integer count = values.get(value);
                if (count == null) {
                    count = 0;
                }
                boolean discard = false;
                if (count == 0) {
                    // We have a new value, so adding it will increase the size of the map
                    // We should perform threshold tests to decide whether to drop the map or not
                    if (values.size() >= MAX_PROPERTY_VALUE_COUNT) {
                        // Exceeded absolute threashold, drop map
                        System.out.println("Property values exceeded maximum count, no longer tracking value set: " + this.key);
                        discard = true;
                    } else if (values.size() >= MIN_PROPERTY_VALUE_SPREAD_COUNT) {
                        // Exceeded minor threshold, test spread and then decide
                        float spread = (float)values.size() / (float)parseCount;
                        if (spread > MAX_PROPERTY_VALUE_SPREAD) {
                            // Exceeded maximum spread, too much property variety, drop map
                            System.out.println("Property shows excessive variation, no longer tracking value set: " + this.key);
                            discard = true;
                        }
                    }
                }
                if (discard) {
                    // Detected too much variety in property values, stop counting
                    dropStats();
                } else {
                    values.put(value, count + 1);
                }
            }
            return value;
        }

        boolean shouldConvert() {
            return parseCount > 10;
        }

        Class< ? extends Object> knownType() {
            Class< ? extends Object> best = String.class;
            int maxCount = 0;
            int countFound = 0;
            for (Class< ? extends Object> klass : parseTypes.keySet()) {
                int count = parseTypes.get(klass);
                // Bias towards Strings
                if (klass == String.class)
                    count *= 2;
                if (maxCount < parseTypes.get(klass)) {
                    maxCount = count;
                    best = klass;
                }
                if (count > 0) {
                    countFound++;
                }
            }
            if (countFound > 1) {
                AbstractLoader.this.notify("Header " + key + " had multiple type matches: ");
                for (Class< ? extends Object> klass : parseTypes.keySet()) {
                    int count = parseTypes.get(klass);
                    if (count > 0) {
                        AbstractLoader.this.notify("\t" + count + ": " + klass + " => " + key);
                    }
                }
            }
            return best;
        }

        /**
         * Disable statistics collection for this header. This is useful if the property is
         * undesirable in some later statistical analysis, either because it is too diverse, or it
         * is a property we can 'grouped by' during the load. Examples of excessive diversity would
         * be element names, ids, timestamps, locations. Examples of grouping by would be site
         * properties, timestamps and locations.
         */
        public void dropStats() {
            values = null;
        }
    }

    protected class IntegerHeader extends Header {
        IntegerHeader(Header old) {
            super(old);
        }

        Integer parse(String field) {
            if (invalid(field))
                return null;
            parseCount++;
            return (Integer)incValue(Integer.parseInt(field));
        }

        boolean shouldConvert() {
            return false;
        }

        Class<Integer> knownType() {
            return Integer.class;
        }
    }

    protected class LongHeader extends Header {
        LongHeader(Header old) {
            super(old);
        }

        Long parse(String field) {
            if (invalid(field))
                return null;
            parseCount++;
            return (Long)incValue(Long.parseLong(field));
        }

        boolean shouldConvert() {
            return false;
        }

        Class<Long> knownType() {
            return Long.class;
        }
    }

    protected class FloatHeader extends Header {
        FloatHeader(Header old) {
            super(old);
        }

        Float parse(String field) {
            if (invalid(field))
                return null;
            parseCount++;
            return (Float)incValue(Float.parseFloat(field));
        }

        boolean shouldConvert() {
            return false;
        }

        Class<Float> knownType() {
            return Float.class;
        }
    }

    protected class StringHeader extends Header {
        StringHeader(Header old) {
            super(old);
        }

        String parse(String field) {
            if (invalid(field))
                return null;
            parseCount++;
            return (String)incValue(field);
        }

        boolean shouldConvert() {
            return false;
        }

        Class<String> knownType() {
            return String.class;
        }
    }

    protected interface PropertyMapper {
        public Object mapValue(String originalValue);
    }

    protected class MappedHeaderRule {
        private String name;
        protected String key;
        private PropertyMapper mapper;

        MappedHeaderRule(String name, String key, PropertyMapper mapper) {
            this.key = key;
            this.name = name;
            this.mapper = mapper;
        }
    }

    /**
     * This class allows for either replacing of duplicating properties. See addMappedHeader for
     * details.
     * 
     * @author craig
     * @since 1.0.0
     */
    protected class MappedHeader extends Header {
        protected PropertyMapper mapper;
        Class< ? extends Object> knownClass = null;

        MappedHeader(Header old, MappedHeaderRule mapRule) {
            super(old);
            this.key = mapRule.key;
            if (mapRule.name != null) {
                // We only replace the name if the new one is valid, otherwise inherit from the old
                // header
                // This allows for support of header replacing rules, as well as duplicating rules
                this.name = mapRule.name;
            }
            this.mapper = mapRule.mapper;
            this.values = new HashMap<Object, Integer>(); // need to make a new values list,
            // otherwise we share the same data as
            // the original
        }

        Object parse(String field) {
            if (invalid(field))
                return null;
            Object result = mapper.mapValue(field);
            parseCount++;
            if (knownClass == null && result != null) {
                // Determine converted class from very first conversion
                knownClass = result.getClass();
            }
            return incValue(result);
        }

        boolean shouldConvert() {
            return false;
        }

        Class< ? extends Object> knownType() {
            return knownClass;
        }
    }

    /**
     * Convenience implementation of a property mapper that understands date and time formats.
     * Construct with a date-time pattern understood by java.text.SimpleDateFormat. If you pass null
     * of an invalid format, then the default of "HH:mm:ss" will be used.
     * 
     * @author craig
     * @since 1.0.0
     */
    protected class DateTimeMapper extends DateMapper {

        /**
         * @param format
         */
        protected DateTimeMapper(String format) {
            super(format);
        }

        @Override
        public Object mapValue(String time) {
            Date datetime = (Date)super.mapValue(time);
            return datetime == null ? 0L : datetime.getTime();
        }
    }

    /**
     * Convenience implementation of a property mapper that understands date formats. Construct with
     * a date-time pattern understood by java.text.SimpleDateFormat. If you pass null of an invalid
     * format, then the default of "HH:mm:ss" will be used.
     */
    protected class DateMapper implements PropertyMapper {
        private SimpleDateFormat format;

        protected DateMapper(String format) {
            try {
                this.format = new SimpleDateFormat(format);
            } catch (Exception e) {
                this.format = new SimpleDateFormat("HH:mm:ss");
            }
        }

        @Override
        public Object mapValue(String time) {
            Date datetime;
            try {
                datetime = format.parse(time);
            } catch (ParseException e) {
                error(e.getLocalizedMessage());
                return null;
            }
            return datetime;
        }
    }
    /**
     * Convenience implementation of a property mapper that assumes the object is a String. This is
     * useful for overriding the default behavior of detecting field formats, and simply keeping the
     * original strings. For example if the site name happens to contain only numbers, but we still
     * want to see it as a string because it is the name.
     * 
     * @author craig
     * @since 1.0.0
     */
    protected class StringMapper implements PropertyMapper {

        @Override
        public Object mapValue(String value) {
            return value;
        }
    }

    /**
     * Initialize Loader with a specified set of parameters
     * 
     * @param type defaults to 'CSV' if empty
     * @param neoService defaults to looking up from Neoclipse if null
     * @param fileName name of file to load
     * @param display Display to use for scheduling plugin lookups and message boxes, or null
     */
    protected final void initialize(String typeString, NeoService neoService, String filenameString, Display display) {
        if (typeString != null && !typeString.isEmpty()) {
            this.typeName = typeString;
        }
        initializeNeo(neoService, display);
        this.display = display;
        this.filename = filenameString;
        this.basename = (new File(filename)).getName();
    }

    private void initializeNeo(NeoService neoService, Display display) {
        if (neoService == null) {
            // if Display is given than start Neo using syncExec
            if (display != null) {
                display.syncExec(new Runnable() {
                    public void run() {
                        initializeNeo();
                    }
                });
            }
            // if Display is not given than initialize Neo as usual
            else {
                initializeNeo();
            }
        } else {
            this.neo = neoService;
        }
    }

    private void initializeNeo() {
        if (this.neoProvider == null)
            this.neoProvider = NeoServiceProvider.getProvider();
        if (this.neo == null)
            this.neo = this.neoProvider.getService();
    }

    private void determineFieldSepRegex(String line) {
        int maxMatch = 0;
        for (String regex : possibleFieldSepRegexes) {
            String[] fields = line.split(regex);
            if (fields.length > maxMatch) {
                maxMatch = fields.length;
                fieldSepRegex = regex;
            }
        }
    }

    protected String[] splitLine(String line) {
    	return line.split(fieldSepRegex);
    }

    /**
     * Converts to lower case and replaces all illegal characters with '_' and removes trailing '_'.
     * This is useful for creating a version of a header or property name that can be used as a
     * variable or method name in programming code, notably in Ruby DSL code.
     * 
     * @param original header String
     * @return edited String
     */
    protected final static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_")
                .replaceAll("\\_$", "").toLowerCase();
    }

    /**
     * @return true if we have parsed the header line and know the properties to load
     */
    protected boolean haveHeaders() {
        return headers.size() > 0;
    }

    /**
     * Get the header name for the specified key, if it exists
     * 
     * @param key
     * @return
     */
    protected String headerName(String key) {
        Header header = headers.get(key);
        return header == null ? null : header.name;
    }

    /**
     * Add a number of regular expression strings to use as filters for deciding which properties to
     * save. If this method is never used, and the filters are empty, then all properties are
     * processed. Since the saving code is done in the specific loader, not using this method can
     * cause a lot more parsing of data than is necessary, so it is advised to use this. Note also
     * that the filter regular expressions are applied to the cleaned headers, not the original ones
     * found in the file.
     * 
     * @param filters
     */
    protected void addHeaderFilters(String[] filters) {
        for (String filter : filters) {
            headerFilters.add(Pattern.compile(filter));
        }
    }

    /**
     * Add a property name and regular expression for a known header. This is used if we want the
     * property name in the database to be some specific text, not the header text in the file. The
     * regular expression is used to find the header in the file to associate with the new property
     * name. Note that the original property will not be saved using its original name. It will be
     * saved with the specified name provided. For example, if you want the first field found that
     * starts with 'lat' to be saved in a property called 'y', then you would call this using:
     * 
     * <pre>
     * addKnownHeader(&quot;y&quot;, &quot;lat.*&quot;);
     * </pre>
     * 
     * @param key the name to use for the property
     * @param regex a regular expression to use to find the property
     */
    protected void addKnownHeader(String key, String regex) {
        addKnownHeader(key, new String[] {regex});
    }

    /**
     * Add a property name and list of regular expressions for a single known header. This is used
     * if we want the property name in the database to be some specific text, not the header text in
     * the file. The regular expressions are used to find the header in the file to associate with
     * the new property name. Note that the original property will not be saved using its original
     * name. It will be saved with the specified name provided. For example, if you want the first
     * field found that starts with either 'lat' or 'y_wert' to be saved in a property called 'y',
     * then you would call this using:
     * 
     * <pre>
     * addKnownHeader(&quot;y&quot;, new String[] {&quot;lat.*&quot;, &quot;y_wert.*&quot;}, true);
     * </pre>
     * 
     * @param key the name to use for the property
     * @param array of regular expressions to use to find the single property
     */
    protected void addKnownHeader(String key, String[] regexes) {
        if (knownHeaders.containsKey(key)) {
            List<String> value = knownHeaders.get(key);
            value.addAll(Arrays.asList(regexes));
            knownHeaders.put(key, value);
        } else {
            knownHeaders.put(key, Arrays.asList(regexes));
        }
    }

    /**
     * Add a special header that creates a new property based on the existence of another property.
     * This includes a mapper that modifies the contents of the value interpreted. For example, if
     * you want to create a new property called 'active' that contains only 'yes/no' values and is
     * based on finding the text 'on air' inside another property, use this:
     * 
     * <pre>
     * addMappedHeader(&quot;status&quot;, &quot;Active&quot;, &quot;active&quot;, new PropertyMapper() {
     *     public String mapValue(String originalValue) {
     *         return originalValue.toLowerCase().contains(&quot;on air&quot;) ? &quot;yes&quot; : &quot;no&quot;;
     *     }
     * });
     * </pre>
     * 
     * @param original header key to base new header on
     * @param name of new header, or null to use the old header (and replace it)
     * @param key of new header
     * @param mapper the mapper required to convert values from the old to the new
     */
    protected final void addMappedHeader(String original, String name, String key, PropertyMapper mapper) {
        mappedHeaders.put(original, new MappedHeaderRule(name, key, mapper));
    }

    /**
     * This uses the same PropertyMapper mechanism as the addMappedHeader() method, but does not
     * create a new property, instead it replaces the original property. Internally it uses the same
     * key for original and new property and also sets the new name to null to signal the system to
     * do replacement. This is especially useful if you want to override the default header parsing
     * logic with your own custom logic. For example, to keep string values for a property:
     * 
     * <pre>
     * useMapper(&quot;site&quot;, new StringMapper());
     * </pre>
     * 
     * @param key of header/property
     * @param mapper the mapper required to convert values
     */
    protected final void useMapper(String key, PropertyMapper mapper) {
        mappedHeaders.put(key, new MappedHeaderRule(null, key, mapper));
    }

    protected final void dropHeaderStats(String[] keys) {
        dropStatsHeaders.addAll(Arrays.asList(keys));
    }

    protected final void addNonDataHeaders(Collection<String> keys) {
        dropStatsHeaders.addAll(keys);
        nonDataHeaders.addAll(keys);
    }

    /**
     * Parse possible header lines and build a set of header objects to be used to parse all data
     * lines later. This allows us to deal with several requirements:
     * <ul>
     * <li>Know when we have passed the header and are in the data body of the file</li>
     * <li>Have objects that automatically learn the tyep of the data as the data is parsed</li>
     * <li>Support mapping headers to known specific names</li>
     * <li>Support mapping values to different values using pre-defined mapper code</li>
     * </ul>
     * 
     * @param line to parse as the header line
     */
    protected final void parseHeader(String line) {
        debug(line);
        determineFieldSepRegex(line);
        String fields[] = splitLine(line);
        if (fields.length < 2)
            return;
        int index = 0;
        for (String headerName : fields) {
            String header = cleanHeader(headerName);
            if (headerAllowed(header)) {
                boolean added = false;
                debug("Added header[" + index + "] = " + header);
                KNOWN: for (String key : knownHeaders.keySet()) {
                    if (!headers.containsKey(key)) {
                        for (String regex : knownHeaders.get(key)) {
                            for (String testString : new String[] {header, headerName}) {
                                if (testString.toLowerCase().matches(regex.toLowerCase())) {
                                    debug("Added known header[" + index + "] = " + key);
                                    headers.put(key, new Header(headerName, key, index));
                                    added = true;
                                    break KNOWN;
                                }
                            }
                        }
                    }
                }
                if (!added/* !headers.containsKey(header) */) {
                    headers.put(header, new Header(headerName, header, index));
                }
            }
            index++;
        }
        // Now add any new properties created from other existing properties using mapping rules
        for (String key : mappedHeaders.keySet()) {
            if (headers.containsKey(key)) {
                MappedHeaderRule mapRule = mappedHeaders.get(key);
                if (headers.containsKey(mapRule.key)) {
                    // We only allow replacement if the user passed null for the name
                    if (mapRule.name == null) {
                        headers.put(mapRule.key, new MappedHeader(headers.get(key), mapRule));
                    } else {
                        notify("Cannot add mapped header with key '" + mapRule.key + "': header with that name already exists");
                    }
                } else {
                    headers.put(mapRule.key, new MappedHeader(headers.get(key), mapRule));
                }
            } else {
                notify("No original header found matching mapped header key: " + key);
            }
        }
        for (String key : dropStatsHeaders) {
            Header header = headers.get(key);
            if (header != null) {
                header.dropStats();
            }
        }
    }

    private boolean headerAllowed(String header) {
        if (headerFilters == null || headerFilters.size() < 1) {
            return true;
        }
        for (Pattern filter : headerFilters) {
            if (filter.matcher(header).matches()) {
                return true;
            }
        }
        return false;
    }

    protected HashMap<Class< ? extends Object>, List<String>> typedProperties = null;
    protected Transaction mainTx;
    private int commitSize = 5000;
    protected String nameGis;

    protected List<String> getProperties(Class< ? extends Object> klass) {
        if (typedProperties == null) {
            makeTypedProperties();
        }
        return typedProperties.get(klass);
    }

    protected List<String> getNumericProperties() {
        ArrayList<String> results = new ArrayList<String>();
        for (Class< ? extends Object> klass : NUMERIC_PROPERTY_TYPES) {
            results.addAll(getProperties(klass));
        }
        return results;
    }

    protected List<String> getDataProperties() {
        ArrayList<String> results = new ArrayList<String>();
        results.addAll(getNumericProperties());
        for (String key : getProperties(String.class)) {
            if (headers.get(key).parseCount > 0) {
                results.add(key);
            }
        }
        return results;
    }

    private void makeTypedProperties() {
        this.typedProperties = new HashMap<Class< ? extends Object>, List<String>>();
        for (Class< ? extends Object> klass : KNOWN_PROPERTY_TYPES) {
            this.typedProperties.put(klass, new ArrayList<String>());
        }
        for (String key : headers.keySet()) {
            Header header = headers.get(key);
            if (header.parseCount > 0) {
                for (Class< ? extends Object> klass : KNOWN_PROPERTY_TYPES) {
                    if (header.knownType() == klass) {
                        this.typedProperties.get(klass).add(header.key);
                    }
                }
            }
        }
    }

    protected final LinkedHashMap<String, Object> makeDataMap(String[] fields) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        for (String key : headers.keySet()) {
            try {
                Header header = headers.get(key);
                String field = fields[header.index];
                if (field == null || field.length() < 1 || field.equals("?")) {
                    continue;
                }
                Object value = header.parse(field);
                map.put(key, value); // TODO: Decide if we should actually use the name here

                // Now speed up parsing once we are certain of the column types
                if (header.shouldConvert()) {
                    Class< ? extends Object> klass = header.knownType();
                    if (klass == Integer.class) {
                        headers.put(key, new IntegerHeader(header));
                    } else if (klass == Float.class) {
                        headers.put(key, new FloatHeader(header));
                    } else {
                        headers.put(key, new StringHeader(header));
                    }
                }
            } catch (Exception e) {
                // TODO Handle Exception
            }
        }
        return map;
    }
    
    private Display currentDisplay = null;

    protected final void debug(final String line) {
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.debug(typeName + ":" + basename + ":" + status() + ": " + line);
            }
        });
    }

    protected final void info(final String line) {
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":" + basename + ":" + status() + ": " + line);
            }
        });
    }

    protected final void notify(final String line) {
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":" + basename + ":" + status() + ": " + line);
            }
        });
    }

    protected final void error(final String line) {
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":" + basename + ":" + status() + ": " + line);
            }
        });
    }

    private final void runInDisplay(Runnable runnable) {
        if (display != null) {
        	if (currentDisplay == null) {
        		currentDisplay = PlatformUI.getWorkbench().getDisplay();
        	}
        	currentDisplay.asyncExec(runnable);
        } else {
            runnable.run();
        }
    }

    protected final String status() {
        if (started <= 0)
            started = System.currentTimeMillis();
        return (lineNumber > 0 ? "line:" + lineNumber : "" + ((System.currentTimeMillis() - started) / 1000.0) + "s");
    }

    public void setLimit(int value) {
        this.limit = value;
    }

    protected boolean isOverLimit() {
        return limit > 0 && savedData > limit;
    }

    protected void incSaved() {
        savedData++;
    }

    /**
     * This is the main method of the class. It opens the file, iterates over the contents and calls
     * parseLine(String) on each line. The subclass needs to implement parseLine(String) to
     * interpret the data and save it to the database.
     * 
     * @param monitor
     * @throws IOException
     */
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        CountingFileInputStream is = new CountingFileInputStream(new File(filename));
        String characterSet = NeoLoaderPlugin.getDefault().getCharacterSet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, characterSet));
        mainTx = neo.beginTx();
        try {
            initializeIndexes();
            int perc = is.percentage();
            int prevPerc = 0;
            int prevLineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!haveHeaders()) {
                	parseHeader(line);                	
                }
                else {
                	parseLine(line);                    
                }
                if (monitor != null) {
                    if (monitor.isCanceled())
                        break;
                    perc = is.percentage();
                    if (perc > prevPerc) {
                        monitor.subTask(basename + ":" + lineNumber + " (" + perc + "%)");
                        monitor.worked(perc - prevPerc);
                        prevPerc = perc;
                    }
                }
                if (lineNumber > prevLineNumber + commitSize) {
                    commit(true);
                    prevLineNumber = lineNumber;
                }
                if (isOverLimit())
                    break;

            }
            commit(true);
            reader.close();
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
        }
    }


    protected void addIndex(MultiPropertyIndex< ? > index) {
        indexes.add(index);
    }

    protected void removeIndex(MultiPropertyIndex< ? > index) {
        indexes.remove(index);
    }

    protected void index(Node node) {
        for (MultiPropertyIndex< ? > index : indexes) {
            try {
                index.add(node);
            } catch (IOException e) {
                // TODO:Log error
                removeIndex(index);
            }
        }
    }

    protected void flushIndexes() {
        for (MultiPropertyIndex< ? > index : indexes) {
            try {
                index.flush();
            } catch (IOException e) {
                // TODO:Log error
                removeIndex(index);
            }
        }
    }
    
    protected void initializeIndexes() {
    	if (indexesInitialized) {
    		return;
    	}
    	for (MultiPropertyIndex< ? > index : indexes) {
            try {
                index.initialize(this.neo, null);
            } catch (IOException e) {
                // TODO:Log error
                removeIndex(index);
            }
        }
    	indexesInitialized = true;
    }

    protected void finishUpIndexes() {
        for (MultiPropertyIndex< ? > index : indexes) {
            index.finishUp();
        }
    }

    protected void commit(boolean restart) {
        if (mainTx != null) {
            flushIndexes();
            mainTx.success();
            mainTx.finish();
            // System.out.println("Commit: Memory: "+(Runtime.getRuntime().totalMemory() -
            // Runtime.getRuntime().freeMemory()));
            if (restart) {
                mainTx = neo.beginTx();
            } else {
                mainTx = null;
            }
        }
    }

    /**
     * This method must be implemented by all readers to parse the data lines. It might save data
     * directly to the database, or it might keep it in a cache for saving later, in the finishUp
     * method. A common pattern is to block data into chunks, saving these to the database at
     * reasonable points, and then using finishUp() to save any remaining data.
     * 
     * @param line
     */
    protected abstract void parseLine(String line);

    /**
     * After all lines have been parsed, this method is called, allowing the implementing class the
     * opportunity to save any cached information, or write any final statistics. It is not abstract
     * because it is possible, or even probable, to write an importer that does not need it.
     */
    protected void finishUp() {
    }

    protected final void checkCRS(float lat, float lon, String hint) {
        if (crs == null) {
            crs = CRS.fromLocation(lat, lon, hint);
            gis.setProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, crs.getType());
            gis.setProperty(INeoConstants.PROPERTY_CRS_NAME, crs.toString());
        }
    }

    /**
     * Search the database for the 'gis' node for this dataset. If none found it created an
     * appropriate node. The search is done for 'gis' nodes that reference the specified main node.
     * If a node needs to be created it is linked to the main node so future searches will return
     * it.
     * 
     * @param mainNode main network or drive data node
     * @return gis node for mainNode
     */
    protected final Node findOrCreateGISNode(Node mainNode, String gisType) {
        if (gis == null) {
            Transaction transaction = neo.beginTx();
            try {
                nameGis = NeoUtils.getNodeName(mainNode);
                Node reference = neo.getReferenceNode();
                for (Relationship relationship : mainNode.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING)) {
                    Node node = relationship.getStartNode();
                    if (node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(INeoConstants.GIS_TYPE_NAME)) {
                        if (!node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator().hasNext()) {
                            node.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                        }
                        gis = node;
                        bbox = (double[])gis.getProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
                        break;
                    }
                }
                if (gis == null) {
                    gis = findMatchingGisNode(nameGis, gisType);
                    if (gis == null) {
                        gis = neo.createNode();
                        gis.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.GIS_TYPE_NAME);
                        gis.setProperty(INeoConstants.PROPERTY_NAME_NAME, nameGis);
                        gis.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, gisType);
                        reference.createRelationshipTo(gis, NetworkRelationshipTypes.CHILD);
                    } else {
                        deleteOldGisNodes(nameGis, gisType, gis);
                    }
                    boolean hasRelationship = false;
                    for (Relationship relation : gis.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                        if (relation.getEndNode().equals(mainNode)) {
                            hasRelationship = true;
                        }
                    }
                    if (!hasRelationship) {
                        gis.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                    }
                }
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        return gis;
    }

    private Traverser makeGisTraverser(final String name, final String gisType) {
        Traverser tr = neo.getReferenceNode().traverse(
                Order.BREADTH_FIRST,
                new StopEvaluator() {

                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        return currentPos.depth() > 3;
                    }
                },
                new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(
                                INeoConstants.GIS_TYPE_NAME)
                                && currentPos.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(name)
                                && currentPos.currentNode().getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").equals(gisType);
                    }
                }, SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING,
                GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        return tr;
    }

    private Node findMatchingGisNode(String name, String gisType) {
        java.util.Iterator<Node> it = makeGisTraverser(name, gisType).iterator();
        return it.hasNext() ? it.next() : null;
    }

    private final void deleteOldGisNodes(String name, String gisType, Node gisNode) {
        for (Node node : makeGisTraverser(name, gisType)) {
            debug("Testing possible Network node " + node + ": " + node.getProperty("name", "").toString());
            if (!node.equals(gisNode)) {
                // remove all incoming relationships
                for (Relationship relationshipIn : node.getRelationships(Direction.INCOMING)) {
                    relationshipIn.delete();
                }
                deleteTree(node);
                deleteNode(node);
            }
        }
    }

    protected void deleteTree(Node root) {
        if (root != null) {
            for (Relationship relationship : root.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                deleteTree(node);
                debug("Deleting node " + node + ": " + (node.hasProperty("name") ? node.getProperty("name") : ""));
                deleteNode(node);
            }
        }
    }

    protected void deleteNode(Node node) {
        if (node != null) {
            for (Relationship relationship : node.getRelationships()) {
                relationship.delete();
            }
            node.delete();
        }
    }

    protected final void saveProperties() {
        if (gis != null) {
            Transaction transaction = neo.beginTx();
            try {
                Node propNode;
                Relationship propRel = gis.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
                if (propRel == null) {
                    propNode = neo.createNode();
                    propNode.setProperty("name", NeoUtils.getNodeName(gis));
                    propNode.setProperty("type", "gis_properties");
                    gis.createRelationshipTo(propNode, GeoNeoRelationshipTypes.PROPERTIES);
                } else {
                    propNode = propRel.getEndNode();
                }
                HashMap<String, Node> propTypeNodes = new HashMap<String, Node>();
                for (Node node : propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                        ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)) {
                    propTypeNodes.put(node.getProperty("name").toString(), node);
                }
                for (Class< ? extends Object> klass : KNOWN_PROPERTY_TYPES) {
                    String typeName = makePropertyTypeName(klass);
                    List<String> properties = getProperties(klass);
                    if (properties != null && properties.size() > 0) {
                        Node propTypeNode = propTypeNodes.get(typeName);
                        if (propTypeNode == null) {
                            propTypeNode = neo.createNode();
                            propTypeNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, typeName);
                            propTypeNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, "gis_property_type");
                            savePropertiesToNode(propTypeNode, properties);
                            propNode.createRelationshipTo(propTypeNode, GeoNeoRelationshipTypes.CHILD);
                        } else {
                            TreeSet<String> combinedProperties = new TreeSet<String>();
                            String[] previousProperties = (String[])propTypeNode.getProperty(INeoConstants.NODE_TYPE_PROPERTIES,
                                    null);
                            if (previousProperties != null)
                                combinedProperties.addAll(Arrays.asList(previousProperties));
                            combinedProperties.addAll(properties);
                            savePropertiesToNode(propTypeNode, combinedProperties);
                        }
                    }
                }
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
    }

    private void savePropertiesToNode(Node propTypeNode, Collection<String> properties) {
        propTypeNode.setProperty("properties", properties.toArray(new String[properties.size()]));
        HashMap<String, Node> valueNodes = new HashMap<String, Node>();
        ArrayList<String> noStatsProperties = new ArrayList<String>();
        ArrayList<String> dataProperties = new ArrayList<String>();
        for (Relationship relation : propTypeNode.getRelationships(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING)) {
            Node valueNode = relation.getEndNode();
            String property = relation.getProperty("property", "").toString();
            valueNodes.put(property, valueNode);
        }
        for (String property : properties) {
            if (!nonDataHeaders.contains(property)) {
                dataProperties.add(property);
            }
            Node valueNode = valueNodes.get(property);
            Header header = headers.get(property);
            HashMap<Object, Integer> values = header.values;
            if (values == null) {
                if (valueNode != null) {
                    for (Relationship relation : valueNode.getRelationships()) {
                        relation.delete();
                    }
                    valueNode.delete();
                }
                noStatsProperties.add(property);
            } else {
                Relationship valueRelation = null;
                if (valueNode == null) {
                    valueNode = neo.createNode();
                    valueRelation = propTypeNode.createRelationshipTo(valueNode, GeoNeoRelationshipTypes.PROPERTIES);
                    valueRelation.setProperty("property", property);
                } else {
                    valueRelation = valueNode.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.INCOMING);
                    for (Object key : valueNode.getPropertyKeys()) {
                        Integer oldCount = (Integer)valueNode.getProperty(key.toString(), null);
                        if (oldCount == null) {
                            oldCount = 0;
                        }
                        Integer newCount = values.get(key);
                        if (newCount == null) {
                            newCount = 0;
                        }
                        values.put(key, oldCount + newCount);
                    }
                }
                int total = 0;
                for (Object key : values.keySet()) {
                    valueNode.setProperty(key.toString(), values.get(key));
                    total += values.get(key);
                }
                if (valueRelation != null) {
                    valueRelation.setProperty("count", total);
                }
            }
        }
        ArrayList<String> statsProperties = new ArrayList<String>(properties);
        for (String noStat : noStatsProperties) {
            statsProperties.remove(noStat);
        }
        propTypeNode.setProperty("data_properties", dataProperties.toArray(new String[0]));
        propTypeNode.setProperty("stats_properties", statsProperties.toArray(new String[0]));
        propTypeNode.setProperty("no_stats_properties", noStatsProperties.toArray(new String[0]));
    }

    public static String makePropertyTypeName(Class< ? extends Object> klass) {
        return klass.getName().replaceAll("java.lang.", "").toLowerCase();
    }

    public void clearCaches() {
        this.headers.clear();
        this.knownHeaders.clear();
    }

    /**
     * This method adds the loaded data to the GIS catalog. This is achieved by
     * <ul>
     * <li>Cleaning the gis node of any old statistics, and then updating the basic statistics</li>
     * <li>Then the data is added to the current AWE project</li>
     * <li>The catalog for Neo data is created or updated</li>
     * </ul>
     * 
     * @param mainNode to use to connect to the AWE project
     * @throws MalformedURLException
     */
    public static final void finishUpGis(Node mainNode) throws MalformedURLException {
        NeoServiceProvider neoProvider = NeoServiceProvider.getProvider();
        if (neoProvider != null) {
            NeoCorePlugin.getDefault().getProjectService().addDataNodeToProject(LoaderUtils.getAweProjectName(), mainNode);
            addDataToCatalog();
        }
    }
    
    /**
     * Is this a test case running outside AWE application
     * 
     * @return true if we have no NeoProvider and so are not running inside AWE
     */
    protected final boolean isTest() {
        return neoProvider == null;
    }

    /**
     * This method adds the loaded data to the GIS catalog. The neo-catalog entry is created or
     * updated.
     * 
     * @throws MalformedURLException
     */
    protected static void addDataToCatalog() throws MalformedURLException {
    	//TODO: Lagutko, 17.12.2009, can be run as a Job
    	NeoServiceProvider neoProvider = NeoServiceProvider.getProvider();
        if (neoProvider != null) {
            String databaseLocation = neoProvider.getDefaultDatabaseLocation();
            NeoCorePlugin.getDefault().getUpdateDatabaseManager().fireUpdateDatabase(
                    new UpdateDatabaseEvent(UpdateDatabaseEventType.GIS));
            ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
            URL url = new URL("file://" + databaseLocation);
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
            for (IService service : services) {
                if (catalog.getById(IService.class, service.getIdentifier(), new NullProgressMonitor()) != null) {
                    catalog.replace(service.getIdentifier(), service);
                } else {
                    catalog.add(service);
                }
            }
        }
    }

    /**
     * Clean the gis node of any old statistics, and then update the basic statistics
     * 
     * @param mainNode to use to connect to the AWE project
     * @throws MalformedURLException
     */
    protected final void cleanupGisNode() {
        if (gis != null) {
            Transaction transaction = neo.beginTx();
            try {
                if (bbox != null) {
                    gis.setProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
                    gis.setProperty("count", savedData + (Long)gis.getProperty("count", 0L));
                }
                HashSet<Node> nodeToDelete = new HashSet<Node>();
                for (Relationship relation : gis.getRelationships(NetworkRelationshipTypes.AGGREGATION, Direction.OUTGOING)) {
                    nodeToDelete.add(relation.getEndNode());
                }
                for (Node node : nodeToDelete) {
                    NeoCorePlugin.getDefault().getProjectService().deleteNode(node);
                }
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
    }

    /**
     * adds gis to active map
     * 
     * @param gis node
     */
    public void addLayerToMap() {
        try {
            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
            URL url = new URL("file://" + databaseLocation);
            IService curService = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, url, null);
            final IMap map = ApplicationGIS.getActiveMap();
            if (curService != null && gis != null && NetworkLoader.findLayerByNode(map, gis) == null
                    && confirmAddToMap(map, NeoUtils.getNodeName(gis))) {
                java.util.List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                java.util.List<ILayer> layerList = new ArrayList<ILayer>();
                for (IGeoResource iGeoResource : curService.resources(null)) {
                    if (iGeoResource.canResolve(Node.class)) {
                        if (iGeoResource.resolve(Node.class, null).equals(gis)) {
                            listGeoRes.add(iGeoResource);
                            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
                            break;
                        }
                    }
                };
                IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
                if (preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER)) {
                    zoomToLayer(layerList);
                }
            }
        } catch (MalformedURLException e) {
            // TODO Handle MalformedURLException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    /**
     * Zoom To 1st layers in list
     * 
     * @param layers list of layers
     */
    private static void zoomToLayer(final List< ? extends ILayer> layers) {
        ActionUtil.getInstance().runTask(new Runnable() {
            @Override
            public void run() {
                ZoomToLayer zoomCommand = new ZoomToLayer();
                zoomCommand.selectionChanged(null, new StructuredSelection(layers));
                zoomCommand.runWithEvent(null, null);
            }
        }, true);
    }

    /**
     * Confirm load network on map
     * 
     * @param map map
     * @param fileName name of loaded file
     * @return true or false
     */
    private static boolean confirmAddToMap(final IMap map, final String fileName) {

        final IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
        return (Integer)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Integer>() {
            int result;

            @Override
            public void run() {
                boolean boolean1 = preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER);
                String message = String.format(NeoLoaderPluginMessages.ADD_LAYER_MESSAGE, fileName, map.getName());
                if (map == ApplicationGIS.NO_MAP) {
                    message = String.format(NeoLoaderPluginMessages.ADD_NEW_MAP_MESSAGE, fileName);
                }
                // MessageBox msg = new
                // MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                // SWT.YES | SWT.NO);
                // msg.setText(NeoLoaderPluginMessages.ADD_LAYER_TITLE);
                // msg.setMessage(message);
                MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell(), NeoLoaderPluginMessages.ADD_LAYER_TITLE, message,
                        NeoLoaderPluginMessages.TOGLE_MESSAGE, boolean1, preferenceStore, DataLoadPreferences.ZOOM_TO_LAYER);
                result = dialog.getReturnCode();
                if (result == IDialogConstants.YES_ID) {
                    preferenceStore.putValue(DataLoadPreferences.ZOOM_TO_LAYER, String.valueOf(dialog.getToggleState()));
                }
            }

            @Override
            public Integer getValue() {
                return result;
            }
        }) == IDialogConstants.YES_ID;
    }

    /**
     * @return Time in milliseconds since this loader started running
     */
    protected long timeTaken() {
        return System.currentTimeMillis() - started;
    }

    protected final void updateBBox(double lat, double lon) {
        if (bbox == null) {
            bbox = new double[] {lon, lon, lat, lat};
        } else {
            if (bbox[0] > lon)
                bbox[0] = lon;
            if (bbox[1] < lon)
                bbox[1] = lon;
            if (bbox[2] > lat)
                bbox[2] = lat;
            if (bbox[3] < lat)
                bbox[3] = lat;
        }
    }

    private void printHeaderStats() {
        notify("Determined Columns:");
        for (String key : headers.keySet()) {
            Header header = headers.get(key);
            if (header.parseCount > 0) {
                notify("\t" + header.knownType() + " loaded: " + header.parseCount + " => " + key);
            }
        }
    }

    public void printStats(boolean verbose) {
        printHeaderStats();
        long taken = timeTaken();
        notify("Finished loading " + basename + " data in " + (taken / 1000.0) + " seconds");
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }

    /**
     * @return Returns the commitSize.
     */
    public int getCommitSize() {
        return commitSize;
    }

}
