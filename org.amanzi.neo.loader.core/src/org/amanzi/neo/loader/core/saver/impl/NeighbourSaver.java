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

import java.util.Arrays;
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *Neighbour Saver
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NeighbourSaver extends Node2NodeSaver {

    @Override
    protected Node defineNeigh(BaseTransferData element) {
        String name = getStringValue("neigh_sector_name", element);
        Integer ci = getNumberValue(Integer.class, "neigh_sector_ci", element);
        Integer lac = getNumberValue(Integer.class, "neigh_sector_lac", element);
        return  service.findSector(rootNode, ci, lac, name, true);
    }

    @Override
    protected Node defineServ(BaseTransferData element) {
        String name = getStringValue("serv_sector_name", element);
        Integer ci = getNumberValue(Integer.class, "serv_sector_ci", element);
        Integer lac = getNumberValue(Integer.class, "serv_sector_lac", element);
        return service.findSector(rootNode, ci, lac, name, true);
    }

    @Override
    protected void storeHandledData(Relationship rel, BaseTransferData element) {
    }

    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "serv_sector_ci", getPossibleHeaders(DataLoadPreferences.NE_SRV_CI));
        defineHeader(headers, "serv_sector_lac", getPossibleHeaders(DataLoadPreferences.NE_SRV_LAC));
        defineHeader(headers, "serv_sector_name", getPossibleHeaders(DataLoadPreferences.NE_SRV_NAME));
        defineHeader(headers, "neigh_sector_ci", getPossibleHeaders(DataLoadPreferences.NE_NBR_CI));
        defineHeader(headers, "neigh_sector_lac", getPossibleHeaders(DataLoadPreferences.NE_NBR_LAC));
        defineHeader(headers, "neigh_sector_name", getPossibleHeaders(DataLoadPreferences.NE_NBR_NAME));
    }

    @Override
    public NodeToNodeRelationModel getModel(String neighbourName) {
        return new NetworkModel(rootNode).getNeighbours(neighbourName);
    }
    @Override
    public Iterable<MetaData> getMetaData() {
        return Arrays.asList(new MetaData[0]);
    }

}
