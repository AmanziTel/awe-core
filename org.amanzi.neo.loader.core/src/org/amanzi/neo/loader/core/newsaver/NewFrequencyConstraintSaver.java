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

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.services.NewNetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * @author Vladislav_Kondratenko
 */
public class NewFrequencyConstraintSaver extends AbstractN2NSaver {
    /*
     * FREQUENCY constraints
     */
    public static String FR_TRX_ID = "trx_id";
    public static String FR_CH_TYPE = "channel type";
    public static String FR_FREQUENCY = "frequency";
    public static String FR_PENALTY = "penalty";
    public static String FR_SCALLING_FACTOR = "scalling_factor";

    private INodeToNodeRelationsModel frSpectrum;

    protected NewFrequencyConstraintSaver(INodeToNodeRelationsModel model, INodeToNodeRelationsModel frspectrum,
            INetworkModel networkModel, ConfigurationDataImpl data, GraphDatabaseService service) {
        super(model, networkModel, data, service);
        this.frSpectrum = frspectrum;
    }

    public NewFrequencyConstraintSaver() {
        super();
    }

    @Override
    protected INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException {
        frSpectrum = networkModel.getNodeToNodeModel(N2NRelTypes.FREQUENCY_SPECTRUM, name, NetworkElementNodeType.SECTOR);
        return networkModel.getNodeToNodeModel(N2NRelTypes.ILLEGAL_FREQUENCY, name, NetworkElementNodeType.SECTOR);
    }

    @Override
    protected void initSynonyms() {
        preferenceStoreSynonyms = preferenceManager.getFrequencySynonyms();
        columnSynonyms = new HashMap<String, Integer>();
    }

    @Override
    protected void saveLine(List<String> row) throws AWEException {
    }
}
