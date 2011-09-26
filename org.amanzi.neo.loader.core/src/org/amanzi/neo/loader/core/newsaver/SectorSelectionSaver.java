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

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.NetworkRowContainer;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.exceptions.DatasetTypeParameterException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.InvalidDatasetParameterException;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.SelectionModel;
import org.amanzi.neo.services.networkModel.IModel;

/**
 * @author Kondratenko_Vladislav
 */
public class SectorSelectionSaver<M extends IModel, D extends IData, C extends IConfiguration> implements ISaver<M, D, C> {
    private ISelectionModel model;
    private List<String> headers;
    private NetworkRowContainer container;

    @Override
    public void init(C configuration, D dataElement) {
        Map<String, Object> rootElement = new HashMap<String, Object>();
        rootElement.put(INeoConstants.PROPERTY_NAME_NAME, configuration.getDatasetNames().get("Network"));
        rootElement.put(INeoConstants.PROPERTY_TYPE_NAME, DatasetTypes.NETWORK.getId());
        try {
            model = new SelectionModel(NeoServiceFactory.getInstance().getDatasetService()
                    .findOrCreateAweProject(configuration.getDatasetNames().get("Project")), new DataElement(rootElement));
        } catch (InvalidDatasetParameterException e) {
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
    public void saveElement(D dataElement) {
        if (dataElement instanceof NetworkRowContainer) {
            container = (NetworkRowContainer)dataElement;
            if (headers == null) {
                headers = container.getHeaders();
            } else {
                for (String value : container.getValues()) {
                    model.linkToSector(value);
                }
            }
        }
    }

    @Override
    public void finishUp() {
    }

}
