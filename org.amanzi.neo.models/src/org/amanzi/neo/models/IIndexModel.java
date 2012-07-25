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

package org.amanzi.neo.models;

import java.util.Iterator;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodetypes.INodeType;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IIndexModel extends IModel {

    Node getSingleNode(INodeType nodeType, String propertyName, Object value) throws ModelException;

    Iterator<Node> getNodes(INodeType nodeType, String propertyName, Object value) throws ModelException;

    void index(final INodeType nodeType, final Node node, final String propertyName, Object value) throws ModelException;

    void indexInMultiProperty(final INodeType nodeType, final Node node, Class< ? > clazz, String... properties)
            throws ModelException;

    <T extends Object> Iterator<Node> getNodes(final INodeType nodeType, final Class<T> clazz, final T[] min, final T[] max,
            final String... properties) throws ModelException;
}
