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

package org.amanzi.neo.core.utils;

import org.neo4j.api.core.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class PropertyHeader {


    private final Node node;

    /**
     * Constructor
     * 
     * @param node - gis Node
     */
    public PropertyHeader(Node node) {
        this.node = node;
    }

    /**
     * get Numeric Fields of Neighbour
     * 
     * @param neighbourName name of neighbour
     * @return array or null
     */
    public String[] getNeighbourNumericFields(String neighbourName) {
        Node neighbour = NeoUtils.findNeighbour(node, neighbourName);
        if (neighbour == null) {
            return null;
        }
        String[] result = NeoUtils.getNumericFields(neighbour);
        return result;
    }

    /**
     * get Numeric Fields of current node
     * 
     * @return array or null
     */
    public String[] getNumericFields() {
        return NeoUtils.getNumericFields(node);
    }
}
