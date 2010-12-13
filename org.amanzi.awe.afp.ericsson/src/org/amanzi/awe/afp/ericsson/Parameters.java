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
package org.amanzi.awe.afp.ericsson;

/**
 * @author Kasnitskij_V
 *
 */
public enum Parameters {
    RECORD_TYPE(1),
    RECORD_LENGTH(2),
    FILE_FORMAT(1),
    YEAR(1),
    MONTH(1),
    DAY(1),
    HOUR(1),
    MINUTE(1),
    SECOND(1),
    RECORD_INFORMATION(4),
    RID(7),
    START_DATE_YEAR(1),
    START_DATE_MONTH(1),
    START_DATE_DAY(1),
    START_TIME_HOUR(1),
    START_TIME_MINUTE(1),
    START_TIME_SECOND(1),
    ABSS(1),
    RELSS_PLUS_MINUS(1),
    RELSS(1),
    RELSS2_PLUS_MINUS(1),
    RELSS2(1),
    RELSS3_PLUS_MINUS(1),
    RELSS3(1),
    RELSS4_PLUS_MINUS(1),
    RELSS4(1),
    RELSS5_PLUS_MINUS(1),
    RELSS5(1),
    NCELLTYPE(1),
    NUMFREQ(1),
    SEGTIME(2),
    TERMINATION_REASON(1),
    RECTIME(2),
    ECNOABSS(1),
    NUCELLTYPE(1),
    TFDDMRR(1),
    NUMUMFI(1),
    
    CELL_NAME(8),
    CHGR(1),
    REP(4),
    REPHR(4),
    REPUNDEFGSM(4),
    AVSS(1),
    
    BSIC(1),
    ARFCN(2),
    IS_NEIGHBOURING_CELL(1),
    RECTIMEARFCN(2),
    REPARFCN(4),
    TIMES(4),
    NAVSS(1),
    TIMES1(4),
    NAVSS1(1),
    TIMES2(4),
    NAVSS2(1),
    TIMES3(4),
    NAVSS3(1),
    TIMES4(4),
    NAVSS4(1),
    TIMES5(4),
    NAVSS5(1),
    TIMES6(4),
    NAVSS6(1),
    TIMESRELSS(4),
    TIMESRELSS2(4),
    TIMESRELSS3(4),
    TIMESRELSS4(4),
    TIMESRELSS5(4),
    TIMESABSS(4),
    TIMESALONE(4),
    
    //.....
    
    REPUNDEFUMTS(4),
    REPUMTS(4),
    
    MFDDARFCN(2),
    MSCRCODE(2),
    DIVERSITY(1),
    RECTIMEUMFI(2),
    REPUMFI(4),
    UTIMES(4),
    AVECNO(1),
    UTIMES1(4),
    AVECNO1(1),
    UTIMES2(4),
    AVECNO2(1),
    UTIMES3(4),
    AVECNO3(1),
    UTIMESECNOABSS(4),
    UTIMESALONE(4);
    
    
    private final int bytes;
    private final Rules rule;

    /**
     * constructor
     */
    private Parameters(int bytes) {
        this.bytes = bytes;
        rule = Rules.INTEGER;
    }

    /**
     * constructor
     * 
     * @param bits length
     * @param rule - interpretation rule
     */
    private Parameters(int bytes, Rules rule) {
        this.bytes = bytes;
        this.rule = rule;
    }

    /**
     * <p>
     * interpretation rules
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public enum Rules {
        INTEGER, STRING, LONG, BITARRAY;
    }

    /**
     * @return
     */
    public int getBytesLen() {
        return bytes;
    }



    /**
     * @return Returns the rule.
     */
    public Rules getRule() {
        return rule;
    }

    /**
     *
     * @return
     */
    public boolean firstBitIsError() {
        return rule!=Rules.STRING&&rule!=Rules.BITARRAY;
    }
    
}
