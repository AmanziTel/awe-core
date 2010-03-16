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
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Network types enum
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NetworkTypes {

    RADIO("radio") {
        @Override
        public boolean isCorrectFileType(NetworkFileType fileType) {
            return fileType != null
                    && (fileType == NetworkFileType.NEIGHBOUR || fileType == NetworkFileType.RADIO_SECTOR || fileType == NetworkFileType.RADIO_SITE || fileType == NetworkFileType.TRANSMISSION);
        }
    },
    PROBE("probe") {
        @Override
        public boolean isCorrectFileType(NetworkFileType fileType) {
            return fileType != null && fileType == NetworkFileType.PROBE;
        }
    };
    public static final String PROPERTY_NAME = "network_type";
    private final String id;

    /**
     * constructor
     * 
     * @param id node type ID
     * @param nonEditableProperties list of not editable properties
     */
    private NetworkTypes(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns NetworkTypes by its ID
     * 
     * @param enumId id of Node Type
     * @return NodeTypes or null
     */
    public static NetworkTypes getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (NetworkTypes call : NetworkTypes.values()) {
            if (call.getId().equals(enumId)) {
                return call;
            }
        }
        return null;
    }

    /**
     * returns type of node
     * 
     * @param container PropertyContainer
     * @param service NeoService
     * @return type of node
     */
    public static NetworkTypes getNodeType(PropertyContainer networkGis, NeoService service) {
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
     *check correct file type
     * 
     * @param fileType filetype
     * @return
     */
    public abstract boolean isCorrectFileType(NetworkFileType fileType);

    /**
     * Set network type to current node
     * 
     * @param node - node
     * @param service NeoService - neo service, if null then transaction do not created
     */
    public void setTypeToNode(Node node, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            node.setProperty(PROPERTY_NAME, getId());
            NeoUtils.successTx(tx);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

}
