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

package org.amanzi.awe.statistics.engine;

import java.util.Collection;
import java.util.List;

import org.amanzi.awe.statistics.CallTimePeriods;
import org.amanzi.awe.statistics.database.PrimaryTypeTraverser;
import org.amanzi.neo.services.utils.Pair;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public interface IDatasetService {
    Collection<Node> getAllNodes();
    Collection<Node> getNodes(Long startTime,Long endTime);
    Pair<Long,Long> getTimeBounds();
    CallTimePeriods getHighestPeriod();
    Long getTime(Node node);
//    Object getProperty(Node node,String propertyName);
    String getKeyProperty(Node node);
    PrimaryTypeTraverser getPrimaryTypeTraverser();
    Node getDatasetNode();
    boolean isDatasetCorrelated();
    
    

}
