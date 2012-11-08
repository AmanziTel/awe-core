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

package org.amanzi.awe.nem.export;

import org.amanzi.neo.models.network.INetworkModel.INetworkElementType;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SynonymsWrapper implements Comparable<SynonymsWrapper> {

    private String header;

    private final String property;

    private final String type;

    /**
     * @param string
     * @param string2
     * @param value
     */
    public SynonymsWrapper(final String type, final String property, final String header) {
        this.type = type;
        this.property = property;
        this.header = header;
    }

    @Override
    public int compareTo(final SynonymsWrapper comparable) {
        try {
            INodeType comparableType = NodeTypeManager.getInstance().getType(comparable.getType());
            INodeType current = NodeTypeManager.getInstance().getType(type);
            if (comparableType instanceof INetworkElementType && current instanceof INetworkElementType) {
                NetworkElementType currentType = (NetworkElementType)current;
                NetworkElementType comparableNType = (NetworkElementType)comparableType;
                if (currentType.getIndex() > comparableNType.getIndex()) {
                    return 1;
                } else if (currentType.getIndex() < comparableNType.getIndex()) {
                    return -1;
                }
            }
        } catch (NodeTypeNotExistsException e) {
            return -1;
        }
        return 0;
    }

    /**
     * @return Returns the header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param header The header to set.
     */
    public void setHeader(final String header) {
        this.header = header;
    }
}
