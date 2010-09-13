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

package org.amanzi.neo.loader.savers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.AbstractLoader;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkSaver extends AbstractHeaderSaver<HeaderTransferData> {
    protected Map<String, String> propertyMap = new HashMap<String, String>();
    private boolean headerNotHandled;
    private boolean is3G;
    private String siteName = null;
    private String bscName = null;
    private String cityName = null;
    private Node site = null;
    private Node bsc = null;
    private Node city = null;
    private final HashMap<String, Node> bsc_s = new HashMap<String, Node>();
    private final HashMap<String, Node> city_s = new HashMap<String, Node>();

    private enum NetworkLevels {
        NETWORK, CITY, BSC, SITE, SECTOR;
    }

    private Set<NetworkLevels> levels = EnumSet.of(NetworkLevels.NETWORK);
    private boolean trimSectorName;
    private long line;

    @Override
    public void init(HeaderTransferData element) {
        super.init(element);
        propertyMap.isEmpty();
        headerNotHandled = true;
        trimSectorName = NeoLoaderPlugin.getDefault().getPreferenceStore().getBoolean(DataLoadPreferences.REMOVE_SITE_NAME);
        line = 0l;
    }

    @Override
    public void save(HeaderTransferData element) {
        line++;
        if (headerNotHandled) {
            definePropertyMap(element);
            startMainTx();
            headerNotHandled = false;

        }
        try {
            String bscField = getStringValue("bsc", element);
            String cityField = getStringValue("city", element);
            String siteField = getStringValue("site", element);
            String sectorField = getStringValue("sector", element);
            if (sectorField == null) {
                error("Missing sector name on line " + line);
                return;
            }

            if (siteField == null) {
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
                    city = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY), cityName);
                    if (city == null) {
                        city = addChild(rootNode, NodeTypes.CITY, cityName);
                        indexStat(rootname, city);
                    }
                    city_s.put(cityField, city);
                }
            }
            if (bscField != null && !bscField.equals(bscName)) {
                if (!levels.contains(NetworkLevels.BSC)) {
                    levels.add(NetworkLevels.BSC);
                }
                bscName = bscField;
                bsc = bsc_s.get(bscField);
                if (bsc == null) {
                    bsc = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
                    if (bsc == null) {
                        bsc = addChild(city == null ? rootNode : city, NodeTypes.BSC, bscName);
                        indexStat(rootname, bsc);
                    }
                    bsc_s.put(bscField, bsc);
                }
            }
            Double lat = getNumberValue(Double.class, INeoConstants.PROPERTY_LAT_NAME, element);
            Double lon = getNumberValue(Double.class, INeoConstants.PROPERTY_LAT_NAME, element);
            if (lat == null) {
                lat = 0d;
            }
            if (lon == null) {
                lon = 0d;
            }
            if (!siteField.equals(siteName)) {
                siteName = siteField;
                Node siteRoot = bsc == null ? (city == null ? rootNode : city) : bsc;
                Node newSite = getIndexService().getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if (newSite != null) {
                    Relationship relation = newSite.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                    Node oldRoot = relation.getOtherNode(newSite);
                    if (!oldRoot.equals(siteRoot)) {
                        // TODO not work in batchinserter?
                        getService().delete(relation);
                        siteRoot.createRelationshipTo(newSite, GeoNeoRelationshipTypes.CHILD);
                    }
                } else {

                    if (lat == 0 && lon == 0) {
                        // not stored site!
                        return;
                    }
                    newSite = addChild(siteRoot, NodeTypes.SITE, siteName);
                    (site == null ? rootNode : site).createRelationshipTo(newSite, GeoNeoRelationshipTypes.NEXT);
                    site = newSite;
                }

                GisProperties gisProperties = getGisProperties(rootNode);
                gisProperties.updateBBox(lat, lon);
                if (gisProperties.getCrs() == null) {
                    gisProperties.checkCRS(lat, lon, null);
                    if (gisProperties.getCrs() != null) {
                        CoordinateReferenceSystem crs = AbstractLoader.askCRSChoise(gisProperties);
                        if (crs != null) {
                            gisProperties.setCrs(crs);
                            gisProperties.saveCRS();
                        }
                    }
                }
                site.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
                site.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);

                index(site);
            }
            Integer ci = getNumberValue(Integer.class, INeoConstants.PROPERTY_SECTOR_CI, element);
            Integer lac = getNumberValue(Integer.class, INeoConstants.PROPERTY_SECTOR_LAC, element);
            // Node sector = luceneInd.getSingleNode(NeoUtils.getLuceneIndexKeyByProperty(basename,
            // INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR), sectorIndexName);
            Node sector = NeoUtils.findSector(rootNode, ci, lac, sectorIndexName, true, getIndexService(), getService());
            if (sector != null) {
                // TODO check
            } else {
                sector = addChild(site, NodeTypes.SECTOR, sectorField, sectorIndexName);
                if (ci != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_CI, ci);
                    getIndexService().index(sector, NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR), ci);
                }
                if (lac != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_LAC, lac);
                    getIndexService().index(sector, NeoUtils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR), lac);
                }
            }
            Double beamwith = getNumberValue(Double.class, "beamwidth", element);
            if (beamwith!=null){
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,"beamwidth",beamwith);
            }
            Double azimuth = getNumberValue(Double.class, "azimuth", element);
            if (azimuth!=null){
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,"azimuth",azimuth);
            }
            // header.parseLine(sector, fields);
            Map<String, Object> sectorData = getSectorData(element);

            for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
                String key = entry.getKey();
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,key,entry.getValue());
            }
            index(sector);
            getGisProperties(rootNode).updateBBox(lat, lon);
            getGisProperties(rootNode).incSaved();
//            updateTx(0,0);
            // return true;
        } catch (Exception e) {
            exception("Error parsing line " + line + ": ", e);
        }
    }

    /*
     * @param element
     * @return
     */
    protected Map<String, Object> getSectorData(HeaderTransferData element) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : element.keySet()) {
            if (!propertyMap.values().contains(key)) {
                Object value=statistic.parseValue(rootname,NodeTypes.SECTOR.getId(),key,element.get(key));
                if(value!=null){
                    result.put(key, value);
                }
                continue;
            }
        }
        return result;
    }

    protected String getStringValue(String key, HeaderTransferData element) {
        String header = propertyMap.get(key);
        if (header == null) {
            header = key;
        }
        return element.get(header);
    }

    /**
     * Gets the number value.
     * 
     * @param <T> the generic type
     * @param klass the klass
     * @param key the key
     * @param element the element
     * @return the number value
     */

    protected <T extends Number> T getNumberValue(Class<T> klass, String key, HeaderTransferData element) {
        String value = getStringValue(key, element);
        try {
            return NeoUtils.getNumberValue(klass, value);
        } catch (SecurityException e) {
            exception(e);
        } catch (IllegalArgumentException e) {
            exception(e);
        } catch (NoSuchMethodException e) {
            exception(e);
        } catch (IllegalAccessException e) {
            exception(e);
        } catch (InvocationTargetException e) {
            exception(e);
        }
        return null;

    }


    /**
     * @param element
     */
    private void definePropertyMap(HeaderTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "city", getPossibleHeaders(DataLoadPreferences.NH_CITY));
        defineHeader(headers, "msc", getPossibleHeaders(DataLoadPreferences.NH_MSC));
        defineHeader(headers, "bsc", getPossibleHeaders(DataLoadPreferences.NH_BSC));
        defineHeader(headers, "site", getPossibleHeaders(DataLoadPreferences.NH_SITE));
        defineHeader(headers, "sector", getPossibleHeaders(DataLoadPreferences.NH_SECTOR));
        defineHeader(headers, INeoConstants.PROPERTY_SECTOR_CI, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_CI));
        defineHeader(headers, INeoConstants.PROPERTY_SECTOR_LAC, getPossibleHeaders(DataLoadPreferences.NH_SECTOR_LAC));
        defineHeader(headers, INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.NH_LATITUDE));
        defineHeader(headers, INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.NH_LONGITUDE));
        defineHeader(headers, "beamwidth", getPossibleHeaders(DataLoadPreferences.NH_BEAMWIDTH));
        defineHeader(headers, "azimuth", getPossibleHeaders(DataLoadPreferences.NH_AZIMUTH));
        is3G = element.keySet().contains("gsm_ne");
        addAnalysedNodeTypes(element.getRootName(), ALL_NODE_TYPES);
    }

    /**
     * @param headers
     * @param string
     * @param possibleHeaders
     */
    private void defineHeader(Set<String> headers, String newName, String[] possibleHeaders) {
        if (possibleHeaders == null) {
            return;
        }
        for (String header : headers) {
            if (propertyMap.values().contains(header)) {
                continue;
            }
            for (String headerRegExp : possibleHeaders) {
                Pattern pat=Pattern.compile(headerRegExp,Pattern.CASE_INSENSITIVE);
                Matcher match = pat.matcher(header);
                if (match.matches()) {
                    propertyMap.put(newName, header);
                    return;
                }
            }
        }
    }
public static void main(String[] args) {
    Pattern pat=Pattern.compile("lat.*",Pattern.CASE_INSENSITIVE);
    Matcher mat = pat.matcher("Lat");
    System.out.println(mat.matches());
}
    @Override
    protected void fillRootNode(Node rootNode, HeaderTransferData element) {

    }

    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    protected String[] getPossibleHeaders(String key) {
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

}
