package org.amanzi.neo.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.NetworkLoader.CRS;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser.Order;

public abstract class AbstractLoader {
    private String typeName = "CSV";
    protected NeoService neo;
    private NeoServiceProvider neoProvider;
    private Node gis = null;
    private CRS crs = null;
    protected String filename = null;
    protected String basename = null;
    private Display display;
    private String fieldSepRegex;
    private String[] possibleFieldSepRegexes = new String[]{"\\t","\\,","\\;"};
    protected int line_number = 0;
    private int limit = 0;
    private double[] bbox;
    private long savedData = 0;
    private long started = System.currentTimeMillis();
    private LinkedHashMap<String, String> knownHeaders = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, Header> headers = new LinkedHashMap<String, Header>();
    @SuppressWarnings("unchecked")
    public static final Class[] KNOWN_PROPERTY_TYPES = new Class[]{Integer.class, Float.class, String.class};

    protected class Header {
        private static final int MAX_PROPERTY_VALUE_COUNT = 200;        // discard value sets if count exceeds 1000
        private static final float MAX_PROPERTY_VALUE_SPREAD = 0.5f;    // discard value sets if spread exceeds 50%
        private static final int MIN_PROPERTY_VALUE_SPREAD_COUNT = 50;  // only calculate spread after this number of data points
        int index;
        String key;
        String name;
        HashMap<Class<? extends Object>,Integer> parseTypes = new HashMap<Class<? extends Object>,Integer>();
        HashMap<Object,Integer> values = new HashMap<Object,Integer>();
        int parseCount = 0;
        Header(String name, String key, int index) {
            this.index = index;
            this.name = name;
            this.key = key;
            for(Class<? extends Object> klass: KNOWN_PROPERTY_TYPES){
                parseTypes.put(klass,0);
            }
        }
        Header(Header old){
            this(old.name,old.key,old.index);
            this.parseCount = old.parseCount;
            this.values = old.values;
        }
        protected boolean invalid(String field) {
            return field==null || field.length()<1 || field.equals("?");
        }
        Object parse(String field) {
            if(invalid(field)) return null;
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
        private void incType(Class<? extends Object> klass) {
            parseTypes.put(klass, parseTypes.get(klass)+1);
        }
        protected Object incValue(Object value) {
            if(values!=null) {
                Integer count = values.get(value);
                if (count == null) {
                    count = 0;
                }
                boolean discard = false;
                if(count == 0) {
                    // We have a new value, so adding it will increase the size of the map
                    // We should perform threshold tests to decide whether to drop the map or not
                    if(values.size() >= MAX_PROPERTY_VALUE_COUNT) {
                        // Exceeded absolute threashold, drop map
                        System.out.println("Property values exceeded maximum count, no longer tracking value set: "+this.key);
                        discard = true;
                    } else if(values.size() >= MIN_PROPERTY_VALUE_SPREAD_COUNT) {
                        // Exceeded minor threshold, test spread and then decide
                        float spread = (float)values.size() / (float)parseCount;
                        if(spread > MAX_PROPERTY_VALUE_SPREAD) {
                            // Exceeded maximum spread, too much property variety, drop map
                            System.out.println("Property shows excessive variation, no longer tracking value set: "+this.key);
                            discard = true;
                        }
                    }
                }
                if(discard) {
                    // Detected too much variety in property values, stop counting
                    values = null;
                } else {
                    values.put(value, count + 1);
                }
            }
            return value;
        }
        boolean shouldConvert() {
            return parseCount > 10;
        }
        Class<? extends Object> knownType() {
            Class<? extends Object> best = String.class;
            int maxCount = 0;
            int countFound = 0;
            for(Class<? extends Object> klass:parseTypes.keySet()){
                int count = parseTypes.get(klass);
                if(maxCount<parseTypes.get(klass)){
                    maxCount = count;
                    best = klass;
                }
                if(count>0){
                    countFound++;
                }
            }
            if(countFound>1){
                AbstractLoader.this.notify("Header "+key+" had multiple type matches: ");
                for(Class<? extends Object> klass:parseTypes.keySet()){
                    int count = parseTypes.get(klass);
                    if(count>0){
                        AbstractLoader.this.notify("\t"+count+": "+klass+" => "+key);
                    }
                }
            }
            return best;
        }
    }
    protected class IntegerHeader extends Header {
        IntegerHeader(Header old){
            super(old);
        }
        Integer parse(String field){
            if(invalid(field)) return null;
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
    protected class FloatHeader extends Header {
        FloatHeader(Header old){
            super(old);
        }
        Float parse(String field){
            if(invalid(field)) return null;
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
        StringHeader(Header old){
            super(old);
        }
        String parse(String field){
            if(invalid(field)) return null;
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
    protected interface StringPropertyMapper {
        public String mapValue(String originalValue);
    }
    protected class MappedStringHeader extends StringHeader {
        private StringPropertyMapper mapper;
        MappedStringHeader(Header old, String name, String key, StringPropertyMapper mapper){
            super(old);
            this.key = key;
            this.name = name;
            this.mapper = mapper;
        }
        String parse(String field){
            if(invalid(field)) return null;
            field = mapper.mapValue(field);
            if(invalid(field)) return null;
            parseCount++;
            return (String)incValue(field);
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
        if(neoService == null) {
            //if Display is given than start Neo using syncExec
            if (display != null) {
                display.syncExec(new Runnable() {
                    public void run() {
                        initializeNeo();
                    }
                });
            }
            //if Display is not given than initialize Neo as usual
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

    private void determineFieldSepRegex(String line){
        int maxMatch = 0;
        for(String regex:possibleFieldSepRegexes){
            String[] fields = line.split(regex);
            if(fields.length > maxMatch){
                maxMatch = fields.length;
                fieldSepRegex = regex;
            }
        }
    }

    protected String[] splitLine(String line){
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
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_").replaceAll("\\_$", "").toLowerCase();
    }

    protected boolean haveHeaders() {
        return headers.size()>0;
    }
    protected void addKnownHeader(String key, String regex) {
        knownHeaders.put(key, regex);
    }

    protected final void parseHeader(String line){
        debug(line);
        determineFieldSepRegex(line);
        String fields[] = splitLine(line);
        if(fields.length<2) return;
        int index=0;
        for(String headerName:fields){
            boolean added = false;
            String header = cleanHeader(headerName);
            debug("Added header["+index+"] = "+header);
            for(String key:knownHeaders.keySet()){
                if (!headers.containsKey(key) && header.matches(knownHeaders.get(key))) {
                    debug("Added known header[" + index + "] = " + key);
                    headers.put(key, new Header(headerName, key, index));
                    added = true;
                    break;
                }
            }
            if(!added/*!headers.containsKey(header)*/){
                headers.put(header, new Header(headerName, header, index));
            }
            index++;
        }
    }

    /**
     * Add a special header that creates a new property based on the existance of another property.
     * This includes a mapper that modifies the contents of the value interpreted.
     *
     * @param property
     * @param name
     * @param key
     * @param mapper
     */
    protected final void addMappedHeader(String property, String name, String key, StringPropertyMapper mapper) {
        Header original = headers.get(property);
        if (original != null) {
            headers.put(key, new MappedStringHeader(original, name, key, mapper));
        }
    }

    private HashMap<Class<? extends Object>,List<String>> typedProperties = null;
    private Transaction mainTx;
    protected List<String> getIntegerProperties() {
        return getProperties(Integer.class);
    }
    protected List<String> getFloatProperties() {
        return getProperties(Integer.class);
    }
    protected List<String> getStringProperties() {
        return getProperties(Integer.class);
    }
    protected List<String> getProperties(Class<? extends Object> klass) {
        if(typedProperties==null) {
            makeTypedProperties();
        }
        return typedProperties.get(klass);
    }
    private void makeTypedProperties() {
        this.typedProperties = new HashMap<Class<? extends Object>,List<String>>();
        this.typedProperties.put(Integer.class, new ArrayList<String>());
        this.typedProperties.put(Float.class, new ArrayList<String>());
        this.typedProperties.put(String.class, new ArrayList<String>());
        for(String key: headers.keySet()){
            Header header = headers.get(key);
            if(header.parseCount>0){
                for(Class<? extends Object> klass:KNOWN_PROPERTY_TYPES){
                    if(header.knownType() == klass){
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
                if(field == null || field.length()<1 || field.equals("?")){
                    continue;
                }
                Object value = header.parse(field);
                map.put(key, value); // TODO: Decide if we should actually use the name here
                
                //Now speed up parsing once we are certain of the column types
                if (header.shouldConvert()) {
                    Class<? extends Object> klass = header.knownType();
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

    protected final int i_of(String header){
        debug("Looking up header index for "+header);
        return headers.get(header).index;
    }

    protected final void debug(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.debug(typeName + ":"+basename+":"+status()+": "+line);
            }
        });
    }
    
    protected final void info(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        }); 
    }
    
    protected final void notify(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        });     
    }
    
    protected final void error(final String line){
        runInDisplay(new Runnable() {
            public void run() {
                NeoLoaderPlugin.notify(typeName + ":"+basename+":"+status()+": "+line);
            }
        }); 
    }

    private final void runInDisplay(Runnable runnable) {
        if(display!=null){
            PlatformUI.getWorkbench().getDisplay().asyncExec(runnable);
        } else {
            runnable.run();
        }
    }
    
    protected final String status(){
        if(started<=0) started = System.currentTimeMillis();
        return (line_number>0 ? "line:"+line_number : ""+((System.currentTimeMillis()-started)/1000.0)+"s");
    }

    public void setLimit(int value){
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        mainTx = neo.beginTx();
        try {
            int perc = is.percentage();
            int prevPerc = 0;
            int prevLineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                line_number++;
                if (!haveHeaders())
                    parseHeader(line);
                else
                    parseLine(line);
                if (monitor != null) {
                    if (monitor.isCanceled())
                        break;
                    perc = is.percentage();
                    if (perc > prevPerc) {
                        monitor.subTask(basename+":" + line_number + " ("+perc+"%)");
                        monitor.worked(perc - prevPerc);
                        prevPerc = perc;
                    }
                }
                // Commit external transaction on large blocks of code
                if(line_number > prevLineNumber + 1000) {
                    commit(true);
                    prevLineNumber = line_number;
                }
            }
            commit(true);
            reader.close();
            saveProperties();
            finishUp();
        } finally {
            commit(false);
        }
    }

    protected void commit(boolean restart){
        if(mainTx!=null) {
            mainTx.success();
            mainTx.finish();
            //System.out.println("Commit: Memory: "+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            if(restart){
                mainTx = neo.beginTx();
            }else{
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

    protected final void checkCRS(String[] latlon) {
        if (crs == null) {
            crs = CRS.fromLocation(Float.parseFloat(latlon[0]), Float.parseFloat(latlon[1]), null);
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
        if(gis == null) {
            Transaction transaction = neo.beginTx();
            try {
                Node reference = neo.getReferenceNode();
                for (Relationship relationship : mainNode.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING)) {
                    Node node = relationship.getStartNode();
                    if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                            && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.GIS_TYPE_NAME)
                            && node.hasProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME)
                            && node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME).toString().equals(GisTypes.DRIVE.getHeader())){
                        if(!node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator().hasNext()) {
                            node.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                        }
                        gis = node;
                        bbox = (double[])gis.getProperty(INeoConstants.PROPERTY_BBOX_NAME, bbox);
                        break;
                    }
                }
                if(gis == null) {
                    gis = neo.createNode();
                    gis.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.GIS_TYPE_NAME);
                    gis.setProperty(INeoConstants.PROPERTY_NAME_NAME, NeoUtils.getNodeName(mainNode));
                    gis.setProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, gisType);
                    reference.createRelationshipTo(gis, NetworkRelationshipTypes.CHILD);
                    gis.createRelationshipTo(mainNode, GeoNeoRelationshipTypes.NEXT);
                }
                transaction.success();
            } finally {
                transaction.finish();
            }
        }
        return gis;
    }

    protected final void saveProperties() {
        if (gis != null) {
            Transaction transaction = neo.beginTx();
            try {
                Node propNode;
                Relationship propRel = gis.getSingleRelationship(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING);
                if(propRel==null){
                    propNode = neo.createNode();
                    propNode.setProperty("name", NeoUtils.getNodeName(gis));
                    propNode.setProperty("type", "gis_properties");
                    gis.createRelationshipTo(propNode, GeoNeoRelationshipTypes.PROPERTIES);
                }else{
                    propNode = propRel.getEndNode();
                }
                HashMap<String,Node> propTypeNodes = new HashMap<String,Node>();
                for(Node node: propNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)){
                    propTypeNodes.put(node.getProperty("name").toString(), node);
                }
                for(Class<? extends Object> klass:KNOWN_PROPERTY_TYPES){
                    String typeName = makePropertyTypeName(klass);
                    List<String> properties = getProperties(klass);
                    if(properties!=null && properties.size()>0){
                        Node propTypeNode = propTypeNodes.get(typeName);
                        if(propTypeNode==null){
                            propTypeNode = neo.createNode();
                            propTypeNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, typeName);
                            propTypeNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, "gis_property_type");
                            savePropertiesToNode(propTypeNode,properties);
                            propNode.createRelationshipTo(propTypeNode, GeoNeoRelationshipTypes.CHILD);
                        } else {
                            TreeSet<String> combinedProperties = new TreeSet<String>();
                            String[] previousProperties = (String[])propTypeNode.getProperty(INeoConstants.NODE_TYPE_PROPERTIES, null);
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
        HashMap<String,Node> valueNodes = new HashMap<String,Node>();
        for(Relationship relation: propTypeNode.getRelationships(GeoNeoRelationshipTypes.PROPERTIES, Direction.OUTGOING)){
            Node valueNode = relation.getEndNode();
            String property = relation.getProperty("property", "").toString();
            valueNodes.put(property, valueNode);
        }
        for(String property: properties){
            Node valueNode = valueNodes.get(property);
            Header header = headers.get(property);
            HashMap<Object,Integer> values = header.values;
            if(values==null) {
                if(valueNode != null){
                    for(Relationship relation: valueNode.getRelationships()){
                        relation.delete();
                    }
                    valueNode.delete();
                }
            }else{
                if(valueNode == null){
                    valueNode = neo.createNode();
                    Relationship relation = propTypeNode.createRelationshipTo(valueNode, GeoNeoRelationshipTypes.PROPERTIES);
                    relation.setProperty("property", property);
                } else {
                    for(Object key: valueNode.getPropertyKeys()) {
                        Integer oldCount = (Integer)valueNode.getProperty(key.toString(),null);
                        if(oldCount == null) {
                            oldCount = 0;
                        }
                        Integer newCount = values.get(key);
                        if(newCount == null) {
                            newCount = 0;
                        }
                        values.put(key,oldCount + newCount);
                    }
                }
                for(Object key: values.keySet()) {
                    valueNode.setProperty(key.toString(), values.get(key));
                }
            }
        }
    }
    
    public static String makePropertyTypeName(Class<? extends Object> klass){
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
    protected final void finishUpGis(Node mainNode) throws MalformedURLException {
        cleanupGisNode();
        if (neoProvider != null) {
            NeoCorePlugin.getDefault().getProjectService().addDataNodeToProject(LoaderUtils.getAweProjectName(), mainNode);
            addDataToCatalog();
        }
    }

    /**
     * This method adds the loaded data to the GIS catalog. The neo-catalog entry is created or updated.
     *
     * @throws MalformedURLException
     */
    protected void addDataToCatalog() throws MalformedURLException {
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
                    && NetworkLoader.confirmLoadNetworkOnMap(map, NeoUtils.getNodeName(gis))) {
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
                NetworkLoader.zoomToLayer(layerList);
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
        for(String key: headers.keySet()){
            Header header = headers.get(key);
            if(header.parseCount>0){
                notify("\t"+header.knownType()+" loaded: "+header.parseCount+" => "+key);
            }
        }
    }

    public void printStats(boolean verbose) {
        printHeaderStats();
        long taken = timeTaken();
        notify("Finished loading " + basename + " data in " + (taken / 1000.0) + " seconds");
    }

}
