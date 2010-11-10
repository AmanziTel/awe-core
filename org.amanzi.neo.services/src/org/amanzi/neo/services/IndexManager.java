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

import java.io.IOException;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

/**
 * <p>
 * Provide work with all indexes for necessary dataset
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class IndexManager {

    /** The root. */
    private final Node root;
    private final DatasetService service;
    private final IndexService index;
    private final String name;

    /**
     * Instantiates a new index manager.
     * 
     * @param root the root
     */
    protected IndexManager(Node root) {
        this.root = root;
        service = NeoServiceFactory.getInstance().getDatasetService();
        index = service.getIndexService();

        name = service.getNodeName(root);
    }

    /**
     * Update indexes.
     * 
     * @param container the container
     * @param propertyName the property name
     * @param value the value
     */
    public void updateIndexes(PropertyContainer container, String propertyName, Object oldValue) {
        // TODO not fully implement
        if (name == null) {
            return;
        }
        Object value = container.getProperty(propertyName, null);
        if (oldValue == null && value == null || oldValue != null && oldValue.equals(value)) {
            return;
        }
        updateLuceneIndex(container, propertyName, oldValue, value);
        updateMultipropertyIndex(container, propertyName, oldValue, value);

    }

    /**
     * Update multiproperty index.
     * 
     * @param container the container
     * @param propertyName the property name
     * @param oldValue the old value
     * @param value the value
     */
    private boolean updateMultipropertyIndex(PropertyContainer container, String propertyName, Object oldValue, Object value) {
        // TODO not fully implement
        if (container instanceof Node) {
            Node node = (Node)container;
            INodeType type = service.getNodeType(node);
            if (null == type) {
                return false;
            }
            if (type == NodeTypes.SITE) {
                if (INeoConstants.PROPERTY_LAT_NAME.equals(propertyName) || INeoConstants.PROPERTY_LON_NAME.equals(propertyName)) {
                    try {
                        Node gis=service.findGisNode(root);
                        if (gis == null) {
                            return false;
                        }
                        GisProperties prop =new GisProperties(gis);
                        Double lat = (Double)node.getProperty(INeoConstants.PROPERTY_LAT_NAME,null);
                        Double lon = (Double)node.getProperty(INeoConstants.PROPERTY_LON_NAME,null);
                        if (lat==null||lon==null){
                            return false;
                        }
//                        if (((Number)value).doubleValue() ==0.0){
//                            return false;
//                        }
                        prop.updateBBox(lat, lon);

                        MultiPropertyIndex<Double> id = Utils.getLocationIndexProperty(name);
                        Transaction tx = DatabaseManager.getInstance().getCurrentDatabaseService().beginTx();
                        try {
                            id.initialize(DatabaseManager.getInstance().getCurrentDatabaseService(), null);
                            id.add(node);
                            id.finishUp();
                            tx.success();
                        } finally {
                            tx.finish();
                        }
                        service.saveGis(prop);
                    } catch (IOException e) {
                        // TODO Handle IOException
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean updateLuceneIndex(PropertyContainer container, String propertyName, Object oldValue, Object value) {
        if (container instanceof Node) {
            Node node = (Node)container;
            INodeType type = service.getNodeType(node);
            if (null == type) {
                return false;
            }
            String idName = getLuceneIndexKeyByProperty(type, propertyName);
            if (oldValue == null) {
                if (value == null) {
                    return false;
                }
                if (needLuceneIndex(container, propertyName)) {
                    index.index(node, idName, value);
                    return true;
                }
                return false;
            }
            if (oldValue.equals(value)) {
                return false;
            }
            boolean indexPr = false;
            for (Node nodeIdx : index.getNodes(idName, oldValue)) {
                if (nodeIdx.equals(node)) {
                    indexPr = true;
                    break;
                }
            }
            if (!indexPr) {
                return false;
            }
            index.removeIndex(node, idName);
            index.index(node, idName, value);
            return true;
        }

        return false;
    };

    /**
     * Check if need to add new Lucene Index for current property
     * 
     * @param container
     * @param propertyName
     * @return
     */
    private boolean needLuceneIndex(PropertyContainer container, String propertyName) {
        INodeType type = service.getNodeType(container);
        if (NodeTypes.SECTOR == type) {
            if (INeoConstants.PROPERTY_SECTOR_CI.equals(propertyName) || INeoConstants.PROPERTY_SECTOR_LAC.equals(propertyName)) {
                return true;
            }
        } else if (INeoConstants.PROPERTY_NAME_NAME.equals(propertyName)) {
            return true;
        }
        return false;
    }

    public String getLuceneIndexKeyByProperty(INodeType type, String propertyName) {
        return Utils.getLuceneIndexKeyByProperty(root, propertyName, type);
    }
}
