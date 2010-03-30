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

package org.amanzi.awe.views.calls.statistics;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class GroupCallConstants implements IStatisticsConstants {

    public final float GROUP_CALL_CONN_TIME_P1 = 0;
    public final float GROUP_CALL_CONN_TIME_P2 = 0.125f;
    public final float GROUP_CALL_CONN_TIME_P3 = 0.25f;
    public final float GROUP_CALL_CONN_TIME_P4 = 0.375f;

    public final float GROUP_CALL_CONN_TIME_L1 = 0.5f;
    public final float GROUP_CALL_CONN_TIME_L2 = 0.75f;
    public final float GROUP_CALL_CONN_TIME_L3 = 1f;
    public final float GROUP_CALL_CONN_TIME_L4 = 2f;

    public final float GROUP_CALL_CONN_TIME_LIMIT = 5;

    public final float GROUP_CALL_DURATION_TIME = 20;

    public final float GROUP_CALL_QUAL_LIMIT = 2.0f;
    public final float GROUP_CALL_QUAL_MIN = -0.5f;
    public final float GROUP_CALL_QUAL_MAX = 4.5f;

    public final float GROUP_CALL_QUAL_P1 = 4.0f;
    public final float GROUP_CALL_QUAL_P2 = 3.5f;
    public final float GROUP_CALL_QUAL_P3 = 3.0f;
    public final float GROUP_CALL_QUAL_P4 = 2.5f;

    public final float GROUP_CALL_QUAL_L1 = 2.4f;
    public final float GROUP_CALL_QUAL_L2 = 2.3f;
    public final float GROUP_CALL_QUAL_L3 = 2.2f;

    public final float GROUP_CALL_DELAY_P1 = 0;
    public final float GROUP_CALL_DELAY_P2 = 150;
    public final float GROUP_CALL_DELAY_P3 = 250;
    public final float GROUP_CALL_DELAY_P4 = 300;

    public final float GROUP_CALL_DELAY_L1 = 350;
    public final float GROUP_CALL_DELAY_L2 = 400;
    public final float GROUP_CALL_DELAY_L3 = 450;
    public final float GROUP_CALL_DELAY_L4 = 550;

    /**
     * @return Returns the indivCallConnTimeP1.
     */
    public float getCallConnTimeP1() {
        return GROUP_CALL_CONN_TIME_P1;
    }

    /**
     * @return Returns the indivCallConnTimeP2.
     */
    public float getCallConnTimeP2() {
        return GROUP_CALL_CONN_TIME_P2;
    }

    /**
     * @return Returns the indivCallConnTimeP3.
     */
    public float getCallConnTimeP3() {
        return GROUP_CALL_CONN_TIME_P3;
    }

    /**
     * @return Returns the indivCallConnTimeP4.
     */
    public float getCallConnTimeP4() {
        return GROUP_CALL_CONN_TIME_P4;
    }

    /**
     * @return Returns the indivCallConnTimeL1.
     */
    public float getCallConnTimeL1() {
        return GROUP_CALL_CONN_TIME_L1;
    }

    /**
     * @return Returns the indivCallConnTimeL2.
     */
    public float getCallConnTimeL2() {
        return GROUP_CALL_CONN_TIME_L2;
    }

    /**
     * @return Returns the indivCallConnTimeL3.
     */
    public float getCallConnTimeL3() {
        return GROUP_CALL_CONN_TIME_L3;
    }

    /**
     * @return Returns the indivCallConnTimeL4.
     */
    public float getCallConnTimeL4() {
        return GROUP_CALL_CONN_TIME_L4;
    }

    /**
     * @return Returns the indivCallConnTimeLimit.
     */
    public float getCallConnTimeLimit() {
        return GROUP_CALL_CONN_TIME_LIMIT;
    }

    /**
     * @return Returns the indivCallDurationTime.
     */
    public float getIndivCallDurationTime() {
        return GROUP_CALL_DURATION_TIME;
    }

    /**
     * @return Returns the indivCallQualLimit.
     */
    public float getIndivCallQualLimit() {
        return GROUP_CALL_QUAL_LIMIT;
    }

    /**
     * @return Returns the indivCallQualMin.
     */
    public float getIndivCallQualMin() {
        return GROUP_CALL_QUAL_MIN;
    }

    /**
     * @return Returns the indivCallQualMax.
     */
    public float getIndivCallQualMax() {
        return GROUP_CALL_QUAL_MAX;
    }

    /**
     * @return Returns the indivCallQualP1.
     */
    public float getIndivCallQualP1() {
        return GROUP_CALL_QUAL_P1;
    }

    /**
     * @return Returns the indivCallQualP2.
     */
    public float getIndivCallQualP2() {
        return GROUP_CALL_QUAL_P2;
    }

    /**
     * @return Returns the indivCallQualP3.
     */
    public float getIndivCallQualP3() {
        return GROUP_CALL_QUAL_P3;
    }

    /**
     * @return Returns the indivCallQualP4.
     */
    public float getIndivCallQualP4() {
        return GROUP_CALL_QUAL_P4;
    }

    /**
     * @return Returns the indivCallQualL1.
     */
    public float getIndivCallQualL1() {
        return GROUP_CALL_QUAL_L1;
    }

    /**
     * @return Returns the indivCallQualL2.
     */
    public float getIndivCallQualL2() {
        return GROUP_CALL_QUAL_L2;
    }

    /**
     * @return Returns the indivCallQualL3.
     */
    public float getIndivCallQualL3() {
        return GROUP_CALL_QUAL_L3;
    }

    /**
     * @return Returns the indivCallDelayP1.
     */
    public float getIndivCallDelayP1() {
        return GROUP_CALL_DELAY_P1;
    }

    /**
     * @return Returns the indivCallDelayP2.
     */
    public float getIndivCallDelayP2() {
        return GROUP_CALL_DELAY_P2;
    }

    /**
     * @return Returns the indivCallDelayP3.
     */
    public float getIndivCallDelayP3() {
        return GROUP_CALL_DELAY_P3;
    }

    /**
     * @return Returns the indivCallDelayP4.
     */
    public float getIndivCallDelayP4() {
        return GROUP_CALL_DELAY_P4;
    }

    /**
     * @return Returns the indivCallDelayL1.
     */
    public float getIndivCallDelayL1() {
        return GROUP_CALL_DELAY_L1;
    }

    /**
     * @return Returns the indivCallDelayL2.
     */
    public float getIndivCallDelayL2() {
        return GROUP_CALL_DELAY_L2;
    }

    /**
     * @return Returns the indivCallDelayL3.
     */
    public float getIndivCallDelayL3() {
        return GROUP_CALL_DELAY_L3;
    }

    /**
     * @return Returns the indivCallDelayL4.
     */
    public float getIndivCallDelayL4() {
        return GROUP_CALL_DELAY_L4;
    }
}
