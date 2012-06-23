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

package org.amanzi.neo.models.n2n;

import java.util.List;
import java.util.Map;

import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.distribution.IDistributionalModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.services.exceptions.DatabaseException;

/**
 * <p>
 * The interface for models describing node relationships, that are beyond common data structure,
 * e.g. neighbouring between sectors.
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface INodeToNodeRelationsModel extends IDistributionalModel {

    INodeToNodeType getNodeToNodeType();

    /**
     * Creates a relationship from <code>source</code> to <code>target</code> through PROXY nodes,
     * and sets <code>params</code> properties to the relationship, if defined. Proxy nodes are
     * added to the node2node model root as C-N-N.
     * 
     * @param source
     * @param target
     * @param params can be <code>null</code>
     */
    void linkElements(IDataElement source, IDataElement target, Map<String, Object> params) throws ModelException;

    /**
     * Traverses database to find elements connected to the <code>source</code> element with
     * relations, defined by current node2node model.
     * 
     * @param source
     * @return <code>IDataElement</code> traverser
     */
    Iterable<IDataElement> getN2NRelatedElements(IDataElement source);

    /**
     * update relationship between service and neighbour elements proxys with required properties
     * 
     * @param serviceElement
     * @param neighbourElement
     * @param properties
     * @param isReplace
     * @throws DatabaseException
     * @throws AWEException
     */
    void updateRelationship(IDataElement serviceElement, IDataElement neighbourElement, Map<String, Object> properties, boolean isReplace) throws ModelException;

    /**
     * return service element by proxy
     * 
     * @param proxy proxy element
     * @return service sector or null if not found
     */
    IDataElement getServiceElementByProxy(IDataElement proxy);

    /**
     * @param sector
     * @return
     */
    List<IDataElement> getNeighboursForCurrentSector(IDataElement sector);

}
