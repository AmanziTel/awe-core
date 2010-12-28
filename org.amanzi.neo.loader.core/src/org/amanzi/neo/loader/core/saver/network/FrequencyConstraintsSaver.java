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

package org.amanzi.neo.loader.core.saver.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.*;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class FrequencyConstraintsSaver extends AbstractHeaderSaver<BaseTransferData> {

    private static final String SECTOR = "Sector";
    private static final String TRX_ID = "TRX_ID";
    private static final String CHANNEL_TYPE = "ChannelType";
    private static final String FREQUENCY = "Frequency";
    private static final String TYPE = "Type";
    private static final String PENALTY = "Penalty";
    
    private boolean headerNotHandled;
    private String networkName;
    private NetworkModel networkModel;
    
    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
        networkModel = new NetworkModel(rootNode);
    }
    
    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled) {
            networkName = element.getFileName();
            definePropertyMap(element);
            startMainTx(1000);
            headerNotHandled = false;
        }
        saveRow(element);
    }
    
    protected void saveRow(BaseTransferData element) {
        // list of trxNode
        ArrayList<Node> listTRX = new ArrayList<Node>();
        
        String sectorName = getStringValue(SECTOR, element);
        String trxId = getStringValue(TRX_ID, element);
        
        // properties to trxNode
        String channelType = getStringValue(CHANNEL_TYPE, element);
        Integer frequency = getNumberValue(Integer.class, FREQUENCY, element);
        Byte type = getNumberValue(Byte.class, TYPE, element);
        Double penalty = getNumberValue(Double.class, PENALTY, element);
        
        //find sector
        Node sector = service.findSector(rootNode, null, null, sectorName, true);
        
        if (sector != null) {
            // get list of trxNodes. It may contains one or some trxNodes
            if (trxId.equals("*")) {
                listTRX = networkModel.getCarriers(sector);
            }
            else {
                listTRX.add(networkModel.getCarrier(sector, Integer.parseInt(trxId)));
            }
            
            if (listTRX.size() != 0) {
                for (Node trx : listTRX){
                    trx.setProperty(CHANNEL_TYPE, channelType);
                    trx.setProperty(FREQUENCY, frequency);
                    trx.setProperty(TYPE, type);
                    trx.setProperty(PENALTY, penalty);
                    updateProperty(networkName, NodeTypes.TRX.getId(), trx, CHANNEL_TYPE, channelType);
                    updateProperty(networkName, NodeTypes.TRX.getId(), trx, FREQUENCY, frequency);
                    updateProperty(networkName, NodeTypes.TRX.getId(), trx, TYPE, type);
                    updateProperty(networkName, NodeTypes.TRX.getId(), trx, PENALTY, penalty);
                }
            }
            else {
                // TODO: need write "trxNode not found" to outputStream
            }
        }
        else {
            // TODO: need write "sector not found" to outputStream
        }
    }

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, SECTOR, new String[] {"Sector"});
        defineHeader(headers, TRX_ID, new String[] {"TRX_ID"});
        defineHeader(headers, CHANNEL_TYPE, new String[] {"ChannelType"});
        defineHeader(headers, FREQUENCY, new String[] {"Frequency"});
        defineHeader(headers, TYPE, new String[] {"Type"});
        defineHeader(headers, PENALTY, new String[] {"Penalty"});
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[0]);
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
