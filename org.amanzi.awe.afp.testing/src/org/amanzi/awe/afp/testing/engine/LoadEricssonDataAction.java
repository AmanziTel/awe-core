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

package org.amanzi.awe.afp.testing.engine;

import java.io.IOException;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.testing.engine.AfpModelFactory.AfpScenario;
import org.amanzi.awe.afp.testing.engine.TestDataLocator.DataType;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class LoadEricssonDataAction extends AbstractDataset {
    
    private static final String DATASET_NAME = "Ericsson_dataset"; 
    
    public LoadEricssonDataAction(String projectName) throws IOException {
        super(projectName);
        
        loadActions.add(new LoadNetworkDataAction(TestDataLocator.getNetworkFile(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadNetworkConfigDataAction(TestDataLocator.getNetworkConfigDirectory(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadNetworkMeasurementDataAction(TestDataLocator.getNetworkMeasurementsDirectory(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadSelectionDataAction(TestDataLocator.getSelectionFile(getDataType()), projectName, DATASET_NAME));
    }

    @Override
    public DataType getDataType() {
        return DataType.ERICSSON;
    }

    @Override
    public String getName() {
        return DATASET_NAME;
    }
    
    @Override
    public AfpModel getAfpModel(AfpScenario scenario) {
        AfpModel model = AfpModelFactory.getAfpModel(this, scenario);
        try {
            model.getNetworkDatasets();
            model.getNetworkSelectionLists(getName());
        
            model.setNetworkSelectionName(TestDataLocator.getSelectionFile(getDataType()).getName());
        }
        catch (IOException e) { 
            //do nothing - error on 
        }
        
        return model;
    }
}
