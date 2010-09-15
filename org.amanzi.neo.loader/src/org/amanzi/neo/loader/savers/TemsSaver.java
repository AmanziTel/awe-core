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
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.loader.core.parser.HeaderTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class TemsSaver extends AbstractHeaderSaver<HeaderTransferData>{

    private boolean headerNotHandled;

    @Override
    public void save(HeaderTransferData element) {
        if (headerNotHandled) {
            startMainTx();
            initializeIndexes();
            headerNotHandled = false;
        }
        if (element.getLine()<3){
            //redefine property header for each element(file)
            propertyMap.clear();
            definePropertyMap(element);
        }
        
    }
    private static final String MS_KEY = "ms";
    private void addDriveIndexes() {
        try {
            String virtualDatasetName = DriveTypes.MS.getFullDatasetName(rootname);
            addIndex(NodeTypes.M.getId(), service.getTimeIndexProperty(rootname));
            addIndex(INeoConstants.HEADER_MS, service.getTimeIndexProperty(virtualDatasetName));
            addIndex(NodeTypes.MP.getId(), service.getLocationIndexProperty(rootname));
            addMappedIndex(MS_KEY, NodeTypes.MP.getId(), service.getLocationIndexProperty(virtualDatasetName));
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    /**
     *
     * @param element
     */
    private void definePropertyMap(HeaderTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, INeoConstants.PROPERTY_BCCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_BCCH));
        defineHeader(headers, INeoConstants.PROPERTY_TCH_NAME, getPossibleHeaders(DataLoadPreferences.DR_TCH));
        defineHeader(headers, INeoConstants.PROPERTY_SC_NAME, getPossibleHeaders(DataLoadPreferences.DR_SC));
        defineHeader(headers, INeoConstants.PROPERTY_PN_NAME, getPossibleHeaders(DataLoadPreferences.DR_PN));
        defineHeader(headers, INeoConstants.PROPERTY_EcIo_NAME, getPossibleHeaders(DataLoadPreferences.DR_EcIo));
        defineHeader(headers, INeoConstants.PROPERTY_RSSI_NAME, getPossibleHeaders(DataLoadPreferences.DR_RSSI));
        defineHeader(headers, INeoConstants.PROPERTY_CI_NAME, getPossibleHeaders(DataLoadPreferences.DR_CI));
        defineHeader(headers, "parsedLongitude", getPossibleHeaders(DataLoadPreferences.DR_LONGITUDE));
        defineHeader(headers, "parsedLatitude", getPossibleHeaders(DataLoadPreferences.DR_LATITUDE));
        defineHeader(headers, "ms",new String[]{"MS","ms"});
        defineHeader(headers, "message_type",new String[]{"message_type","Message Type"});
        defineHeader(headers, "event",new String[]{"Event Type","event_type"});
        defineHeader(headers, INeoConstants.SECTOR_ID_PROPERTIES,new String[]{".*Cell Id.*"});
        defineHeader(headers, "time",new String[]{"Timestamp","timestamp"});
        defineHeader(headers, "all_rxlev_full",new String[]{"All-RxLev Full","all_rxlev_full"});
        defineHeader(headers, "all_rxlev_sub",new String[]{"All-RxLev Sub", "all_rxlev_sub"});
        defineHeader(headers, "all_rxqual_full",new String[]{"All-RxQual Full", "all_rxqual_full"});
        defineHeader(headers, "all_rxqual_sub",new String[]{"All-RxQual Sub", "all_rxqual_sub"});
        defineHeader(headers, "all_sqi",new String[]{"All-SQI", "all_sqi"});
        defineHeader(headers, "all_sqi_mos",new String[]{"All-SQI MOS", "all_sqi_mos"});
        addAnalysedNodeTypes(element.getRootName(), ALL_NODE_TYPES);

    }
    @Override
    public void init(HeaderTransferData element) {
        super.init(element);
        headerNotHandled=true;
        addDriveIndexes();
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
    protected void fillRootNode(Node rootNode, HeaderTransferData element) {
        DriveTypes.TEMS.setTypeToNode(rootNode, getService());
    }

    @Override
    protected String getRootNodeType() {
        return NodeTypes.DATASET.getId();
    }

}
