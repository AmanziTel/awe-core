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

package org.amanzi.neo.models.impl.network;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractDataModel;
import org.amanzi.neo.models.network.ISelectionModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SelectionModel extends AbstractDataModel implements ISelectionModel {

    /**
     * @param nodeService
     */
    public SelectionModel(INodeService nodeService, IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    @Override
    public void finishUp() throws ModelException {
    }

    @Override
    public void linkToSector(IDataElement element) throws ModelException {
    }

    @Override
    public int getSelectedNodesCount() {
        return 0;
    }

    @Override
    public boolean isExistSelectionLink(IDataElement element) {
        return false;
    }

    @Override
    public void deleteSelectionLink(IDataElement element) {
    }

}
