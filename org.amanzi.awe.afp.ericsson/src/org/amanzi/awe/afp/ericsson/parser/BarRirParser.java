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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import org.amanzi.neo.loader.core.saver.ISaver;

/**
 * @author Kasnitskij_V Class to parsing of RAR- or BAR- data
 */
public class BarRirParser extends CommonFilesParser<RecordTransferData, CommonConfigData> {

    List<FileElement>barFiles=new LinkedList<FileElement>();
    List<FileElement>rirFiles=new LinkedList<FileElement>();
    @Override
    protected RecordTransferData getFinishData() {
        return null;
    }
    @Override
    public void init(CommonConfigData properties, ISaver<RecordTransferData> saver) {
        super.init(properties, saver);
        barFiles.clear();
        rirFiles.clear();
    }
@Override
protected List<org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement> getElementList() {
    barFiles.addAll(super.getElementList());
    Collection<File> fileToLoad=(Collection<File>)getProperties().getAdditionalProperties().get("RIR_FILES");
    if (fileToLoad!=null){
        rirFiles.addAll(formFileElements(fileToLoad));
    }
    List<FileElement> result=new LinkedList<FileElement>(); 
    result.addAll(barFiles);
    result.addAll(rirFiles);
    return result;
}
    @Override
    protected boolean parseElement(FileElement element) {
        DataType dataType = getDataType(element);
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
                    int recordLength = inputStream.read() + inputStream.read()*256;
                    mainRecord.record.addProperty(Parameters.RECORD_TYPE, idRecordType);
                    mainRecord.record.addProperty(Parameters.RECORD_LENGTH, recordLength);

                    // get type of record
                    recordType = findById(dataType,idRecordType);
                    if (recordType==null){
                        System.err.println("incorrect type");
                        break;
                    }
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
                                byte[]count=(byte[])mainRecord.record.getProperties().get(Parameters.NUMBER_OF_FREQUENCIES);
                                int c=(0x000000FF & ((int)count[0]));
                                for (int i = 1; i <= c; i++) {
                                    for (int j = 0; j < block.getParameters().length; j++) {
                                        byte data[] = new byte[block.getParameters()[j].getBytesLen()];
                                        inputStream.read(data, 0, block.getParameters()[j].getBytesLen());
                                        mainRecord.record.addProperty(new CountableParameters(block.getParameters()[j], i), data);
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


    /**
     * Gets the data type.
     *
     * @param element the element
     * @return the data type
     */
    private DataType getDataType(FileElement element) {
        return barFiles.contains(element)?DataType.BAR_DATA:DataType.RIR_DATA;
    }
    @Override
    protected RecordTransferData getStartupElement(FileElement element) {
        RecordTransferData record = new RecordTransferData();
        record.setFileName(element.getFile().getName());
        record.setType(getDataType(element));
        return record;
    }

    @Override
    protected RecordTransferData getInitData(CommonConfigData properties) {
        RecordTransferData initdata = new RecordTransferData();
        initdata.setProjectName(properties.getProjectName());
        initdata.setRootName(properties.getDbRootName());
        return initdata;
    }

    /**
     * find record by id
     * 
     * @param idRecordType
     * @return bar or rir record
     */
    private IRecords findById(DataType dataType,int idRecordType) {
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
