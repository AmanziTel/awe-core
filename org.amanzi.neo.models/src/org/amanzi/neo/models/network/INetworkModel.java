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

package org.amanzi.neo.models.network;

import java.util.List;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.internal.IDatasetModel;
import org.amanzi.neo.models.render.IGISModel.ILocationElement;
import org.amanzi.neo.nodetypes.INodeType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
// TODO: LN: 10.10.2012, add comments
public interface INetworkModel extends IDatasetModel {

    public interface INetworkElementType extends INodeType {

    }

    public interface ISectorElement extends IDataElement {

        Double getAzimuth();

        Double getBeamwidth();

    }

    public interface ISiteElement extends ILocationElement {

        List<ISectorElement> getSectors();

    }

    IDataElement createElement(INetworkElementType elementType, IDataElement parent, String name, Map<String, Object> properties)
            throws ModelException;

    void updateSynonyms(Map<String, Object> synonymnsMap) throws ModelException;

    IDataElement findElement(INetworkElementType elementType, String elementName) throws ModelException;

    IDataElement findSector(String sectorName, Integer ci, Integer lac) throws ModelException;

    INodeType[] getNetworkStructure();

    /**
     * @return
     */
    Map<String, String> getSynonyms();

    IDataElement replaceChild(IDataElement child, IDataElement newParent) throws ModelException;

}
