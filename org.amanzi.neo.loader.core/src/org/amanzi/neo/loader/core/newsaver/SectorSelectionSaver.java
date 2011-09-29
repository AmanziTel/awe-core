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

import java.util.List;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;

/**
 * @author Kondratenko_Vladislav
 */
public class SectorSelectionSaver extends AbstractSaver<DriveModel, CSVContainer, ConfigurationDataImpl> {
    private ISelectionModel model;
    private List<String> headers;
    private CSVContainer container;
    protected static Logger LOGGER = Logger.getLogger(SectorSelectionSaver.class);
    private final int MAX_TX_BEFORE_COMMIT = 1000;

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        try {
            INetworkModel network = getActiveProject().findNetwork(configuration.getDatasetNames().get(CONFIG_VALUE_NETWORK));
            
            //selection data is a single file - so we can just get first element
            String selectionName = configuration.getFilesToLoad().get(0).getName();
            
            model = network.getSelectionModel(selectionName);
        } catch (AWEException e) {
            LOGGER.info("Error while creating Selection Model ", e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        openOrReopenTx();
        try {
            container = dataElement;
            if (headers == null) {
                headers = container.getHeaders();
            } else {
                for (String value : container.getValues()) {
                    model.linkToSector(value);
                    markTxAsSuccess();
                    increaseActionCount();
                }
            }

        } catch (Exception e) {
            LOGGER.error(e);
            markTxAsFailure();
        }
    }
}
