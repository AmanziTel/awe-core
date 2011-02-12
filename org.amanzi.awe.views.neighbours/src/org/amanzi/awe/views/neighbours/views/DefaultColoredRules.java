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

import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Default neighbour colored rules
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class DefaultColoredRules implements IcoloredRules {
    public static final RGB RGB_OTHERS = new RGB(255, 255, 255);
    public static final RGB RGB_GSM = new RGB(255, 0, 0);
    private final Node mainNode;

    private final Set<Node> outgoingNode;
    private DatasetService ds;
    private INodeType type;

    public DefaultColoredRules(Node mainNode, Set<Node> outgoingNode) {
        this.mainNode = mainNode;
        this.outgoingNode = new HashSet<Node>();
        this.outgoingNode.addAll(outgoingNode);
        ds=NeoServiceFactory.getInstance().getDatasetService();
        type=ds.getNodeType(mainNode);
        
    }

    @Override
    public RGB getColor(Node visualNode) {
        if (mainNode.equals(visualNode)) {
            return IcoloredRules.RGB_MAIN;
        } else if (outgoingNode.contains(visualNode)) {
            return RGB_GSM;
        } else {
            return type.equals(ds.getNodeType(visualNode))?RGB_OTHERS:null;
        }
    }

}
