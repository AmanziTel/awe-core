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

import java.util.Map;

import org.amanzi.neo.loader.core.config.NetworkConfiguration;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService.DatasetTypes;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkSaver extends AbstractMappedDataSaver<INetworkModel, NetworkConfiguration> {
        
    // Default network structure
    private final static NetworkElementNodeType[] DEFAULT_NETWORK_STRUCTURE = {NetworkElementNodeType.CITY,
            NetworkElementNodeType.MSC, NetworkElementNodeType.BSC, NetworkElementNodeType.SITE, NetworkElementNodeType.SECTOR};

    /**
     * create saver instance
     */
    public NetworkSaver() {
        super();
    }

    /**
     * Constructor for tests
     * 
     * @param model
     * @param config
     */
    NetworkSaver(INetworkModel model, NetworkConfiguration config) {
        commitTx();
        if (model != null) {
            setMainModel(model);
            addModel(model);
        }
    }

    @Override
    protected boolean isRenderable() {
        return true;
    }

    @Override
    protected String getDatasetType() {
        return DatasetTypes.NETWORK.getId();
    }
    
    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        IDataElement parent = null;
        
        for (NetworkElementNodeType type : DEFAULT_NETWORK_STRUCTURE) {
            Map<String, Object> values = getDataElementProperties(getMainModel(), type, dataElement, type == NetworkElementNodeType.SECTOR);
            
            if (!values.isEmpty()) {
                values.put(AbstractService.TYPE, type.getId());
                parent = getMainModel().findElement(values);
                if (parent == null) {
                    getMainModel().createElement(parent, values);
                } else {
                    getMainModel().completeProperties(parent, values, true);
                }
            }   
        }
    }

    @Override
    protected String getSubType() {
        return null;
    }

    @Override
    protected INetworkModel createMainModel(NetworkConfiguration configuration) throws AWEException {
        return getActiveProject().getNetwork(configuration.getDatasetName());
    }

}
