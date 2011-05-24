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
import java.util.LinkedList;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.testing.engine.internal.AfpModelFactory.AfpScenario;
import org.amanzi.neo.services.NeoServiceFactory;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractDataset implements IDataset {
    
    protected String projectName;
    
    protected LinkedList<AbstractLoadAction> loadActions = new LinkedList<AbstractLoadAction>();
    
    public AbstractDataset(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void run() {
        for (AbstractLoadAction singleAction : loadActions) {
            singleAction.run();
        }
    }

    @Override
    public Node getRootNode() {
        return NeoServiceFactory.getInstance().getDatasetService().findRoot(projectName, getName());
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
