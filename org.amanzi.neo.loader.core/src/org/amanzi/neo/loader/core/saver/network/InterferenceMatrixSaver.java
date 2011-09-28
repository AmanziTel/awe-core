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
import java.util.Set;

import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.saver.MetaData;
import org.amanzi.neo.loader.core.saver.Node2NodeSaver;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Interference Matrix saver
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class InterferenceMatrixSaver extends Node2NodeSaver<BaseTransferData> {

    private NetworkModel networkModel;

    @Override
    public void init(BaseTransferData element) {
        super.init(element);
        networkModel = new NetworkModel(rootNode);
    }
    
    @Override
    protected Node defineNeigh(BaseTransferData element) {
        String name = getStringValue("neigh_sector_name", element);
        return networkModel.findSector(name);
    }

    @Override
    protected Node defineServ(BaseTransferData element) {
        String name = getStringValue("serv_sector_name", element);
        return networkModel.findSector(name);
    }

    @Override
    protected void storeHandledData(Relationship rel, BaseTransferData element) {
        Double co =  getNumberValue(Double.class, "co", element);
        updateProperty(neighbourName, NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "co", co);
        
        Double adj =  getNumberValue(Double.class, "adj", element);
        updateProperty(neighbourName, NodeTypes.NODE_NODE_RELATIONS.getId(), rel, "adj", adj);
    }

    @Override
    protected void definePropertyMap(BaseTransferData element) {
        Set<String> headers = element.keySet();
        defineHeader(headers, "neigh_sector_name", getPossibleHeaders(DataLoadPreferences.NE_SRV_NAME));
        defineHeader(headers, "serv_sector_name",  getPossibleHeaders(DataLoadPreferences.NE_NBR_NAME));
        defineHeader(headers, "co", getPossibleHeaders(DataLoadPreferences.NE_SRV_CO));
        defineHeader(headers, "adj", getPossibleHeaders(DataLoadPreferences.NE_SRV_ADJ));
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        //TODO define correct metadata
        return Arrays.asList(new MetaData[0]);
    }
    @Override
    public NodeToNodeRelationModel getModel(String neighbourName) {
        return networkModel.getInterferenceMatrix(neighbourName);
    }    
}
