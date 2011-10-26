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

package org.amanzi.neo.services.model;

import java.util.Map;

import org.amanzi.neo.services.exceptions.AWEException;
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
public interface INodeToNodeRelationsModel extends IPropertyStatisticalModel {

    public INodeToNodeRelationsType getNodeToNodeRelationsType();

    /**
     * Creates a relationship from <code>source</code> to <code>target</code> through PROXY nodes,
     * and sets <code>params</code> properties to the relationship, if defined. Proxy nodes are
     * added to the node2node model root as C-N-N.
     * 
     * @param source
     * @param target
     * @param params can be <code>null</code>
     * @throws DatabaseException
     * @throws AWEException
     */
    public void linkNode(IDataElement source, IDataElement target, Map<String, Object> params) throws DatabaseException,
            AWEException;

    /**
     * Traverses database to find elements connected to the <code>source</code> element with
     * relations, defined by current node2node model.
     * 
     * @param source
     * @return <code>IDataElement</code> traverser
     */
    public Iterable<IDataElement> getN2NRelatedElements(IDataElement source);
}
