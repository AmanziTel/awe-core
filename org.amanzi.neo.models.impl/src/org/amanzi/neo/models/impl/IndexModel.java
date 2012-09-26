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

package org.amanzi.neo.models.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IIndexService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexModel extends AbstractModel implements IIndexModel {

    private interface IKey {

    }

    private static final class MultiPropertyKey implements IKey {

        private final String[] properties;

        public MultiPropertyKey(final String... properties) {
            this.properties = properties;
        }

        @Override
        public int hashCode() {
            return new Integer(properties.length).hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof MultiPropertyKey) {
                MultiPropertyKey key = (MultiPropertyKey)o;

                return Arrays.equals(properties, key.properties);
            }

            return false;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(IndexModel.class);

    private final IIndexService indexService;

    private final Map<IKey, MultiPropertyIndex< ? >> indexMap = new HashMap<IKey, MultiPropertyIndex< ? >>();

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public IndexModel(final IGeneralNodeProperties generalNodeProperties, final IIndexService indexService) {
        super(null, generalNodeProperties);
        this.indexService = indexService;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        setRootNode(rootNode);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    @Override
    public void finishUp() throws ModelException {
        LOGGER.info("Finishing up model <" + getName() + ">");
        flush();
    }

    @Override
    public Node getSingleNode(final INodeType nodeType, final String propertyName, final Object value) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getSingleNode", nodeType, propertyName, value));
        }

        Node result = null;

        try {
            result = getNodeIndexHits(getRootNode(), nodeType, propertyName, value).getSingle();
        } catch (ServiceException e) {
            processException("Exception on searching for a Node in Index", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getSingleNode"));
        }

        return result;
    }

    protected IndexHits<Node> getNodeIndexHits(final Node node, final INodeType nodeType, final String propertyName,
            final Object value) throws ServiceException {
        Index<Node> index = indexService.getIndex(node, nodeType);

        return index.get(propertyName, value);
    }

    @Override
    public void index(final INodeType nodeType, final Node node, final String propertyName, final Object value)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("index", nodeType, node, propertyName, value));
        }

        try {
            indexService.addToIndex(getRootNode(), nodeType, node, propertyName, value);
        } catch (ServiceException e) {
            processException("Exception on indexing Node", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("index"));
        }
    }

    @Override
    public void indexInMultiProperty(final INodeType nodeType, final Node node, final Class< ? > clazz, final String... properties)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("indexMultiProperty", nodeType, node, properties));
        }

        MultiPropertyIndex< ? > index = getMultiPropertyIndex(nodeType, clazz, properties);
        index.add(node);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("indexMultiProperty"));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> MultiPropertyIndex<T> getMultiPropertyIndex(final INodeType nodeType, final Class<T> clazz,
            final String... properties) throws ModelException {
        IKey key = new MultiPropertyKey(properties);

        MultiPropertyIndex<T> index = (MultiPropertyIndex<T>)indexMap.get(key);

        if (index == null) {
            try {
                index = indexService.createMultiPropertyIndex(nodeType, getRootNode(), clazz, properties);
                indexMap.put(key, index);
            } catch (ServiceException e) {
                processException("Error on initializing MultiPropertyIndex", e);
            }
        }

        return index;
    }

    @Override
    public Iterator<Node> getNodes(final INodeType nodeType, final String propertyName, final Object value) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getNodes", nodeType, propertyName, value));
        }

        Iterator<Node> result = null;

        try {
            result = getNodeIndexHits(getRootNode(), nodeType, propertyName, value).iterator();
        } catch (ServiceException e) {
            processException("Exception on searching for a Node in Index", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getNodes"));
        }

        return result;
    }

    @Override
    public <T> Iterator<Node> getNodes(final INodeType nodeType, final Class<T> clazz, final T[] min, final T[] max,
            final String... properties) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getNodes", nodeType, min, max));
        }

        MultiPropertyIndex<T> index = getMultiPropertyIndex(nodeType, clazz, properties);

        Iterator<Node> result = index.find(min, max).iterator();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getNodes"));
        }

        return result;
    }

    @Override
    public void flush() throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Flushing indexes");
        }
        for (MultiPropertyIndex< ? > index : indexMap.values()) {
            index.finishUp();
        }
        super.flush();
    }

    @Override
    public String getName() {
        return "IndexModel <" + getParentNode() + ">";
    }

    @Override
    public Iterator<Node> getAllNodes(final INodeType type) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getAllNodes", type));
        }

        Iterator<Node> result = null;

        try {
            Index<Node> index = indexService.getIndex(getRootNode(), type);

            result = index.query(getGeneralNodeProperties().getNodeTypeProperty() + ":*").iterator();
        } catch (ServiceException e) {
            processException("Exception on searching for a Node in Index", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getAllNodes"));
        }

        return result;
    }

}
