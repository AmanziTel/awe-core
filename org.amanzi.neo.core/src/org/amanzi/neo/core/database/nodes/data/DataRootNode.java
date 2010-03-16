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

package org.amanzi.neo.core.database.nodes.data;

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Node;

/**
 * <p>
 * Data root node wrapper
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public abstract class DataRootNode extends AbstractChildNextNode{

    /**
     * constructor 
     * @param node - wrapped node
     */
    protected DataRootNode(Node node) {
        super(node);
    }
    /**
     * add child node to data
     * @param child - child node
     */
    @Override
    public void addChild(AbstractChildNextNode child){
        super.addChild(child);
    }
    /**
     * Get instance of node
     * @param node - data root node
     * @return wrapper of node
     */
    public static DataRootNode getInstance(Node node) {
        if (NeoUtils.isDatasetNode(node)){
            return new DatasetNode(node);
        }else{
            return new NetworkNode(node);
        }
    }
}
