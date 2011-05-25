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

import org.amanzi.neo.services.statistic.internal.StatisticProperties;
import org.amanzi.neo.services.statistic.internal.StatisticRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kruglik_A
 * @since 1.0.0
 */
public class StatisticService extends AbstractService{
    
    public Node findStatRoot(Node root){
        Node statRoot;
        Relationship rel = root.getSingleRelationship(StatisticRelationshipTypes.STATISTIC_PROP,Direction.OUTGOING);
        if (rel==null)
            statRoot = null;
        else statRoot=rel.getEndNode();
        return statRoot;
    }
    
    public Node findOrCreateStatRoot(Node root){
        Node statRoot = findStatRoot(root);
        if (statRoot == null){
            statRoot=databaseService.createNode();
            statRoot.setProperty(StatisticProperties.KEY, "PROPERTIES");
            root.createRelationshipTo(statRoot, StatisticRelationshipTypes.STATISTIC_PROP);
        }
        return statRoot;
    }

}
