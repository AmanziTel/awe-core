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

package org.amanzi.neo.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IIndexService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexService extends AbstractService implements IIndexService {

    private static final String INDEX_SEPARATOR = "@";

    private final Map<String, Index<Node>> nodeIndexMap = new HashMap<String, Index<Node>>();

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    public IndexService(final GraphDatabaseService graphDb) {
        super(graphDb, null);
    }

    @Override
    public Index<Node> getIndex(final Node rootNode, final INodeType nodeType) throws ServiceException {
        assert rootNode != null;
        assert nodeType != null;

        String key = getIndexKey(rootNode, nodeType);

        Index<Node> result = nodeIndexMap.get(key);

        if (result == null) {
            result = createNodeIndex(key);

            nodeIndexMap.put(key, result);
        }

        return result;
    }

    protected Index<Node> createNodeIndex(final String key) throws ServiceException {
        Index<Node> result = null;

        Transaction tx = getGraphDb().beginTx();
        try {
            result = getGraphDb().index().forNodes(key);

            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    protected String getIndexKey(final Node rootNode, final INodeType nodeType) {
        return new StringBuilder().append(rootNode.getId()).append(INDEX_SEPARATOR).append(nodeType.getId()).toString();
    }

    @Override
    public void addToIndex(final Node rootNode, final INodeType nodeType, final Node node, final String propertyName,
            final Object value) throws ServiceException {
        assert rootNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(propertyName);
        assert value != null;

        Transaction tx = getGraphDb().beginTx();
        try {
            Index<Node> index = getIndex(rootNode, nodeType);
            index.add(node, propertyName, value);
            tx.success();
        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }
}
