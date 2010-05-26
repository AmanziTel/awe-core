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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.services.events.ShowViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkSiteType;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class was written to handle CSV (tab delimited) network data from ice.net in Sweden. It has
 * been written in a partially generic way so as to be possible to change to various other data
 * sources, but some things are hard coded, like the names of key columns and the assumption of RT90
 * projection for non-angular coordinates. It also assumes the data is structured as
 * BSC->Site->Sector in a tree layout.
 * 
 * @author craig
 */
public class NetworkLoader extends AbstractLoader {
    /**
     * This class handles the CRS specification. Currently it is hard coded to return WGS84
     * (EPSG:4326) for data that looks like lat/long and RT90 2.5 gon V (EPSG:3021) for data that
     * looks like it is in meters and no hints are given. If the user passes a hint, the following
     * are considered:
     * 
     * @author craig
     */
    public static class CRS {
        protected String type = null;
        protected String epsg = null;

        private CRS() {
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return epsg;
        }

        public static CRS fromLocation(float lat, float lon, String hint) {
            CRS crs = new CRS();
            crs.type = "geographic";
            crs.epsg = "EPSG:4326";
            if ((lat > 90 || lat < -90) && (lon > 180 || lon < -180)) {
                crs.type = "projected";
                if (hint != null && hint.toLowerCase().startsWith("germany")) {
                    crs.epsg = "EPSG:31467";
                } else {
                    crs.epsg = "EPSG:3021";
                }
            }
            return crs;
        }

        public static CRS fromCRS(String crsType, String crsName) {
            CRS crs = new CRS();
            crs.type = crsType;
            crs.epsg = crsName;
            return crs;
        }

        /**
         * @param crs
         */
        public static CRS fromCRS(CoordinateReferenceSystem crs) {
            CRS result = new CRS();
            result.type = "geographic";
            result.epsg = crs.getIdentifiers().iterator().next().toString();
            return result;
        }
    }

    private static Pattern channelPattern = Pattern.compile("(^BCCH$)|(^TRX\\d+$)|(^TCH\\d+$)");
    // private Map<Integer, Integer> channalMap;
    private String siteName = null;
    private String bscName = null;
    private String cityName = null;
    private Node site = null;
    private final HashMap<String, Node> bsc_s = new HashMap<String, Node>();
    private final HashMap<String, Node> city_s = new HashMap<String, Node>();
    private Node bsc = null;
    private Node city = null;
    private Node network = null;
    private final ArrayList<String> mainHeaders = new ArrayList<String>();
    public ArrayList<String> shortLines = new ArrayList<String>();
    public ArrayList<String> emptyFields = new ArrayList<String>();
    public ArrayList<String> badFields = new ArrayList<String>();
    public ArrayList<String> lineErrors = new ArrayList<String>();
    private long siteNumber = 0;
    private long sectorNumber = 0;
    private boolean trimSectorName = true;
    private NetworkHeader networkHeader = null;
    private boolean needParceHeader;
    private LuceneIndexService luceneInd;
    private NetworkSiteType sitesType;

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService
     * 
     * @param filename of file to load
     * @param display for opening message dialogs
     * @param dataset to add data to
     */
    public NetworkLoader(String gisName, String filename, Display display) {
        initialize("Network", null, filename, display);
        basename = gisName;
        initializeKnownHeaders();
        luceneInd = NeoServiceProvider.getProvider().getIndexService();
        addNetworkIndexes();
    }

    /**
     * Constructor for loading data in test mode, with no display and NeoService passed
     * 
     * @param neo database to load data into
     * @param filename of file to load
     * @param display
     */
    public NetworkLoader(GraphDatabaseService neo, String filename) {
        initialize("Network", neo, filename, null);
        initializeKnownHeaders();
        luceneInd = NeoServiceProvider.getProvider().getIndexService();
        addNetworkIndexes();
    }

    /**
     * Constructor for loading data in test mode, with no display and NeoService passed
     * 
     * @param neo database to load data into
     * @param filename of file to load
     * @param display
     */
    public NetworkLoader(GraphDatabaseService neo, String filename, LuceneIndexService indexService) {
        initialize("Network", neo, filename, null);
        initializeKnownHeaders();
        addNetworkIndexes();
        if (indexService == null) {
            luceneInd = NeoServiceProvider.getProvider().getIndexService();
        } else {
            luceneInd = indexService;
        }
    }

    /**
     *
     */
    private void addNetworkIndexes() {
        try {
            addIndex(NodeTypes.SITE.getId(), NeoUtils.getLocationIndexProperty(basename));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        needParceHeader = true;
        // addHeaderFilters(new String[] {"time.*", "events", ".*latitude.*", ".*longitude.*",
        // ".*server_report.*",
        // ".*state_machine.*", ".*layer_3_message.*", ".*handover_analyzer.*"});

        // Known headers that are not sector data properties
        addMainHeader("city", getPossibleHeaders(DataLoadPreferences.NH_CITY));
        addMainHeader("msc", getPossibleHeaders(DataLoadPreferences.NH_MSC));
        addMainHeader("bsc", getPossibleHeaders(DataLoadPreferences.NH_BSC));
        addMainHeader("site", getPossibleHeaders(DataLoadPreferences.NH_SITE));
        addMainHeader("sector", getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
        addMainHeader(INeoConstants.PROPERTY_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_CI));
        addMainHeader(INeoConstants.PROPERTY_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_LAC));
        addMainHeader(INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.NH_LATITUDE));
        addMainHeader(INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.NH_LONGITUDE));
        // Stop statistics collection for properties we will not save to the sector
        addNonDataHeaders(1, mainHeaders);

        // force String types on some risky headers (sometimes these look like integers)
        useMapper(1, "site", new StringMapper());
        useMapper(1, "sector", new StringMapper());

        // Known headers that are sector data properties
        addKnownHeader(1, "beamwidth", getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH));
        addKnownHeader(1, "azimuth", getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH));
    }

    /**
     * Add a known header entry as well as mark it as a main header. All other fields will be
     * assumed to be sector properties.
     * 
     * @param key
     * @param regexes
     */
    private void addMainHeader(String key, String[] regexes) {
        addKnownHeader(1, key, regexes);
        mainHeaders.add(key);
    }

    public boolean setup() {
        try {
            trimSectorName = NeoLoaderPlugin.getDefault().getPreferenceStore().getBoolean(DataLoadPreferences.REMOVE_SITE_NAME);
        } catch (Exception e) {
        }
        Node node = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
        network = findOrCreateNetworkNode(node);
        return true;
    }

    /**
     * Returns created Network Node
     * 
     * @return network Node
     */
    public Node getNetworkNode() {
        return network;
    }

    /**
     * After all lines have been parsed, this method is called, allowing the implementing class the
     * opportunity to save any cached information, or write any final statistics. It is not abstract
     * because it is possible, or even probable, to write an importer that does not need it.
     */
    @Override
    protected void finishUp() {
        super.finishUp();
        printWarnings(emptyFields, "empty fields", 0, lineNumber);
        printWarnings(badFields, "field parsing warnings", 10, lineNumber);
        printWarnings(shortLines, "missing fields", 10, lineNumber);
        printWarnings(lineErrors, "uncaught errors", 10, lineNumber);
        Transaction transaction = neo.beginTx();
        try {
            if (networkHeader != null) {
                networkHeader.saveStatistic(network);
            }
            network.setProperty("site_count", siteNumber);
            network.setProperty("sector_count", sectorNumber);
            network.setProperty("bsc_count", bsc_s.size());
            network.setProperty("city_count", city_s.size());
            transaction.success();
        } finally {
            transaction.finish();
        }
        // add network to project and gis node to catalog
        super.cleanupGisNode();

        if (!isTest()) {
            showNetworkTree();
        }
    }

    private void printWarnings(ArrayList<String> warnings, String warning_type, int limit, long lineNumber) {
        if (warnings.size() > 0) {
            info("Had " + warnings.size() + " " + warning_type + " warnings in " + lineNumber + " lines parsed");
            if (limit > 0) {
                int i = 0;
                for (String warning : warnings) {
                    info("\t" + warning);
                    if (i++ > limit) {
                        info("\t... and " + (warnings.size() - 10) + " more ...");
                        break;
                    }
                }
            }
        }
    }

    private void showNetworkTree() {
        // TODO: See if we need this event
        NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDatabaseEvent(UpdateViewEventType.GIS));

        // Lagutko, 21.07.2009, show NeworkTree
        ActionUtil.getInstance().runTask(new Runnable() {
            @Override
            public void run() {
                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new ShowViewEvent(NetworkTreeView.NETWORK_TREE_VIEW_ID));
            }
        }, false);
    }

    /**
     *Returns layer, that contains necessary gis node
     * 
     * @param map map
     * @param gisNode gis node
     * @return layer or null
     */
    public static ILayer findLayerByNode(IMap map, Node gisNode) {
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(Node.class);
                if (resource != null && resource.resolve(Node.class, null).equals(gisNode)) {
                    return layer;
                }
            }
            return null;
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            return null;
        }
    }

    private class NetworkHeader {
        private final Map<String, String> mainKeys = new HashMap<String, String>();
        private String crsHint = null;
        private final ArrayList<String> sectorData = new ArrayList<String>();
        private final Map<String, Integer> channelMap = new HashMap<String, Integer>();
        Map<String, Object> lineData = null;
        private final boolean is3G;

        private NetworkHeader(List<String> fields) {
            lineData = makeDataMap(fields);
            HeaderMaps headerMap = getHeaderMap(1);
            for (String header : lineData.keySet()) {
                String name = headerMap.headerName(header);
                if (mainHeaders.contains(header)) {
                    mainKeys.put(header, name);
                    if (name.toLowerCase().startsWith("wert", 2) && (header.startsWith("lat") || header.startsWith("lon"))) {
                        crsHint = "germany";
                    }
                } else if (channelPattern.matcher(name).matches()) {
                    channelMap.put(header, 0);
                    sectorData.add(header);
                } else {
                    sectorData.add(header);
                }
            }
            is3G = headerMap.headers.keySet().contains("gsm_ne");
        }

        private void setData(List<String> fields) {
            lineData = makeDataMap(fields);
        }

        private void saveStatistic(Node vault) {
            Set<String> channelProperties = new HashSet<String>();
            for (String header : channelMap.keySet()) {
                if (channelMap.get(header) > 0) {
                    channelProperties.add(header);
                }
            }
            vault.setProperty(INeoConstants.PROPERTY_ALL_CHANNELS_NAME, channelProperties.toArray(new String[0]));
        }

        private String getString(String key) {
            Object value = lineData.get(key);
            if (value == null || value instanceof String) {
                return (String)value;
            } else {
                return value.toString();
            }
        }

        private Integer getInteger(String key) {
            Object value = lineData.get(key);
            if (value instanceof Number) {
                return ((Number)value).intValue();
            } else {
                return (Integer)value;
            }
        }

        private Float getFloat(String key) {
            Object value = lineData.get(key);
            if (value instanceof Integer) {
                return ((Integer)value).floatValue();
            } else {
                return (Float)value;
            }
        }

        private Float getLat() {
            return getFloat(INeoConstants.PROPERTY_LAT_NAME);
        }

        private Float getLon() {
            return getFloat(INeoConstants.PROPERTY_LON_NAME);
        }

        private Map<String, Object> getSectorData() {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            for (String key : sectorData) {
                if (lineData.containsKey(key)) {
                    if (channelMap.containsKey(key)) {
                        channelMap.put(key, channelMap.get(key) + 1);
                    }
                    data.put(key, lineData.get(key));
                }
            }
            return data;
        }

        private String getCrsHint() {
            return crsHint;
        }

        /**
         * is 3g network file?
         * 
         * @return
         */
        public boolean is3G() {
            return is3G;
        }
    }

    @Override
    protected void parseLine(String line) {
        debug(line);
        List<String> fields = splitLine(line);
        if (fields.size() < 3)
            return;
        if (this.isOverLimit())
            return;
        Transaction transaction = neo.beginTx();
        try {
            if (networkHeader == null) {
                networkHeader = new NetworkHeader(fields);
                sitesType = networkHeader.is3G() ? NetworkSiteType.SITE_3G : NetworkSiteType.SITE_2G;
            } else {
                networkHeader.setData(fields);
            }
            String bscField = networkHeader.getString("bsc");
            String cityField = networkHeader.getString("city");
            String siteField = networkHeader.getString("site");
            String sectorField = networkHeader.getString("sector");
            if (sectorField == null) {
                lineErrors.add("Missing sector name on line " + lineNumber);
                return;
            }
            if (siteField == null) {
                // lineErrors.add("Missing site name on line " + lineNumber);
                // return;
                siteField = sectorField.substring(0, sectorField.length() - 1);
            }
            // Lagutko, 24.02.2010, sector name can be repeatable (for example 'sector1') so we need
            // additional variable for Lucene Index
            String sectorIndexName = sectorField;
            if (trimSectorName) {
                sectorField = sectorField.replaceAll(siteField + "[\\:\\-]?", "");
            }
            if (cityField != null && !cityField.equals(cityName)) {
                cityName = cityField;
                city = city_s.get(cityField);
                if (city == null) {
                    city = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY), cityName);
                    if (city == null) {
                        debug("New City: " + cityName);
                        city = addChild(network, NodeTypes.CITY, cityName);
                    }
                    city_s.put(cityField, city);
                }
            }
            if (bscField != null && !bscField.equals(bscName)) {
                bscName = bscField;
                bsc = bsc_s.get(bscField);
                if (bsc == null) {
                    bsc = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
                    if (bsc == null) {
                        debug("New BSC: " + bscName);
                        bsc = addChild(city == null ? network : city, NodeTypes.BSC, bscName);
                    }
                    bsc_s.put(bscField, bsc);
                }
            }
            Float latitude = networkHeader.getLat();
            Float longitude = networkHeader.getLon();
            if (!siteField.equals(siteName)) {
                siteName = siteField;
                debug("New site: " + siteName);
                Node siteRoot = bsc == null ? (city == null ? network : city) : bsc;
                Node newSite = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if (newSite != null) {
                    Relationship relation = newSite.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                    Node oldRoot = relation.getOtherNode(newSite);
                    if (!oldRoot.equals(siteRoot)) {
                        relation.delete();
                        siteRoot.createRelationshipTo(newSite, GeoNeoRelationshipTypes.CHILD);
                    }
                } else {

                    if (latitude == 0 && longitude == 0) {
                        // not stored site!
                        return;
                    }
                    newSite = addChild(siteRoot, NodeTypes.SITE, siteName);
                    sitesType.setSiteType(newSite, neo);

                    (site == null ? network : site).createRelationshipTo(newSite, GeoNeoRelationshipTypes.NEXT);
                    site = newSite;
                    siteNumber++;

                }

                GisProperties gisProperties = getGisProperties(basename);
                gisProperties.updateBBox(latitude, longitude);
                if (gisProperties.getCrs() == null) {
                    gisProperties.checkCRS(latitude, longitude, networkHeader.getCrsHint());
                    if (!isTest() && gisProperties.getCrs() != null) {
                        CoordinateReferenceSystem crs = askCRSChoise(gisProperties);
                        if (crs != null) {
                            gisProperties.setCrs(crs);
                            gisProperties.saveCRS();
                        }
                    }
                }
                site.setProperty(INeoConstants.PROPERTY_LAT_NAME, latitude.doubleValue());
                site.setProperty(INeoConstants.PROPERTY_LON_NAME, longitude.doubleValue());

                index(site);
            }
            debug("New Sector: " + sectorField);
            // TODO check by necessary sector
            Integer ci = networkHeader.getInteger(INeoConstants.PROPERTY_SECTOR_CI);
            Integer lac = networkHeader.getInteger(INeoConstants.PROPERTY_SECTOR_LAC);
            // Node sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename,
            // INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorIndexName);
            Node sector = NeoUtils.findSector(basename, ci, lac, sectorIndexName, true, luceneInd, neo);
            if (sector != null) {
                // TODO check
            } else {
                sector = addChild(site, NodeTypes.SECTOR, sectorField, sectorIndexName);
                if (ci != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_CI, ci);
                    luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR), ci);
                }
                if (lac != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_LAC, lac);
                    luceneInd.index(sector, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR), lac);
                }
            }
            // TODO: deprecated sectorNumber in favour of saved data
            sectorNumber++;
            // header.parseLine(sector, fields);
            Map<String, Object> sectorData = networkHeader.getSectorData();

            for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
                String key = entry.getKey();
                sector.setProperty(key, entry.getValue());
            }
            getGisProperties(basename).incSaved();
            transaction.success();
            // return true;
        } catch (Exception e) {
            lineErrors.add("Error parsing line " + lineNumber + ": " + e);
            error(lineErrors.get(lineErrors.size() - 1));
            if (lineErrors.size() == 1) {
                e.printStackTrace(System.err);
            } else if (lineErrors.size() > 10) {
                e.printStackTrace(System.err);
                // return false;
            }
        } finally {
            transaction.finish();
        }
    }

    private Node addChild(Node parent, NodeTypes type, String name) {
        return addChild(parent, type, name, name);
    }

    /**
     * This code expects you to create a transaction around it, so don't forget to do that.
     * 
     * @param parent
     * @param type
     * @param name
     * @return
     */
    private Node addChild(Node parent, NodeTypes type, String name, String indexName) {
        Node child = null;
        child = neo.createNode();
        child.setProperty(INeoConstants.PROPERTY_TYPE_NAME, type.getId());
        child.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
        child.setProperty(INeoConstants.PROPERTY_SECTOR_NAME, indexName);
        luceneInd.index(child, NeoUtils.getLuceneIndexKeyByProperty(basename, INeoConstants.PROPERTY_NAME_NAME, type), indexName);
        if (parent != null) {
            parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            debug("Added '" + name + "' as child of '" + parent.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        }
        return child;
    }

    @Override
    public void printStats(boolean verbose) {
        if (network != null) {
            if (verbose) {
                Transaction tx = neo.beginTx();
                try {
                    printChildren(network, 0);
                    tx.success();
                } finally {
                    tx.finish();
                }
            }
            info("Finished loading " + siteNumber + " sites and " + sectorNumber + " sectors from " + lineNumber + " lines");
        } else {
            error("No network node found");
        }
    }

    private void printChildren(Node node, int depth) {
        if (node == null || depth > 4 || !node.hasProperty(INeoConstants.PROPERTY_NAME_NAME))
            return;
        StringBuffer tab = new StringBuffer();
        for (int i = 0; i < depth; i++)
            tab.append("    ");
        StringBuffer properties = new StringBuffer();
        for (String property : node.getPropertyKeys()) {
            if (!property.equals(INeoConstants.PROPERTY_NAME_NAME))
                properties.append(" - ").append(property).append(" => ").append(node.getProperty(property));
        }
        info(tab.toString() + node.getProperty(INeoConstants.PROPERTY_NAME_NAME) + properties);
        for (Relationship relationship : node.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
            // debug(tab.toString()+"("+relationship.toString()+") - "+relationship.getStartNode().getProperty("name")+" -("+relationship.getType()+")-> "+relationship.getEndNode().getProperty("name"));
            printChildren(relationship.getEndNode(), depth + 1);
        }
    }

    /**
     * A main method for useful quick-turn-around testing and debugging of data parsing on various
     * sample files.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // NeoLoaderPlugin.debug = true;
        if (args.length < 1)
            args = new String[] {"amanzi/network.txt"};
        EmbeddedGraphDatabase neo = new EmbeddedGraphDatabase("../../testing/neo");
        try {
            for (String filename : args) {
                long startTime = System.currentTimeMillis();
                NetworkLoader networkLoader = new NetworkLoader(neo, filename);
                networkLoader.setup();
                networkLoader.setLimit(1000);
                networkLoader.setCommitSize(1000);
                networkLoader.run(null);
                networkLoader.printStats(true);
                networkLoader.info("Ran test in " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
            }
        } catch (IOException e) {
            System.err.println("Failed to load network: " + e);
            e.printStackTrace(System.err);
        } finally {
            neo.shutdown();
        }
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return getNetworkNode();
    }

    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.SECTOR.getId();
    }

    @Override
    public Node[] getRootNodes() {
        return new Node[] {getNetworkNode()};
    }

    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }
}
