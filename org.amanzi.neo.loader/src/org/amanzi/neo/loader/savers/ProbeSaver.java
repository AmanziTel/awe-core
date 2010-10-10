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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.GisProperties;
import org.amanzi.neo.loader.AbstractLoader;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * <p>
 * Saver for probe data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class ProbeSaver extends AbstractHeaderSaver<BaseTransferData> {
    private boolean headerNotHandled;

    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
        addNetworkIndexes();
    }

    private void addNetworkIndexes() {
        try {
            addIndex(NodeTypes.PROBE.getId(), service.getLocationIndexProperty(rootname));
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
     * @param element
     */
    protected void saveRow(BaseTransferData element) {
        String probeName = getStringValue("name", element);
        if (probeName == null) {
            error("Probe not stored:\t" + element.getLine());
            return;
        }
        NodeResult probeNode = service.getProbe(rootNode, probeName);
        if (probeNode.isCreated()){
            updateTx(1, 1);
        }
        Double currentLatitude = (Double)probeNode.getProperty(INeoConstants.PROPERTY_LAT_NAME, null);
        Double currentLongitude = (Double)probeNode.getProperty(INeoConstants.PROPERTY_LON_NAME, null);
        Map<String, Object> sectorData = getNotHandledData(element, rootname, NodeTypes.PROBE.getId());

        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            updateProperty(rootname, NodeTypes.PROBE.getId(), probeNode, key, entry.getValue());
        }
        if (currentLatitude != null && currentLongitude != null) {
            setProperty(rootname, NodeTypes.PROBE.getId(), probeNode, INeoConstants.PROPERTY_LAT_NAME, currentLatitude);
            setProperty(rootname, NodeTypes.PROBE.getId(), probeNode, INeoConstants.PROPERTY_LON_NAME, currentLongitude);
            GisProperties gisProperties = getGisProperties(rootNode);
            gisProperties.updateBBox(currentLatitude, currentLongitude);
            if (gisProperties.getCrs() == null) {
                gisProperties.checkCRS(currentLatitude, currentLongitude, null);
                if (gisProperties.getCrs() != null) {
                    CoordinateReferenceSystem crs = AbstractLoader.askCRSChoise(gisProperties);
                    if (crs != null) {
                        gisProperties.setCrs(crs);
                        gisProperties.saveCRS();
                    }
                }
            }
        }
        index(probeNode);
    }

    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, INeoConstants.PROPERTY_LAT_NAME, getPossibleHeaders(DataLoadPreferences.PR_LATITUDE));
        defineHeader(headers, "name", getPossibleHeaders(DataLoadPreferences.PR_NAME));
        defineHeader(headers, INeoConstants.PROPERTY_LON_NAME, getPossibleHeaders(DataLoadPreferences.PR_LONGITUDE));
        defineHeader(headers, "probe_type", getPossibleHeaders(DataLoadPreferences.PR_TYPE));
    }

    @Override
    protected void fillRootNode(Node rootNode, BaseTransferData element) {
        Transaction tx = getService().beginTx();
        try {
            NetworkTypes.PROBE.setTypeToNode(rootNode, getService());
            tx.success();
        } finally {
            tx.finish();
        }
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.NETWORK.getId();
    }

    @Override
    protected String getTypeIdForGisCount(GisProperties gis) {
        return NodeTypes.PROBE.getId();
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
}
