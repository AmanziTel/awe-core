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

package org.amanzi.awe.distribution.dto.impl;

import org.amanzi.awe.distribution.dto.IAggregationRelation;
import org.amanzi.neo.impl.dto.Relation;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AggregationRelation extends Relation implements IAggregationRelation {

    private double value;

    private int count;

    /**
     * @param relation
     */
    public AggregationRelation(final Relationship relation) {
        super(relation);
        // TODO Auto-generated constructor stub
    }

    /**
     * @return Returns the value.
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    @Override
    public void setValue(final double value) {
        this.value = value;
    }

    /**
     * @return Returns the count.
     */
    @Override
    public int getCount() {
        return count;
    }

    /**
     * @param count The count to set.
     */
    @Override
    public void setCount(final int count) {
        this.count = count;
    }

}
