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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.ui.preferences.DataLoadPreferences;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.lucene.LuceneIndexService;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Network Site loader
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkSiteLoader extends AbstractLoader {
    
    /** String SITE_ID_KEY field. */
    private static final String SITE_ID_KEY = "site_id";
    
    /** The main headers. */
    private final ArrayList<String> mainHeaders = new ArrayList<String>();
    
    /** The network header. */
    private NetworkHeader networkHeader = null;
    
    /** The line errors. */
    public ArrayList<String> lineErrors = new ArrayList<String>();
    
    /** The network node. */
    private final Node networkNode;
    
    /** The need parce header. */
    private boolean needParceHeader;
    
    /** The lucene ind. */
    private final LuceneIndexService luceneInd;

    /**
     * Constructor for loading data in AWE, with specified display and dataset, but no NeoService.
     *
     * @param gisName the gis name
     * @param filename of file to load
     * @param display for opening message dialogs
     */
    public NetworkSiteLoader(String gisName, String filename, Display display) {
        initialize("Network", null, filename, display);
        basename = gisName;
        Node gis = findOrCreateGISNode(basename, GisTypes.NETWORK.getHeader(), NetworkTypes.RADIO);
        networkNode = findOrCreateNetworkNode(gis);
        luceneInd = NeoServiceProvider.getProvider().getIndexService();
        initializeKnownHeaders();
    }

    /**
     * Parses the line.
     *
     * @param line the line
     */
    @Override
    protected void parseLine(String line) {
        List<String> fields = splitLine(line);
        if (fields.size() < 3)
            return;
        if (this.isOverLimit())
            return;
        Transaction transaction = neo.beginTx();
        try {
            if (networkHeader == null) {
                networkHeader = new NetworkHeader(fields);
            } else {
                networkHeader.setData(fields);
            }
            String siteField = networkHeader.getString(SITE_ID_KEY);

            if (siteField == null) {
                lineErrors.add("Missing sector name on line " + lineNumber);
                return;
            }

            Node siteNode = findOrCreateSiteNode(siteField);
            if (siteNode == null) {
                lineErrors.add("Missing sector name on line " + lineNumber);
                return;
            }
            // header.parseLine(sector, fields);
            Map<String, Object> sectorData = networkHeader.getSiteData();
            for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
                String key = entry.getKey();
                if ("type".equals(key)) {
                    key = "site_type";
                }
                siteNode.setProperty(key, entry.getValue());
                if (key.equals(INeoConstants.PROPERTY_SITE_NO)){
                    luceneInd.index(siteNode, NeoUtils.getLuceneIndexKeyByProperty( getStoringNode(1), INeoConstants.PROPERTY_SITE_NO, NodeTypes.SITE), entry.getValue().toString());
                }
            }
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

    /**
     * finds or create site node.
     *
     * @param siteField site name
     * @return site node.
     */
    private Node findOrCreateSiteNode(final String siteField) {
        Node site = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(getStoringNode(1), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteField);
        if (site == null) {
            //create and link as child of networkNode
            Transaction tx = neo.beginTx();
            try {
                site = neo.createNode();
                site.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.SITE.getId());
                site.setProperty(INeoConstants.PROPERTY_NAME_NAME, siteField);
                luceneInd.index(site, NeoUtils.getLuceneIndexKeyByProperty(getStoringNode(1), INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteField);
                networkNode.createRelationshipTo(site, NetworkRelationshipTypes.CHILD);
                storingProperties.values().iterator().next().incSaved();
                gisNodes.values().iterator().next().incSaved();

                debug("Added '" + siteField + "' as child of '" + networkNode.getProperty(INeoConstants.PROPERTY_NAME_NAME));
            } finally {
                tx.finish();
            }

        }
        return site;
    }

    /**
     * Add a known header entry as well as mark it as a main header. All other fields will be
     * assumed to be sector properties.
     *
     * @param key the key
     * @param regexes the regexes
     */
    private void addMainHeader(String key, String[] regexes) {
        addKnownHeader(1, key, regexes, false);
        mainHeaders.add(key);
    }

    /**
     * Build a map of internal header names to format specific names for types that need to be known
     * in the algorithms later.
     */
    private void initializeKnownHeaders() {
        needParceHeader = true;
        // Known headers that are not sector data properties
        addMainHeader(SITE_ID_KEY, getPossibleHeaders(DataLoadPreferences.NH_SITE));
        // addMainHeader("type", new String[] {"TYPE"});
        // Stop statistics collection for properties we will not save to the sector
        addNonDataHeaders(1, mainHeaders);

        // force String types on some risky headers (sometimes these look like integers)
        useMapper(1, SITE_ID_KEY, new StringMapper());

        // Known headers that are sector data properties
        addKnownHeader(1, "beamwidth", getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH), false);
        addKnownHeader(1, "azimuth", getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH), false);
    }

    /**
     * The Class NetworkHeader.
     */
    private class NetworkHeader {
        
        /** The main keys. */
        private final Map<String, String> mainKeys = new HashMap<String, String>();
        
        /** The site data. */
        private final ArrayList<String> siteData = new ArrayList<String>();
        
        /** The line data. */
        Map<String, Object> lineData = null;

        /**
         * Instantiates a new network header.
         *
         * @param fields the fields
         */
        private NetworkHeader(List<String> fields) {
            lineData = makeDataMap(fields);
            HeaderMaps headerMap = getHeaderMap(1);
            for (String header : lineData.keySet()) {
                String name = headerMap.headerName(header);
                if (mainHeaders.contains(header)) {
                    mainKeys.put(header, name);
                } else {
                    siteData.add(header);
                }
            }
        }

        /**
         * Sets the data.
         *
         * @param fields the new data
         */
        private void setData(List<String> fields) {
            lineData = makeDataMap(fields);
        }

        /**
         * Gets the site data.
         *
         * @return the site data
         */
        private Map<String, Object> getSiteData() {
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            for (String key : siteData) {
                if (lineData.containsKey(key)) {
                    data.put(key, lineData.get(key));
                }
            }
            return data;
        }

        /**
         * Gets the string.
         *
         * @param key the key
         * @return the string
         */
        private String getString(String key) {
            Object value = lineData.get(key);
            if (value == null || value instanceof String) {
                return (String)value;
            } else {
                return value.toString();
            }
        }
    }

    /**
     * Gets the storing node.
     *
     * @param key the key
     * @return the storing node
     */
    @Override
    protected Node getStoringNode(Integer key) {
        // because gis node we use for sector storing node root, for site property we use
        // networkNode
        return NeoUtils.findOrCreateVirtualDatasetNode(networkNode, getVitrualDatasetName(), neo);
    }

    @Override
    public Node[] getRootNodes() {
        return new Node[]{networkNode};
    }
    @Override
    protected String getPrymaryType(Integer key) {
        return NodeTypes.SITE.getId();
    }
    /**
     * Gets the vitrual dataset name.
     * 
     * @return the vitrual dataset name
     */
    private String getVitrualDatasetName() {
        return String.format("Site datasets of network '%s'",basename);
    }

    /**
     * Need parce headers.
     *
     * @return true, if successful
     */
    @Override
    protected boolean needParceHeaders() {
        if (needParceHeader) {
            needParceHeader = false;
            return true;
        }
        return false;
    }
    /**
     * After all lines have been parsed, this method is called, allowing the implementing class the
     * opportunity to save any cached information, or write any final statistics. It is not abstract
     * because it is possible, or even probable, to write an importer that does not need it.
     */
    @Override
    protected void finishUp() {
        super.finishUp();
        super.cleanupGisNode();//(datasetNode == null ? file : datasetNode);
    }

}
