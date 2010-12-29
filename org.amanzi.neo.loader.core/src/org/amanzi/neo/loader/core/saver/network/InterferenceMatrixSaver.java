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

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.GisProperties;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Interference Matrix saver
 * </p>
 * .
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class InterferenceMatrixSaver extends AbstractHeaderSaver<BaseTransferData> {

    /** The header not handled. */
    private boolean headerNotHandled;
    private NodeToNodeRelationModel imModel;
    private NetworkModel networkModel;
    private String neighbourName;

    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        propertyMap.clear();
        headerNotHandled = true;
    }

    @Override
    public void save(BaseTransferData element) {
        if (headerNotHandled) {
            networkModel = new NetworkModel(rootNode);
            neighbourName = element.getFileName();
            imModel = networkModel.getInterferenceMatrix(neighbourName);

            definePropertyMap(element);
            startMainTx(1000);
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
        String name = getStringValue("serv_sector_name", element);
        
        Node serSector = networkModel.findSector(name);
        if (serSector == null) {
            error(String.format("Line %s not saved. Not found serve sector.", element.getLine()));
            return;
        }
        name = getStringValue("neigh_sector_name", element);
        Node neighSector = networkModel.findSector(name);
        if (neighSector == null) {
            error(String.format("Line %s not saved. Not found neighbour sector.", element.getLine()));
            return;
        }
        createNeighbour(serSector, neighSector, element);

    }

    /**
     * Creates the neighbour.
     * 
     * @param serSector the ser sector
     * @param neighSector the neigh sector
     * @param element the element
     */
    private void createNeighbour(Node serSector, Node neighSector, BaseTransferData element) {
        Map<String, Object> sectorData = getNotHandledData(element, neighbourName, NodeTypes.PROXY.getId());
        
        int proxyCount = imModel.addRelation(serSector, neighSector, sectorData);
        
        //update statistics for proxy nodes
        updateTx(proxyCount, proxyCount);
        statistic.updateTypeCount(neighbourName, NodeTypes.PROXY.getId(), proxyCount);

        //update statistics for values
        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null || (value instanceof String && StringUtil.isEmpty((String)value))) {
                continue;
            }
            statistic.indexValue(key, NodeTypes.PROXY.getId(), key, entry.getValue());
        }
    }

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "serv_sector_name", new String[] {"Serving Sector"});
        defineHeader(headers, "neigh_sector_name", new String[] {"Interfering Sector"});
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

    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[0]);
    }

}
