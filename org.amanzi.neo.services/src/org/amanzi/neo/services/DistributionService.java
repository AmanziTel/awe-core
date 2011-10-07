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

package org.amanzi.neo.services;

import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.services.enums.INodeType;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * Service to work with Distribution Structure
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionService extends NewAbstractService {

    private static final Logger LOGGER = Logger.getLogger(DistributionService.class);
    
    /*
     * Property of Root Aggregation Node. Name of Property to analyse
     */
    public static final String PROPERTY_NAME = "property_name";
    
    /**
     * Node Types for Distribution Database Structure
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum DistributionNodeTypes implements INodeType {
        ROOT_AGGREGATION, AGGREGATION_BAR;
        
        static {
            NodeTypeManager.registerNodeType(DistributionNodeTypes.class);
        }

        @Override
        public String getId() {
            return name().toLowerCase();
        }
        
    }
    
    /**
     * Searches for a Root Aggregation Node
     *
     * @param parentNode parent node of Distribution
     * @param distributionName name of Distribution
     * @return
     */
    public Node findRootAggregationNode(Node parentNode, String distributionName) {
        return null;
    }
    
    /**
     * Creates new Root Aggregation Node
     *
     * @param parentNode parent node of Distribution 
     * @param distributionName name of Distribution
     * @return
     */
    public Node createRootAggregationNode(Node parentNode, String distributionName) {
        return null;
    }
    
    /**
     * Searches for Aggregation Bars in Database
     *
     * @param rootAggregationNode
     * @return
     */
    public Iterable<Node> findAggregationBars(Node rootAggregationNode) {
        return null;
    }
    
    /**
     * Creates new Aggregation Bar Node in Database
     *
     * @param rootAggregationNode root node of Distribution Structure
     * @param previousBarNode previous Bar node
     * @param bar info about Bar
     */
    public Node createAggregationBarNode(Node rootAggregationNode, Node previousBarNode, IDistributionBar bar) {
        return null;
    }
    
}
