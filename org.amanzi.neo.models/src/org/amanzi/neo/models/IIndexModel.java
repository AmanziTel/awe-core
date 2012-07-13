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

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
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

    String getIndexKey(Node rootNode, INodeType nodeType);

    String getMultiPropertyIndexKey(Node rootNode, INodeType nodeType, String indexName);

    Node getSingleNode(String indexKey, String propertyName, Object value);

    void index(String key, String proeprtyName, Node node);

    <T extends Object> MultiPropertyIndex<T> getMultiPropertyIndex(INodeType nodeType, Node rootNode, String... propertyNames);
}
