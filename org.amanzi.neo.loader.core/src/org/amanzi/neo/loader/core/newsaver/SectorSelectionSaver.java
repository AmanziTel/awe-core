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
import java.util.Map;

import org.amanzi.neo.db.manager.NeoServiceProvider;
import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.amanzi.neo.services.model.impl.SelectionModel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * @author Kondratenko_Vladislav
 */
public class SectorSelectionSaver implements ISaver<DriveModel, CSVContainer, ConfigurationDataImpl> {
    private ISelectionModel model;
    private List<String> headers;
    private CSVContainer container;
    
    //TODO: LN: make AbstractSaver to handle some general fields/methods
    /**
     * graph database instance
     */
    private GraphDatabaseService database;
    /**
     * top level trasnaction
     */
    private Transaction tx;
    /**
     * transactions count
     */
    private int txCounter;

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        database = NeoServiceProvider.getProvider().getService();
        Map<String, Object> rootElement = new HashMap<String, Object>();
        rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get("Network"));
        rootElement.put(INeoConstants.PROPERTY_TYPE_NAME, DatasetTypes.NETWORK.getId());
        try {
            model = new SelectionModel(NeoServiceFactory.getInstance().getDatasetService()
                    .findOrCreateAweProject(configuration.getDatasetNames().get("Project")), new DataElement(rootElement));
        } catch (InvalidDatasetParameterException e) {
            //TODO: LN: here can be handled one general AWEException
            // TODO Handle InvalidDatasetParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DatasetTypeParameterException e) {
            // TODO Handle DatasetTypeParameterException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (DuplicateNodeNameException e) {
            // TODO Handle DuplicateNodeNameException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        if (tx == null) {
            tx = database.beginTx();
        } else if (txCounter > 1000) {
            finishUp();
            tx = database.beginTx();
        }
        try {
            container = dataElement;
            if (headers == null) {
                headers = container.getHeaders();
            } else {
                for (String value : container.getValues()) {
                    model.linkToSector(value);
                }
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
        }
    }

    @Override
    public void finishUp() {
        NeoServiceProvider.getProvider().commit();
        txCounter = 0;
    }
}
