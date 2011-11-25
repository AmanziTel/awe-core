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

import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;

/**
 * interference matrix saver
 * 
 * @author Vladislav_Kondratenko
 */
public class InterferenceSaver extends AbstractN2NSaver {

    /*
     * neighbours
     */
    public final static String INTERFERE_SECTOR_NAME = "interfering_sector";
    public final static String SERVING_SECTOR_NAME = "serv_sector_name";

    protected InterferenceSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data) {
        super(model, networkModel, data);
    }

    /**
     * create class instance
     */
    public InterferenceSaver() {
        super();
    }

    @Override
    protected INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException {
        return parametrizedModel.getNodeToNodeModel(N2NRelTypes.INTERFERENCE_MATRIX, name, NetworkElementNodeType.SECTOR);
    }

    @Override
    protected Map<String, String[]> initializeSynonyms() {
        return preferenceManager.getInterfererSunonyms();
    }

    @Override
    protected String getSourceElementName() {
        return SERVING_SECTOR_NAME;
    }

    @Override
    protected String getNeighborElementName() {
        return INTERFERE_SECTOR_NAME;
    }

}
