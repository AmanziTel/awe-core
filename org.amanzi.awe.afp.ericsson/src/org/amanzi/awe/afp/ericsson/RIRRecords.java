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
public enum RIRRecords implements IRecords {
	ADMINISTRATIVE(40, FILE_FORMAT, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, RECORD_INFORMATION, RID, TTIME, PERCENTILE_VALUE),
	RADIO_INTERFERENCE_RECORDING_CELL_DATA(41, CELL_NAME, NUMBER_OF_FREQUENCIES, new BlockParameters(150, ARFCN, AVMEDIAN, AVPERCENTILE, NOOFMEAS));
	
	// record id
	private final int id;
	
	private IParameters[] allParameters = null; 
	
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
