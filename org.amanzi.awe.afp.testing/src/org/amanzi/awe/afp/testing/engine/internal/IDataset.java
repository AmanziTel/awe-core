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

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.testing.engine.internal.AfpModelFactory.AfpScenario;
import org.amanzi.awe.afp.testing.engine.internal.TestDataLocator.DataType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public interface IDataset extends Runnable {
    
    public Node getRootNode();
    
    public DataType getDataType();
    
    public String getName();
    
    public AfpModel getAfpModel(AfpScenario scenario); 

}
