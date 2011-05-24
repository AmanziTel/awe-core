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

package org.amanzi.awe.afp.testing.engine.internal;

import java.io.IOException;

import org.amanzi.awe.afp.testing.engine.internal.TestDataLocator.DataType;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class LoadGermanyDataAction extends AbstractDataset {

    private static final String DATASET_NAME = "Germany_dataset"; 
    
    public LoadGermanyDataAction(String projectName) throws IOException {
        super(projectName);
        
        loadActions.add(new LoadNetworkDataAction(TestDataLocator.getNetworkFile(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadNeighbourDataAction(TestDataLocator.getNeighbourFile(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadIMDataAction(TestDataLocator.getIMFile(getDataType()), projectName, DATASET_NAME));
        loadActions.add(new LoadSelectionDataAction(TestDataLocator.getSelectionFile(getDataType()), projectName, DATASET_NAME));
    }

    @Override
    public DataType getDataType() {
        return DataType.GERMANY;
    }

    @Override
    public String getName() {
        return DATASET_NAME;
    }    
}
