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
import org.apache.commons.lang.StringUtils;
import org.jaitools.jiffle.parser.JiffleParser.conCall_return;

/**
 * network saver
 * 
 * @author Kondratenko_Vladislav
 */
public class NetworkSaver extends AbstractMappedDataSaver<INetworkModel, NetworkConfiguration> {
        
    // Default network structure
    private final static NetworkElementNodeType[] DEFAULT_NETWORK_STRUCTURE = {NetworkElementNodeType.CITY,
            NetworkElementNodeType.MSC, NetworkElementNodeType.BSC, NetworkElementNodeType.SITE, NetworkElementNodeType.SECTOR};
    
    private NetworkElementNodeType startNetworkElement;
    
    private NetworkElementNodeType allElementsFor;

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
    
    private boolean shouldStart(NetworkElementNodeType currentElement) {
        if (StringUtils.isEmpty(getStartElement())) {
            return true;
        }
        
        if (startNetworkElement == null) {
            startNetworkElement = NetworkElementNodeType.valueOf(getStartElement());
        }
        
        return startNetworkElement == currentElement;
    }
    
    private boolean shouldAddAllElements(NetworkElementNodeType currentElement) {
        if (StringUtils.isEmpty(getAllElementsFor())) {
            return false;
        }
        
        if (allElementsFor == null) {
            allElementsFor = NetworkElementNodeType.valueOf(getAllElementsFor());
        }
        
        return currentElement == allElementsFor;
    }
    
    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        IDataElement parent = null;
        IDataElement element = null;
        
        boolean shouldStart = false;
        
        for (NetworkElementNodeType type : DEFAULT_NETWORK_STRUCTURE) {
            if (!shouldStart) {
                shouldStart = shouldStart(type);
            }
            
            if (!shouldStart) {
                continue;
            }
            
            Map<String, Object> values = getDataElementProperties(getMainModel(), type, dataElement, shouldAddAllElements(type));
            
            if (!values.isEmpty()) {
                values.put(AbstractService.TYPE, type.getId());
                
                try {
                    element = getMainModel().findElement(values);
                
                    if (element == null) {
                        element = getMainModel().createElement(parent, values);
                    } else {
                        getMainModel().completeProperties(element, values, true);
                    }
                    parent = element;
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }   
        }
        
        commitTx();
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
