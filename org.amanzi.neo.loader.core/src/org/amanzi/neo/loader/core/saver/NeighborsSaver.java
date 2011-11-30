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

package org.amanzi.neo.loader.core.saver;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;

/**
 * @author Kondratneko_Vladislav
 */
public class NeighborsSaver extends AbstractN2NSaver {
    /*
     * neighbours
     */
    public final static String NEIGHBOUR_SECTOR_NAME = "neigh_sector_name";
    public final static String SERVING_SECTOR_NAME = "serv_sector_name";

    /**
     * create saver instance
     */
    public NeighborsSaver() {
        super();
    }

    /**
     * Constructor for tests
     * 
     * @param model
     * @param networkModel
     * @param data
     */
    protected NeighborsSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data) {
        super(model, networkModel, data);
    }

    @Override
    protected String getSourceElementName() {
        return SERVING_SECTOR_NAME;
    }

    @Override
    protected String getNeighborElementName() {
        return NEIGHBOUR_SECTOR_NAME;
    }

    @Override
    protected N2NRelTypes getN2NType() {
        return N2NRelTypes.NEIGHBOUR;
    }

    @Override
    protected INodeType getN2NNodeType() {
        return NetworkElementNodeType.SECTOR;
    }

}
