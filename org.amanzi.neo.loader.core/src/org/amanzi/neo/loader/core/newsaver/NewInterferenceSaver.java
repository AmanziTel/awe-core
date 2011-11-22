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

package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public class NewInterferenceSaver extends AbstractN2NSaver {
    /*
     * neighbours
     */
    public final static String INTERFERE_SECTOR_NAME = "interfering_sector";
    public final static String SERVING_SECTOR_NAME = "serv_sector_name";

    protected NewInterferenceSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data,
            GraphDatabaseService service) {
        super(model, networkModel, data, service);
    }

    public NewInterferenceSaver() {
        super();
    }

    @Override
    protected INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException {
        return networkModel.getNodeToNodeModel(N2NRelTypes.INTERFERENCE_MATRIX, name, NetworkElementNodeType.SECTOR);
    }

    @Override
    protected void initSynonyms() {
        preferenceStoreSynonyms = preferenceManager.getNeighbourSynonyms();
        columnSynonyms = new HashMap<String, Integer>();
    }

    /**
     * try create a neighbour relationship between sectors
     * 
     * @param value
     * @throws DatabaseException
     */
    @Override
    protected void saveLine(List<String> row) throws AWEException {
        String neighbSectorName = getValueFromRow(INTERFERE_SECTOR_NAME, row);
        String serviceNeighName = getValueFromRow(SERVING_SECTOR_NAME, row);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(NewAbstractService.TYPE, NetworkElementNodeType.SECTOR.getId());
        properties.put(NewAbstractService.NAME, neighbSectorName);
        IDataElement findedNeighSector = networkModel.findElement(properties);
        properties.put(NewAbstractService.NAME, serviceNeighName);
        IDataElement findedServiceSector = networkModel.findElement(properties);
        for (String head : headers) {
            if (fileSynonyms.containsValue(head) && !fileSynonyms.get(INTERFERE_SECTOR_NAME).equals(head)
                    && !fileSynonyms.get(SERVING_SECTOR_NAME).equals(head)) {
                properties.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
            }
        }
        if (findedNeighSector != null && findedServiceSector != null) {
            n2nModel.linkNode(findedServiceSector, findedNeighSector, properties);
        } else {
            LOGGER.warn("cann't find service or neighbour sector on line " + lineCounter);
        }
    }

}
