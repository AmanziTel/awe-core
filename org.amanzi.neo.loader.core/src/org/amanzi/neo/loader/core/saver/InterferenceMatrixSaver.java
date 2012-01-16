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

package org.amanzi.neo.loader.core.saver;

import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.INodeToNodeRelationsType;
import org.amanzi.neo.services.model.impl.NodeToNodeRelationshipModel.N2NRelTypes;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class InterferenceMatrixSaver extends AbstractN2NSaver {

    @Override
    protected INodeToNodeRelationsType getN2NType() {
        return N2NRelTypes.INTERFERENCE_MATRIX;
    }

    @Override
    protected INodeType getN2NNodeType() {
        return NetworkElementNodeType.SECTOR;
    }

}
