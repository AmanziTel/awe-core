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
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

/**
 * <p>
 * Network site type
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NetworkSiteType {
    SITE_2G("site_2g"), SITE_3G("site_3g");
    public static final String PROPERTY_NAME = "site_type";
    private final String id;


    private NetworkSiteType(String id) {
        this.id = id;
    }

    /**
     * Returns NetworkTypes by its ID
     * 
     * @param enumId id of Node Type
     * @return NodeTypes or null
     */
    public static NetworkSiteType getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (NetworkSiteType oss : NetworkSiteType.values()) {
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
    public static NetworkSiteType getNetworkSiteType(PropertyContainer networkGis, NeoService service) {
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
     * returns type of node
     * 
     * @param container PropertyContainer
     * @param service NeoService
     * @return type of node
     */
    public void setSiteType(PropertyContainer container, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            container.setProperty(PROPERTY_NAME,getId());
            NeoUtils.successTx(tx);
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

}
