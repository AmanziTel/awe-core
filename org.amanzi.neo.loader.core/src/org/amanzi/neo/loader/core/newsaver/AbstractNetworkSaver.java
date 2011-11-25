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
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;

/**
 * common actions for saver network savers
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractNetworkSaver extends AbstractCSVSaver<INetworkModel> {
    
    /**
     * create class instance
     */
    public AbstractNetworkSaver() {
        super();
    }

    @Override
    protected void initializeNecessaryModels() throws AWEException {
        parametrizedModel = getActiveProject().getNetwork(
                configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));

    }

    @Override
    protected Map<String, String[]> initializeSynonyms() {
        return preferenceManager.getSynonyms(DatasetTypes.NETWORK);
    }

    @Override
    protected void commonLinePreparationActions(CSVContainer dataElement) throws Exception {
    }

    @Override
    protected String getSubType() {
        return null;
    }
}
