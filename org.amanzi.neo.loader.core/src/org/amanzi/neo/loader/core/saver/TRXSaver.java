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
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.parser.MappedData;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.ISelectionModel;

/**
 * 
 * TODO Purpose of 
 * <p>
 *  Saver for trx data
 * </p>
 * @author Ladornaya_A
 * @since 1.0.0
 */
public class TRXSaver extends AbstractNetworkSaver<ISelectionModel, ConfigurationDataImpl> {

    /*
     * Name of Dataset Synonyms
     */
    private static final String SYNONYMS_DATASET_TYPE = "network";
    
    @Override
    public void saveElement(MappedData dataElement) throws AWEException {
        Map<String, Object> values = getDataElementProperties(getMainModel(), null, dataElement, true);

        IDataElement trxSector = getNetworkElement(getSectorNodeType(), "sector_name", values);
        
        //collectTRXElement(values);
        
        

        //getMainModel().linkToSector(selectionElement);
    }

    @Override
    protected boolean isRenderable() {
        return false;
    }

    @Override
    protected ISelectionModel createMainModel(ConfigurationDataImpl configuration) throws AWEException {
        networkModel = getActiveProject().getNetwork(
                configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));

       // String selectionName = configuration.getFilesToLoad().get(0).getName();

        //return networkModel.getSelectionModel(selectionName);
        return null;
    }

    
    @Override
    protected String getDatasetType() {
        return SYNONYMS_DATASET_TYPE;
    }

    @Override
    protected String getSubType() {
        return NetworkElementNodeType.TRX.getId();
    }

}
