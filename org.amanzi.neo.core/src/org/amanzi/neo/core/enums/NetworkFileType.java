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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>
 * Enumeration of network file types
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum NetworkFileType {
    RADIO_SITE(EnumsMessages.NetworkFileType_RADIO_SITE,0),
    RADIO_SECTOR(EnumsMessages.NetworkFileType_RADIO_SECTOR,5),
    TRANSMISSION(EnumsMessages.NetworkFileType_TRANSMISSION,10),
    NEIGHBOUR(EnumsMessages.NetworkFileType_NEIGHBOUR,15),
    PROBE(EnumsMessages.NetworkFileType_PROBE,20),
    UTRAN(EnumsMessages.NetworkFileType_UTRAN,25),
    NOKIA_TOPOLOGY(EnumsMessages.NetworkFileType_NOKIA,30);
    private String id;
    private Integer order;

    /**
     * constructor
     * 
     * @param id node type ID
     * @param nonEditableProperties list of not editable properties
     */
    private NetworkFileType(String id, Integer orderValue) {
        this.id = id;
        order = orderValue;
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
    
    public static List<NetworkFileType> getAllTypesSorted(){
        List<NetworkFileType> result = Arrays.asList(values());
        Collections.sort(result, new Comparator<NetworkFileType>() {
            @Override
            public int compare(NetworkFileType o1, NetworkFileType o2) {
                return o1.order.compareTo(o2.order);
            }
        });
        return result;
    }
}
