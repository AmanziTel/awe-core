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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.CSVContainer;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;
import org.apache.log4j.Logger;

/**
 * common actions for saver using n2n models
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractN2NSaver extends AbstractCSVSaver<INetworkModel> {

    private static final Logger LOGGER = Logger.getLogger(AbstractN2NSaver.class);
    /**
     * related n2nModel
     */
    protected INodeToNodeRelationsModel n2nModel;
    
    protected AbstractN2NSaver() {
        super();
    }

    /**
     * Constructor for testing
     * 
     * @param model
     * @param networkModel
     * @param data
     */
    AbstractN2NSaver(INodeToNodeRelationsModel model, INetworkModel networkModel, ConfigurationDataImpl data) {
        preferenceStoreSynonyms = initializeSynonyms();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        if (model != null) {
            n2nModel = model;
            if (networkModel == null) {
                try {
                    parametrizedModel = getActiveProject().getNetwork(
                            data.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
                    networkModel = this.parametrizedModel;
                } catch (AWEException e) {
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
            } else {
                this.parametrizedModel = networkModel;
            }
        }

    }

    /**
     * try create a neighbour relationship between sectors
     * 
     * @param value
     * @throws DatabaseException
     */
    @Override
    protected void saveLine(List<String> row) throws AWEException {
        String neighbSectorName = getValueFromRow(getNeighborElementName(), row);
        String serviceNeighName = getValueFromRow(getSourceElementName(), row);

        Map<String, Object> properties = new HashMap<String, Object>();

        Set<IDataElement> findedNeighSector = parametrizedModel.findElementByPropertyValue(NetworkElementNodeType.SECTOR,
                AbstractService.NAME, neighbSectorName);

        Set<IDataElement> findedServiceSector = parametrizedModel.findElementByPropertyValue(NetworkElementNodeType.SECTOR,
                AbstractService.NAME, serviceNeighName);
        if (!findedNeighSector.isEmpty() && !findedServiceSector.isEmpty()) {
            for (String head : headers) {
                if (fileSynonyms.containsValue(head)) {
                    properties.put(head.toLowerCase(), getSynonymValueWithAutoparse(head, row));
                }
            }
            n2nModel.linkNode(findedServiceSector.iterator().next(), findedNeighSector.iterator().next(), properties);
            addSynonyms(n2nModel, properties);
        } else {
            LOGGER.warn("cann't find service or neighbour sector on line " + lineCounter);
        }

    }

    /**
     * initialize necessary models
     * 
     * @return model used in top cases(parametrized model)
     * @throws AWEException
     */
    protected void initializeNecessaryModels() throws AWEException {
        parametrizedModel = getActiveProject().getNetwork(
                configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
        n2nModel = getNode2NodeModel(configuration.getFilesToLoad().get(0).getName());
        useableModels.add(n2nModel);
    }

    /**
     * @return name of source element
     */
    protected abstract String getSourceElementName();

    /**
     * @return name of neighbor element
     */
    protected abstract String getNeighborElementName();

    /**
     * initialize required n2n models
     * 
     * @param name
     * @return
     * @throws AWEException
     */
    protected INodeToNodeRelationsModel getNode2NodeModel(String name) throws AWEException {
        return parametrizedModel.getNodeToNodeModel(getN2NType(), name, getN2NNodeType());
    }

    @Override
    protected void commonLinePreparationActions(CSVContainer dataElement) throws Exception {

    }
    
    @Override
    protected Map<String, String[]> initializeSynonyms() {
        return preferenceManager.getSynonyms(getN2NType());
    }
    
    /**
     * Returns type of N2N Model of this Saver
     *
     * @return
     */
    protected abstract N2NRelTypes getN2NType();
    
    /**
     * Returns type of Nodes for N2N Model
     *
     * @return
     */
    protected abstract INodeType getN2NNodeType();

    @Override
    protected String getSubType() {
        return null;
    }
}
