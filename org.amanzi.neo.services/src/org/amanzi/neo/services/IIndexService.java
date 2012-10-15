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

package org.amanzi.neo.services;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */

// TODO: LN: 10.10.2012, add comments
public interface IIndexService extends IService {

    Index<Node> getIndex(Node rootNode, INodeType nodeType) throws ServiceException;

    void addToIndex(Node rootNode, INodeType nodeType, Node node, String propertyName, Object value) throws ServiceException;

    <T extends Object> MultiPropertyIndex<T> createMultiPropertyIndex(INodeType nodeType, Node node, Class<T> clazz,
            String... properties) throws ServiceException;

    void deleteFromIndexes(Node rootNode, Node node, INodeType nodeType) throws DatabaseException;

    void deleteAll(Node node) throws DatabaseException;
}
