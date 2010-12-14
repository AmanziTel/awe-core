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
package org.amanzi.awe.afp.ericsson.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.Parameters;

/**
 * @author Kasnitskij_V
 *
 */
public class BARParser {
    public static void main(String[] args) throws IOException {
    	File file = new File("d://–¿¡Œ“¿/AFP SRS/AFP data/Network_1/BARFIL00-0000000012");
    	InputStream input = new FileInputStream(file);
    	BufferedInputStream stream = new BufferedInputStream(input);
    	parseRecord(stream);
    	
    	ArrayList<BARRecord> rec = (ArrayList<BARRecord>) getRecords();
    }
    
    private static List<BARRecord> records;
    
    public static void parseRecord(BufferedInputStream inputStream) throws IOException {
    	int administrative = 0;
    	boolean startFile = false, endFile = false;
    	records = new ArrayList<BARRecord>();
    	
    	while (inputStream.available() != 0) {
    		BARRecord barRecord = new BARRecord();
	    	int idRecordType = inputStream.read();
	    	BARRecords recordType = BARRecords.findById(idRecordType);
	    	
	    	if (recordType.toString().equals(BARRecords.ADMINISTRATIVE.toString())) {
	    		administrative++;
	    		if (administrative == 1) {
	    			startFile = true;
	    		}
	    		if (administrative == 2) {
	    			endFile = true;
	    		}
	    	}
	    	barRecord.record.setType(recordType);
	    	
	    	Parameters[] parameters = recordType.getAllParameters();
	    	for (Parameters parameter : parameters) {
	    		if (!parameter.toString().equals(Parameters.RECORD_TYPE.toString())) {
					byte data[] = new byte[parameter.getBytesLen()];
					inputStream.read(data, 0, parameter.getBytesLen());
					
					barRecord.record.addProperty(parameter, data);
	    		}
	    	}
	    	records.add(barRecord);
	    	if (startFile && endFile) {
	    		break;
	    	}
    	}
    }

	/**
	 * @param records the records to set
	 */
	public static void setRecords(List<BARRecord> records) {
		BARParser.records = records;
	}

	/**
	 * @return the records
	 */
	public static List<BARRecord> getRecords() {
		return records;
	}
}
