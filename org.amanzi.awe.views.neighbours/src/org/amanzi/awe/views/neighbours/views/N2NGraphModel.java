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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.ui.IGraphModel;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.node2node.NodeToNodeRelationService;
import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Graph Model for N2N relation model
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class N2NGraphModel implements IGraphModel {
    private IcoloredRules colorRules;
    private Boolean drawLines;
    private Map<Node, Set<Node>> relMap = new HashMap<Node, Set<Node>>();
    
    public N2NGraphModel(Relationship relation, boolean showAllOutgoing, boolean drawLines) {
        this.drawLines = drawLines;
        NodeToNodeRelationService n2nService = NeoServiceFactory.getInstance().getNodeToNodeRelationService();
        Set<Node> outgoingNode = new HashSet<Node>();
        if (showAllOutgoing) {
            for (Relationship rel : n2nService.getOutgoingRelations(relation.getStartNode())) {
                outgoingNode.add(n2nService.findNodeFromProxy(rel.getEndNode()));
            }
        } else {
            outgoingNode.add(n2nService.findNodeFromProxy(relation.getEndNode()));
        }
        relMap.put(n2nService.findNodeFromProxy(relation.getStartNode()), outgoingNode);
        Entry<Node, Set<Node>> entry = relMap.entrySet().iterator().next();
        setColorRules(new DefaultColoredRules(entry.getKey(), entry.getValue()));
    }
    public N2NGraphModel(IcoloredRules colorRules, Map<Node,Set<Node> >maps,boolean drawLines) {
        this.colorRules = colorRules;
        this.drawLines = drawLines;
        relMap.putAll(maps);
    }

    @Override
    public RGB getColor(Node visualNode) {
        return colorRules == null ? null : colorRules.getColor(visualNode);
    }

    public IcoloredRules getColorRules() {
        return colorRules;
    }

    public void setColorRules(IcoloredRules colorRules) {
        this.colorRules = colorRules;
    }

    public void setDrawLines(Boolean drawLines) {
        this.drawLines = drawLines;
    }

    @Override
    public Set<Node> getOutgoingRelation(Node visualNode) {
        Set<Node> result;
        if (drawLines) {
            result=relMap.get(visualNode);
        } else {
            result = Collections.<Node> emptySet();
        }
        return result==null?Collections.<Node> emptySet():result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((colorRules == null) ? 0 : colorRules.hashCode());
        result = prime * result + ((drawLines == null) ? 0 : drawLines.hashCode());
        result = prime * result + ((relMap == null) ? 0 : relMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof N2NGraphModel)) {
            return false;
        }
        N2NGraphModel other = (N2NGraphModel)obj;
        if (colorRules == null) {
            if (other.colorRules != null) {
                return false;
            }
        } else if (!colorRules.equals(other.colorRules)) {
            return false;
        }
        if (drawLines == null) {
            if (other.drawLines != null) {
                return false;
            }
        } else if (!drawLines.equals(other.drawLines)) {
            return false;
        }
        if (relMap == null) {
            if (other.relMap != null) {
                return false;
            }
        } else if (!relMap.equals(other.relMap)) {
            return false;
        }
        return true;
    }
    @Override
    public Map<Node, Set<Node>> getOutgoingRelationMap() {
        return drawLines?Collections.unmodifiableMap(relMap):Collections.<Node, Set<Node>>emptyMap();
    }

}
