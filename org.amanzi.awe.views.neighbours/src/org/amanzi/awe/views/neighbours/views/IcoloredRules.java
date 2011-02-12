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

package org.amanzi.awe.views.neighbours.views;

import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * colored rules 
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public interface IcoloredRules {
    public static final RGB RGB_MAIN=new RGB(0,0,255);  
    /**
     * Gets the color of node
     *
     * @param visualNode the visual node
     * @return the color or null if this node not handled by current rule
     */
    RGB getColor(Node visualNode);
}
