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

package org.amanzi.awe.views.calls;

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.core.enums.CallProperties;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * CallHandler calculate statistic
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CallHandler {
	
	private static final double ROUND_CONSTANT = 1000;
	
    private final CallProperties aProperties;
    private int count;
    private Double sum;
    private Double min;
    private Double max;
    private boolean isNumber;
    private Map<Object, Integer> mappedCount;

    public CallHandler(CallProperties aProperties) {
        this.aProperties = aProperties;
        clear();
        if (aProperties.needMappedCount()) {
            mappedCount = new HashMap<Object, Integer>();
        } else {
            mappedCount = null;
        }

    }

    public void clear() {
        count = 0;
        sum = 0d;
        min = null;
        max = null;
        isNumber = true;
    }

    public void analyseNode(Node node) {
        if (!node.hasProperty(aProperties.getId())) {
            return;
        }
        count++;
        if (aProperties.needMappedCount()) {
            Object property = node.getProperty(aProperties.getId());
            Integer count = mappedCount.get(property);
            if (count == null) {
                count = 0;
            }
            count++;
            mappedCount.put(property, count);
        }
        if (!isNumber()) {
            return;
        }
        Object property = node.getProperty(aProperties.getId());

        if (property instanceof Number) {
            double numProperties = ((Number)property).doubleValue();
            sum += numProperties;
            max = max == null ? numProperties : Math.max(max, numProperties);
            min = min == null ? numProperties : Math.min(min, numProperties);

        } else {
            setNumber(false);
        }
    }

    public Integer getMappedCount(Object property) {
        if (aProperties.needMappedCount()) {
            Integer value = mappedCount.get(property);
            return value == null ? 0 : value;
        } else {
            return null;
        }
    }
    public Double getAggregateValue(AggregateCall aggregateType) {
        Double result;
        switch (aggregateType) {
        case AVERAGE:
            result = !isNumber() || count == 0 ? Double.NaN : round(sum / count);
            break;
        case COUNT:
            result = Double.valueOf(count);
            break;
        case MAX:
            result = !isNumber() || max == null ? Double.NaN : round(max);
            break;
        case MIN:
            result = !isNumber() || min == null ? Double.NaN : round(min);
            break;
        case SUM:
            result = !isNumber() ? Double.NaN : sum;
            break;
        default:
            result = Double.NaN;
            break;
        }
        return result;
    }
    
    private Double round(double aValue){
    	return ((int)(aValue*ROUND_CONSTANT+0.5))/ROUND_CONSTANT;
    }

    /**
     * @return Returns the isNumber.
     */
    public boolean isNumber() {
        return isNumber;
    }

    /**
     * @param isNumber The isNumber to set.
     */
    public void setNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
