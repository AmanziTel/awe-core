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

import static org.amanzi.awe.afp.ericsson.Parameters.ARFCN;
import static org.amanzi.awe.afp.ericsson.Parameters.AVMEDIAN;
import static org.amanzi.awe.afp.ericsson.Parameters.AVPERCENTILE;
import static org.amanzi.awe.afp.ericsson.Parameters.CELL_NAME;
import static org.amanzi.awe.afp.ericsson.Parameters.DAY;
import static org.amanzi.awe.afp.ericsson.Parameters.FILE_FORMAT;
import static org.amanzi.awe.afp.ericsson.Parameters.HOUR;
import static org.amanzi.awe.afp.ericsson.Parameters.MINUTE;
import static org.amanzi.awe.afp.ericsson.Parameters.MONTH;
import static org.amanzi.awe.afp.ericsson.Parameters.NOOFMEAS;
import static org.amanzi.awe.afp.ericsson.Parameters.NUMBER_OF_FREQUENCIES;
import static org.amanzi.awe.afp.ericsson.Parameters.PERCENTILE_VALUE;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_INFORMATION_RIR;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_LENGTH;
import static org.amanzi.awe.afp.ericsson.Parameters.RECORD_TYPE;
import static org.amanzi.awe.afp.ericsson.Parameters.RID;
import static org.amanzi.awe.afp.ericsson.Parameters.SECOND;
import static org.amanzi.awe.afp.ericsson.Parameters.TTIME;
import static org.amanzi.awe.afp.ericsson.Parameters.YEAR;

/**
 * @author Kasnitskij_V
 * class to represent of RIR- records
 */
public enum RIRRecords implements IRecords {
	ADMINISTRATIVE(40, FILE_FORMAT, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, RECORD_INFORMATION_RIR, RID, TTIME, PERCENTILE_VALUE),
	RADIO_INTERFERENCE_RECORDING_CELL_DATA(41, CELL_NAME, NUMBER_OF_FREQUENCIES, new BlockParameters(150, ARFCN, AVMEDIAN, AVPERCENTILE, NOOFMEAS));
	
	// record id
	private final int id;
	
	private IParameters[] allParameters = null; 
	
	// constructor to RIR- record
	private RIRRecords(int id, IParameters...parameters) {
		
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
     * @return
     */
	public IParameters[] getAllParameters7Version() {
		return getAllParameters();
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
    public static RIRRecords findById(int id) {
        for (RIRRecords record : values()) {
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
