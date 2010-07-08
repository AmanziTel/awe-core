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
 * <p>
 * 3GPP to dBm calculator
 * </p>.
 *
 * @author NiCK
 * @since 1.0.0
 */
public class Calculator3GPPdBm {
    
    public static void main(String[] args) {
        for (int i=21;i<=104;i++){
            System.out.println(ValueType.UETXPOWER.getValue(i));
        }
    }

    /**
     * The Enum ValueType.
     */
    public enum ValueType {
        
        /** The RSCP. */
        RSCP(-115D, -25D, 1D, 0, "RSCP"),
        
        /** The ECNO. */
        ECNO(-24D, 0D, 0.5D, 0, "EcNo"),
        
        /** The UETXPOWER. */
        UETXPOWER(-49D, 33D, 1D, 21, "UeTxPower"),
        UL_INTERFERENCE(-112D, -50D, 0.1D, 0, "UlInterference"),
        DL_TX_CARRIER_POWER(0d,100d,0.1d,0,"DlTxCarrierPower"),
        HSDSCH_REQUIRED_POWER(0d,100d,0.1d,0,"HSDSCH_REQUIRED_POWER"),
        NON_HS_POWER(0d,100d,0.1d,0,"NON_HS_POWER");
        

        
        /** The min3 gpp. */
        private final int min3GPP;
        
        /** The min value. */
        private final double minValue;
        
        /** The max value. */
        private final double maxValue;
        
        /** The step. */
        private final double step;
        
        /** The string val. */
        private final String stringVal;

        /**
         * Instantiates a new value type.
         *
         * @param minValue the min value
         * @param maxValue the max value
         * @param step the step
         * @param min3GPP the min3 gpp
         * @param stringVal the string val
         */
        private ValueType(double minValue, double maxValue, double step, int min3GPP, String stringVal) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.step = step;
            this.min3GPP = min3GPP;
            this.stringVal = stringVal;
        }

        /**
         * Gets the value.
         *
         * @param val3GPP the val3 gpp
         * @return the value
         */
        public String getValue(int val3GPP) {
            double curVal = (val3GPP-min3GPP) * step + minValue;
            if (getLeft(curVal) == null)
                return stringVal + " < " + minValue;
            else if (getRight(curVal) == null)
                return maxValue + " <= " + stringVal;
            return getLeft(curVal) + " <= " + stringVal + " < " + getRight(curVal);
        }

        /**
         * Gets the min3 gpp.
         *
         * @return the min3 gpp
         */
        public int getMin3GPP() {
            return min3GPP;
        }

        /**
         * Gets the left.
         *
         * @param curVal the cur val
         * @return the left
         */
        public Double getLeft(double curVal) {
            if (curVal > minValue)
                return curVal-step;
            return null;
        }

        /**
         * Gets the right.
         *
         * @param curVal the cur val
         * @return the right
         */
        public Double getRight(double curVal) {
            if (curVal <= maxValue)
                return curVal ;
            return null;
        }
        public int getMax3GPP(){
            return (int)((maxValue-minValue)/step+min3GPP+1);
        }
    }
}
