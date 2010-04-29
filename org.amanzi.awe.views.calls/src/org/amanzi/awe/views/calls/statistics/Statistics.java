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

package org.amanzi.awe.views.calls.statistics;

import java.util.ArrayList;
import java.util.HashMap;

import org.amanzi.awe.views.calls.enums.StatisticsHeaders;
import org.neo4j.graphdb.Node;

/**
 * Class for calculating statistics
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class Statistics extends HashMap<StatisticsHeaders, Object> {
    
    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;
    
    /*
     * Map for storing source nodes for each statistics element
     */
    private HashMap<StatisticsHeaders, ArrayList<Node>> sourceNodes = new HashMap<StatisticsHeaders, ArrayList<Node>>();

    /**
     * Updates Statistics and source node
     *
     * @param header header of Statistics
     * @param value value to update
     * @param sourceNode affected node
     */
    public void updateHeaderWithCall(StatisticsHeaders header, Object value, Node sourceNode) {
        boolean added = false;
        if (value!=null) {
            switch (header.getType()) {
            case COUNT:
                added = updateHeaderCount(header, (Integer)value);
                break;
            case SUM:
                added = updateHeaderSum(header, (Float)value);
                break;
            case MIN:
                added = updateHeaderMin(header, (Float)value);
                break;
            case MAX:
                added = updateHeaderMax(header, (Float)value);
                break;
            }
        }
        if (added) {
            updateSourceNodes(header, sourceNode);
        }
    }
    
    /**
     * Updates Statistics without updating source nodes
     *
     * @param header header of Statistics
     * @param value value to update
     */
    public void updateHeader(StatisticsHeaders header, Object value) {
        switch (header.getType()) {
        case COUNT:
            updateHeaderCount(header, (Integer)value);
            break;
        case SUM:
            updateHeaderSum(header, (Float)value);
            break;
        case MIN:
            updateHeaderMin(header, (Float)value);
            break;
        case MAX:
            updateHeaderMax(header, (Float)value);
            break;      
        }
    }
    
    /**
     * Copies all Source node to current statistics
     *
     * @param header header of Statistics
     * @param additionalNodes additional source nodes
     */
    public void copyAllSourceNodes(StatisticsHeaders header, ArrayList<Node> additionalNodes) {
        if (additionalNodes != null) {
            ArrayList<Node> nodes = sourceNodes.get(header);
            if (nodes == null) {
                nodes = new ArrayList<Node>();
                sourceNodes.put(header, nodes);
            }
            nodes.addAll(additionalNodes);
        }
    }
    
    void updateSourceNodes(StatisticsHeaders header, Node source) {
        ArrayList<Node> nodes = sourceNodes.get(header);
        if (nodes == null) {
            nodes = new ArrayList<Node>();
            sourceNodes.put(header, nodes);
        }
        nodes.add(source);
    }
    
    private boolean updateHeaderCount(StatisticsHeaders header, int count) {
        Integer oldCount = (Integer)get(header);
        if (oldCount == null) {
            put(header, count);
        }
        else {
            put(header, oldCount + count);
        }
        
        return true;
    }
    
    private boolean updateHeaderSum(StatisticsHeaders header, float count) {
        Float oldCount = (Float)get(header);
        if (oldCount == null) {
            put(header, count);
        }
        else {
            put(header, oldCount + count);
        }
        
        return true;
    }
    
    private boolean updateHeaderMax(StatisticsHeaders header, float newMax) {
        Float oldMax = (Float)get(header);
        if (oldMax == null) {
            put(header, newMax);
            return true;
        }
        else if (newMax > oldMax) {
            put(header, newMax);
            return true;
        }
        
        return false;
    }
    
    private boolean updateHeaderMin(StatisticsHeaders header, float newMin) {
        Float oldMin = (Float)get(header);
        if (oldMin == null) {
            put(header, newMin);
            return true;
        }
        else if (newMin < oldMin) {
            put(header, newMin);
            return true;
        }
        
        return false;
    }
    
    public ArrayList<Node> getAllAffectedCalls(StatisticsHeaders header) {
        return sourceNodes.get(header);
    }
    
    
}