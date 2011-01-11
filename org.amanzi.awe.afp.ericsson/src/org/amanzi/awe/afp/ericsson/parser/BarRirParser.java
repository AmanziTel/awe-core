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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.amanzi.awe.afp.ericsson.BARRecords;
import org.amanzi.awe.afp.ericsson.BlockParameters;
import org.amanzi.awe.afp.ericsson.CountableParameters;
import org.amanzi.awe.afp.ericsson.DataType;
import org.amanzi.awe.afp.ericsson.IParameters;
import org.amanzi.awe.afp.ericsson.IRecords;
import org.amanzi.awe.afp.ericsson.Parameters;
import org.amanzi.awe.afp.ericsson.RIRRecords;
import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.parser.CommonFilesParser;

/**
 * @author Kasnitskij_V Class to parsing of RAR- or BAR- data
 */
public class BarRirParser extends CommonFilesParser<RecordTransferData, CommonConfigData> {

    protected DataType dataType;

    @Override
    protected RecordTransferData getFinishData() {
        return null;
    }

    @Override
    protected boolean parseElement(FileElement element) {
        // stream of data from file
        CountingFileInputStream input = null;
        try {
            input = new CountingFileInputStream(element.getFile());
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
        // records = new ArrayList<MainRecord>();
        int persentageOld = 0;
        try {
            // read file while it is possible
            while (inputStream.available() != 0) {
                try {
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
                    mainRecord.record.addProperty(Parameters.RECORD_TYPE, idRecordType);
                    mainRecord.record.addProperty(Parameters.RECORD_LENGTH, recordLength);

                    // get type of record
                    recordType = findById(idRecordType);

                    if (recordType.toString().equals(BARRecords.ADMINISTRATIVE.toString())) {
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
                        if (!parameter.toString().equals(Parameters.RECORD_TYPE.toString()) && !parameter.toString().equals(Parameters.RECORD_LENGTH.toString())) {

                            if (parameter.isBlock()) {
                                BlockParameters block = (BlockParameters)parameter;
                                for (int i = 0; i < block.getCount(); i++) {
                                    for (int j = 0; j < block.getParameters().length; j++) {
                                        byte data[] = new byte[parameter.getBytesLen()];
                                        inputStream.read(data, 0, parameter.getBytesLen());

                                        mainRecord.record.addProperty(new CountableParameters(parameter, i), data);
                                    }
                                }
                            } else {
                                byte data[] = new byte[parameter.getBytesLen()];
                                inputStream.read(data, 0, parameter.getBytesLen());

                                mainRecord.record.addProperty(parameter, data);
                            }
                        }
                    }
                    RecordTransferData record = new RecordTransferData();
                    record.setFileName(element.getFile().getName());
                    record.setType(dataType);
                    record.setRecord(mainRecord);
                    // add read data
                    // records.add(mainRecord);
                    getSaver().save(record);
                    // if read all file then break from cycle
                    if (startFile && endFile) {
                        break;
                    }
                } finally {
                    int persentage = input.percentage();
                    if (persentage - persentageOld > PERCENTAGE_FIRE) {
                        persentageOld = persentage;
                        if (fireSubProgressEvent(element, new ProgressEventImpl(String.format(getDescriptionFormat(), element.getFile().getName()), persentage / 100d))) {
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            exception(e);
        } finally {
            closeStream(inputStream);
        }
        return false;

    }

    @Override
    protected RecordTransferData getStartupElement(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        RecordTransferData record = new RecordTransferData();
        record.setFileName(element.getFile().getName());
        record.setType(dataType);
        return record;
    }

    @Override
    protected RecordTransferData getInitData(CommonConfigData properties) {
        RecordTransferData initdata = new RecordTransferData();
        initdata.setProjectName(properties.getProjectName());
        initdata.setRootName(properties.getDbRootName());
        initdata.setType(dataType);
        return initdata;
    }

    /**
     * find record by id
     * 
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
