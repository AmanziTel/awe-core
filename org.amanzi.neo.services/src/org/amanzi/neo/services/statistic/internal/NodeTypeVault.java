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

package org.amanzi.neo.services.statistic.internal;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.statistic.ChangeClassRule;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NodeTypeVault {
    private final String nodeType;
    private static final TraversalDescription PROPERTYS=Traversal.description().depthFirst().relationships(StatisticRelationshipTypes.NODE_TYPES, Direction.OUTGOING).uniqueness(Uniqueness.NONE).filter(Traversal.returnAllButStartNode()).prune(Traversal.pruneAfterDepth( 1));

    private HashMap<String,PropertyStatistics> propertyMap=new HashMap<String,PropertyStatistics>();
 private boolean isChanged;
private Object parent;
private Node vaultNode;
    /**
     * @param nodeType
     */
    public NodeTypeVault(String nodeType) {
        super();
        this.nodeType = nodeType;
        isChanged=false;
    }

    /**
     *
     * @param propertySet
     * @param propertyName
     * @return
     */
    public PropertyStatistics getPropertyStatistic(String propertyName) {
        PropertyStatistics result = propertyMap.get(propertyName);
        if (result==null){
            result=new PropertyStatistics(propertyName);
            propertyMap.put(propertyName, result);
        }
        return result;
    }


    public boolean registerProperty(String propertyName, Class klass, ChangeClassRule rule) {
        return getPropertyStatistic(propertyName).register(klass,rule);
    }

    /**
     *
     * @param vaultNode
     * @return
     */
    public static Map< String, NodeTypeVault> loadNodeTypes(Node vaultNode) {
        Map<String, NodeTypeVault> result=new HashMap<String, NodeTypeVault>();
        for (Path path:PROPERTYS.traverse(vaultNode)){
            String key= (String)path.endNode().getProperty(StatisticProperties.KEY);
            NodeTypeVault vault=new NodeTypeVault(key);
            result.put(key, vault);
            vault.loadVault(vaultNode,path.endNode());
         }
        return result;
    }

    /**
     *
     * @param vaultNode
     * @param endNode
     */
    private void loadVault(Node parent, Node vaultNode) {
        clearVault();
        this.parent = parent;
        this.vaultNode = vaultNode;
        propertyMap.putAll(PropertyStatistics.loadProperties(vaultNode));
        isChanged=false;
    }

    /**
     *
     */
    private void clearVault() {
        propertyMap.clear();
    }
    
}
