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

package org.amanzi.neo.loader.core.saver.impl;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.preferences.PreferenceStore;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.services.Utils;
import org.geotools.referencing.CRS;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Network saver
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NetworkSaver extends AbstractHeaderSaver<BaseTransferData> {

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
    private CoordinateReferenceSystem crs;

    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
        trimSectorName = PreferenceStore.getPreferenceStore().getValue(DataLoadPreferences.REMOVE_SITE_NAME);
        addNetworkIndexes();
        String crsWkt =element.get("CRS");
        if (crsWkt!=null){
            try {
                crs=CRS.parseWKT(crsWkt);
            } catch (FactoryException e) {
                exception(e);
                crs=null;
            }
        }
        
    }
    private void addNetworkIndexes() {
        try {
            addIndex(NodeTypes.SITE.getId(),service.getLocationIndexProperty(rootname));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }


    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled) {
            definePropertyMap(element);
            startMainTx(1000);
            initializeIndexes();
            headerNotHandled = false;

        }
        saveRow(element);
    }
    
    /**
     * Save row.
     *
     * @param element the element
     */
    protected void saveRow(BaseTransferData element) {
        try {
            String bscField = getStringValue("bsc", element);
            String cityField = getStringValue("city", element);
            String siteField = getStringValue("site", element);
            String sectorField = getStringValue("sector", element);
            if (sectorField == null) {
                error("Missing sector name on line " + element.getLine());
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
                    city = getIndexService().getSingleNode(Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.CITY), cityName);
                    if (city == null) {
                        city = addSimpleChild(rootNode, NodeTypes.CITY, cityName);
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
                    bsc = getIndexService().getSingleNode(Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.BSC), bscName);
                    if (bsc == null) {
                        bsc = addSimpleChild(city == null ? rootNode : city, NodeTypes.BSC, bscName);
                    }
                    bsc_s.put(bscField, bsc);
                }
            }
            Double lat = getNumberValue(Double.class, INeoConstants.PROPERTY_LAT_NAME, element);
            Double lon = getNumberValue(Double.class, INeoConstants.PROPERTY_LON_NAME, element);
            if (lat == null) {
                lat = 0d;
            }
            if (lon == null) {
                lon = 0d;
            }
            if (!siteField.equals(siteName)) {
                siteName = siteField;
                Node siteRoot = bsc == null ? (city == null ? rootNode : city) : bsc;
                Node newSite = getIndexService().getSingleNode(Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SITE), siteName);
                if (newSite != null) {
                    Relationship relation = newSite.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                    Node oldRoot = relation.getOtherNode(newSite);
                    if (!oldRoot.equals(siteRoot)) {
                        // TODO check work in batchinserter
                        getService().delete(relation);
                        siteRoot.createRelationshipTo(newSite, GeoNeoRelationshipTypes.CHILD);
                    }
                } else {

                    if (lat == 0 && lon == 0) {
                        // not stored site!
                        return;
                    }
                    newSite = addSimpleChild(siteRoot, NodeTypes.SITE, siteName);
                    (site == null ? rootNode : site).createRelationshipTo(newSite, GeoNeoRelationshipTypes.NEXT);
                }
                site = newSite;

                GisProperties gisProperties = getGisProperties(rootNode);
                gisProperties.updateBBox(lat, lon);
                if (gisProperties.getCrs() == null) {
                    if (crs==null){
                    gisProperties.checkCRS(lat, lon, null);
                    }else{
                      gisProperties.setCrs(crs);
                      gisProperties.saveCRS();                        
                    }
//                    if (gisProperties.getCrs() != null) {
//                        CoordinateReferenceSystem crs = AbstractLoader.askCRSChoise(gisProperties);
//                        if (crs != null) {
//                            gisProperties.setCrs(crs);
//                            gisProperties.saveCRS();
//                        }
//                    }
                }
                site.setProperty(INeoConstants.PROPERTY_LAT_NAME, lat);
                site.setProperty(INeoConstants.PROPERTY_LON_NAME, lon);

                index(site);
            }
            Integer ci = getNumberValue(Integer.class, INeoConstants.PROPERTY_SECTOR_CI, element);
            Integer lac = getNumberValue(Integer.class, INeoConstants.PROPERTY_SECTOR_LAC, element);
            Node sector = service.findSector(rootNode, ci, lac, sectorIndexName, true);
            if (sector != null) {
                // TODO check
            } else {
                sector = addSimpleChild(site, NodeTypes.SECTOR, sectorField);
                sector.setProperty("sector_id", sectorIndexName);
                service.indexByProperty(rootNode.getId(), sector, "sector_id");
                if (ci != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_CI, ci);
                    getIndexService().index(sector, Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_SECTOR_CI, NodeTypes.SECTOR), ci);
                }
                if (lac != null) {
                    sector.setProperty(INeoConstants.PROPERTY_SECTOR_LAC, lac);
                    getIndexService().index(sector, Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_SECTOR_LAC, NodeTypes.SECTOR), lac);
                }
            }
            Integer beamwith = getNumberValue(Integer.class, "beamwidth", element);
            if (beamwith!=null){
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,"beamwidth",beamwith);
            }
            Integer azimuth = getNumberValue(Integer.class, "azimuth", element);
            if (azimuth!=null){
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,"azimuth",azimuth);
            }
            // header.parseLine(sector, fields);
            Map<String, Object> sectorData = getNotHandledData(element,rootname,NodeTypes.SECTOR.getId());

            for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
                String key = entry.getKey();
                updateProperty(rootname,NodeTypes.SECTOR.getId(),sector,key,entry.getValue());
            }
            index(sector);
            getGisProperties(rootNode).updateBBox(lat, lon);
//            updateTx(0,0);
            // return true;
        } catch (Exception e) {
            exception("Error parsing line " + element.getLine() + ": ", e);
        }
    }








    /**
     * @param element
     */
    protected void definePropertyMap(BaseTransferData element) {
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




    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {

    }



    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }
    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.SECTOR.getId();
    }

}
