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
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * 
 * TODO Purpose of 
 * <p>
 * Saver for antenna optimization constraints 
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class AntennaOptimizationConstraintsSaver extends AbstractNetworkSaver<INetworkModel, NetworkConfiguration>{
    /*
     * Name of Dataset Synonyms for antenna optimization constraints
     */
    private static final String SYNONYMS_DATASET_TYPE = "antenna_optimization";

    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        Map<String, Object> values = getDataElementProperties(getMainModel(), NetworkElementNodeType.SECTOR.getId(), dataElement, true, true);

        IDataElement sector = getNetworkElement(getSectorNodeType(), "name", values);        
        
        if(sector==null){
            throw new NullPointerException("sector cann't be null");
        }
        
        getMainModel().completeProperties(sector, values, true);
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected INetworkModel createMainModel(NetworkConfiguration configuration) throws AWEException {
        networkModel = getActiveProject().getNetwork(configuration.getDatasetName()); 
        return networkModel;      
    }

    @Override
    protected String getDatasetType() {
        return SYNONYMS_DATASET_TYPE;
    }

    @Override
    protected String getSubType() {
        return null;
    }

}
