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

import static org.amanzi.awe.afp.ericsson.Parameters.ABSS;
import static org.amanzi.awe.afp.ericsson.Parameters.ARFCN;
import static org.amanzi.awe.afp.ericsson.Parameters.AVECNO;
import static org.amanzi.awe.afp.ericsson.Parameters.AVECNO1;
import static org.amanzi.awe.afp.ericsson.Parameters.AVECNO2;
import static org.amanzi.awe.afp.ericsson.Parameters.AVECNO3;
import static org.amanzi.awe.afp.ericsson.Parameters.AVSS;
import static org.amanzi.awe.afp.ericsson.Parameters.BSIC;
import static org.amanzi.awe.afp.ericsson.Parameters.CELL_NAME;
import static org.amanzi.awe.afp.ericsson.Parameters.CHGR;
import static org.amanzi.awe.afp.ericsson.Parameters.DAY;
import static org.amanzi.awe.afp.ericsson.Parameters.DIVERSITY;
import static org.amanzi.awe.afp.ericsson.Parameters.ECNOABSS;
import static org.amanzi.awe.afp.ericsson.Parameters.FILE_FORMAT;
import static org.amanzi.awe.afp.ericsson.Parameters.HOUR;
import static org.amanzi.awe.afp.ericsson.Parameters.IS_NEIGHBOURING_CELL;
import static org.amanzi.awe.afp.ericsson.Parameters.MFDDARFCN;
import static org.amanzi.awe.afp.ericsson.Parameters.MINUTE;
import static org.amanzi.awe.afp.ericsson.Parameters.MONTH;
import static org.amanzi.awe.afp.ericsson.Parameters.MSCRCODE;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS1;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS2;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS3;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS4;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS5;
import static org.amanzi.awe.afp.ericsson.Parameters.NAVSS6;
import static org.amanzi.awe.afp.ericsson.Parameters.NCELLTYPE;
import static org.amanzi.awe.afp.ericsson.Parameters.NUCELLTYPE;
import static org.amanzi.awe.afp.ericsson.Parameters.NUMFREQ;
import static org.amanzi.awe.afp.ericsson.Parameters.NUMUMFI;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_INFORMATION;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_LENGTH;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_TYPE;
import static org.amanzi.awe.afp.ericsson.Parameters.RECTIME;
import static org.amanzi.awe.afp.ericsson.Parameters.RECTIMEARFCN;
import static org.amanzi.awe.afp.ericsson.Parameters.RECTIMEUMFI;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS2;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS2_PLUS_MINUS;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS3;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS3_PLUS_MINUS;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS4;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS4_PLUS_MINUS;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS5;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS5_PLUS_MINUS;
import static org.amanzi.awe.afp.ericsson.Parameters.RELSS_PLUS_MINUS;
import static org.amanzi.awe.afp.ericsson.Parameters.REP;
import static org.amanzi.awe.afp.ericsson.Parameters.REPARFCN;
import static org.amanzi.awe.afp.ericsson.Parameters.REPHR;
import static org.amanzi.awe.afp.ericsson.Parameters.REPUMFI;
import static org.amanzi.awe.afp.ericsson.Parameters.REPUMTS;
import static org.amanzi.awe.afp.ericsson.Parameters.REPUNDEFGSM;
import static org.amanzi.awe.afp.ericsson.Parameters.REPUNDEFUMTS;
import static org.amanzi.awe.afp.ericsson.Parameters.RID;
import static org.amanzi.awe.afp.ericsson.Parameters.SECOND;
import static org.amanzi.awe.afp.ericsson.Parameters.SEGTIME;
import static org.amanzi.awe.afp.ericsson.Parameters.START_DATE_DAY;
import static org.amanzi.awe.afp.ericsson.Parameters.START_DATE_MONTH;
import static org.amanzi.awe.afp.ericsson.Parameters.START_DATE_YEAR;
import static org.amanzi.awe.afp.ericsson.Parameters.START_TIME_HOUR;
import static org.amanzi.awe.afp.ericsson.Parameters.START_TIME_MINUTE;
import static org.amanzi.awe.afp.ericsson.Parameters.START_TIME_SECOND;
import static org.amanzi.awe.afp.ericsson.Parameters.TERMINATION_REASON;
import static org.amanzi.awe.afp.ericsson.Parameters.TFDDMRR;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES1;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES2;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES3;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES4;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES5;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMES6;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESABSS;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESALONE;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESRELSS;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESRELSS2;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESRELSS3;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESRELSS4;
import static org.amanzi.awe.afp.ericsson.Parameters.TIMESRELSS5;
import static org.amanzi.awe.afp.ericsson.Parameters.TMBCR;
import static org.amanzi.awe.afp.ericsson.Parameters.TNCCPERM_BITMAP;
import static org.amanzi.awe.afp.ericsson.Parameters.TNCCPERM_VALIDITY_INDICATOR;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMES;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMES1;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMES2;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMES3;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMESALONE;
import static org.amanzi.awe.afp.ericsson.Parameters.UTIMESECNOABSS;
import static org.amanzi.awe.afp.ericsson.Parameters.YEAR;

/**
 * @author Kasnitskij_V
 * class to represent of BAR- records
 */
public enum BARRecords implements IRecords {
	ADMINISTRATIVE(50, FILE_FORMAT, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, RECORD_INFORMATION, RID, START_DATE_YEAR, START_DATE_MONTH, START_DATE_DAY, START_TIME_HOUR, START_TIME_MINUTE, START_TIME_SECOND, ABSS, RELSS_PLUS_MINUS, RELSS, RELSS2_PLUS_MINUS, RELSS2, RELSS3_PLUS_MINUS, RELSS3, RELSS4_PLUS_MINUS, RELSS4, RELSS5_PLUS_MINUS, RELSS5, NCELLTYPE, NUMFREQ, SEGTIME, TERMINATION_REASON, RECTIME, ECNOABSS, NUCELLTYPE, TFDDMRR, NUMUMFI),
	ACTIVE_BALIST_RECORDING_CELL_DATA(51, CELL_NAME, CHGR, REP, REPHR, REPUNDEFGSM, AVSS),
	ACTIVE_BALIST_RECORDING_NEIGHBOURING_CELL_DATA(52, CELL_NAME, CHGR, BSIC, ARFCN, IS_NEIGHBOURING_CELL, RECTIMEARFCN, REPARFCN, TIMES, NAVSS, TIMES1, NAVSS1, TIMES2, NAVSS2, TIMES3, NAVSS3, TIMES4, NAVSS4, TIMES5, NAVSS5, TIMES6, NAVSS6, TIMESRELSS, TIMESRELSS2, TIMESRELSS3, TIMESRELSS4, TIMESRELSS5, TIMESABSS, TIMESALONE),
	ACTIVE_BALIST_RECORDING_FREQUENCIES_NOT_REPORTED_DATA(53, CELL_NAME, ARFCN, RECTIMEARFCN, REPARFCN),
	ACTIVE_BALIST_RECORDING_UMTS_CELL_DATA(54, CELL_NAME, REPUNDEFUMTS, REPUMTS),
	ACTIVE_BALIST_RECORDING_NEIGHBOURING_UMTS_CELL_DATA(55, CELL_NAME, MFDDARFCN, MSCRCODE, DIVERSITY, IS_NEIGHBOURING_CELL, RECTIMEUMFI, REPUMFI, UTIMES, AVECNO, UTIMES1, AVECNO1, UTIMES2, AVECNO2, UTIMES3, AVECNO3, UTIMESECNOABSS, UTIMESALONE),
	ACTIVE_BALIST_RECORDING_UMFIS_NOT_REPORTED_DATA(56, CELL_NAME, MFDDARFCN, MSCRCODE, DIVERSITY, RECTIMEUMFI, REPUMFI);
	
	// record id
	private final int id;
	
	private IParameters[] allParameters = null; 
	
	// constructor to BAR- record
	private BARRecords(int id, IParameters...parameters) {
		
		this.id = id;
		
		int paramCount = 2;
		allParameters = new IParameters[parameters.length + paramCount];
		
		allParameters[0] = RECORD_TYPE;
		allParameters[1] = RECORD_LENGTH;
		
		for (IParameters parameter : parameters) {
			allParameters[paramCount++] = parameter;
		}
	}
	
	/**
     * get additional parameters of record
     * 
     * @return all parameters to 7th version
     */
	public IParameters[] getAllParameters7Version() {
		int paramCount = getAllParameters().length;
		IParameters[] parameters7version = new IParameters[paramCount + 3];
		for (int i = 0; i < paramCount; i++) {
			parameters7version[i] = getAllParameters()[i];
		}
		parameters7version[paramCount] = TNCCPERM_VALIDITY_INDICATOR;
		parameters7version[paramCount + 1] = TNCCPERM_BITMAP;
		parameters7version[paramCount + 2] = TMBCR;
		
		return parameters7version;
	}
	
	
    /**
     * get all parameters of record
     * 
     * @return
     */
    public IParameters[] getAllParameters() {
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

