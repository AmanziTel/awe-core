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

package org.amanzi.neo.core.enums;

import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum OssType {
    GPEH(EnumsMessages.OssType_GPEH), 
 COUNTER(EnumsMessages.OssType_COUNTER), APD("APD");
    
    public static final String PROPERTY_NAME = "oss_type";
    private final String id;

    /**
 * 
 */
    private OssType(String id) {
        this.id = id;
    }

    /**
     * Returns NetworkTypes by its ID
     * 
     * @param enumId id of Node Type
     * @return NodeTypes or null
     */
    public static OssType getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (OssType oss : OssType.values()) {
            if (oss.getId().equals(enumId)) {
                return oss;
            }
        }
        return null;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * returns type of node
     * 
     * @param container PropertyContainer
     * @param service NeoService
     * @return type of node
     */
    public static OssType getOssType(PropertyContainer networkGis, GraphDatabaseService service) {
        Transaction tx = service == null ? null : service.beginTx();
        try {
            return getEnumById((String)networkGis.getProperty(PROPERTY_NAME, null));
        } finally {
            if (service != null) {
                tx.finish();
            }
        }
    }
    /**
     * Check node by type
     * 
     * @param currentNode - node
     * @return true if node type
     */
    public boolean checkNode(Node currentNode) {
        return getId().equals(currentNode.getProperty(PROPERTY_NAME, null));
    }
    /**
     * returns type of node
     * 
     * @param container PropertyContainer
     * @param service NeoService
     * @return type of node
     */
    public void setOssType(PropertyContainer container, GraphDatabaseService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            container.setProperty(PROPERTY_NAME, getId());
            NeoUtils.successTx(tx);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }    
}
