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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.neo4j.graphdb.Node;

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
    private Node network = null;
    private final HashMap<String, Node> bsc_s = new HashMap<String, Node>();
    private final HashMap<String, Node> city_s = new HashMap<String, Node>();
    private enum NetworkLevels{
        NETWORK, CITY,BSC,SITE,SECTOR;
    }
    private Set<NetworkLevels> levels=EnumSet.of(NetworkLevels.NETWORK);
    private boolean trimSectorName;
    private long line;
    @Override
    public void init(HeaderTransferData element) {
        super.init(element);
        propertyMap.isEmpty();
        headerNotHandled = true;
        trimSectorName = NeoLoaderPlugin.getDefault().getPreferenceStore().getBoolean(DataLoadPreferences.REMOVE_SITE_NAME);
        line=0l;
    }

    @Override
    public void save(HeaderTransferData element) {
        line++;
        if (headerNotHandled) {
            definePropertyMap(element);
            startMainTx();
            headerNotHandled = false;
            
        }
        String bscField = getStringValue("bsc",element);
        String cityField =getStringValue("city",element);
        String siteField = getStringValue("site",element);
        String sectorField = getStringValue("sector",element);
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
                    city = addChild(network, NodeTypes.CITY, cityName);
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
                    bsc = addChild(city == null ? network : city, NodeTypes.BSC, bscName);
                }
                bsc_s.put(bscField, bsc);
            }
        }

    }




    /**
     *
     */
    protected void startMainTx() {
    }

    private String getStringValue(String key, HeaderTransferData element) {
        String header=propertyMap.get(key);
        if (header==null){
            header=key;
        }
        return element.get(header);
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

                if (Pattern.matches(headerRegExp, header)) {
                    propertyMap.put(newName, header);
                    return;
                }
            }
        }
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
