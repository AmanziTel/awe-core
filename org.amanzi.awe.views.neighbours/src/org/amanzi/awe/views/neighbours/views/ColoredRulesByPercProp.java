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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class ColoredRulesByPercProp implements IcoloredRules {
    
    private final Map<Node, RGB>colorMap=new HashMap<Node, RGB>();
    /**
     * @param colorMap
     */
    public ColoredRulesByPercProp(Map<Node, RGB> colorMap) {
        this.colorMap.putAll(colorMap);
    }

    @Override
    public RGB getColor(Node visualNode) {
        RGB result=colorMap.get(visualNode);
        return result==null?DefaultColoredRules.RGB_OTHERS:result;
    }

}
