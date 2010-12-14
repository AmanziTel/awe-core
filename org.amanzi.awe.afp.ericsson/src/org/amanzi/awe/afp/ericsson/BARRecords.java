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

import static org.amanzi.awe.afp.ericsson.Parameters.*;

/**
 * @author Kasnitskij_V
 *
 */
public enum BARRecords {
	ADMINISTRATIVE(50, FILE_FORMAT, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, RECORD_INFORMATION, RID, START_DATE_YEAR, START_DATE_MONTH, START_DATE_DAY, START_TIME_HOUR, START_TIME_MINUTE, START_TIME_SECOND, ABSS, RELSS_PLUS_MINUS, RELSS, RELSS2_PLUS_MINUS, RELSS2, RELSS3_PLUS_MINUS, RELSS3, RELSS4_PLUS_MINUS, RELSS4, RELSS5_PLUS_MINUS, RELSS5, NCELLTYPE, NUMFREQ, SEGTIME, TERMINATION_REASON, RECTIME, ECNOABSS, NUCELLTYPE, TFDDMRR, NUMUMFI),
	ACTIVE_BALIST_RECORDING_CELL_DATA(51, CELL_NAME, CHGR, REP, REPHR, REPUNDEFGSM, AVSS),
	ACTIVE_BALIST_RECORDING_NEIGHBOURING_CELL_DATA(52, CELL_NAME, CHGR, BSIC, ARFCN, IS_NEIGHBOURING_CELL, RECTIMEARFCN, REPARFCN, TIMES, NAVSS, TIMES1, NAVSS1, TIMES2, NAVSS2, TIMES3, NAVSS3, TIMES4, NAVSS4, TIMES5, NAVSS5, TIMES6, NAVSS6, TIMESRELSS, TIMESRELSS2, TIMESRELSS3, TIMESRELSS4, TIMESRELSS5, TIMESABSS, TIMESALONE),
	ACTIVE_BALIST_RECORDING_FREQUENCIES_NOT_REPORTED_DATA(53, CELL_NAME, ARFCN, RECTIMEARFCN, REPARFCN),
	ACTIVE_BALIST_RECORDING_UMTS_CELL_DATA(54, CELL_NAME, REPUNDEFUMTS, REPUMTS),
	ACTIVE_BALIST_RECORDING_NEIGHBOURING_UMTS_CELL_DATA(55, CELL_NAME, MFDDARFCN, MSCRCODE, DIVERSITY, IS_NEIGHBOURING_CELL, RECTIMEUMFI, REPUMFI, UTIMES, AVECNO, UTIMES1, AVECNO1, UTIMES2, AVECNO2, UTIMES3, AVECNO3, UTIMESECNOABSS, UTIMESALONE),
	ACTIVE_BALIST_RECORDING_UMFIS_NOT_REPORTED_DATA(56, CELL_NAME, MFDDARFCN, MSCRCODE, DIVERSITY, RECTIMEUMFI, REPUMFI);
	
	// record id
	private final int id;
	
	private Parameters[] allParameters = null; 
	
	private BARRecords(int id, Parameters...parameters) {
		
		this.id = id;
		
		int paramCount = 2;
		allParameters = new Parameters[parameters.length + paramCount];
		
		allParameters[0] = RECORD_TYPE;
		allParameters[1] = RECORD_LENGTH;
		
		for (Parameters parameter : parameters) {
			allParameters[paramCount++] = parameter;
		}
	}
	
    /**
     * get all parameters of record
     * 
     * @return
     */
    public Parameters[] getAllParameters() {
        return allParameters;
    }
    
    /**
     * find enum by id
     * @param id - event id
     * @return event or null
     */
    public static BARRecords findById(int id) {
        for (BARRecords record : values()) {
            if (record.id == id) {
                return record;                
            }
        }
        return null;
    }
    
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }
}

