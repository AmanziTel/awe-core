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

package org.amanzi.awe.filters.experimental;

import java.util.List;

import org.eclipse.core.internal.expressions.InstanceofExpression;

/**
 * Represents a simple filter for a given property like
 * <p>
 * property>=value
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Filter implements IFilter {
    private String property;
    private Object value;
    private Condition condition;

    public Filter(String property) {
        this.property = property;
    }

    @Override
    public boolean accept(Object value) {
        if (condition.equals(Condition.HAS_PROPERTY)) {
            return value!=null;
        }
        if (value instanceof Number) {
            double leftSideNumber = ((Number)value).doubleValue();
            if (this.value instanceof Number) {
                double rightSideNumber = ((Number)this.value).doubleValue();
                return checkCondition(leftSideNumber, rightSideNumber);
            } else if (this.value instanceof String) {
                final double rightSideNumber = Double.parseDouble((String)this.value);
                return checkCondition(leftSideNumber, rightSideNumber);
            } else if (this.value instanceof List) {
                if (condition.equals(Condition.IN)) {
                    List list = (List)this.value;
                    for (Object obj : list) {
                        if (obj instanceof Number) {
                            double rightSideNumber = ((Number)obj).doubleValue();
                            if (leftSideNumber == rightSideNumber)
                                return true;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    

    /**
     * @return Returns the property.
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property The property to set.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return Returns the condition.
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @param condition The condition to set.
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    private boolean checkCondition(double leftSideNumber, double rightSideNumber) {
        switch (condition) {
        case EQ:
            return leftSideNumber == rightSideNumber;
        case GE:
            return leftSideNumber >= rightSideNumber;
        case GT:
            return leftSideNumber > rightSideNumber;
        case LE:
            return leftSideNumber <= rightSideNumber;
        case LT:
            return leftSideNumber < rightSideNumber;
        case NE:
            return leftSideNumber != rightSideNumber;
        case HAS_PROPERTY:
            return true;
        case IN:
            return leftSideNumber == rightSideNumber;
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    
}
