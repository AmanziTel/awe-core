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

package org.amanzi.awe.neighbours.gpeh;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class Calculator3GPPdBm {

    public enum ValueType {
        RSCP(-115D, 25D, 1D, "RSCP"),
        ECNO(-24D, 0D, 0.5D, "EcNo"),
        UETXPOWER(-50D, 33D, 1D, "UeTxPower"),
        RTWP(-112D, -50D, 0.1D, "RTWP");
        private final double minValue;
        private final double maxValue;
        private final double step;
        private final String stringVal;

        /**
         * @param minValue
         * @param maxValue
         * @param step
         */
        private ValueType(double minValue, double maxValue, double step, String stringVal) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.stringVal = stringVal;
        }

        public String getValue(int val3GPP) {
            double curVal = val3GPP * step + minValue;
            if (getLeft(curVal) == null)
                return stringVal + " < " + minValue;
            else if (getRight(curVal) == null)
                return maxValue + " <= " + stringVal;
            return getLeft(curVal) + " <= " + stringVal + " < " + getRight(curVal);
        }

        public Double getLeft(double curVal) {
            if (curVal >= minValue)
                return curVal;
            return null;
        }

        public Double getRight(double curVal) {
            if (curVal < maxValue)
                return curVal - step;
            return null;
        }
    }
}
