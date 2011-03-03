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

package org.amanzi.awe.statistics.template;


/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class Threshold {
    private Number thresholdValue;
    private Condition condition;
    public Threshold(){
        
    }
    /**
     * @param thresholdValue
     * @param condition
     */
    public Threshold(Number thresholdValue, Condition condition) {
        this.thresholdValue = thresholdValue;
        this.condition = condition;
    }
    /**
     * @param thresholdValue
     * @param condition
     */
    public Threshold(Number thresholdValue, String condition) {
        this.thresholdValue = thresholdValue;
        this.condition = Condition.findConditionByText(condition);
    }

    /**
     * @return Returns the thresholdValue.
     */
    public Number getThresholdValue() {
        return thresholdValue;
    }

    /**
     * @param thresholdValue The thresholdValue to set.
     */
    public void setThresholdValue(Number thresholdValue) {
        this.thresholdValue = thresholdValue;
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
    @Override
    public String toString() {
        return String.format("Threshold[alert %s %s]", condition.getText(),thresholdValue);
    }

}
