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
    
    private String globalTimestamp = "";
    private int globalEventId = 0;
    private CsvFile csvFileToWork = null;
    private ArrayList<String> headers = new ArrayList<String>();
    
    private final static String TIMESTAMP = "timestamp";
    private final static String SIMPLE_DATE_FORMAT = "yyyyMMdd.hhmm";
    private final static String FILE_FORMAT = ".txt";
    
    @Override
    public void init(GpehTransferData element) {
        outputDirectory = element.get(GpehTransferData.OUTPUT_DIRECTORY).toString();
    }

    @Override
    public void save(GpehTransferData element) {
        String timestamp = element.get(TIMESTAMP).toString();
        if (!globalTimestamp.equals(timestamp)) {
            if (!globalTimestamp.equals(""))
                finishUp(element);
            
            globalTimestamp = timestamp;
        }
        
        // read id of event
        Event event = (Event)element.remove(GpehTransferData.EVENT);
        int eventId = event.getId();
        
        if (!openedFiles.containsKey(eventId)) {
            
            Events events = Events.findById(eventId);
            
            Date date = new Date(Long.parseLong(globalTimestamp));

            String wayToFile = outputDirectory + "\\" + eventId + "_" + 
                                (new SimpleDateFormat(SIMPLE_DATE_FORMAT).
                                 format(date)) + FILE_FORMAT;
            
            // create new file 
            File file = new File(wayToFile);
            CsvFile csvFile;
            try {
                csvFile = new CsvFile(file);
            } catch (FileNotFoundException e1) {
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
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        
        // get headers from csvfile
        if (globalEventId != eventId) {
            globalEventId = eventId;
            csvFileToWork = openedFiles.get(eventId);
            headers = csvFileToWork.getHeaders();
            headers.remove("EVENT_PARAM_TIMESTAMP_HOUR");
            headers.remove("EVENT_PARAM_TIMESTAMP_MINUTE");
            headers.remove("EVENT_PARAM_TIMESTAMP_SECOND");
            headers.remove("EVENT_PARAM_TIMESTAMP_MILLISEC");
        }
        
        // create array list of data
        ArrayList<String> data = new ArrayList<String>();
        data.add(((Object)event.getHour()).toString());
        data.add(((Object)event.getMinute()).toString());
        data.add(((Object)event.getSecond()).toString());
        data.add(((Object)event.getMillisecond()).toString());

        String currentHeaderValue = null;
        for (String header : headers) {
            if (element.get(header) != null) {
                if (header.equals("EVENT_PARAM_MESSAGE_CONTENTS")) {
                    currentHeaderValue = new String((byte[])element.get(header));
                }
                else {
                    currentHeaderValue = element.get(header).toString();
                }
            }
            
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
            // TODO Handle IOException
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    @Override
    public void finishUp(GpehTransferData element) {
        for (CsvFile csvFile : openedFiles.values()) {
            try {
                csvFile.close();
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        openedFiles.clear();
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
