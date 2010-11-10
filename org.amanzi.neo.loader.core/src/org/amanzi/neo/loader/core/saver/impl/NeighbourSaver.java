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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.saver.AbstractHeaderSaver;
import org.amanzi.neo.services.DatasetService.NodeResult;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.amanzi.neo.services.GisProperties;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Neighbour saver
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NeighbourSaver extends AbstractHeaderSaver<BaseTransferData> {

    /** The header not handled. */
    private boolean headerNotHandled;
    private Node neighbourRoot;
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
            neighbourName = element.getFileName();
            neighbourRoot = service.getNeighbour(rootNode, neighbourName);

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
        Integer ci = getNumberValue(Integer.class, "serv_sector_ci", element);
        Integer lac = getNumberValue(Integer.class, "serv_sector_lac", element);
        Node serSector = service.findSector(rootNode, ci, lac, name, true);
        if (serSector == null) {
            error(String.format("Line %s not saved. Not found serve sector.", element.getLine()));
            return;
        }
        name = getStringValue("neigh_sector_name", element);
        ci = getNumberValue(Integer.class, "neigh_sector_ci", element);
        lac = getNumberValue(Integer.class, "neigh_sector_lac", element);
        Node neighSector = service.findSector(rootNode, ci, lac, name, true);
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
        NodeResult proxyServ = service.getNeighbourProxy(neighbourRoot, serSector);
        if (proxyServ.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(neighbourName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId(), 1);
        }
        NodeResult proxyNeigh = service.getNeighbourProxy(neighbourRoot, neighSector);
        if (proxyNeigh.isCreated()) {
            updateTx(1, 1);
            statistic.updateTypeCount(neighbourName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId(), 1);
        }
        Relationship rel;
        if (proxyServ.isCreated() || proxyNeigh.isCreated()) {
            rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR);
            updateTx(0, 1);
        } else {
            Iterator<Relationship> it = Utils.getRelations(proxyServ, proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR).iterator();
            rel = it.hasNext() ? it.next() : null;
            if (rel == null) {
                rel = proxyServ.createRelationshipTo(proxyNeigh, NetworkRelationshipTypes.NEIGHBOUR);
                updateTx(0, 1);
            }
        }
        Map<String, Object> sectorData = getNotHandledData(element, neighbourName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId());

        for (Map.Entry<String, Object> entry : sectorData.entrySet()) {
            String key = entry.getKey();
            updateProperty(neighbourName, NodeTypes.SECTOR_SECTOR_RELATIONS.getId(), rel, key, entry.getValue());
        }
    }

    /**
     * Define property map.
     * 
     * @param element the element
     */
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "serv_sector_ci", getPossibleHeaders(DataLoadPreferences.NE_CI));
        defineHeader(headers, "serv_sector_lac", getPossibleHeaders(DataLoadPreferences.NE_LAC));
        defineHeader(headers, "serv_sector_name", getPossibleHeaders(DataLoadPreferences.NE_BTS));
        defineHeader(headers, "neigh_sector_ci", getPossibleHeaders(DataLoadPreferences.NE_ADJ_CI));
        defineHeader(headers, "neigh_sector_lac", getPossibleHeaders(DataLoadPreferences.NE_ADJ_LAC));
        defineHeader(headers, "neigh_sector_name", getPossibleHeaders(DataLoadPreferences.NE_ADJ_BTS));
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
