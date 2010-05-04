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
package org.amanzi.awe.statistic;

import org.neo4j.graphdb.Node;

/**
 * 
 * <p>
 *  Implementation of IStatisticElementNode
 *  Comparable, equals and hashCode() only by getStartTime()!
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class StatisticElementNodeImpl implements IStatisticElementNode,Comparable<IStatisticElementNode> {

    
    /** The node. */
    private final Node node;


    /** The end time. */
    private final long endTime;
    
    /** The period. */
    private final CallTimePeriods period;
    
    /** The start time. */
    private final long startTime;
    
    /**
     * Instantiates a new statistic element node impl.
     *
     * @param node the node
     * @param endTime the end time
     * @param period the period
     * @param startTime the start time
     */
    public StatisticElementNodeImpl(Node node,CallTimePeriods period, long startTime, long endTime) {
        super();
        this.node = node;
        this.endTime = endTime;
        this.period = period;
        this.startTime = startTime;
    }
    
    /**
     * Gets the node.
     *
     * @return the node
     */
    @Override
    public Node getNode() {
        return node;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    @Override
    public long getEndTime() {
        return endTime;
    }

    /**
     * Gets the period.
     *
     * @return the period
     */
    @Override
    public CallTimePeriods getPeriod() {
        return period;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    @Override
    public long getStartTime() {
        return startTime;
    }



    @Override
    public int compareTo(IStatisticElementNode o) {
        return startTime<o.getStartTime() ? -1 : (startTime==o.getStartTime() ? 0 : 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(startTime ^ (startTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IStatisticElementNode other = (IStatisticElementNode)obj;
        if (startTime != other.getStartTime())
            return false;
        return true;
    }


    /**
     * Contains.
     *
     * @param time the time
     * @return true, if successful
     */
    public boolean contains(Long time) {
        return time>=startTime&&time<=endTime;
    }


}
