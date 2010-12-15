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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.BlockParameters;
import org.amanzi.awe.afp.ericsson.CountableParameters;
import org.amanzi.awe.afp.ericsson.DataType;
import org.amanzi.awe.afp.ericsson.IParameters;
import org.amanzi.awe.afp.ericsson.IRecords;
import org.amanzi.awe.afp.ericsson.Parameters;
import org.amanzi.awe.afp.ericsson.RIRRecords;
import org.amanzi.neo.loader.core.parser.BaseTransferData;
import org.amanzi.neo.loader.core.parser.CommonFilesParser;
import org.amanzi.neo.loader.core.saver.ISaver;

/**
 * @author Kasnitskij_V
 * Class to parsing of RAR- or BAR- data
 */
public class RecordsParser extends
		CommonFilesParser<BaseTransferData, RecordConfigData> {
	// method to testing
//	public static void main(String[] args) throws IOException {
//		File file = new File(
//				"d://–¿¡Œ“¿/AFP SRS/AFP data/network_2/BARFIL00-0000000002");
//		InputStream input = new FileInputStream(file);
//		BufferedInputStream stream = new BufferedInputStream(input);
//		
//		FileElement elem = new FileElement(file, "");
//		RecordsParser rp = new RecordsParser();
//		rp.dataType = DataType.BAR_DATA;
//		rp.parseElement(elem);
//		ArrayList<MainRecord> rec = (ArrayList<MainRecord>) getRecords();
// 	}

	private DataType dataType;
	
	// list of BAR-records
	private static List<MainRecord> records;

	/**
	 * @param records the records to set
	 */
	public static void setRecords(List<MainRecord> records) {
		RecordsParser.records = records;
	}

	/**
	 * @return the records
	 */
	public static List<MainRecord> getRecords() {
		return records;
	}

	@Override
	protected BaseTransferData getFinishData() {
		return null;
	}
	
    @Override
    public void init(RecordConfigData properties, ISaver<BaseTransferData> saver) {
        super.init(properties, saver);
        dataType = properties.getDataType();
    }

    /**
     * method to parsing file
     */
	@Override
	protected boolean parseElement(FileElement element) {
		//stream of data from file
		InputStream input = null;
		try {
			input = new FileInputStream(element.getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// buffered stream of data from file
		BufferedInputStream inputStream = new BufferedInputStream(input);
		
		// variable to be aware of what was the beginning and end of file
		// administrative = 1 - is the beginning of the file
		// administrative = 2 - is the end of the file
		int administrative = 0;
		// if administrative = 1 startFile will true
		// if administrative = 2 endFile will true
		boolean startFile = false, endFile = false;
		// records of BAR- of RIR- data
		records = new ArrayList<MainRecord>();

		try {
			// read file while it is possible
			while (inputStream.available() != 0) {
				// variable to represent one record from file
				MainRecord mainRecord = new MainRecord();
				// type of record. may be RIR- or BAR-
				IRecords recordType = null;
				// parameters of record
				IParameters[] parameters = null;

				// id of record
				int idRecordType = inputStream.read();
				// length of record
				int recordLength = inputStream.read() + inputStream.read();
				mainRecord.record.addProperty(Parameters.RECORD_TYPE,
						idRecordType);
				mainRecord.record.addProperty(Parameters.RECORD_LENGTH,
						recordLength);

				// get type of record
				recordType = findById(idRecordType);

				if (recordType.toString().equals(
						BARRecords.ADMINISTRATIVE.toString())) {
					if (recordLength == 49 || recordLength == 23) {
						parameters = recordType.getAllParameters();
					} else {
						parameters = recordType.getAllParameters7Version();
					}

					administrative++;
					if (administrative == 1) {
						startFile = true;
					}
					if (administrative == 2) {
						endFile = true;
					}
				} else {
					parameters = recordType.getAllParameters();
				}
				mainRecord.record.setType(recordType);

				// read data to all parameters from file
				for (IParameters parameter : parameters) {
					if (!parameter.toString().equals(
							Parameters.RECORD_TYPE.toString())
							&& !parameter.toString().equals(
									Parameters.RECORD_LENGTH.toString())) {

						if (parameter.isBlock()) {
							BlockParameters block = (BlockParameters) parameter;
							for (int i = 0; i < block.getCount(); i++) {
								for (int j = 0; j < block.getParameters().length; j++) {
									byte data[] = new byte[parameter.getBytesLen()];
									inputStream.read(data, 0, parameter.getBytesLen());

									mainRecord.record.addProperty(
											new CountableParameters(parameter, i), data);
								}
							}
						} else {
							byte data[] = new byte[parameter.getBytesLen()];
							inputStream.read(data, 0, parameter.getBytesLen());

							mainRecord.record.addProperty(parameter, data);
						}
					}
				}
				// add read data
				records.add(mainRecord);
				// if read all file then break from cycle
				if (startFile && endFile) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected BaseTransferData getInitData(RecordConfigData properties) {
		BaseTransferData data = new BaseTransferData();
        data.put("DataType", properties.getDataType().toString());
        return data;
	}
	
	/**
	 * find record by id
	 * @param idRecordType
	 * @return bar or rir record
	 */
	private IRecords findById(int idRecordType) {
		IRecords record = null;
		switch (dataType) {
		case BAR_DATA:
			record = BARRecords.findById(idRecordType);
			break;
		case RIR_DATA:
			record = RIRRecords.findById(idRecordType);
			break;
		}
		return record;
	}
}
