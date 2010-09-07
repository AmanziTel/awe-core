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

package org.amanzi.neo.loader.core;

import org.neo4j.graphdb.Node;

public class DatasetInfo {
    
    private long nodeCount = 0;
    
    private long maxTimestamp = Long.MIN_VALUE;
    
    private long minTimestamp = Long.MAX_VALUE;
    
    public DatasetInfo(Node datasetNode) {
        nodeCount = (Long)datasetNode.getProperty("count",0l);
        maxTimestamp = (Long)datasetNode.getProperty("max_timestamp",0l);
        minTimestamp = (Long)datasetNode.getProperty("min_timestamp",0l);
    }
    
    public DatasetInfo() {
        
    }
    
    public void updateTimestamp(long timestamp) {
        maxTimestamp = Math.max(maxTimestamp, timestamp);
        minTimestamp = minTimestamp>0?Math.min(minTimestamp, timestamp):timestamp;
    }
    
    public void increaseNodes() {
        nodeCount++;
    }
    
    public long getNodeCount() {
        return nodeCount;
    }
    
    public long getMaxTimestamp() {
        return maxTimestamp;
    }
    
    public long getMinTimestamp() {
        return minTimestamp;
    }
    
}