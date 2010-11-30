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

package org.amanzi.awe.gpeh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.gpeh.parser.Events;
import org.amanzi.awe.gpeh.parser.Parameters;
import org.amanzi.awe.gpeh.parser.internal.GPEHEvent.Event;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.MetaData;

/**
 * Class to save GPEH-data in csv-format
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public class GpehCSVSaver implements ISaver<GpehTransferData> {
    // opened files
    private Map<Integer,CsvFile> openedFiles = new HashMap<Integer,CsvFile>();
    // output directory to saving csv-files
    private String outputDirectory = null;
    
    //TODO: LN: comments?    
    private long globalTimestamp = 0;
    private int globalEventId = 0;
    private CsvFile csvFileToWork = null;
    private ArrayList<String> headers = new ArrayList<String>();
    
    //TODO: LN: comments? 
    private final static String TIMESTAMP = "timestamp";
    private final static String SIMPLE_DATE_FORMAT = "yyyyMMdd.hhmm";
    private final static String FILE_FORMAT = ".txt";
    private final static SimpleDateFormat simpleDateFormat = 
                                    new SimpleDateFormat(SIMPLE_DATE_FORMAT);
    //||| for testing
    private long count = 0;
    @Override
    public void init(GpehTransferData element) {
        outputDirectory = element.get(GpehTransferData.OUTPUT_DIRECTORY).toString();
    }

    @Override
    public void save(GpehTransferData element) {
        count++;
        long timestamp = (Long)element.get(TIMESTAMP);
        if (globalTimestamp != timestamp) {
            if (globalTimestamp != 0)
                //TODO: LN: we should not call finishUp to clean up files - 
                //but a architecture finishUp is a method that should be called only once
                //as a solution - move all logic for cleaning files to another method and call this methis 
                //from this place and from finishUp
                finishUp(element);
            
            globalTimestamp = timestamp;
        }
        
        // read id of event
        Event event = (Event)element.remove(GpehTransferData.EVENT);
        int eventId = event.getId();
        
        //TODO: LN: to make it more quick we can replace 
        //calling 1. contains, 2. get with
        //1. get, 2. if result is null - than create, otherwise use result
        if (!openedFiles.containsKey(eventId)) {
            
            Events events = Events.findById(eventId);
            
            Date date = new Date(globalTimestamp);

            String wayToFile = outputDirectory + "\\" + eventId + "_" + 
                                (simpleDateFormat.format(date)) + FILE_FORMAT;
            
            // create new file 
            File file = new File(wayToFile);
            CsvFile csvFile;
            try {
                csvFile = new CsvFile(file);
            } catch (FileNotFoundException e1) {
                //TODO: LN: implement good exception handling - in case if we cannot create new file - we should 
                //inform user about this error and stop loading
                
                // TODO Handle FileNotFoundException
                throw (RuntimeException) new RuntimeException( ).initCause( e1 );
            }
            // add id to file to associate his with some event
            csvFile.setEventId(eventId);
            
            openedFiles.put(eventId,csvFile);
            
            // create headers
            Parameters[] parameters = events.getAllParametersWithTimestamp();
            
            // create array list of headers
            ArrayList<String> headers = new ArrayList<String>();
            for (Parameters parameter : parameters) {
                headers.add(parameter.name());
            }
            
            // add headers to csvfile
            csvFile.setHeaders(headers);
            // write headers to csvfile
            try {
                csvFile.writeData(headers);
                //TODO: LN: use constants instead of strings
                headers.remove("EVENT_PARAM_TIMESTAMP_HOUR");
                headers.remove("EVENT_PARAM_TIMESTAMP_MINUTE");
                headers.remove("EVENT_PARAM_TIMESTAMP_SECOND");
                headers.remove("EVENT_PARAM_TIMESTAMP_MILLISEC");
            } catch (IOException e) {
                //TODO: LN: implement good exception handling - in case if we cannot write data to file - we should 
                //inform user about this error and stop loading
                
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        
        // get headers from csvfile
        if (globalEventId != eventId) {
            globalEventId = eventId;
            csvFileToWork = openedFiles.get(eventId);
            headers = csvFileToWork.getHeaders();
        }
        
        // create array list of data
        ArrayList<String> data = new ArrayList<String>();
        data.add(Long.toString(event.getHour()));
        data.add(Long.toString(event.getMinute()));
        data.add(Long.toString(event.getSecond()));
        data.add(Long.toString(event.getMillisecond()));

        String currentHeaderValue = null;
        for (String header : headers) {
            if (element.get(header) != null) {
                //TODO: LN: use constants instead of strings
                if (header.equals("EVENT_PARAM_MESSAGE_CONTENTS")) {
                    currentHeaderValue = new String((byte[])element.get(header));
                }
                else {
                    currentHeaderValue = element.get(header).toString();
                }
            }
            
            //TODO: LN: unnesessary if - anyway if currentHeaderValue is null we put to data null
            if (currentHeaderValue != null) {
                data.add(currentHeaderValue);
            }
            else {
                data.add(null);
            }
            currentHeaderValue = null;
        }
        
        
        // write data to needing csvfile
        try {
            csvFileToWork.writeData(data);
        } catch (IOException e) {
            //TODO: LN: implement good exception handling - in case if we cannot write data to file - we should 
            //inform user about this error and stop loading
            
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    @Override
    public void finishUp(GpehTransferData element) {
        for (CsvFile csvFile : openedFiles.values()) {
            try {
                csvFile.close();
            } catch (IOException e) {
                //TODO: LN: implement good exception handling - in case if we cannot write data to file - we should 
                //inform user about this error and stop loading
                
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        openedFiles.clear();
        System.out.println("Count = " + count);
    }

    @Override
    public PrintStream getPrintStream() {
        return null;
    }

    @Override
    public void setPrintStream(PrintStream outputStream) {
    }

    @Override
    public Iterable<MetaData> getMetaData() {
        return null;
    }

}
