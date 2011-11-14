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

package org.amanzi.neo.services.model.impl;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.statistic.IVault;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Implements the basic methods of all the models.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public abstract class AbstractModel implements IModel {

    protected String name;
    protected Node rootNode;
    protected INodeType nodeType;
    protected IVault statisticsVault;
    
    protected AbstractModel(INodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public INodeType getType() {
        return nodeType;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public void finishUp() throws AWEException {
        //do nothing
    }
}
