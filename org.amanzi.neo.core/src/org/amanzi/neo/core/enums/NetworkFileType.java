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


/**
 * <p>
 * Enumeration of network file types
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NetworkFileType {
    RADIO_SITE(EnumsMessages.NetworkFileType_RADIO_SITE),
    RADIO_SECTOR(EnumsMessages.NetworkFileType_RADIO_SECTOR),
    TRANSMISSION(EnumsMessages.NetworkFileType_TRANSMISSION),
    NEIGHBOUR(EnumsMessages.NetworkFileType_NEIGHBOUR),
    PROBE(EnumsMessages.NetworkFileType_PROBE);

    private String id;

    /**
     * constructor
     * 
     * @param id node type ID
     * @param nonEditableProperties list of not editable properties
     */
    private NetworkFileType(String id) {
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
    public static NetworkFileType getEnumById(String enumId) {
        if (enumId == null) {
            return null;
        }
        for (NetworkFileType enumElem : NetworkFileType.values()) {
            if (enumElem.getId().equals(enumId)) {
                return enumElem;
            }
        }
        return null;
    }
}
